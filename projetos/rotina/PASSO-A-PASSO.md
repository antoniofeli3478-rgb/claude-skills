# Projeto Rotina: vídeos do app (passo a passo)

Este arquivo guarda só o que foi feito, como foi feito e onde estão as habilidades. Ele não tem cópia de skills nem o código.

## Onde está cada coisa

- **Código do projeto** (Remotion, telas, áudio e vídeos prontos): branch `rotina-video` deste repositório, pasta `rotina-video/`.
  https://github.com/danielvieirasilva0257-droid/claude-skills/tree/rotina-video/rotina-video
  - Para trabalhar nele: `git fetch origin rotina-video && git checkout rotina-video`, depois `cd rotina-video && npm install`.
- **O app** (fonte das telas): artifact "Rotina" em https://claude.ai/artifact/FsKaH7SRjcPc4wRLXSJcFE
  - Abas: Hábitos · Treinos (Treino, Planejar, Progresso, Cardio) · Financeiro (Visão, Transações, Cartões, Rendas, Investimentos, Relatórios) · Ajustes.
  - Visual: fundo `#080808`, vermelho `#CC0000` / `#FF5A5A`, fontes Barlow e Barlow Condensed.

## Habilidades usadas (abra só a que precisar)

| Para quê | Habilidade | Caminho |
|---|---|---|
| Vídeo em Remotion, regras de motion, 3D, voz, música, legendas | remotion-video | `skills/remotion-video/SKILL.md` |
| Estilo premium em canvas (alternativa ao Remotion) | motion-video | `skills/motion-video/.claude/skills/motion-video/SKILL.md` |
| Animações de botões e de UI nas telas | button-animation | `skills/button-animation/SKILL.md` |
| Paleta, fontes e estilo | ui-ux-pro-max | `skills/ui-ux-pro-max/SKILL.md` |
| Pesquisa completa (o que funciona, números, pendências) | relatório | `reports/Motion design 3D e voz IA.md` |
| Voz Gemini (como chamar a API) | notas | `research_notes/Motion design 3D e voz IA/gemini_tts_teste.md` |
| Vozes locais e legendas sincronizadas | notas | `research_notes/Motion design 3D e voz IA/voice_round2.md` |
| 3D sem GPU (tempos e correções) | notas | `research_notes/Motion design 3D e voz IA/3d_round2.md` |

## O que já foi feito

1. **Teaser de 10 s** (composição `Rotina`, 1080×1920, sem áudio).
   - Logo, 3 telas (Hábitos, Treinos, Financeiro) num celular em CSS 3D e giro rápido de câmera entre as telas.
   - Desfoque de movimento com `CameraMotionBlur`, granulação e vinheta.
   - Saída: `rotina-video/rotina.mp4`.
2. **Explicativo de 60 s** (composição `RotinaExplainer`, 1080×1920, com voz e música).
   - **Roteiro em 13 falas.** Cada fala foi gerada separada com edge-tts (`pt-BR-AntonioNeural`), com o silêncio cortado e a duração medida: `public/vo/NN.wav`.
   - **Linha do tempo montada pelas durações reais:** `src/explainer/timeline.ts`, com `VO`, `wordAt()` e keyframes `kf()`.
   - **Trilha original de 120 BPM**, feita em Python/numpy (`audio/make_music.py`): intro tensa, drop na logo aos 10 s, impactos em 22, 36 e 50 s.
   - **Mixagem com ffmpeg** (`audio/mix.sh`): música abaixando sob a voz, depois −14 LUFS. Resultado: `public/soundtrack.wav`.
   - **Telas recriadas em React:** `src/explainer/screens/`, com planilha, hábitos, métricas, plano semanal, estilos de treino, séries, financeiro, transações e despesas.
   - **Movimento:** celular 3D com frente e verso (giro de 360° entre seções), zoom no que é narrado, toques com ondulação, selos e cartões saltando em 3D, e legendas palavra por palavra.
   - Saída: `rotina-video/rotina-explainer.mp4`.
3. **Remotion atualizado** de 4.0.250 para **4.0.534**. Testado: tipos OK e quadros iguais aos de antes.

## Comandos (dentro de `rotina-video/`)

```bash
npm install
npx remotion studio                                   # pré-visualizar
npx remotion render src/index.ts RotinaExplainer out/rotina-explainer.mp4 --codec=h264 --crf=17 \
  --browser-executable=/opt/pw-browsers/chromium_headless_shell-1194/chrome-linux/headless_shell
bash audio/mix.sh audio/music.wav                     # refazer a mixagem (gere a música antes com make_music.py)
```

## Aprendizados deste projeto

- **O vídeo ficou "simples" por quatro motivos:**
  - a mola padrão (passa do ponto e treme);
  - elementos parados;
  - transições genéricas;
  - voz sem licença comercial.
- **Os números para corrigir isso** estão na skill `remotion-video`: molas, curvas, as 9 regras de movimento, zonas seguras e legendas.
- **O `wordAt()`** erra cerca de 0,2 s. Para legendas exatas, use o alinhamento com WhisperX (notas de voz), que erra cerca de 17 ms.
- **Licença de voz:** a voz edge-tts não tem licença para anúncio pago. As opções testadas estão na seção seguinte.

## Próximo vídeo: plano

1. **Voz:** o usuário escolhe nas amostras (`research_notes/Motion design 3D e voz IA/samples/`):
   - Gemini: Puck, Fenrir, Orus ou Charon;
   - Kokoro: pm_alex ou pm_santa.
2. Gerar as 13 falas com a voz escolhida e alinhar as palavras com WhisperX.
3. **Aplicar as regras premium** da skill `remotion-video`:
   - molas `{stiffness: 380, damping: 31}`;
   - nada parado;
   - eventos sobrepostos de 4 a 12 quadros;
   - transições que nascem de um elemento da cena.
4. **Planos-herói em 3D de verdade:** `@remotion/three` com `--gl=swangle`, telas em sequência JPEG e a correção do quadro 0 (ver `3d_round2.md`).
5. **Música com licença para anúncio pago,** ou Lyria/ElevenLabs Music. Efeitos sonoros no quadro exato, mix em −14 LUFS.
6. Storyboard em imagens aprovado pelo usuário antes do render final.
