from __future__ import annotations

import asyncio
import json
import logging
import mimetypes
import os
import tempfile
import threading
import traceback
from contextlib import asynccontextmanager
from pathlib import Path
from typing import Any

from fastapi import Depends, FastAPI, File, Form, Header, HTTPException, UploadFile
from fastapi.responses import FileResponse, Response, StreamingResponse
from pydantic import BaseModel, Field
from whagent import Agent

from .agent import run_agent
from .config import ADMIN_SECRET, DATA_DIR, MEDIA_DIR, SEED_DEMO, TMP_DIR, WHATSAPP_AGENT_TOKEN
from .database import Booking, Business, Vehicle, NeonOffsetStore, db_session, get_business, get_vehicle_with_image, init_db, ping_db, seed_demo_if_empty, was_message_processed, mark_message_processed
from .telemetry import MONITOR, emit_event, instrument_fastapi
from .tools import synthesize_speech, transcribe_audio
from sqlalchemy import func, select

ROOT = Path(__file__).resolve().parents[1]
STATIC_DIR = ROOT / "static"

logging.basicConfig(level=logging.INFO, format="%(asctime)s | %(levelname)s | %(message)s")
logger = logging.getLogger("whatsapp-business-agent")


class WhatsAppRunner:
    def __init__(self) -> None:
        self.thread: threading.Thread | None = None
        self.started = False
        self.error: str | None = None

    def start(self) -> None:
        if not WHATSAPP_AGENT_TOKEN:
            logger.warning("WHATSAPP_AGENT_TOKEN is not configured; WhatsApp polling is disabled.")
            return
        if self.thread and self.thread.is_alive():
            return
        self.thread = threading.Thread(target=self._run, name="whatsapp-poller", daemon=True)
        self.thread.start()
        self.started = True

    def _run(self) -> None:
        try:
            emit_event("whatsapp.thread_started")
            agent = Agent(WHATSAPP_AGENT_TOKEN, mark_read=True, typing=True, start="new", offset_store=NeonOffsetStore("whatsapp-main"))

            @agent.on_text
            def on_text(ctx):
                handle_text_message(ctx)

            @agent.on_message("audio")
            def on_audio(ctx):
                handle_audio_message(ctx)

            @agent.on_message("document")
            def on_document(ctx):
                ctx.reply("📄 For this service, I currently handle car rental questions, quotes and bookings. Send your request by text or voice.")

            @agent.on_message("image")
            def on_image(ctx):
                ctx.reply("🖼️ I can send you photos of available vehicles. Tell me which car you want to see.")

            @agent.on_update
            def on_update(update):
                logger.info("WhatsApp update received: %s", type(update).__name__)
                emit_event("whatsapp.update", update_type=type(update).__name__)

            @agent.on_error
            def on_error(exc, ctx):
                self.error = str(exc)
                logger.exception("whagent error: %s", exc)
                emit_event("whatsapp.error", error=str(exc))
                try:
                    if ctx:
                        ctx.reply("Sorry — something went wrong while processing that request. Please try again.")
                except Exception:
                    pass

            logger.info("WhatsApp transport: whagent polling started")
            emit_event("whatsapp.started")
            agent.run()
        except Exception as exc:
            self.error = str(exc)
            logger.error("WhatsApp thread stopped:\n%s", traceback.format_exc())
            emit_event("whatsapp.stopped", error=str(exc))


RUNNER = WhatsAppRunner()


def whatsapp_session(ctx: Any) -> str:
    """Best-effort stable sender/session identity across whagent versions."""
    candidates = []
    for obj in (ctx, getattr(ctx, "message", None)):
        if obj is None:
            continue
        for name in ("sender_id", "from_id", "chat_id", "user_id", "wa_id", "phone"):
            value = getattr(obj, name, None)
            if value:
                candidates.append(str(value))
        sender = getattr(obj, "sender", None)
        if sender:
            candidates.append(str(sender))
    return "whatsapp:" + (candidates[0] if candidates else "single-agent-user")


def handle_text_message(ctx: Any) -> None:
    text = (getattr(ctx, "text", "") or "").strip()
    if not text:
        return
    session = whatsapp_session(ctx)
    message_id = getattr(getattr(ctx, "message", None), "id", "")
    if was_message_processed(message_id):
        logger.info("Skipping previously processed WhatsApp message: %s", message_id)
        emit_event("whatsapp.message.skipped_duplicate", message_id=message_id)
        return
    logger.info("WhatsApp text: %s", text[:160])
    try:
        answer = run_agent(text, ctx=ctx, session=session)
        ctx.reply(answer)
        mark_message_processed(message_id)
    except Exception:
        logger.exception("Text handler failed")
        emit_event("whatsapp.text.error")
        ctx.reply("Sorry — I couldn't complete that request. Please try again.")


def handle_audio_message(ctx: Any) -> None:
    media = getattr(getattr(ctx, "message", None), "media", None)
    if not media or not getattr(media, "id", None):
        ctx.reply("I couldn't access that voice note. Please send it again.")
        return
    session = whatsapp_session(ctx)
    message_id = getattr(getattr(ctx, "message", None), "id", "")
    if was_message_processed(message_id):
        logger.info("Skipping previously processed WhatsApp message: %s", message_id)
        emit_event("whatsapp.message.skipped_duplicate", message_id=message_id)
        return
    suffix = Path(getattr(media, "filename", None) or "voice.ogg").suffix or ".ogg"
    with tempfile.NamedTemporaryFile(dir=TMP_DIR, suffix=suffix, delete=False) as handle:
        audio_path = Path(handle.name)
    try:
        ctx.download(audio_path)
        transcript = transcribe_audio(audio_path)
        emit_event("voice.transcript.internal", session=session, transcript=transcript[:160])
        answer = run_agent(transcript, ctx=ctx, session=session)
        audio_reply = synthesize_speech(answer)
        try:
            ctx.reply_audio(file=audio_reply)
            emit_event("voice.reply.sent", session=session, mode="voice-only")
            mark_message_processed(message_id)
        finally:
            audio_reply.unlink(missing_ok=True)
    except Exception as exc:
        logger.exception("Voice handler failed")
        emit_event("voice.error", error=str(exc))
        try:
            ctx.reply("Sorry — I couldn't create the voice reply. Please send the voice note again.")
            mark_message_processed(message_id)
        except Exception:
            pass
    finally:
        audio_path.unlink(missing_ok=True)


async def require_admin(x_admin_secret: str | None = Header(default=None)) -> None:
    if not ADMIN_SECRET:
        raise HTTPException(status_code=503, detail="ADMIN_SECRET is not configured.")
    if x_admin_secret != ADMIN_SECRET:
        raise HTTPException(status_code=401, detail="Invalid admin secret.")


@asynccontextmanager
async def lifespan(app: FastAPI):
    init_db()
    if SEED_DEMO:
        seed_demo_if_empty()
    try:
        db = ping_db()
        logger.info("Neon PostgreSQL ready in %sms", db["latency_ms"])
        emit_event("database.ready", latency_ms=db["latency_ms"])
    except Exception as exc:
        logger.exception("Database startup check failed")
        emit_event("database.error", error=str(exc))
    if WHATSAPP_AGENT_TOKEN:
        RUNNER.start()
    else:
        logger.warning("Running browser-only mode because WHATSAPP_AGENT_TOKEN is not configured.")
    yield


app = FastAPI(title="WhatsApp Business AI Agent", version="2.0.0", description="LangChain WhatsApp sales, quote and booking agent with Neon PostgreSQL and live observability.", lifespan=lifespan)
instrument_fastapi(app)


@app.get("/")
def monitor() -> FileResponse:
    return FileResponse(STATIC_DIR / "index.html")


@app.get("/admin")
def admin() -> FileResponse:
    return FileResponse(STATIC_DIR / "admin.html")


@app.get("/health")
def health() -> dict[str, Any]:
    db_ok = False
    db_error = None
    try:
        with db_session() as db:
            db.execute(select(Vehicle).limit(1))
        db_ok = True
    except Exception as exc:
        db_error = str(exc)
    return {"status": "ok" if db_ok else "degraded", "database": db_ok, "database_error": db_error, "whatsapp_configured": bool(WHATSAPP_AGENT_TOKEN), "whatsapp_thread_started": RUNNER.started, "whatsapp_thread_alive": bool(RUNNER.thread and RUNNER.thread.is_alive())}


@app.get("/api/whatsapp/status")
def whatsapp_status() -> dict[str, Any]:
    return {"configured": bool(WHATSAPP_AGENT_TOKEN), "thread_started": RUNNER.started, "thread_alive": bool(RUNNER.thread and RUNNER.thread.is_alive()), "error": RUNNER.error}


@app.get("/api/db/status")
def db_status() -> dict[str, Any]:
    try:
        ping = ping_db()
        with db_session() as db:
            vehicle_count = db.scalar(select(func.count()).select_from(Vehicle)) or 0
            booking_count = db.scalar(select(func.count()).select_from(Booking)) or 0
        return {"ok": True, "vehicles": int(vehicle_count), "bookings": int(booking_count), "latency_ms": ping["latency_ms"], "database": "Neon PostgreSQL"}
    except Exception as exc:
        return {"ok": False, "error": str(exc)}


@app.get("/api/traces")
def traces(limit: int = 200) -> list[dict[str, Any]]:
    return MONITOR.recent(max(1, min(limit, 600)))


@app.get("/api/stream")
async def stream() -> StreamingResponse:
    q = MONITOR.subscribe()
    async def generator():
        try:
            while True:
                try:
                    item = await asyncio.to_thread(q.get, True, 15)
                    yield f"data: {json.dumps(item, ensure_ascii=False)}\n\n"
                except Exception:
                    yield ": keep-alive\n\n"
        finally:
            MONITOR.unsubscribe(q)
    return StreamingResponse(generator(), media_type="text/event-stream", headers={"Cache-Control":"no-cache, no-transform", "X-Accel-Buffering":"no"})


@app.get("/api/business")
def business() -> dict[str, Any]:
    b = get_business()
    return {"id": b.id, "name": b.name, "tagline": b.tagline, "city": b.city, "phone": b.phone, "email": b.email, "hours": b.hours, "pickup_areas": b.pickup_areas, "driver_daily_fee": float(b.driver_daily_fee), "welcome_message": b.welcome_message}


@app.get("/media/vehicles/{vehicle_id}")
def vehicle_media(vehicle_id: str) -> Response:
    v = get_vehicle_with_image(vehicle_id)
    if not v or not v.image_data:
        raise HTTPException(status_code=404, detail="Vehicle image not found")
    return Response(content=v.image_data, media_type=v.image_mime or "image/jpeg", headers={"Cache-Control": "public, max-age=3600"})


@app.get("/api/admin/status")
def admin_status(x_admin_secret: str | None = Header(default=None)) -> dict[str, Any]:
    configured = bool(ADMIN_SECRET)
    return {"configured": configured, "authorized": bool(configured and x_admin_secret == ADMIN_SECRET)}


@app.get("/api/admin/vehicles")
def admin_vehicles(_: None = Depends(require_admin)) -> list[dict[str, Any]]:
    with db_session() as db:
        rows = list(db.scalars(select(Vehicle).order_by(Vehicle.created_at)).all())
        return [{"id": v.id, "name": v.name, "category": v.category, "daily_rate": float(v.daily_rate), "short_description": v.short_description, "image_url": f"/media/vehicles/{v.id}" if v.image_filename else None, "active": v.active} for v in rows]


@app.post("/api/admin/vehicles")
async def admin_add_vehicle(
    name: str = Form(...), category: str = Form(...), daily_rate: float = Form(...), description: str = Form(...),
    vehicle_id: str = Form(...), active: bool = Form(True), image: UploadFile | None = File(default=None), _: None = Depends(require_admin),
) -> dict[str, Any]:
    from sqlalchemy import func, select
    payload = await image.read() if image else None
    if payload and len(payload) > 8*1024*1024: raise HTTPException(status_code=413, detail="Image too large; max 8 MB.")
    with db_session() as db:
        if db.get(Vehicle, vehicle_id):
            raise HTTPException(status_code=409, detail="Vehicle ID already exists.")
        business = db.scalar(select(Business).order_by(Business.id).limit(1))
        if not business:
            raise HTTPException(status_code=503, detail="No business exists in Neon yet.")
        v = Vehicle(id=vehicle_id.strip(), business_id=business.id, name=name.strip(), category=category.strip(), daily_rate=daily_rate, short_description=description.strip(), image_filename=image.filename if image else None, image_mime=image.content_type if image else None, image_data=payload, active=active)
        db.add(v)
    emit_event("admin.vehicle.created", vehicle_id=vehicle_id)
    return {"ok": True, "id": vehicle_id}


@app.put("/api/admin/vehicles/{vehicle_id}")
async def admin_edit_vehicle(
    vehicle_id: str,
    name: str = Form(...), category: str = Form(...), daily_rate: float = Form(...), description: str = Form(...), active: bool = Form(True), image: UploadFile | None = File(default=None), _: None = Depends(require_admin),
) -> dict[str, Any]:
    payload = await image.read() if image else None
    if payload and len(payload) > 8*1024*1024: raise HTTPException(status_code=413, detail="Image too large; max 8 MB.")
    with db_session() as db:
        v = db.get(Vehicle, vehicle_id)
        if not v: raise HTTPException(status_code=404, detail="Vehicle not found.")
        v.name = name.strip(); v.category = category.strip(); v.daily_rate = daily_rate; v.short_description = description.strip(); v.active = active
        if image:
            v.image_filename = image.filename; v.image_mime = image.content_type; v.image_data = payload
    emit_event("admin.vehicle.updated", vehicle_id=vehicle_id)
    return {"ok": True}


@app.delete("/api/admin/vehicles/{vehicle_id}")
def admin_delete_vehicle(vehicle_id: str, _: None = Depends(require_admin)) -> dict[str, Any]:
    with db_session() as db:
        v = db.get(Vehicle, vehicle_id)
        if not v: raise HTTPException(status_code=404, detail="Vehicle not found.")
        v.active = False
    emit_event("admin.vehicle.disabled", vehicle_id=vehicle_id)
    return {"ok": True}


@app.get("/api/admin/bookings")
def admin_bookings(_: None = Depends(require_admin)) -> list[dict[str, Any]]:
    with db_session() as db:
        rows = list(db.scalars(select(Booking).order_by(Booking.created_at.desc()).limit(100)).all())
        return [{"id": b.id, "customer": b.customer_name, "phone": b.customer_phone, "vehicle": b.vehicle_name, "pickup_date": str(b.pickup_date), "return_date": str(b.return_date), "pickup_location": b.pickup_location, "total": float(b.total), "status": b.status, "created_at": b.created_at.isoformat()} for b in rows]
