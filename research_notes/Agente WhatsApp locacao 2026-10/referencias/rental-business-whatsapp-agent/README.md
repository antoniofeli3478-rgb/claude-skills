# WhatsApp Business AI Agent — Neon Edition

A deployable FastAPI + LangChain WhatsApp sales/booking agent for a service business. This version moves business inventory, customers, conversations, quotes and bookings into Neon PostgreSQL so adding vehicles does not require code changes or redeploys.

## What it does

- Text in → text out.
- Voice note in → internal Groq transcription → agent → Deepgram voice reply only. The transcript is not sent back to the customer.
- Vehicle inventory comes from Neon PostgreSQL.
- Vehicle photos are stored as binary data in Neon and can be uploaded from the `/admin` page.
- Availability is checked against confirmed bookings before quotes are created.
- Quote PDF is generated and sent on WhatsApp.
- The agent waits for explicit confirmation before booking.
- Confirmation creates the booking first, then sends a receipt PDF to the customer and a Resend email to the business owner. Email failure never rolls back the booking.
- OpenTelemetry monitors meaningful FastAPI requests, WhatsApp polling, Groq/Deepgram/Resend HTTP calls and LangChain/tool spans through a live local dashboard. The `/api/stream` SSE channel and its send/receive middleware spans are excluded so the monitor does not trace itself.
- `/docs` is FastAPI Swagger. `/` is monitoring only. `/admin` is inventory/bookings management.

## Environment

Copy `.env.example` to `.env` and fill:

```env
WHATSAPP_AGENT_TOKEN=...
GROQ_API_KEY=...
DEEPGRAM_API_KEY=...
RESEND_API_KEY=...
OWNER_EMAIL=...
DATABASE_URL=postgresql://...
ADMIN_SECRET=...
```

Neon provides a standard PostgreSQL connection URI; the app accepts both direct and pooled `postgresql://` Neon URIs and automatically switches them to the `psycopg` SQLAlchemy driver. On startup the app warms the database connection so the first user request does not have to pay the Neon connection cost.

## First run locally

```bash
uv venv
source .venv/bin/activate
uv pip install -r requirements.txt --index-url https://pypi.org/simple --refresh
cp .env.example .env
uv run python test_imports.py
uv run python run.py
```

Open:

- `http://127.0.0.1:8000/` — live observability monitor
- `http://127.0.0.1:8000/docs` — Swagger
- `http://127.0.0.1:8000/admin` — inventory/bookings admin (add, edit, disable vehicles + upload photos)
- `http://127.0.0.1:8000/health` — health check

## Neon seed behavior

On startup, tables are created automatically. If the `lahore-ride-rentals` business does not exist, the seed data in `data/demo_business.json` creates it and the three demo vehicles. If matching vehicle images exist in `data/demo_media/` during the first seed, they are loaded into Neon.

After that, use `/admin` to add or edit vehicles. Inventory changes are stored in Neon and are used by the agent immediately — no redeploy.

Vehicle image bytes are deferred in normal inventory queries, so the agent does not download every stored image from Neon on every question. Image bytes are fetched only when a photo is actually sent.

The current seed image filenames are:

```text
toyota_corolla.jpeg
honda_civic.jpeg
toyota_yaris.jpeg
```

Put your real images into `data/demo_media/` before the first database seed if you want them loaded automatically. Later, upload new images through `/admin`.

## Render deployment

Use a Render **Web Service** for this current whagent polling architecture.

Build command:

```bash
pip install -r requirements.txt
```

Start command:

```bash
python run.py
```

The app binds to `0.0.0.0` and reads Render's `PORT` automatically. Use a paid always-on Render service for the current WhatsApp polling architecture; the poller must remain running.

Set all `.env` variables in Render Environment Variables. Never commit `.env`.

## Important production note

For a real multi-business product, move from one seeded business to tenant-scoped businesses and admin authentication. This repo intentionally keeps the demo single-business while making the inventory and booking data persistent and editable.

### Render shortcut

A `render.yaml` is included. You can create the web service from the repo and keep the secret values as Render-managed environment variables.

## First-run notes

- `OTEL_CONSOLE_EXPORTER` and `OTEL_EXPORTER_OTLP_ENDPOINT` are optional. The app defaults them to disabled/empty, so missing telemetry env vars will not break startup.
- For faster WhatsApp replies, use `GROQ_LLM_MODEL=openai/gpt-oss-20b` for this sales/booking workload.
- `ADMIN_SECRET` must be a separate application password; do not reuse your Neon database password.

## Render / WhatsApp polling persistence

The WhatsApp poll offset is stored in Neon (`whatsapp_state`) rather than the local filesystem. This is important for Render Free because the service filesystem is ephemeral across sleep/restarts. The app also records processed WhatsApp message IDs in Neon so a replayed update cannot send the same reply twice.

If your Git repository already contains an old runtime offset file, remove it once with:

```bash
git rm --cached data/whatsapp.offset
```

Then commit and push. Do not commit `data/whatsapp.offset`; it is ignored and no longer needed.
