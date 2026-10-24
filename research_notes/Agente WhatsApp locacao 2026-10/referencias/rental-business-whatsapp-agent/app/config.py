from __future__ import annotations

import json
import os
from pathlib import Path
from urllib.parse import urlparse, urlunparse

from dotenv import load_dotenv

ROOT = Path(__file__).resolve().parents[1]
load_dotenv(ROOT / ".env")

WHATSAPP_AGENT_TOKEN = os.getenv("WHATSAPP_AGENT_TOKEN", "").strip()
GROQ_API_KEY = os.getenv("GROQ_API_KEY", "").strip()
DEEPGRAM_API_KEY = os.getenv("DEEPGRAM_API_KEY", "").strip()
RESEND_API_KEY = os.getenv("RESEND_API_KEY", "").strip()
OWNER_EMAIL = os.getenv("OWNER_EMAIL", "").strip()
RESEND_FROM_EMAIL = os.getenv("RESEND_FROM_EMAIL", "onboarding@resend.dev").strip()
ADMIN_SECRET = os.getenv("ADMIN_SECRET", "").strip()
DATABASE_URL = os.getenv("DATABASE_URL", "").strip()

GROQ_LLM_MODEL = os.getenv("GROQ_LLM_MODEL", "openai/gpt-oss-20b").strip()
GROQ_STT_MODEL = os.getenv("GROQ_STT_MODEL", "whisper-large-v3-turbo").strip()
DEEPGRAM_TTS_MODEL = os.getenv("DEEPGRAM_TTS_MODEL", "aura-2-thalia-en").strip()

# OpenTelemetry is local-first by default. These are optional and safe to omit.
OTEL_EXPORTER_OTLP_ENDPOINT = os.getenv("OTEL_EXPORTER_OTLP_ENDPOINT", "").strip()
OTEL_CONSOLE_EXPORTER = os.getenv("OTEL_CONSOLE_EXPORTER", "false").strip().lower() in {"1", "true", "yes", "on"}
BUSINESS_SEED_FILE = os.getenv("BUSINESS_SEED_FILE", "data/demo_business.json").strip()
SEED_DEMO = os.getenv("SEED_DEMO", "true").lower() in {"1", "true", "yes", "on"}

DATA_DIR = ROOT / "data"
MEDIA_DIR = DATA_DIR / "demo_media"
TMP_DIR = ROOT / "tmp"
TRACE_FILE = DATA_DIR / "traces.jsonl"
SEED_PATH = ROOT / BUSINESS_SEED_FILE

DATA_DIR.mkdir(exist_ok=True)
MEDIA_DIR.mkdir(parents=True, exist_ok=True)
TMP_DIR.mkdir(exist_ok=True)

MAX_HISTORY_MESSAGES = 16
MAX_VOICE_CHARS = 1600
MAX_IMAGE_BYTES = 8 * 1024 * 1024


def sqlalchemy_url(raw: str) -> str:
    if not raw:
        return raw
    if raw.startswith("postgresql://"):
        raw = "postgresql+psycopg://" + raw[len("postgresql://"):]
    elif raw.startswith("postgres://"):
        raw = "postgresql+psycopg://" + raw[len("postgres://"):]
    return raw


def load_seed_business() -> dict:
    return json.loads(SEED_PATH.read_text(encoding="utf-8"))


def require_keys(*, include_optional_integrations: bool = True) -> None:
    missing = []
    for name, value in [("WHATSAPP_AGENT_TOKEN", WHATSAPP_AGENT_TOKEN), ("GROQ_API_KEY", GROQ_API_KEY), ("DEEPGRAM_API_KEY", DEEPGRAM_API_KEY), ("DATABASE_URL", DATABASE_URL)]:
        if not value:
            missing.append(name)
    if include_optional_integrations:
        if not RESEND_API_KEY:
            missing.append("RESEND_API_KEY")
        if not OWNER_EMAIL:
            missing.append("OWNER_EMAIL")
    if missing:
        raise SystemExit("Missing required environment variable(s): " + ", ".join(missing) + ". Add them to .env and run again.")
