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
| Passo a passo completo do tipo de vídeo deste projeto | combo-video-explicativo | `skills/combo-video-explicativo/SKILL.md` |
| Roteiro da narração (copy que convence, sem cara de IA) | combo-copy-humanizada | `skills/combo-copy-humanizada/SKILL.md` |
| Vídeo em Remotion, regras de motion, 3D | remotion-video | `skills/remotion-video/SKILL.md` (+ `APRENDIZADOS.md`) |
| Narração pt-BR e legendas sincronizadas | voz-pt-br | `skills/voz-pt-br/SKILL.md` (+ `APRENDIZADOS.md`) |
| Música, efeitos e mixagem | trilha-e-mixagem | `skills/trilha-e-mixagem/SKILL.md` (+ `APRENDIZADOS.md`) |
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
4. **Promo v2 de 30 s** (composição `RotinaV2`, 1080×1920, 900 quadros, visual novo do zero). Saída: `rotina-video/rotina-v2.mp4`.
   - **Roteiro:** versão 2 aprovada (combo `combo-copy-humanizada`), 13 falas, com o fecho "Baixe o Rotina agora. Link na bio.". Texto em `audio/v2/falas-v2.txt`.
   - **Voz:** Gemini `gemini-3.8-flash-tts`, voz **Leda**, o roteiro inteiro num pedido só (52 s em velocidade normal).
     - Acelerada **1,6x** com `atempo` (a 1,5x a fala sozinha passava de 31 s). O `atempo` manteve 97% de acerto na transcrição; o `rubberband` caiu para 93–94%.
     - Pausas apertadas por código (`audio/v2/compose_vo.py`): 0,15–0,26 s entre falas. A voz termina aos 29,3 s.
     - Palavras alinhadas com WhisperX na faixa final: `src/v2/voz.json` (tempo de cada palavra) manda em todo o vídeo.
   - **Trilha original** em código (`audio/v2/trilha_v2.py`, numpy + pedalboard), 120 BPM, travada na grade do vídeo: batidas nos quadros 10 + 15k, virada no quadro 160 ("Com o app Rotina"), seções em 340, 580 e 790, pausa em 820–850 e golpe final em 880. O Lyria não foi usado porque a cota grátis dele é zero.
   - **Efeitos sonoros:** 117 efeitos (`src/v2/sons.ts`), cada um preso a uma palavra ou movimento, sintetizados e mixados por `audio/v2/mixar_v2.py`.
     - Música e efeitos abaixam sob a voz (sidechain). Resultado: −13,9 LUFS e a voz de 12 a 21 dB acima do fundo em todas as falas.
   - **Visual "papel e tinta":** fundo papel claro, tinta preta e o vermelho do app. Fontes Anton, Instrument Serif (itálico) e Inter Tight, todas de licença livre (OFL), em `public/fonts/v2`.
     - **Gancho:** planilha frenética; as células viram a manchete; a planilha é espremida num celular; tudo desmorona em "sob controle?".
     - **Revelação:** o ponto vermelho do "?" abre o vermelho com a marca nova (laço + check) e "você tem.".
     - **Celular 3D de verdade** (React Three Fiber): sai da marca girando. As telas do app aparecem nele, sincronizadas palavra por palavra.
     - **Seções:** Hábitos (papel), Treino (tinta preta, giro rápido do celular) e Financeiro (papel, celular capotando). Cartões saem da tela do celular e as palavras-chave entram no tempo da fala.
     - **Fechamento:** o celular voa para dentro da tela e cai no cartão vermelho final com o botão "BAIXE AGORA" e "LINK NA BIO".
   - **Como foi montado** (camadas, cada uma renderizada à parte):
     1. `V2Tela`: as telas do app em 1080×2340, como sequência JPEG (`public/v2/tela`, cerca de 1,5 min).
     2. `V2Celular`: o celular 3D com fundo transparente, como sequência PNG (`public/v2/celular`, cerca de 1 s por quadro, 11 min).
     3. `RotinaV2`: o 2D por cima e por baixo, mais o áudio final (render completo em cerca de 3 min).
   - O `src/v2/rig.ts` dá a posição do celular em cada quadro, e o 3D e o 2D usam a mesma conta. Assim os cartões saem exatamente da tela do celular.
   - **Conferido:** transcrição automática do áudio final com 97,8% de acerto, e 48 quadros revisados num rascunho em meia resolução antes do render final.
5. **v2.1 (10/10/2026): correções pedidas pelo usuário, que é editor de motion.** Saída: `rotina-video/rotina-v2.1.mp4`.
   - **As 4 regras dele** (detalhes em `skills/remotion-video/APRENDIZADOS.md`, adendo de 10/10/2026):
     1. nada de texto sobre texto, nem logo sobre texto;
     2. nenhum recorte (máscara) cortando a animação no meio da tela;
     3. o celular fica por cima do texto, e o texto fica entre o fundo e o celular;
     4. o texto entra de fora do quadro ou de trás do celular.
   - **O que mudou no código** (`src/v2`):
     - `parts.tsx` ganhou `Entra` (da borda para dentro), `Atras` (sai de trás do celular) e `PalavraDividida` (metades do título vindo das duas bordas, sem se cruzar). O antigo `Slam`, revelação por máscara, saiu.
     - Todos os textos e cartões ficam abaixo da camada do celular e param em áreas livres.
     - **Gancho:** em vez de recortar a planilha até o celular, a câmera se afasta. A planilha com os adesivos já era a tela do celular, e a moldura entra pelas bordas do quadro.
     - "TUDO SOB CONTROLE?" sai pela esquerda quando o ponto do "?" vira o fundo vermelho.
     - **Fechamento:** "um app só." sobe de trás do celular.
     - Os efeitos sonoros foram movidos para os quadros de pouso das novas entradas. A voz ficou de 12,7 a 21,5 dB acima do fundo.
   - **Camada 3D:** só os quadros afetados foram renderizados de novo (786–846 pela nova pose final e 474–567 pelo atraso do movimento para Séries).
   - **Revisão:** primeiro com o celular em silhueta, depois dois rascunhos com o 3D. O segundo rascunho ainda achou 5 falhas, todas corrigidas antes do render final:
     - o recorte no gancho;
     - "TUDO SOB CONTROLE?" sumindo de repente;
     - o calendário entrando sobre o título;
     - o DELOAD coberto pelo celular antes de ser lido;
     - um chip que ficava só 3 quadros na tela.

## Comandos (dentro de `rotina-video/`)

```bash
npm install
npx remotion studio                                   # pré-visualizar
npx remotion render src/index.ts RotinaExplainer out/rotina-explainer.mp4 --codec=h264 --crf=17 \
  --browser-executable=/opt/pw-browsers/chromium_headless_shell-1194/chrome-linux/headless_shell
bash audio/mix.sh audio/music.wav                     # refazer a mixagem (gere a música antes com make_music.py)
```

**Promo v2 (30 s).** Rode dentro de `rotina-video/`. Use o mesmo `--browser-executable` acima, aqui abreviado como `$BR`. As pastas `public/v2/tela` e `public/v2/celular` não vão para o Git: gere de novo com:

```bash
npx remotion render src/index.ts V2Tela public/v2/tela --sequence --image-format=jpeg --jpeg-quality=90 --frames=182-850 --browser-executable=$BR
npx remotion render src/index.ts V2Celular public/v2/celular --sequence --image-format=png --frames=182-846 --concurrency=1 --gl=swangle --browser-executable=$BR
npx esbuild audio/v2/exportar_sons.ts --bundle --platform=node --outfile=out/exportar_sons.js && node out/exportar_sons.js > audio/v2/sons.json
python -I audio/v2/mixar_v2.py                          # efeitos + voz + trilha -> public/v2/rotina-v2-mix.wav
npx remotion render src/index.ts RotinaV2 out/v2-rascunho.mp4 --scale=0.5 --browser-executable=$BR      # rascunho (~1 min)
npx remotion render src/index.ts RotinaV2 out/rotina-v2.mp4 --crf=17 --audio-codec=aac --audio-bitrate=256k --browser-executable=$BR
```

- **Python:** venv com numpy, scipy, soundfile e pedalboard. Para regenerar a trilha: `python -I audio/v2/trilha_v2.py saida.wav audio/v2/trilha-v2_eventos.json`.
- **Se mudar o `rig.ts`:** renderize de novo só os quadros afetados do `V2Celular` (`--frames=a-b` numa pasta temporária, depois copie para `public/v2/celular`).

## Aprendizados deste projeto

- **O vídeo ficou "simples" por quatro motivos:**
  - a mola padrão (passa do ponto e treme);
  - elementos parados;
  - transições genéricas;
  - voz sem licença comercial.
- **Os números para corrigir isso** estão na skill `remotion-video`: molas, curvas, as 9 regras de movimento, zonas seguras e legendas.
- **O `wordAt()`** erra cerca de 0,2 s. Para legendas exatas, use o alinhamento com WhisperX (notas de voz), que erra cerca de 17 ms.
- **Licença de voz:** a voz edge-tts não tem licença para anúncio pago. As opções testadas estão na seção seguinte.

### Aprendizados da v2 (09/10/2026)

- **A voz decide a duração.** Com 139 palavras, 30 s só cabem com a voz em ~1,6x e pausas curtas. Para a voz ficar mais calma, o roteiro precisa ter menos palavras: umas 100 para 30 s a 1,3x.
- **O pedido "30 s" e o pedido "1,5x" brigaram.** Foi priorizado o tempo total, porque a velocidade veio com "se puder". Avise o usuário dessas escolhas.
- **Grade da música = grade do vídeo.** Com a música feita em código, a virada, as trocas de seção e o golpe final caem no quadro exato da fala, porque a música é gerada a partir dos tempos da voz.
- **Camadas separadas economizam tempo.** Mudar o 2D não exige renderizar de novo o 3D. Uma mudança no `rig.ts` só exige renderizar de novo os quadros afetados (13 quadros = 20 s).
- **Revisão barata:** um rascunho em meia resolução (72 s) e folhas com 12 quadros cada encontraram 5 problemas antes do render final: texto saindo da tela, sobreposições e um cartão rápido demais.
- **Medir a voz contra o fundo:** a primeira mixagem deixou a chamada final só 3 dB acima da música e dos efeitos. Uma versão só com o fundo (`VOICE_DB=-120`) mostrou isso, e o ajuste levou a 12 dB ou mais em todas as falas.

## Próximo vídeo: plano

1. **Ouvir e avaliar a v2** (o Claude não ouve). Pontos para o usuário decidir:
   - a velocidade da voz (1,6x);
   - o volume da trilha sob a voz;
   - o estilo da trilha feita em código.
2. **Licença:**
   - a voz Gemini segue os termos do Google (no plano grátis, o Google usa o conteúdo);
   - a trilha e os efeitos são originais, gerados por código, e podem ir para anúncio.
   - Confirme os termos antes de impulsionar.
3. **Música licenciada ou Lyria pago:** se a trilha em código ainda soar "básica", use uma faixa com licença para anúncio pago e mantenha a grade da música nos tempos da voz.
4. **Versões menores:** reaproveitar as camadas para cortes de 15 s (gancho + uma seção + CTA), trocando só o `voz.json` e a lista de seções.
