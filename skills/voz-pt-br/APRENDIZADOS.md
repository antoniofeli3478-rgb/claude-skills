# Aprendizados: voz-pt-br

Adendos datados. O mais novo fica embaixo. A skill original continua no `SKILL.md`.

## 09/10/2026: Gemini TTS (Google AI Studio)

- **A chave do AI Studio funciona deste container.**
  - A chamada que funciona é `POST /v1beta/models/gemini-3.8-flash-tts:generateContent`, com `responseModalities: ["AUDIO"]` e `prebuiltVoiceConfig.voiceName`.
  - A resposta vem em `audio/wav`, 24 kHz mono. Leia como WAV, não como PCM cru.
- **O modelo lê em voz alta as instruções de estilo** ("Fale como locutor…", "Say in an energetic voice:"), e o `systemInstruction` é recusado. Mande só o roteiro.
- **Cota gratuita:** `GenerateRequestsPerDayPerProjectPerModel-FreeTier = 10`, ou seja, 10 pedidos por dia por modelo. Ao estourar, a API devolve HTTP 429 com `retryDelay` de várias horas.
- **Abertura do Rotina (linhas 01–03), duração por voz:** Puck 12,0 s, Orus 13,6 s, Charon 14,8 s, Fenrir 16,8 s. A transcrição automática confirmou o texto em todas.
- **A mesma chave dá acesso à música Lyria** (`lyria-3.5`, `lyria-3-clip-preview`, `lyria-3-pro-preview`, `lyria-realtime-exp`), que ainda não foi testada.

## 09/10/2026: vozes locais e legendas (rodada 2 da pesquisa)

- **Kokoro fp32** (CPU): o roteiro de 50 s saiu em cerca de 15 s com a máquina livre. O modelo int8 é 4–6 vezes mais lento aqui.
- **Inteligibilidade** medida por transcrição automática: Kokoro 0,86–0,90, edge-tts 0,91, Chatterbox 0,91, Piper 0,49–0,81.
- **Piper:** as vozes pt_BR derivam de vozes com dados só para pesquisa ou não comerciais. Evite em anúncio.
- **Chatterbox pt-BR** (MIT): cerca de 15 min de CPU por 60 s de fala. Só tem voz oficial feminina e põe marca d'água no áudio.
- **WhisperX alinhando o roteiro conhecido** é muito melhor que transcrever: 17 ms de erro mediano, contra 57–78 ms dos modelos Whisper.
- **Detalhes e números completos:** `research_notes/Motion design 3D e voz IA/voice_round2.md` e `gemini_tts_teste.md`.
