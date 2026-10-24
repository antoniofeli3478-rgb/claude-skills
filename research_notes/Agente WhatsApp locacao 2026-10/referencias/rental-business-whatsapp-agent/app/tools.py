from __future__ import annotations

import base64
import contextvars
import logging
import mimetypes
import re
import tempfile
import uuid
from datetime import date, datetime, timedelta, timezone
from decimal import Decimal
from pathlib import Path
from typing import Any

import requests
import resend
from groq import Groq
from langchain.tools import tool
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.platypus import Paragraph, SimpleDocTemplate, Spacer, Table, TableStyle

from .config import DEEPGRAM_API_KEY, DEEPGRAM_TTS_MODEL, GROQ_API_KEY, GROQ_STT_MODEL, OWNER_EMAIL, RESEND_API_KEY, RESEND_FROM_EMAIL, TMP_DIR
from .database import (
    Booking, Conversation, Quote, Vehicle, attach_customer_to_conversation, confirm_quote_and_create_booking,
    create_quote_record, db_session, find_conflicts, find_vehicle, get_business, get_latest_pending_quote,
    get_or_create_customer, get_or_create_conversation, get_quote, get_vehicle, get_vehicle_with_image, list_bookings, list_vehicles,
)
from .telemetry import emit_event, tracer
from sqlalchemy import select


CURRENT_WHATSAPP_CONTEXT: contextvars.ContextVar[Any | None] = contextvars.ContextVar("current_whatsapp_context", default=None)
CURRENT_SESSION: contextvars.ContextVar[str] = contextvars.ContextVar("current_session", default="whatsapp:default")


def _ctx() -> Any | None:
    return CURRENT_WHATSAPP_CONTEXT.get()


def set_whatsapp_context(ctx: Any | None, session: str):
    return CURRENT_WHATSAPP_CONTEXT.set(ctx), CURRENT_SESSION.set(session)


def reset_whatsapp_context(tokens: tuple[Any, Any]) -> None:
    CURRENT_WHATSAPP_CONTEXT.reset(tokens[0])
    CURRENT_SESSION.reset(tokens[1])


def _money(value: Decimal | float | int) -> str:
    return f"PKR {float(value):,.0f}"


def _normalise_date(value: str) -> date:
    raw = value.strip()
    for fmt in ("%Y-%m-%d", "%d-%m-%Y", "%d/%m/%Y", "%d %B %Y", "%d %b %Y"):
        try:
            return datetime.strptime(raw, fmt).date()
        except ValueError:
            pass
    low = raw.lower()
    today = date.today()
    if low == "today": return today
    if low == "tomorrow": return today + timedelta(days=1)
    if low in {"day after tomorrow", "day-after-tomorrow"}: return today + timedelta(days=2)
    raise ValueError(f"Could not understand date: {value}")


def _business_payload() -> dict[str, Any]:
    b = get_business()
    return {
        "name": b.name, "tagline": b.tagline, "city": b.city, "phone": b.phone,
        "email": b.email, "hours": b.hours, "driver_daily_fee": float(b.driver_daily_fee),
        "pickup_areas": b.pickup_areas or [], "welcome_message": b.welcome_message, "terms": b.terms,
    }


def _vehicle_payload(v: Vehicle) -> dict[str, Any]:
    return {
        "id": v.id, "name": v.name, "category": v.category,
        "daily_rate": float(v.daily_rate), "short_description": v.short_description,
        "image_filename": v.image_filename, "active": v.active,
    }


def _make_quote_pdf(quote: Quote) -> Path:
    path = TMP_DIR / f"quote_{quote.id}.pdf"
    b = get_business()
    styles = getSampleStyleSheet()
    doc = SimpleDocTemplate(str(path), pagesize=A4, rightMargin=15*mm, leftMargin=15*mm, topMargin=15*mm, bottomMargin=15*mm)
    story = [
        Paragraph(b.name, styles["Title"]),
        Paragraph("OFFICIAL QUOTATION", styles["Heading2"]),
        Spacer(1, 6*mm),
        Paragraph(f"Quote ID: {quote.id}", styles["Normal"]),
        Paragraph(f"Prepared: {quote.created_at.astimezone().strftime('%Y-%m-%d %H:%M %Z') if quote.created_at.tzinfo else quote.created_at.strftime('%Y-%m-%d %H:%M')}", styles["Normal"]),
        Spacer(1, 6*mm),
    ]
    rows = [
        ["Vehicle", "Days", "Rate / Day", "Amount"],
        [quote.vehicle_name, str(quote.days), _money(quote.daily_rate), _money(quote.rental_total)],
        ["Driver (optional)", "-", "-", _money(quote.driver_fee)],
        ["Total", "", "", _money(quote.total)],
    ]
    table = Table(rows, colWidths=[70*mm, 25*mm, 35*mm, 40*mm])
    table.setStyle(TableStyle([
        ("BACKGROUND", (0,0), (-1,0), colors.HexColor("#111827")),
        ("TEXTCOLOR", (0,0), (-1,0), colors.white),
        ("GRID", (0,0), (-1,-1), 0.5, colors.HexColor("#d1d5db")),
        ("BACKGROUND", (0,-1), (-1,-1), colors.HexColor("#eef2ff")),
        ("FONTNAME", (0,0), (-1,0), "Helvetica-Bold"),
        ("FONTNAME", (0,-1), (-1,-1), "Helvetica-Bold"),
        ("ALIGN", (1,1), (-1,-1), "RIGHT"),
        ("VALIGN", (0,0), (-1,-1), "MIDDLE"),
        ("BOTTOMPADDING", (0,0), (-1,-1), 7), ("TOPPADDING", (0,0), (-1,-1), 7),
    ]))
    story += [table, Spacer(1,8*mm), Paragraph(f"Customer: {quote.customer_name}", styles["Normal"]), Paragraph(f"Phone: {quote.customer_phone}", styles["Normal"]), Paragraph(f"Pickup: {quote.pickup_location}", styles["Normal"]), Paragraph(f"Pickup date: {quote.pickup_date}", styles["Normal"]), Paragraph(f"Return date: {quote.return_date}", styles["Normal"]), Spacer(1,5*mm), Paragraph(b.terms, styles["Italic"])]
    doc.build(story)
    return path


def _make_receipt_pdf(booking: Booking) -> Path:
    path = TMP_DIR / f"booking_receipt_{booking.id}.pdf"
    b = get_business()
    styles = getSampleStyleSheet()
    doc = SimpleDocTemplate(str(path), pagesize=A4, rightMargin=15*mm, leftMargin=15*mm, topMargin=15*mm, bottomMargin=15*mm)
    story = [Paragraph(b.name, styles["Title"]), Paragraph("BOOKING CONFIRMATION / RECEIPT", styles["Heading2"]), Spacer(1,5*mm), Paragraph(f"Booking ID: {booking.id}", styles["Normal"]), Paragraph(f"Quote ID: {booking.quote_id}", styles["Normal"]), Spacer(1,5*mm)]
    rows = [["Customer", booking.customer_name],["Phone", booking.customer_phone],["Vehicle", booking.vehicle_name],["Pickup date", str(booking.pickup_date)],["Return date", str(booking.return_date)],["Pickup location", booking.pickup_location],["Total", _money(booking.total)],["Status", "CONFIRMED"]]
    table = Table(rows, colWidths=[45*mm,125*mm])
    table.setStyle(TableStyle([("GRID",(0,0),(-1,-1),0.5,colors.HexColor("#d1d5db")),("BACKGROUND",(0,0),(0,-1),colors.HexColor("#eef2ff")),("FONTNAME",(0,0),(0,-1),"Helvetica-Bold"),("FONTNAME",(1,-1),(1,-1),"Helvetica-Bold"),("BOTTOMPADDING",(0,0),(-1,-1),8),("TOPPADDING",(0,0),(-1,-1),8)]))
    story += [table, Spacer(1,7*mm), Paragraph("Thank you for booking with us. Please keep this receipt for your records.", styles["Normal"])]
    doc.build(story)
    return path


def _send_whatsapp_document(path: Path, caption: str) -> None:
    ctx = _ctx()
    if ctx is not None:
        ctx.reply_document(file=path, caption=caption)


@tool
def get_business_info() -> str:
    """Get the current business profile, contact details, hours, pickup areas, and booking rules from the database."""
    with tracer.start_as_current_span("tool.get_business_info"):
        b = _business_payload()
        emit_event("tool.completed", tool="get_business_info")
        return f"{b['name']}\nAddress/City: {b['city']}\nHours: {b['hours']}\nPhone: {b['phone']}\nPickup areas: {', '.join(b['pickup_areas'])}"


@tool
def list_available_vehicles(category: str = "") -> str:
    """List currently active vehicles and their prices from the database. Use this for inventory questions."""
    with tracer.start_as_current_span("tool.list_available_vehicles"):
        rows = list_vehicles(active_only=True)
        if category.strip(): rows = [v for v in rows if category.lower() in v.category.lower()]
        if not rows: return "No vehicles are currently active."
        lines = ["🚗 AVAILABLE CARS", ""]
        for index, v in enumerate(rows, start=1):
            lines.extend([
                f"{index}. {v.name}",
                f"   Type: {v.category}",
                f"   Rate: {_money(v.daily_rate)}/day",
                f"   {v.short_description}",
                "",
            ])
        emit_event("tool.completed", tool="list_available_vehicles", count=len(rows))
        return "\n".join(lines).strip()


@tool
def check_vehicle_availability(vehicle_name: str, pickup_date: str, return_date: str) -> str:
    """Check whether a vehicle is available for the requested dates. Always use before creating a quote."""
    with tracer.start_as_current_span("tool.check_vehicle_availability"):
        vehicle = find_vehicle(vehicle_name)
        if not vehicle: return f"Vehicle not found: {vehicle_name}."
        start, end = _normalise_date(pickup_date), _normalise_date(return_date)
        if end <= start: return "Return date must be after pickup date."
        conflicts = find_conflicts(vehicle.id, start, end)
        available = not conflicts
        emit_event("tool.completed", tool="check_vehicle_availability", available=available, vehicle=vehicle.name)
        if available: return f"AVAILABLE: {vehicle.name} is available from {start} to {end}."
        return f"UNAVAILABLE: {vehicle.name} is already booked for part of that period."


@tool
def calculate_quote(vehicle_name: str, pickup_date: str, return_date: str, with_driver: bool = False) -> str:
    """Calculate rental price without booking. Use after availability is checked."""
    with tracer.start_as_current_span("tool.calculate_quote"):
        vehicle = find_vehicle(vehicle_name)
        if not vehicle: return f"Vehicle not found: {vehicle_name}."
        start, end = _normalise_date(pickup_date), _normalise_date(return_date)
        if end <= start: return "Return date must be after pickup date."
        days = max(1, (end - start).days)
        rental_total = Decimal(str(vehicle.daily_rate)) * days
        driver_fee = Decimal(str(get_business().driver_daily_fee)) * days if with_driver else Decimal("0")
        total = rental_total + driver_fee
        return f"Vehicle: {vehicle.name}\nDays: {days}\nRate/day: {_money(vehicle.daily_rate)}\nDriver fee: {_money(driver_fee)}\nTOTAL: {_money(total)}"


@tool
def send_vehicle_photos(vehicle_name: str) -> str:
    """Send the selected vehicle's stored image to the current WhatsApp customer."""
    with tracer.start_as_current_span("tool.send_vehicle_photos"):
        vehicle = find_vehicle(vehicle_name)
        if not vehicle:
            return f"Vehicle not found: {vehicle_name}."
        vehicle = get_vehicle_with_image(vehicle.id) or vehicle
        ctx = _ctx()
        if not vehicle.image_data:
            return f"No image is stored for {vehicle.name}. Ask the administrator to upload the vehicle photo."
        if ctx is None:
            return f"Vehicle photo available for {vehicle.name} (WhatsApp mode sends the stored image)."
        suffix = Path(vehicle.image_filename or "photo.jpg").suffix or ".jpg"
        path = TMP_DIR / f"vehicle_{vehicle.id}_{uuid.uuid4().hex[:6]}{suffix}"
        path.write_bytes(vehicle.image_data)
        try:
            ctx.reply_image(file=path, caption=f"🚗 {vehicle.name} — {_money(vehicle.daily_rate)}/day")
        finally:
            path.unlink(missing_ok=True)
        emit_event("vehicle.photo.sent", vehicle=vehicle.name)
        return f"Sent the {vehicle.name} photo."


@tool
def create_quote(customer_name: str, customer_phone: str, vehicle_name: str, pickup_date: str, return_date: str, pickup_location: str, with_driver: bool = False) -> str:
    """Create a pending quote after required customer details and availability are confirmed. The PDF is sent in WhatsApp mode. It does not book the car."""
    with tracer.start_as_current_span("tool.create_quote") as span:
        vehicle = find_vehicle(vehicle_name)
        if not vehicle: return f"Vehicle not found: {vehicle_name}."
        start, end = _normalise_date(pickup_date), _normalise_date(return_date)
        if end <= start: return "Return date must be after pickup date."
        conflicts = find_conflicts(vehicle.id, start, end)
        if conflicts: return f"UNAVAILABLE: {vehicle.name} is already booked for part of that period."
        customer_id = get_or_create_customer(customer_name, customer_phone)
        session = CURRENT_SESSION.get()
        conv = get_or_create_conversation(session)
        attach_customer_to_conversation(session, customer_id)
        days = max(1, (end - start).days)
        rental_total = Decimal(str(vehicle.daily_rate)) * days
        driver_fee = Decimal(str(get_business().driver_daily_fee)) * days if with_driver else Decimal("0")
        total = rental_total + driver_fee
        quote = Quote(
            id=f"Q-{datetime.now().strftime('%Y%m%d')}-{uuid.uuid4().hex[:4].upper()}",
            conversation_id=conv.id, customer_id=customer_id, vehicle_id=vehicle.id,
            customer_name=customer_name.strip(), customer_phone=customer_phone.strip(), vehicle_name=vehicle.name,
            pickup_date=start, return_date=end, pickup_location=pickup_location.strip(), with_driver=with_driver,
            days=days, daily_rate=vehicle.daily_rate, driver_fee=driver_fee, rental_total=rental_total, total=total,
            status="pending_confirmation",
            created_at=datetime.now(timezone.utc),
        )
        create_quote_record({
            "id": quote.id, "conversation_id": quote.conversation_id, "customer_id": quote.customer_id, "vehicle_id": quote.vehicle_id,
            "customer_name": quote.customer_name, "customer_phone": quote.customer_phone, "vehicle_name": quote.vehicle_name,
            "pickup_date": quote.pickup_date, "return_date": quote.return_date, "pickup_location": quote.pickup_location,
            "with_driver": quote.with_driver, "days": quote.days, "daily_rate": quote.daily_rate, "driver_fee": quote.driver_fee,
            "rental_total": quote.rental_total, "total": quote.total, "status": quote.status,
        })
        pdf = _make_quote_pdf(quote)
        try:
            _send_whatsapp_document(pdf, f"📄 Quote {quote.id} — Total {_money(total)}")
        finally:
            pdf.unlink(missing_ok=True)
        span.set_attribute("quote.id", quote.id)
        emit_event("quote.created", quote_id=quote.id, total=float(total), vehicle=vehicle.name)
        return f"Quote {quote.id} prepared for {vehicle.name}. Total: {_money(total)}. The PDF quote has been sent. Wait for explicit customer confirmation before booking."


@tool
def confirm_booking() -> str:
    """Confirm the latest pending quote for the current conversation. Only call this after the customer explicitly confirms the quote."""
    with tracer.start_as_current_span("tool.confirm_booking") as span:
        session = CURRENT_SESSION.get()
        quote = get_latest_pending_quote(session)
        if not quote: return "No pending quote exists for this conversation. Create a quote first."
        booking, created = confirm_quote_and_create_booking(quote.id)
        span.set_attribute("booking.id", booking.id)
        receipt_pdf = _make_receipt_pdf(booking)
        try:
            try:
                _send_whatsapp_document(receipt_pdf, f"✅ Booking confirmed — {booking.id}\nTotal: {_money(booking.total)}")
                emit_event("booking.receipt.sent", booking_id=booking.id)
            except Exception as exc:
                logging.getLogger("whatsapp-business-agent").exception("Customer receipt failed")
                emit_event("booking.receipt.failed", booking_id=booking.id, error=str(exc))
            email_ok, email_status = _send_owner_email(booking, receipt_pdf)
            emit_event("booking.confirmed", booking_id=booking.id, email_status=email_status, created=created)
        finally:
            receipt_pdf.unlink(missing_ok=True)
        email_line = "Owner email sent." if email_ok else "Owner email could not be sent automatically; the booking is still confirmed."
        return f"BOOKING CONFIRMED — {booking.id}\nVehicle: {booking.vehicle_name}\nDates: {booking.pickup_date} to {booking.return_date}\nPickup: {booking.pickup_location}\nTotal: {_money(booking.total)}\n{email_line}"


@tool
def list_upcoming_bookings(days_ahead: int = 30) -> str:
    """List confirmed bookings for the next N days. Intended for the business owner, not general customers."""
    with tracer.start_as_current_span("tool.list_upcoming_bookings"):
        rows = list_bookings(max(1, min(days_ahead, 365)))
        if not rows: return "No confirmed bookings found in the requested window."
        return "\n\n".join(f"{b.id}\nDate: {b.pickup_date}\nCustomer: {b.customer_name}\nVehicle: {b.vehicle_name}\nTotal: {_money(b.total)}" for b in rows)


@tool
def get_booking_by_id(booking_id: str) -> str:
    """Look up a confirmed booking by booking ID."""
    with tracer.start_as_current_span("tool.get_booking_by_id"):
        with db_session() as db:
            row = db.get(Booking, booking_id.strip())
            if not row: return f"Booking not found: {booking_id}"
            return f"Booking: {row.id}\nCustomer: {row.customer_name}\nVehicle: {row.vehicle_name}\nDates: {row.pickup_date} to {row.return_date}\nPickup: {row.pickup_location}\nTotal: {_money(row.total)}\nStatus: {row.status}"


def transcribe_audio(path: Path) -> str:
    with tracer.start_as_current_span("media.stt"):
        client = Groq(api_key=GROQ_API_KEY)
        with path.open("rb") as handle:
            result = client.audio.transcriptions.create(file=(path.name, handle.read()), model=GROQ_STT_MODEL, response_format="json", temperature=0)
        return str(result.text).strip()


def _speech_clean(text: str) -> str:
    s = text.replace("```", " ").replace("**", " ").replace("__", " ").replace("`", " ")
    s = re.sub(r"https?://\S+", "", s)
    s = re.sub(r"[🇦-🇿]{2}", "", s)
    s = re.sub(r"[🌀-🫿☀-➿]", "", s)
    s = re.sub(r"^\s*[-*•✅🚗📅📍📄🔔💰📝]+\s*", "", s, flags=re.MULTILINE)
    s = re.sub(r"[#*_]+", " ", s)
    s = re.sub(r"\s+", " ", s).strip()
    return s


def synthesize_speech(text: str) -> Path:
    with tracer.start_as_current_span("media.tts"):
        cleaned = _speech_clean(text)
        if len(cleaned) > 1600: cleaned = cleaned[:1600].rsplit(" ", 1)[0] + "..."
        output = TMP_DIR / f"reply_{uuid.uuid4().hex[:8]}.mp3"
        response = requests.post("https://api.deepgram.com/v1/speak", params={"model": DEEPGRAM_TTS_MODEL, "encoding": "mp3"}, headers={"Authorization": f"Token {DEEPGRAM_API_KEY}", "Content-Type": "application/json"}, json={"text": cleaned}, timeout=60)
        response.raise_for_status()
        output.write_bytes(response.content)
        emit_event("tts.completed", characters=len(cleaned))
        return output


def _send_owner_email(booking: Booking, receipt_pdf: Path | None) -> tuple[bool, str]:
    if not RESEND_API_KEY or not OWNER_EMAIL:
        emit_event("owner.email.skipped", reason="missing Resend config")
        return False, "not configured"
    b = get_business()
    params: dict[str, Any] = {
        "from": RESEND_FROM_EMAIL,
        "to": [OWNER_EMAIL],
        "subject": f"New Booking — {booking.id}",
        "html": f"<div style='font-family:Arial,sans-serif;max-width:680px;margin:auto'><h2>New booking — {booking.id}</h2><p>Confirmed through the WhatsApp AI agent for <b>{b.name}</b>.</p><table cellpadding='8' style='border-collapse:collapse;width:100%'><tr><td><b>Customer</b></td><td>{booking.customer_name}</td></tr><tr><td><b>Phone</b></td><td>{booking.customer_phone}</td></tr><tr><td><b>Vehicle</b></td><td>{booking.vehicle_name}</td></tr><tr><td><b>Pickup</b></td><td>{booking.pickup_date}</td></tr><tr><td><b>Return</b></td><td>{booking.return_date}</td></tr><tr><td><b>Location</b></td><td>{booking.pickup_location}</td></tr><tr><td><b>Total</b></td><td>{_money(booking.total)}</td></tr><tr><td><b>Status</b></td><td>Confirmed</td></tr></table></div>",
    }
    if receipt_pdf and receipt_pdf.exists():
        params["attachments"] = [{"content": base64.b64encode(receipt_pdf.read_bytes()).decode("ascii"), "filename": receipt_pdf.name, "content_type": "application/pdf"}]
    try:
        with tracer.start_as_current_span("email.resend.send") as span:
            span.set_attribute("email.to", OWNER_EMAIL)
            resend.api_key = RESEND_API_KEY
            resend.Emails.send(params)
        emit_event("owner.email.sent", booking_id=booking.id)
        return True, "sent"
    except Exception as exc:
        logging.getLogger("whatsapp-business-agent").exception("Owner email failed")
        emit_event("owner.email.failed", booking_id=booking.id, error=str(exc))
        return False, "failed"


TOOLS = [get_business_info, list_available_vehicles, check_vehicle_availability, calculate_quote, send_vehicle_photos, create_quote, confirm_booking, list_upcoming_bookings, get_booking_by_id]
