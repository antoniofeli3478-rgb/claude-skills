"""Align the KNOWN script to the audio (WhisperX, wav2vec2 pt model) and write captions
in @remotion/captions Caption[] format for the whole video.

Input JSON: [{"arquivo": "vo/01.wav", "texto": "Fala 1...", "inicio_s": 0.3}, ...]
  - "inicio_s" = when that line starts in the video (seconds); captions are offset by it.
Usage:  python alinhar_legendas.py falas.json legendas.json
Install (CPU, measured 2026-10-09):
  uv venv -p 3.12 .venv-wx
  uv pip install --python .venv-wx --torch-backend cpu "torch==2.6.*" "torchaudio==2.6.*" transformers nltk pandas
  uv pip install --python .venv-wx --no-deps whisperx==3.8.6
  NLTK data (the proxy blocks nltk's downloader unless you opt in):
  NLTK_ALLOW_PROXIED_URLOPEN=1 .venv-wx/bin/python -c "import nltk; nltk.download('punkt_tab', download_dir='nltk_data')"
  then run with:  NLTK_DATA=nltk_data .venv-wx/bin/python alinhar_legendas.py falas.json legendas.json
Accuracy measured on the Rotina voice lines: median error 17 ms, 79% of words within 1 frame at 30 fps
(the old character-position estimate was off by ~174 ms).
"""
import json
import sys

import torch
from whisperx.alignment import align, load_align_model
from whisperx.audio import load_audio

torch.set_num_threads(4)


def main(src, dst):
    lines = json.load(open(src, encoding="utf-8"))
    model, meta = load_align_model(language_code="pt", device="cpu")
    caps = []
    for line in lines:
        audio = load_audio(line["arquivo"])
        dur = len(audio) / 16000
        res = align([{"start": 0.0, "end": dur, "text": line["texto"]}], model, meta, audio, "cpu",
                    return_char_alignments=False)
        offset = float(line.get("inicio_s", 0)) * 1000
        prev_end = 0.0
        for w in res["word_segments"]:
            # words the aligner cannot time (e.g. digits) inherit the previous end
            s = w.get("start", prev_end)
            e = w.get("end", s)
            prev_end = e
            caps.append({
                "text": ("" if not caps else " ") + w["word"],
                "startMs": round(offset + s * 1000),
                "endMs": round(offset + e * 1000),
                "timestampMs": round(offset + (s + e) / 2 * 1000),
                "confidence": round(float(w["score"]), 3) if w.get("score") is not None else None,
            })
    json.dump(caps, open(dst, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
    print(len(caps), "captions ->", dst)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
