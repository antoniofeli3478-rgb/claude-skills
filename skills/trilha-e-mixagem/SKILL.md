---
name: trilha-e-mixagem
description: Trilha sonora e mixagem para vídeos curtos (Reels/anúncios). Cobre licença de música para anúncio pago, trilha original gerada em Python (BPM casado com os quadros), efeitos sonoros no quadro exato, voz por cima com a música abaixando sozinha e volume final em −14 LUFS. Use ao montar o áudio de qualquer vídeo.
---

# Trilha sonora e mixagem

Scripts de exemplo em `scripts/`, do vídeo do Rotina. Os aprendizados novos ficam em `APRENDIZADOS.md`.

## 1. De onde vem a música (a licença decide)

- **Anúncio pago na Meta** exige uma licença que cite anúncios pagos. Exemplos:
  - Artlist Music & SFX Pro (~US$16,58/mês, anual);
  - Epidemic Pro (~US$17/mês);
  - o plano mais alto do Uppbeat.
  - Os planos de entrada **não** cobrem anúncio pago.
- **A música do Instagram** é para uso pessoal e não vale para anúncio.
- **Música por IA:**
  - ElevenLabs Music (`music_v2_5`, uso comercial nos planos pagos);
  - Google Lyria (API: cerca de US$0,04–0,08 por faixa; o Lyria RealTime fixa o BPM). A chave do AI Studio já dá acesso.
  - Evite o Suno, que tem processos e não tem API, e o Udio, que não deixa mais baixar.
- **Trilha original por código** (sem licença de terceiros):
  - Use `scripts/exemplo_trilha_rotina.py` como base. Ele foi feito em numpy, a 120 BPM: intro tensa, subida, drop na logo e impactos nas trocas de seção.
  - Ajuste os tempos no topo do arquivo.

## 2. Casar a música com o vídeo

- **Quadros por batida a 30 fps** = 1800 / BPM. Em 120 BPM, uma batida = 15 quadros e um compasso = 2 s.
- **Cortes e trocas de seção** caem nos compassos. Use uma subida (riser) antes do momento-chave, como a logo, e um impacto na batida.
- **Efeitos sonoros:**
  - O whoosh atinge o pico no quadro em que o movimento pousa; o impacto cai no quadro do contato.
  - Som adiantado incomoda mais que som atrasado, então **nunca** adiante mais que ~1 quadro.
  - Fontes de efeitos: ElevenLabs Sound Effects, ZapSplat, Freesound filtrado para CC0, ou os efeitos sintetizados do kit `motion-video`.

## 3. Mixar no ffmpeg (não no Remotion)

- **Ducking:** a música abaixa 6–12 dB quando a voz fala, com `sidechaincompress` usando a voz como chave. Ataque de 5–20 ms, release de 300–500 ms.
- **Junção das faixas:** `amix` sempre com `normalize=0`.
- **Volume final:** `loudnorm=I=-14:TP=-1.5`. No Remotion, toque o resultado com um único `<Audio>`.
- **Exemplo completo:** `scripts/exemplo_mix_rotina.sh`. Ele posiciona cada fala no tempo certo, abaixa a música sob a voz e normaliza. Para outro vídeo, troque a lista `STARTS` e os caminhos.

## 4. Conferir sem ouvir

O Claude não ouve áudio. Para conferir:
- **Volume:** `ffmpeg -i x.wav -af ebur128 -f null -` (veja a linha `I:`).
- **Estrutura:** um espectrograma `ffmpeg -i x.wav -lavfi showspectrumpic=s=1600x400 spec.png`, para olhar intro, drop e impactos.
- **Texto da voz:** transcrição automática (faster-whisper), para confirmar o que foi falado.
