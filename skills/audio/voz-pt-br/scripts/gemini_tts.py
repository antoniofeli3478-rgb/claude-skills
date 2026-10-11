"""Generate pt-BR narration with Gemini TTS (Google AI Studio API).

Usage:
  python gemini_tts.py --voz Puck --texto "Com o app Rotina, você tem." --saida 03.wav
  python gemini_tts.py --voz Puck --falas falas.txt --pasta vo/     # one line per line -> vo/01.wav, vo/02.wav...

Key: env var GEMINI_API_KEY, or file /root/.config/rotina/gemini.key. NEVER commit the key.
Notes (measured 2026-10-09):
  - Send ONLY the script text. gemini-3.8-flash-tts reads any style instruction aloud
    ("Fale como locutor..." or "Say in an energetic voice:"), and systemInstruction is rejected.
  - Pick the style through the voice: Puck (upbeat), Fenrir (excitable), Orus (firm), Charon (informative).
  - Output: WAV, PCM 16-bit, 24 kHz, mono. ~7-8 s per request.
"""
import argparse
import base64
import io
import json
import os
import ssl
import sys
import urllib.request
import wave

MODEL = "gemini-3.8-flash-tts"
URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent"


def api_key():
    key = os.environ.get("GEMINI_API_KEY")
    if not key and os.path.exists("/root/.config/rotina/gemini.key"):
        key = open("/root/.config/rotina/gemini.key").read().strip()
    if not key:
        sys.exit("No key: set GEMINI_API_KEY or create /root/.config/rotina/gemini.key")
    return key


def ssl_context():
    ca = os.environ.get("SSL_CERT_FILE") or ("/root/.ccr/ca-bundle.crt" if os.path.exists("/root/.ccr/ca-bundle.crt") else None)
    return ssl.create_default_context(cafile=ca) if ca else ssl.create_default_context()


def synthesize(text, voice, model=MODEL):
    body = {
        "contents": [{"parts": [{"text": text}]}],
        "generationConfig": {
            "responseModalities": ["AUDIO"],
            "speechConfig": {"voiceConfig": {"prebuiltVoiceConfig": {"voiceName": voice}}},
        },
    }
    req = urllib.request.Request(
        URL.format(model=model),
        data=json.dumps(body).encode(),
        headers={"x-goog-api-key": api_key(), "Content-Type": "application/json"},
    )
    try:
        with urllib.request.urlopen(req, context=ssl_context(), timeout=120) as r:
            data = json.load(r)
    except urllib.error.HTTPError as e:
        sys.exit(f"HTTP {e.code}: {e.read().decode()[:400]}")
    part = data["candidates"][0]["content"]["parts"][0]["inlineData"]
    audio = base64.b64decode(part["data"])
    if "wav" in part["mimeType"]:
        return audio
    # raw PCM (audio/L16) -> wrap in a WAV header
    buf = io.BytesIO()
    with wave.open(buf, "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(24000)
        w.writeframes(audio)
    return buf.getvalue()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--voz", default="Puck")
    ap.add_argument("--modelo", default=MODEL)
    ap.add_argument("--texto")
    ap.add_argument("--saida")
    ap.add_argument("--falas", help="text file, one voice line per line")
    ap.add_argument("--pasta", default=".")
    a = ap.parse_args()
    if a.texto:
        open(a.saida or "fala.wav", "wb").write(synthesize(a.texto, a.voz, a.modelo))
        print("ok:", a.saida or "fala.wav")
    elif a.falas:
        os.makedirs(a.pasta, exist_ok=True)
        lines = [l.strip() for l in open(a.falas, encoding="utf-8") if l.strip()]
        for i, line in enumerate(lines, 1):
            out = os.path.join(a.pasta, f"{i:02d}.wav")
            open(out, "wb").write(synthesize(line, a.voz, a.modelo))
            print("ok:", out)
    else:
        ap.error("use --texto or --falas")


if __name__ == "__main__":
    main()
