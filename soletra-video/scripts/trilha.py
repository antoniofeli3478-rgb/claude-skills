"""Original goofy game-show loop (marimba pluck + kick + hats + bass), 120 BPM, fully synthesized (no samples).
  python scripts/trilha.py <seconds> <out.wav>
"""
import sys

import numpy as np
import soundfile as sf

SR = 48000
BPM = 120
BEAT = 60 / BPM
dur = float(sys.argv[1])
N = int(SR * (dur + 1))
out = np.zeros(N, np.float32)
rng = np.random.default_rng(3)


def add(sig, t, g=1.0):
    i = int(t * SR)
    j = min(N, i + len(sig))
    if i < N:
        out[i:j] += sig[: j - i] * g


def mtof(m):
    return 440 * 2 ** ((m - 69) / 12)


def marimba(m, d=0.35):
    n = int(d * SR)
    t = np.arange(n) / SR
    f = mtof(m)
    s = np.sin(2 * np.pi * f * t) * np.exp(-t * 9) + 0.35 * np.sin(2 * np.pi * f * 4 * t) * np.exp(-t * 30)
    return (s * np.minimum(1, t / 0.002)).astype(np.float32)


def kick():
    n = int(0.25 * SR)
    t = np.arange(n) / SR
    fr = 50 + 110 * np.exp(-t * 35)
    return (np.sin(2 * np.pi * np.cumsum(fr) / SR) * np.exp(-t * 14)).astype(np.float32)


def hat():
    n = int(0.05 * SR)
    t = np.arange(n) / SR
    x = rng.standard_normal(n)
    x = np.diff(x, prepend=0)
    return (x * np.exp(-t * 90) * 0.25).astype(np.float32)


def bass(m, d):
    n = int(d * SR)
    t = np.arange(n) / SR
    f = mtof(m)
    s = np.tanh(1.5 * np.sin(2 * np.pi * f * t)) * np.exp(-t * 3)
    return (s * np.minimum(1, t / 0.005) * np.minimum(1, (d - t) / 0.02)).astype(np.float32)


# I - vi - IV - V in C, one bar each (bar = 4 beats = 2 s)
CH = [[60, 64, 67, 72], [57, 60, 64, 69], [53, 57, 60, 65], [55, 59, 62, 67]]
ROOT = [36, 33, 29, 31]
ARP = [0, 2, 1, 3, 2, 1, 3, 2]
beats = int(dur / BEAT) + 1
for b in range(beats):
    t = b * BEAT
    bar = (b // 4) % 4
    add(kick(), t, 0.9)
    add(hat(), t + BEAT / 2, 1.0)
    if b % 2 == 1:
        add(hat(), t, 0.6)
    for k in range(2):  # 8th-note marimba arpeggio
        add(marimba(CH[bar][ARP[(b * 2 + k) % 8]] + 12), t + k * BEAT / 2, 0.32)
    add(bass(ROOT[bar], BEAT * 0.9), t, 0.38)

out = out[: int(SR * dur)]
fade = int(0.6 * SR)
out[-fade:] *= np.linspace(1, 0, fade)
out /= np.abs(out).max() + 1e-9
sf.write(sys.argv[2], (out * 0.8).astype(np.float32), SR)
print("ok", sys.argv[2])
