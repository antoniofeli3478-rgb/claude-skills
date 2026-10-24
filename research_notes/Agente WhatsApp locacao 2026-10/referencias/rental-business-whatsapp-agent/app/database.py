from __future__ import annotations

from contextlib import contextmanager
from datetime import datetime, timezone
from decimal import Decimal
import json
from pathlib import Path
from typing import Any, Iterator

from sqlalchemy import Boolean, Date, DateTime, ForeignKey, Integer, JSON, LargeBinary, Numeric, String, Text, UniqueConstraint, create_engine, func, select, text
from sqlalchemy.orm import DeclarativeBase, Mapped, Session, mapped_column, relationship, sessionmaker, undefer

from .config import DATABASE_URL, MEDIA_DIR, OWNER_EMAIL, load_seed_business, sqlalchemy_url


class Base(DeclarativeBase):
    pass


class Business(Base):
    __tablename__ = "businesses"
    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    slug: Mapped[str] = mapped_column(String(120), unique=True, index=True)
    name: Mapped[str] = mapped_column(String(200))
    tagline: Mapped[str] = mapped_column(String(300))
    city: Mapped[str] = mapped_column(String(200))
    phone: Mapped[str] = mapped_column(String(50))
    email: Mapped[str] = mapped_column(String(255))
    hours: Mapped[str] = mapped_column(String(120))
    driver_daily_fee: Mapped[Decimal] = mapped_column(Numeric(12, 2), default=0)
    pickup_areas: Mapped[list[str]] = mapped_column(JSON, default=list)
    welcome_message: Mapped[str] = mapped_column(Text)
    terms: Mapped[str] = mapped_column(Text)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    vehicles: Mapped[list["Vehicle"]] = relationship(back_populates="business", cascade="all, delete-orphan")


class Vehicle(Base):
    __tablename__ = "vehicles"
    id: Mapped[str] = mapped_column(String(80), primary_key=True)
    business_id: Mapped[int] = mapped_column(ForeignKey("businesses.id", ondelete="CASCADE"), index=True)
    name: Mapped[str] = mapped_column(String(200), index=True)
    category: Mapped[str] = mapped_column(String(120))
    daily_rate: Mapped[Decimal] = mapped_column(Numeric(12, 2))
    short_description: Mapped[str] = mapped_column(Text)
    image_filename: Mapped[str | None] = mapped_column(String(255), nullable=True)
    image_mime: Mapped[str | None] = mapped_column(String(100), nullable=True)
    image_data: Mapped[bytes | None] = mapped_column(LargeBinary, deferred=True, nullable=True)
    active: Mapped[bool] = mapped_column(Boolean, default=True, index=True)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    business: Mapped[Business] = relationship(back_populates="vehicles")


class Customer(Base):
    __tablename__ = "customers"
    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    name: Mapped[str] = mapped_column(String(200))
    phone: Mapped[str] = mapped_column(String(50), unique=True, index=True)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))


class Conversation(Base):
    __tablename__ = "conversations"
    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    session_key: Mapped[str] = mapped_column(String(200), unique=True, index=True)
    channel: Mapped[str] = mapped_column(String(50), default="whatsapp")
    customer_id: Mapped[int | None] = mapped_column(ForeignKey("customers.id"), nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    updated_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc))
    customer: Mapped[Customer | None] = relationship()


class Message(Base):
    __tablename__ = "messages"
    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    conversation_id: Mapped[int] = mapped_column(ForeignKey("conversations.id", ondelete="CASCADE"), index=True)
    role: Mapped[str] = mapped_column(String(30))
    content: Mapped[str] = mapped_column(Text)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), index=True)


class Quote(Base):
    __tablename__ = "quotes"
    id: Mapped[str] = mapped_column(String(80), primary_key=True)
    conversation_id: Mapped[int] = mapped_column(ForeignKey("conversations.id", ondelete="CASCADE"), index=True)
    customer_id: Mapped[int] = mapped_column(ForeignKey("customers.id"), index=True)
    vehicle_id: Mapped[str] = mapped_column(ForeignKey("vehicles.id"))
    customer_name: Mapped[str] = mapped_column(String(200))
    customer_phone: Mapped[str] = mapped_column(String(50))
    vehicle_name: Mapped[str] = mapped_column(String(200))
    pickup_date: Mapped[datetime.date] = mapped_column(Date)
    return_date: Mapped[datetime.date] = mapped_column(Date)
    pickup_location: Mapped[str] = mapped_column(String(255))
    with_driver: Mapped[bool] = mapped_column(Boolean, default=False)
    days: Mapped[int] = mapped_column(Integer)
    daily_rate: Mapped[Decimal] = mapped_column(Numeric(12, 2))
    driver_fee: Mapped[Decimal] = mapped_column(Numeric(12, 2))
    rental_total: Mapped[Decimal] = mapped_column(Numeric(12, 2))
    total: Mapped[Decimal] = mapped_column(Numeric(12, 2))
    status: Mapped[str] = mapped_column(String(40), default="pending_confirmation", index=True)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))


class Booking(Base):
    __tablename__ = "bookings"
    id: Mapped[str] = mapped_column(String(80), primary_key=True)
    quote_id: Mapped[str] = mapped_column(ForeignKey("quotes.id"), unique=True, index=True)
    conversation_id: Mapped[int] = mapped_column(ForeignKey("conversations.id", ondelete="CASCADE"), index=True)
    customer_id: Mapped[int] = mapped_column(ForeignKey("customers.id"), index=True)
    vehicle_id: Mapped[str] = mapped_column(ForeignKey("vehicles.id"))
    customer_name: Mapped[str] = mapped_column(String(200))
    customer_phone: Mapped[str] = mapped_column(String(50))
    vehicle_name: Mapped[str] = mapped_column(String(200))
    pickup_date: Mapped[datetime.date] = mapped_column(Date)
    return_date: Mapped[datetime.date] = mapped_column(Date)
    pickup_location: Mapped[str] = mapped_column(String(255))
    with_driver: Mapped[bool] = mapped_column(Boolean, default=False)
    days: Mapped[int] = mapped_column(Integer)
    daily_rate: Mapped[Decimal] = mapped_column(Numeric(12, 2))
    driver_fee: Mapped[Decimal] = mapped_column(Numeric(12, 2))
    rental_total: Mapped[Decimal] = mapped_column(Numeric(12, 2))
    total: Mapped[Decimal] = mapped_column(Numeric(12, 2))
    status: Mapped[str] = mapped_column(String(40), default="confirmed", index=True)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))

    __table_args__ = (UniqueConstraint("quote_id", name="uq_booking_quote"),)


class WhatsAppState(Base):
    __tablename__ = "whatsapp_state"
    key: Mapped[str] = mapped_column(String(80), primary_key=True)
    offset: Mapped[int | None] = mapped_column(Integer, nullable=True)
    updated_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc))


class ProcessedWhatsAppMessage(Base):
    __tablename__ = "processed_whatsapp_messages"
    message_id: Mapped[str] = mapped_column(String(255), primary_key=True)
    processed_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))



if not DATABASE_URL:
    raise RuntimeError("DATABASE_URL is required. Create a Neon PostgreSQL database and put its connection string in .env.")

ENGINE = create_engine(
    sqlalchemy_url(DATABASE_URL),
    pool_pre_ping=False,
    pool_recycle=900,
    pool_size=5,
    max_overflow=5,
    pool_timeout=8,
    connect_args={"connect_timeout": 8, "application_name": "whatsapp-business-agent"},
    future=True,
)
SessionLocal = sessionmaker(bind=ENGINE, expire_on_commit=False, autoflush=False, autocommit=False)


@contextmanager
def db_session() -> Iterator[Session]:
    session = SessionLocal()
    try:
        yield session
        session.commit()
    except Exception:
        session.rollback()
        raise
    finally:
        session.close()


def ping_db() -> dict[str, Any]:
    import time
    started = time.perf_counter()
    with ENGINE.connect() as connection:
        connection.execute(text("SELECT 1"))
    return {"ok": True, "latency_ms": round((time.perf_counter() - started) * 1000, 2)}


def init_db() -> None:
    Base.metadata.create_all(ENGINE)


def _read_local_image(filename: str | None) -> tuple[bytes | None, str | None]:
    if not filename:
        return None, None
    path = MEDIA_DIR / Path(filename).name
    if not path.exists() or not path.is_file():
        return None, None
    data = path.read_bytes()
    if len(data) > 8 * 1024 * 1024:
        return None, None
    suffix = path.suffix.lower()
    mime = {".jpg":"image/jpeg", ".jpeg":"image/jpeg", ".png":"image/png", ".webp":"image/webp"}.get(suffix, "application/octet-stream")
    return data, mime


def seed_demo_if_empty() -> None:
    seed = load_seed_business()
    with db_session() as db:
        business = db.scalar(select(Business).where(Business.slug == "lahore-ride-rentals"))
        if not business:
            business = Business(
                slug="lahore-ride-rentals",
                name=seed["name"],
                tagline=seed["tagline"],
                city=seed["city"],
                phone=seed["phone"],
                email=seed["email"],
                hours=seed["hours"],
                driver_daily_fee=Decimal(str(seed["driver_daily_fee"])),
                pickup_areas=seed["pickup_areas"],
                welcome_message=seed["welcome_message"],
                terms=seed["terms"],
            )
            db.add(business)
            db.flush()
        elif OWNER_EMAIL and business.email.endswith(".demo"):
            # Do not replace a user-edited database email with an environment default unless it is still the seed demo value.
            pass

        existing = {v.id: v for v in db.scalars(select(Vehicle).where(Vehicle.business_id == business.id).options(undefer(Vehicle.image_data))).all()}
        for item in seed["vehicles"]:
            image_data, image_mime = _read_local_image(item.get("image"))
            if item["id"] in existing:
                current = existing[item["id"]]
                if not current.image_filename and image_data:
                    current.image_filename = item.get("image")
                    current.image_mime = image_mime
                    current.image_data = image_data
                continue
            db.add(Vehicle(
                id=item["id"],
                business_id=business.id,
                name=item["name"],
                category=item["category"],
                daily_rate=Decimal(str(item["daily_rate"])),
                short_description=item["short_description"],
                image_filename=item.get("image"),
                image_mime=image_mime,
                image_data=image_data,
                active=True,
            ))


def get_business() -> Business:
    with db_session() as db:
        business = db.scalar(select(Business).order_by(Business.id).limit(1))
        if not business:
            raise RuntimeError("No business configured in the database.")
        db.expunge(business)
        return business


def list_vehicles(active_only: bool = True) -> list[Vehicle]:
    with db_session() as db:
        stmt = select(Vehicle).order_by(Vehicle.created_at)
        if active_only:
            stmt = stmt.where(Vehicle.active.is_(True))
        rows = list(db.scalars(stmt).all())
        for row in rows:
            db.expunge(row)
        return rows


def get_vehicle(vehicle_id: str) -> Vehicle | None:
    """Load lightweight vehicle metadata. Image bytes stay deferred."""
    with db_session() as db:
        row = db.scalar(select(Vehicle).where(Vehicle.id == vehicle_id))
        if row:
            db.expunge(row)
        return row


def get_vehicle_with_image(vehicle_id: str) -> Vehicle | None:
    """Load a vehicle including image bytes only when a media response needs them."""
    with db_session() as db:
        row = db.scalar(select(Vehicle).where(Vehicle.id == vehicle_id).options(undefer(Vehicle.image_data)))
        if row:
            db.expunge(row)
        return row


def find_vehicle(name_or_id: str) -> Vehicle | None:
    needle = name_or_id.strip().lower()
    with db_session() as db:
        rows = list(db.scalars(select(Vehicle).where(Vehicle.active.is_(True))).all())
        for row in rows:
            hay = f"{row.name} {row.category} {row.id}".lower()
            if needle == row.id.lower() or needle == row.name.lower() or needle in hay or hay in needle:
                db.expunge(row)
                return row
    return None


def get_or_create_customer(name: str, phone: str) -> int:
    with db_session() as db:
        customer = db.scalar(select(Customer).where(Customer.phone == phone.strip()))
        if customer:
            if name.strip() and customer.name != name.strip():
                customer.name = name.strip()
            db.flush()
            return customer.id
        customer = Customer(name=name.strip(), phone=phone.strip())
        db.add(customer)
        db.flush()
        return customer.id


def get_or_create_conversation(session_key: str, channel: str = "whatsapp") -> Conversation:
    with db_session() as db:
        conv = db.scalar(select(Conversation).where(Conversation.session_key == session_key))
        if not conv:
            conv = Conversation(session_key=session_key, channel=channel)
            db.add(conv)
            db.flush()
        conv.updated_at = datetime.now(timezone.utc)
        db.flush()
        db.expunge(conv)
        return conv


def attach_customer_to_conversation(session_key: str, customer_id: int) -> None:
    with db_session() as db:
        conv = db.scalar(select(Conversation).where(Conversation.session_key == session_key))
        if conv:
            conv.customer_id = customer_id
            conv.updated_at = datetime.now(timezone.utc)


def get_conversation_messages(session_key: str, limit: int = 16) -> list[dict[str, str]]:
    with db_session() as db:
        conv = db.scalar(select(Conversation).where(Conversation.session_key == session_key))
        if not conv:
            return []
        rows = list(db.scalars(select(Message).where(Message.conversation_id == conv.id).order_by(Message.created_at.desc()).limit(limit)).all())
        rows.reverse()
        return [{"role": row.role, "content": row.content} for row in rows]


def save_message(session_key: str, role: str, content: str) -> None:
    with db_session() as db:
        conv = db.scalar(select(Conversation).where(Conversation.session_key == session_key))
        if not conv:
            conv = Conversation(session_key=session_key, channel="whatsapp")
            db.add(conv)
            db.flush()
        db.add(Message(conversation_id=conv.id, role=role, content=content))
        conv.updated_at = datetime.now(timezone.utc)


def find_conflicts(vehicle_id: str, pickup_date, return_date) -> list[Booking]:
    with db_session() as db:
        rows = list(db.scalars(select(Booking).where(
            Booking.vehicle_id == vehicle_id,
            Booking.status.in_(["confirmed", "pending"]),
            Booking.pickup_date < return_date,
            Booking.return_date > pickup_date,
        )).all())
        return rows


def create_quote_record(data: dict[str, Any]) -> None:
    with db_session() as db:
        db.add(Quote(**data))


def get_latest_pending_quote(session_key: str) -> Quote | None:
    with db_session() as db:
        conv = db.scalar(select(Conversation).where(Conversation.session_key == session_key))
        if not conv:
            return None
        quote = db.scalar(select(Quote).where(Quote.conversation_id == conv.id, Quote.status == "pending_confirmation").order_by(Quote.created_at.desc()).limit(1))
        if quote:
            db.expunge(quote)
        return quote


def get_quote(quote_id: str) -> Quote | None:
    with db_session() as db:
        quote = db.get(Quote, quote_id)
        if quote:
            db.expunge(quote)
        return quote


def confirm_quote_and_create_booking(quote_id: str) -> tuple[Booking, bool]:
    with db_session() as db:
        quote = db.get(Quote, quote_id)
        if not quote:
            raise ValueError("Quote not found.")
        existing = db.scalar(select(Booking).where(Booking.quote_id == quote_id))
        if existing:
            db.expunge(existing)
            return existing, False
        conflicts = list(db.scalars(select(Booking).where(
            Booking.vehicle_id == quote.vehicle_id,
            Booking.status == "confirmed",
            Booking.pickup_date < quote.return_date,
            Booking.return_date > quote.pickup_date,
        )).all())
        if conflicts:
            raise ValueError("The selected vehicle is no longer available for those dates.")
        booking = Booking(
            id=_booking_id(),
            quote_id=quote.id,
            conversation_id=quote.conversation_id,
            customer_id=quote.customer_id,
            vehicle_id=quote.vehicle_id,
            customer_name=quote.customer_name,
            customer_phone=quote.customer_phone,
            vehicle_name=quote.vehicle_name,
            pickup_date=quote.pickup_date,
            return_date=quote.return_date,
            pickup_location=quote.pickup_location,
            with_driver=quote.with_driver,
            days=quote.days,
            daily_rate=quote.daily_rate,
            driver_fee=quote.driver_fee,
            rental_total=quote.rental_total,
            total=quote.total,
            status="confirmed",
        )
        quote.status = "confirmed"
        db.add(booking)
        db.flush()
        db.expunge(booking)
        return booking, True


def _booking_id() -> str:
    import secrets
    return f"BK-{datetime.now().strftime('%m%d')}-{secrets.token_hex(2).upper()}"


def list_bookings(days_ahead: int = 90) -> list[Booking]:
    from datetime import date, timedelta
    today = date.today()
    end = today + timedelta(days=days_ahead)
    with db_session() as db:
        rows = list(db.scalars(select(Booking).where(Booking.pickup_date >= today, Booking.pickup_date <= end).order_by(Booking.pickup_date)).all())
        return rows


class NeonOffsetStore:
    """Durable whagent offset store backed by Neon/PostgreSQL.

    Render Free instances have an ephemeral filesystem, so a file-based offset
    can disappear across sleep/restarts. Storing the poll offset in Neon lets
    whagent resume from the exact next offset after the process starts again.
    """
    def __init__(self, key: str = "default") -> None:
        self.key = key

    def load(self) -> int | None:
        with db_session() as db:
            state = db.get(WhatsAppState, self.key)
            return int(state.offset) if state and state.offset is not None else None

    def save(self, offset: int) -> None:
        with db_session() as db:
            state = db.get(WhatsAppState, self.key)
            if state is None:
                db.add(WhatsAppState(key=self.key, offset=int(offset)))
            else:
                state.offset = int(offset)
                state.updated_at = datetime.now(timezone.utc)


def was_message_processed(message_id: str) -> bool:
    if not message_id:
        return False
    with db_session() as db:
        return db.get(ProcessedWhatsAppMessage, message_id) is not None


def mark_message_processed(message_id: str) -> None:
    if not message_id:
        return
    with db_session() as db:
        if db.get(ProcessedWhatsAppMessage, message_id) is None:
            db.add(ProcessedWhatsAppMessage(message_id=message_id))
