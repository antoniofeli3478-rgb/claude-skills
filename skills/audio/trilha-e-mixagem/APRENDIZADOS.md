# Aprendizados: trilha-e-mixagem

Adendos datados. O mais novo fica embaixo.

## 08/10/2026: vídeo explicativo do Rotina (60 s)

- **A trilha numpy de 120 BPM ficou pronta em cerca de 8 s** de processamento. A estrutura foi conferida no espectrograma: intro com tique-taque, subida de 8 a 10 s, drop aos 10 s e impactos em 22, 36, 50 e 58 s.
- **Mixagem com `sidechaincompress` + `loudnorm=I=-14`:** o resultado mediu −13,8 LUFS integrado. Bom para Instagram.
- **Ajuste fino:** os cortes de seção foram encaixados nos compassos (a cada 2 s), e o fim de cada fala foi ajustado para não atravessar um impacto.
- **A falha principal foi a percepção, não a técnica.** O usuário achou a trilha sintetizada "básica". Para a próxima, prefira música licenciada para anúncio ou o Lyria, e deixe a trilha por código para rascunho ou para quando não houver orçamento.
- **Pesquisa completa** (licenças, números, cadeia ffmpeg): `research_notes/Motion design 3D e voz IA/music_sound_design.md`.

## 09/10/2026: trilha e efeitos do Rotina v2 (30 s)

- **Lyria:** a cota grátis é **zero**. A API respondeu "limit: 0 requests per day on Free Tier" para `lyria-3-clip-preview`, `lyria-3-pro-preview` e `lyria-3.5`, então é preciso um plano pago.
  - A chamada é pela Interactions API: `POST /v1beta/interactions` com `{"model": ..., "input": "prompt"}`. O áudio vem em `steps[].content[]` com `type: "audio"` (MP3, 44,1 kHz, 30 s no clip).
  - Script pronto: `audio/v2/lyria.py` na branch `rotina-video`.
- **Trilha em código melhorada** (`trilha_v2.py`, numpy + pedalboard): supersaw com polyBLEP, bumbo com envelope de pitch, palmas com 3 rajadas, sidechain "pumping", reverb e delay por envio, abertura de filtro na intro, rolos de caixa, risers e impactos.
  - Tudo travado na grade do vídeo: a virada cai no quadro em que a voz diz "Com o app Rotina".
  - Gera em 17 s.
- **Masterização:**
  - O `Limiter` do pedalboard tem um clipper em 0 dB e deixou o pico em 1,0.
  - Use `alimiter` + `loudnorm` em duas passadas no ffmpeg. Resultado: −14 LUFS e −1,2 dBTP.
- **ffmpeg:**
  - `amix=duration=first` cortou o fim da música, porque a primeira entrada (já com sidechain) era mais curta.
  - Use `duration=longest` + `apad=whole_dur=30` e `-t 30`. Confira que o arquivo tem exatamente 1.440.000 amostras.
- **Medir a voz contra o fundo:** mixe uma versão sem voz, mas com a voz ainda abaixando a música (`VOICE_DB=-120`), e compare as energias em cada fala.
  - A primeira mixagem deixou a chamada final só 3 dB acima do fundo.
  - **Correção:** música −8 dB, ducking com ratio 10, efeitos também abaixando sob a voz (ratio 3) e efeitos mais baixos nas palavras-chave.
  - **Resultado:** 12,2 dB no mínimo e 14,8 dB na mediana.
- **Efeitos no início de palavra mascaram a palavra:** um "slam" forte no mesmo quadro do início da fala cobre a consoante. Deixe esses efeitos 3–4 dB mais baixos que os efeitos das pausas.
- **Lista de efeitos no código do vídeo:** os efeitos ficam em `src/v2/sons.ts`, calculados pelas mesmas palavras (`W(fala, palavra)`) que movem a animação. São exportados para JSON e sintetizados em Python. Se a voz mudar, os efeitos acompanham.

## 11/10/2026: proposta aprovada e salva no SKILL.md

- O Daniel aprovou a proposta com os aprendizados do Rotina v2. O `SKILL.md` agora incorpora tudo até 11/10/2026, já com os caminhos das pastas por categoria.
