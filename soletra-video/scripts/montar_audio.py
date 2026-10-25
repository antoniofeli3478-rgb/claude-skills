"""Builds public/audio/voz.wav (each clip placed at its row frame) and public/audio/sons.json (SFX cue list) from
src/timeline.json, so voice, effects and animation share the same frames. Then mix with the trilha-e-mixagem skill:

  python scripts/montar_audio.py
  python scripts/trilha.py <seconds> public/audio/trilha.wav
  python <claude-skills>/skills/audio/trilha-e-mixagem/scripts/mixar_com_efeitos.py --voz public/audio/voz.wav \
      --musica public/audio/trilha.wav --efeitos public/audio/sons.json --saida public/audio/mix.wav --duracao <seconds>
"""
import json
import os

import numpy as np
import soundfile as sf

AQUI = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SR = 48000
tl = json.load(open(os.path.join(AQUI, "src", "timeline.json"), encoding="utf-8"))
FPS = tl["fps"]
total = tl["total"] / FPS
voz = np.zeros(int(SR * total) + SR, np.float32)
sons = []
os.makedirs(os.path.join(AQUI, "public", "audio"), exist_ok=True)

linhas_ok = 0
for r in tl["rodadas"] + [tl["final"]]:
    i0 = r["inicio"]
    # title lands from the top: whoosh peaking on the landing, then a slam
    sons.append({"name": "whoosh", "frame": i0 - 0.35 * 0.55 * FPS, "gain": 0.35, "dur": 0.35, "peak": 0.55, "f0": 500, "f1": 4000})
    sons.append({"name": "slam", "frame": i0 + 7, "gain": 0.35})
    for k, l in enumerate(r["linhas"]):
        f = i0 + l["frame"]
        a, sr = sf.read(os.path.join(AQUI, "public", "vo", l["vo"]), dtype="float32")
        assert sr == SR
        s = int(f / FPS * SR)
        voz[s:s + len(a)] += a
        sons.append({"name": "swish", "frame": f - 1, "gain": 0.18, "pan": -0.4})
        if l["certo"]:
            # rising pitch for each right answer in the round
            sons.append({"name": "pop", "frame": f + 7, "gain": 0.5, "f0": 900 + 120 * k, "f1": 500 + 60 * k})
        elif l["texto"].startswith("Comenta"):
            sons.append({"name": "chime", "frame": f + 7, "gain": 0.35})
        else:
            sons.append({"name": "thump", "frame": f + 7, "gain": 0.6})
            sons.append({"name": "glitch", "frame": f + 7, "gain": 0.22, "dur": 0.16})
    rx = r["reacao"]
    if rx == "explosao":
        sons.append({"name": "boom", "frame": i0 + r["reacaoIni"], "gain": 1.0, "dur": 1.4})
        sons.append({"name": "slam", "frame": i0 + r["reacaoIni"], "gain": 0.8})
        sons.append({"name": "whoosh", "frame": i0 + r["reacaoIni"], "gain": 0.4, "dur": 0.6, "peak": 0.2, "f0": 2000, "f1": 200})
    elif rx:
        sons.append({"name": "whoosh", "frame": i0 + r["reacaoIni"] + 9 - 0.3 * 0.6 * FPS, "gain": 0.4, "dur": 0.3, "peak": 0.6})
        sons.append({"name": "boom", "frame": i0 + r["reacaoIni"] + 8, "gain": 1.0, "dur": 1.1})
        sons.append({"name": "whoosh", "frame": i0 + r["saidaIni"] + 10 - 0.3 * 0.6 * FPS, "gain": 0.35, "dur": 0.3, "peak": 0.6})
    elif r is not tl["final"]:
        sons.append({"name": "whoosh", "frame": i0 + r["saidaIni"], "gain": 0.3, "dur": 0.35, "peak": 0.4, "panFrom": -0.5, "panTo": 0.7})

sf.write(os.path.join(AQUI, "public", "audio", "voz.wav"), voz[: int(SR * total)], SR)
json.dump(sons, open(os.path.join(AQUI, "public", "audio", "sons.json"), "w"), indent=0)
print(f"voz.wav {total:.2f}s, {len(sons)} sons")
