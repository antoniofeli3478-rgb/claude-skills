"""Generate one Kokoro voice clip per leaderboard row (and per round title), trimmed, and the timeline.json
that drives BOTH the Remotion animation and the sound-effect cue list.

  python scripts/gerar_vozes.py --modelos <dir with kokoro-v1.0.onnx + voices-v1.0.bin>
"""
import argparse
import json
import os
import subprocess

import soundfile as sf
from kokoro_onnx import Kokoro

FPS = 30
AQUI = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# Misspellings must SOUND misspelled: respell for the espeak pt-br front end where needed.
FALA = {
    "Excessão": "Ecessão",
    "Exeção": "Ezeção",
    "Esseção": "Essessão",
    "Pedra": "Pedra!",
    "Cabelerero": "Cabelerêro",
    "Paraleleprípedo": "Paralelê prípedo",
    "Otorrinolaringolojista": "Otorrino laringolojista",
}

# timeline (frames)
T_TITULO = 6        # title lands; first row starts after it
GAP = 3             # frames of air between rows
MIN_LINHA = 16      # a row never comes faster than this
HOLD = 14           # hold the full board before reaction / exit
REACAO = {"choque": 42, "olhar": 40, "choro": 42, "explosao": 36, None: 0}
SAIDA = 10          # rows exit before the next title enters (no text over text)


def dur_wav(path):
    info = sf.info(path)
    return info.frames / info.samplerate


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--modelos", required=True)
    ap.add_argument("--voz", default="pm_alex")
    ap.add_argument("--velocidade", type=float, default=1.12)
    a = ap.parse_args()
    data = json.load(open(os.path.join(AQUI, "data.json"), encoding="utf-8"))
    k = Kokoro(os.path.join(a.modelos, "kokoro-v1.0.onnx"), os.path.join(a.modelos, "voices-v1.0.bin"))
    vo = os.path.join(AQUI, "public", "vo")
    os.makedirs(vo, exist_ok=True)
    cache = {}

    def clip(texto):
        if texto in cache:
            return cache[texto]
        nome = f"{len(cache):02d}.wav"
        bruto = os.path.join(vo, "_raw.wav")
        audio, sr = k.create(FALA.get(texto, texto) + ".", voice=a.voz, speed=a.velocidade, lang="pt-br")
        sf.write(bruto, audio, sr)
        out = os.path.join(vo, nome)
        subprocess.run(["ffmpeg", "-v", "error", "-y", "-i", bruto, "-af",
                        "silenceremove=start_periods=1:start_threshold=-45dB,areverse,"
                        "silenceremove=start_periods=1:start_threshold=-45dB,areverse,apad=pad_dur=0.03",
                        "-ar", "48000", "-ac", "1", out], check=True)
        os.remove(bruto)
        cache[texto] = (nome, dur_wav(out))
        return cache[texto]

    t = 4
    rodadas = []
    for r in data["rodadas"]:
        ini = t
        linhas = []
        f = T_TITULO + 4
        for item in r["linhas"]:
            texto = item[0]
            nome, d = clip(texto)
            linhas.append({"texto": texto, "tempo": item[1] if len(item) > 1 else None,
                           "certo": len(item) > 1, "frame": f, "vo": nome, "voDur": round(d, 3)})
            f += max(MIN_LINHA, int(round(d * FPS)) + GAP)
        fim_linhas = f + HOLD
        reacao_ini = fim_linhas
        dur = fim_linhas + REACAO[r["reacao"]] + SAIDA
        rodadas.append({"palavra": r["palavra"], "reacao": r["reacao"], "inicio": ini, "dur": dur,
                        "linhas": linhas, "reacaoIni": reacao_ini, "saidaIni": dur - SAIDA})
        t += dur
    # end card: same board, no exit, holds to the end
    fz = data["final"]
    linhas = []
    f = T_TITULO + 4
    for item in fz["linhas"]:
        nome, d = clip(item[0])
        linhas.append({"texto": item[0], "tempo": item[1] if len(item) > 1 else None,
                       "certo": len(item) > 1, "frame": f, "vo": nome, "voDur": round(d, 3)})
        f += max(MIN_LINHA, int(round(d * FPS)) + GAP + 4)
    final = {"palavra": fz["palavra"], "reacao": None, "inicio": t, "dur": f + 50, "linhas": linhas,
             "reacaoIni": f + 50, "saidaIni": f + 50 + 999}
    fim = t + final["dur"]
    out = {"fps": FPS, "rodadas": rodadas, "final": final, "total": fim, "marca": data["marca"]}
    json.dump(out, open(os.path.join(AQUI, "src", "timeline.json"), "w", encoding="utf-8"), ensure_ascii=False, indent=1)
    print(f"{len(cache)} clips, {fim} frames = {fim / FPS:.1f} s")


if __name__ == "__main__":
    main()
