# Repositórios de edição de vídeo baixados (01/11/2026)

Pedido do Daniel: "quero editar vídeos aqui com você, baixe todos os repositórios que vão te ajudar".
Os repositórios ficam **fora** deste repositório (o container é temporário). Num container novo, rode os comandos abaixo de novo.

## O que foi baixado e para que serve

| Repositório | Onde fica no container | Para que serve | Commit |
|---|---|---|---|
| `antoniofeli3478-rgb/video-edit-skill` (fork de @tenfoldmarc) | `/home/user/antoniofeli3478-rgb/video-edit-skill` | Clipes de "cabeça falante" 9:16 gravados no celular → reel pronto: escolhe a melhor tomada de cada frase, corta silêncio, recorta a pessoa do fundo (texto atrás da cabeça), 3 looks, legendas, sons, checa a zona segura do Reels | 3f01f2e |
| `antoniofeli3478-rgb/video-use` (fork do browser-use) | `/home/user/antoniofeli3478-rgb/video-use` | Editar qualquer vídeo conversando: transcreve, corta "éé/hum" e falsos começos, correção de cor por ffmpeg, legendas queimadas, animações por cima (Manim/Remotion) | 43cfc56 |
| `antoniofeli3478-rgb/claude-motion-design` | `/home/user/antoniofeli3478-rgb/claude-motion-design` | Motion design 100% em código (HTML + Playwright + ffmpeg): mapa de batidas, quadros por tempo, motion blur, música e efeitos no pico medido | 3d90d34 |
| `danielvieirasilva0257-droid/claude-skills`, branch `rotina-video` | `/home/user/danielvieirasilva0257-droid/rotina-video/rotina-video` | Código dos vídeos do Rotina (v1 a v6, Remotion 4.0.534), já com `npm install` feito | 00599bd |

Comandos (clone raso, só leitura):

```bash
mkdir -p /home/user/antoniofeli3478-rgb /home/user/danielvieirasilva0257-droid
for r in video-edit-skill video-use claude-motion-design; do
  GIT_LFS_SKIP_SMUDGE=1 git clone --depth 1 https://github.com/antoniofeli3478-rgb/$r /home/user/antoniofeli3478-rgb/$r
done
GIT_LFS_SKIP_SMUDGE=1 git clone --depth 1 -b rotina-video https://github.com/danielvieirasilva0257-droid/claude-skills /home/user/danielvieirasilva0257-droid/rotina-video
cd /home/user/danielvieirasilva0257-droid/rotina-video/rotina-video && npm install
```

## Ferramentas instaladas (Python do sistema)

```bash
PIP_CERT=/root/.ccr/ca-bundle.crt python3 -m pip install "faster-whisper==1.2.1" "av>=11,<19" pillow requests librosa matplotlib numpy yt-dlp
```

- Já existiam no container: ffmpeg/ffprobe, Node 22, Chromium do Playwright.
- O `setup.py` do video-edit-skill **não foi executado** (a segurança bloqueia rodar script baixado). Instalei à mão os mesmos pacotes que ele pede (`faster-whisper==1.2.1`, `av<19`, `pillow`).
- O video-use usa a API Scribe da ElevenLabs para transcrever (precisa de chave paga). Aqui dá para trocar por faster-whisper (local, grátis).
- Teste feito: faster-whisper `small` transcreveu `public/vo/01.wav` do Rotina certinho ("Controle da sua rotina em planilhas de excel no celular."). O modelo baixado fica em `~/.cache/huggingface` (~460 MB).
- Teste feito: `npx remotion still Rotina` renderizou o quadro 60 com o navegador `chromium_headless_shell-1243`.

## Ainda não decidido

- Nenhum destes repositórios virou skill do nosso repositório: trazer qualquer um para `skills/video/` precisa da aprovação do Daniel.
- Para qual tipo de vídeo usar cada um: depende do material que o Daniel mandar (gravação dele falando → video-edit-skill ou video-use; motion de app → remotion-video, motion-video ou claude-motion-design).
