"""Generate pt-BR narration locally with Kokoro-82M (Apache-2.0; commercial use OK), CPU only.

Usage:  python kokoro_tts.py --voz pm_alex --falas falas.txt --pasta vo/ --modelos /path/to/models
Voices: pm_alex, pm_santa (male), pf_dora (female).
Setup (measured 2026-10-09):
  uv venv -p 3.13 .venv-kokoro && uv pip install --python .venv-kokoro kokoro-onnx soundfile
  Model files from the kokoro-onnx GitHub release "model-files-v1.0":
    kokoro-v1.0.onnx (fp32, 325 MB; the int8 file is 4-6x SLOWER on this CPU) and voices-v1.0.bin.
Loanwords: the espeak pt-br front end misreads English words, so respell them (RESPELL below).
"""
import argparse
import os

import soundfile as sf
from kokoro_onnx import Kokoro

# English loanwords -> how they should sound in pt-BR (tested by ear-proxy ASR on the Rotina script)
RESPELL = {
    "Excel": "Éksel",
    "app": "ép",
    "deload": "dilôudi",
}


def respell(text):
    for word, sound in RESPELL.items():
        text = text.replace(word, sound)
    return text


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--voz", default="pm_alex")
    ap.add_argument("--falas", required=True, help="text file, one voice line per line")
    ap.add_argument("--pasta", default="vo")
    ap.add_argument("--modelos", default=".", help="folder with kokoro-v1.0.onnx and voices-v1.0.bin")
    ap.add_argument("--velocidade", type=float, default=1.0)
    a = ap.parse_args()
    k = Kokoro(os.path.join(a.modelos, "kokoro-v1.0.onnx"), os.path.join(a.modelos, "voices-v1.0.bin"))
    os.makedirs(a.pasta, exist_ok=True)
    lines = [l.strip() for l in open(a.falas, encoding="utf-8") if l.strip()]
    for i, line in enumerate(lines, 1):
        audio, sr = k.create(respell(line), voice=a.voz, speed=a.velocidade, lang="pt-br")
        out = os.path.join(a.pasta, f"{i:02d}.wav")
        sf.write(out, audio, sr)
        print("ok:", out, f"{len(audio) / sr:.2f}s")


if __name__ == "__main__":
    main()
