from __future__ import annotations

import json
import os
import queue
import threading
from datetime import datetime, timezone
from typing import Any

from opentelemetry import trace
from opentelemetry.exporter.otlp.proto.http.trace_exporter import OTLPSpanExporter
from opentelemetry.instrumentation.fastapi import FastAPIInstrumentor
from opentelemetry.instrumentation.httpx import HTTPXClientInstrumentor
from opentelemetry.instrumentation.requests import RequestsInstrumentor
from opentelemetry.sdk.resources import Resource
from opentelemetry.sdk.trace import ReadableSpan, TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor, ConsoleSpanExporter, SimpleSpanProcessor, SpanExportResult, SpanExporter

from .config import OTEL_CONSOLE_EXPORTER, OTEL_EXPORTER_OTLP_ENDPOINT, TRACE_FILE


class LiveMonitor:
    def __init__(self) -> None:
        self._events: list[dict[str, Any]] = []
        self._subscribers: set[queue.Queue] = set()
        self._lock = threading.Lock()
        self._max_events = 600

    def emit(self, event: dict[str, Any]) -> None:
        with self._lock:
            self._events.append(event)
            if len(self._events) > self._max_events:
                self._events = self._events[-self._max_events:]
            subscribers = list(self._subscribers)
        for q in subscribers:
            try:
                q.put_nowait(event)
            except queue.Full:
                pass

    def recent(self, limit: int = 200) -> list[dict[str, Any]]:
        with self._lock:
            return list(self._events[-limit:])

    def subscribe(self) -> queue.Queue:
        q: queue.Queue = queue.Queue(maxsize=300)
        with self._lock:
            self._subscribers.add(q)
        return q

    def unsubscribe(self, q: queue.Queue) -> None:
        with self._lock:
            self._subscribers.discard(q)


MONITOR = LiveMonitor()


def _iso(ts_ns: int | None) -> str:
    if not ts_ns:
        return datetime.now(timezone.utc).isoformat()
    return datetime.fromtimestamp(ts_ns / 1_000_000_000, tz=timezone.utc).isoformat()


def _status(span: ReadableSpan) -> str:
    status = getattr(span, "status", None)
    code = getattr(getattr(status, "status_code", None), "name", None)
    return code or "UNSET"


def span_to_dict(span: ReadableSpan) -> dict[str, Any]:
    duration_ms = (span.end_time - span.start_time) / 1_000_000 if span.end_time else None
    attrs = {str(k): str(v) for k, v in (span.attributes or {}).items()}
    return {
        "kind": "trace",
        "timestamp": _iso(span.end_time),
        "name": span.name,
        "trace_id": format(span.context.trace_id, "032x") if span.context else "",
        "span_id": format(span.context.span_id, "016x") if span.context else "",
        "parent_id": format(span.parent.span_id, "016x") if span.parent else "",
        "status": _status(span),
        "duration_ms": round(duration_ms, 2) if duration_ms is not None else None,
        "attributes": attrs,
    }


NOISE_PREFIXES = (
    "GET /api/stream",
    "GET /docs",
    "GET /openapi.json",
    "GET /redoc",
    "GET /favicon.ico",
)


def _is_noise(span: ReadableSpan) -> bool:
    return any(span.name.startswith(prefix) for prefix in NOISE_PREFIXES)


class LiveSpanExporter(SpanExporter):
    def export(self, spans: list[ReadableSpan]) -> SpanExportResult:
        try:
            TRACE_FILE.parent.mkdir(exist_ok=True)
            with TRACE_FILE.open("a", encoding="utf-8") as handle:
                for span in spans:
                    if _is_noise(span):
                        continue
                    item = span_to_dict(span)
                    MONITOR.emit(item)
                    handle.write(json.dumps(item, ensure_ascii=False) + "\n")
            return SpanExportResult.SUCCESS
        except Exception:
            return SpanExportResult.FAILURE

    def shutdown(self) -> None:
        return None


def emit_event(name: str, **fields: Any) -> None:
    event = {"kind": "event", "timestamp": datetime.now(timezone.utc).isoformat(), "name": name, **fields}
    MONITOR.emit(event)


def setup_telemetry() -> TracerProvider:
    resource = Resource.create({"service.name": "whatsapp-business-agent"})
    provider = TracerProvider(resource=resource)
    provider.add_span_processor(SimpleSpanProcessor(LiveSpanExporter()))
    if OTEL_CONSOLE_EXPORTER:
        provider.add_span_processor(SimpleSpanProcessor(ConsoleSpanExporter()))
    if OTEL_EXPORTER_OTLP_ENDPOINT:
        provider.add_span_processor(BatchSpanProcessor(OTLPSpanExporter(endpoint=OTEL_EXPORTER_OTLP_ENDPOINT)))
    trace.set_tracer_provider(provider)
    RequestsInstrumentor().instrument()
    HTTPXClientInstrumentor().instrument()
    return provider


def instrument_fastapi(app: Any) -> None:
    FastAPIInstrumentor.instrument_app(
        app,
        excluded_urls=r"^/api/stream$|^/docs$|^/openapi\.json$|^/redoc$|^/favicon\.ico$",
        exclude_spans=["receive", "send"],
    )


setup_telemetry()
tracer = trace.get_tracer("whatsapp-business-agent")
