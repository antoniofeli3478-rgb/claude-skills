# Soletra Aí: vídeo estilo "Spelling Race" (TikTok) em português

**Pedido (25/10/2026):** fazer um vídeo no estilo de https://vt.tiktok.com/ZSgx1hBTW/ (vídeo `@super_nova_444`, meme do jogo Spelling Race do Roblox).

**Código:** branch `claude/tiktok-style-video-46tcja`, pasta `soletra-video/`. O vídeo final (`out/soletra-ai.mp4`) não vai para o Git: gere de novo pelos comandos abaixo.

## O que a referência faz (analisado quadro a quadro)

- Palavra no topo; linhas de um placar entram uma a uma (avatar, resposta, tempo).
- Acertos em verde com tempo; erros em vermelho com X, e o último erro é sempre uma piada.
- Uma voz robótica lê cada resposta, inclusive os erros: é aí que está a graça.
- Reações-meme: a tela gira como um cubo e mostra um rosto em preto e branco; às vezes uma explosão.

## O que foi feito

- 6 rodadas com palavras difíceis do português (Bicicleta, Exceção, Ansioso, Cabeleireiro, Paralelepípedo, Otorrinolaringologista) e final "E você? / Comenta aí embaixo". Roteiro em `data.json`.
- Tudo original: avatares quadrados desenhados em SVG, rostos de reação cartoon (choque, olhar de lado, choro), explosão cartoon. Nenhuma imagem do Roblox nem foto de pessoa real.
- Voz: Kokoro `pm_alex` (Apache-2.0, local), velocidade 1,12, uma fala por linha do placar.
- Trilha original por código (marimba + bumbo, 120 BPM) e efeitos sintetizados (pop com tom subindo nos acertos, thump + glitch nos erros, boom nas reações).
- Regras do editor: linhas entram da borda esquerda com mola `{300, 26}` e blur direcional; título entra pelo topo; o placar sai antes do próximo título (nada de texto sobre texto); filtros com região maior que o quadro (sem linha de corte).

## Comandos

```bash
cd soletra-video && npm install
pip install kokoro-onnx soundfile scipy        # modelos: release model-files-v1.0 do kokoro-onnx
python3 scripts/gerar_vozes.py --modelos <pasta-kokoro>   # vozes + src/timeline.json
python3 scripts/montar_audio.py                            # voz.wav + sons.json
python3 scripts/trilha.py 56.53 public/audio/trilha.wav
python3 -I ../skills/audio/trilha-e-mixagem/scripts/mixar_com_efeitos.py --voz public/audio/voz.wav \
  --musica public/audio/trilha.wav --efeitos public/audio/sons.json --saida public/audio/mix.wav --duracao 56.53
B=/opt/pw-browsers/chromium_headless_shell-1243/chrome-headless-shell-linux64/chrome-headless-shell
npx remotion render src/index.ts Soletra out/rascunho.mp4 --scale=0.5 --browser-executable=$B   # ~1,5 min
npx remotion render src/index.ts Soletra out/soletra-ai.mp4 --crf=18 --browser-executable=$B   # ~4 min
```

Para trocar palavras: edite `data.json` e rode de novo do `gerar_vozes.py` para baixo (os tempos de animação, voz e efeitos saem todos do `timeline.json`).

## Aprendizados

- **Baixar TikTok:** `pip install "yt-dlp[default,curl-cffi]"` e `yt-dlp --impersonate chrome <url>`. Sem o `curl-cffi`, o TikTok recusa.
- **Ouvir a referência:** faster-whisper `small` transcreve a voz e mostra o ritmo (aqui, uma palavra lida por linha do placar). Passe o áudio como array numpy: a versão do `av` instalada quebra o `decode_audio`.
- **Kokoro e erros de ortografia:** homófonos (Exceção/Excessão, Ansioso/Ancioso) soam iguais, e tudo bem: a piada está no texto. Para erros que precisam soar errados, respelle (dicionário `FALA` em `gerar_vozes.py`).
- **Tremor de câmera** revela a borda preta: escale a camada 1,06 durante o tremor.
- **Explosão cartoon:** estrelas pontudas em camadas (vermelho, laranja, amarelo, creme) crescendo com expo-out e nuvens de fumaça cinza; círculos soltos ficaram "bolinhas" com buraco no meio.
- O Chromium do container mudou: agora é `chromium_headless_shell-1243/chrome-headless-shell-linux64/chrome-headless-shell`.

## Próximo passo

- Esperando a opinião do Daniel. Ideias: voz mais robótica (como a da referência), mais rodadas, gameplay desfocado no fundo.

## Habilidades usadas

- `skills/video/remotion-video/SKILL.md` (regras de composição do editor, molas, revisão por folhas de quadros)
- `skills/audio/voz-pt-br/SKILL.md` (Kokoro, corte de silêncios, conferência por transcrição)
- `skills/audio/trilha-e-mixagem/SKILL.md` (`mixar_com_efeitos.py`: efeitos por lista de quadros, ducking, −14 LUFS)
