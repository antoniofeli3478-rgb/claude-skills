---
name: voz-pt-br
description: Gera narração em português do Brasil para vídeos (Gemini TTS, Kokoro local, edge-tts para rascunho), escolhe a voz pela licença e pela qualidade, e cria legendas sincronizadas palavra por palavra (WhisperX → Caption[] do Remotion). Use quando precisar de locução, voz de IA, narração de anúncio ou legendas sincronizadas com a fala.
---

# Voz em pt-BR + legendas sincronizadas

Scripts prontos em `scripts/`. Os aprendizados novos ficam em `APRENDIZADOS.md` (leia depois deste arquivo).

## 1. Escolha a voz pela licença primeiro

| Opção | Qualidade pt-BR | Pode em anúncio pago? | Custo | Quando usar |
|---|---|---|---|---|
| **Gemini TTS** (`scripts/gemini_tts.py`) | boa; vozes Puck, Fenrir, Orus, Charon | sim (termos do Google; no gratuito o Google usa o conteúdo) | grátis: **10 pedidos/dia por modelo** | narração final, se o usuário escolher |
| **Kokoro-82M** (`scripts/kokoro_tts.py`) | boa; pm_alex, pm_santa, pf_dora | **sim** (Apache-2.0) | grátis, roda aqui na CPU | sem chave e sem limite |
| **ElevenLabs v4** | a melhor candidata (ranking em inglês) | só em plano pago | ~US$6+/mês | quando o usuário pagar |
| **Azure Speech** (camada gratuita) | vozes neurais da Microsoft | sim | 0,5 M caracteres/mês grátis | alternativa oficial ao edge-tts |
| **edge-tts** | boa (pt-BR-AntonioNeural) | **não**: sem termos de uso comercial | grátis | só rascunho |
| Piper / Chatterbox | fraca / lenta | Piper: risco de licença | grátis | evitar |

Não existe teste cego de vozes em pt-BR. Faça um A/B com o roteiro real e deixe o usuário escolher de ouvido. Amostras prontas: `research_notes/Motion design 3D e voz IA/samples/`.

## 2. Gerar as falas

1. Escreva o roteiro com **uma fala por linha** num `falas.txt`. Uma fala = um momento do vídeo.
2. Gere:
   - **Gemini:** `python scripts/gemini_tts.py --voz Puck --falas falas.txt --pasta vo/`
     - A chave fica em `GEMINI_API_KEY` ou em `/root/.config/rotina/gemini.key`, **nunca no repositório**.
     - Mande **só o texto**. Qualquer instrução de estilo é lida em voz alta.
     - O limite gratuito é de 10 pedidos/dia **por modelo**. Para um roteiro de 13 falas, gere o roteiro inteiro num pedido só e depois corte nas falas com o alinhamento (passo 4). Outra saída é alternar entre os modelos `gemini-3.8-flash-tts`, `gemini-3.8-flash-lite-tts` e `gemini-3.1-flash-tts-preview`, porque cada um tem a sua cota.
   - **Kokoro:** `python scripts/kokoro_tts.py --voz pm_alex --falas falas.txt --pasta vo/ --modelos <pasta>`
     - Estrangeirismos precisam ser escritos como se falam. O dicionário `RESPELL` do script já troca "Excel" por "Éksel", "app" por "ép" e "deload" por "dilôudi".
3. **Corte os silêncios das pontas e meça a duração.** A linha do tempo do vídeo é montada com as durações reais:
   ```bash
   ffmpeg -i in.wav -af "silenceremove=start_periods=1:start_threshold=-45dB,areverse,silenceremove=start_periods=1:start_threshold=-45dB,areverse,apad=pad_dur=0.05" -ar 48000 -ac 1 out.wav
   ```

## 3. Mixagem

A voz vai por cima da música, com a música abaixando sozinha (ducking). Veja a skill `trilha-e-mixagem` (`skills/trilha-e-mixagem/SKILL.md`).

## 4. Legendas sincronizadas (palavra por palavra)

1. Monte um `falas.json`: `[{"arquivo": "vo/01.wav", "texto": "Fala 1", "inicio_s": 0.3}, ...]`. O `inicio_s` é o momento em que a fala começa no vídeo.
2. Rode `python scripts/alinhar_legendas.py falas.json legendas.json`. O resultado já sai no formato `Caption[]` do `@remotion/captions`.
   - Medido: erro mediano de 17 ms, com 79% das palavras dentro de 1 quadro. Estimar pela posição das letras erra cerca de 174 ms.
   - **Instalação** (está no topo do script): torch CPU e `whisperx==3.8.6` com `--no-deps`, mais `nltk` e `pandas`.
   - **Dados do NLTK:** neste container, baixe o `punkt_tab` com `NLTK_ALLOW_PROXIED_URLOPEN=1 python -c "import nltk; nltk.download('punkt_tab', download_dir='nltk_data')"` e rode com `NLTK_DATA=nltk_data`.
3. No Remotion, agrupe as legendas com `createTikTokStyleCaptions`. Monte as páginas por fala, para nenhuma página atravessar duas falas.

## 5. Acabamento da voz

- Volume final do vídeo: −14 LUFS, com pico em −1 a −1,5 dBTP. Isso é feito na mixagem.
- Para comparar vozes de forma justa, normalize as amostras no mesmo volume, por exemplo `loudnorm=I=-16`.
