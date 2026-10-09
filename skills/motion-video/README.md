# motion-video (kit enviado pelo usuário)

Kit completo e sem alterações: a skill fica em `.claude/skills/motion-video/SKILL.md` e o motor em `motion-studio/`. O guia do autor está em `LEIA-ME.txt` e no PDF.

**Como usar a partir deste repositório:** trate **esta pasta** (`skills/motion-video/`) como a raiz do kit. Os comandos da skill rodam de dentro de `skills/motion-video/motion-studio/`.

Neste container (Linux, root, sem GPU), já foi testado:

```bash
cd skills/motion-video/motion-studio
python3 -m venv .venv && .venv/bin/pip install -q numpy scipy
PUPPETEER_SKIP_DOWNLOAD=1 npm install --no-fund --no-audit
printf '#!/bin/sh\nexec /opt/pw-browsers/chromium-1194/chrome-linux/chrome --no-sandbox "$@"\n' > /tmp/chrome-nosandbox.sh && chmod +x /tmp/chrome-nosandbox.sh
PUPPETEER_EXECUTABLE_PATH=/tmp/chrome-nosandbox.sh bash tools/render.sh exemplo preview   # ou sem "preview" para o render final
```

- **Render travado:** refaça só a faixa que falta com `FROM=a TO=b node tools/render.mjs projects/<nome> full 8777`, depois rode o ffmpeg e `bash tools/render.sh <nome> audio`.
- **Velocidade medida:** cerca de 1,1 s por quadro em 1920×1080 a 60 fps. O exemplo é horizontal; para Reels, use 1080×1920.
