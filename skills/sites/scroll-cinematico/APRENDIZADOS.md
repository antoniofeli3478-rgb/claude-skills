# APRENDIZADOS: scroll-cinematico

Adendos datados. O mais novo fica embaixo.
O `SKILL.md` incorpora os aprendizados até 11/10/2026.

## 11/10/2026: entrada na base

**Para que serve:** fazer a animação "estilo Apple" em que a rolagem controla o filme: o produto gira, abre ou monta enquanto o visitante rola, com textos entrando no tempo certo. Cobre a escolha da técnica, os números, a produção dos quadros (Blender, Remotion ou IA de vídeo), as regras do editor, acessibilidade, desempenho e teste.

**Quando usar:** "site estilo Apple", "animação no scroll", "produto girando quando rola", "scrollytelling", "vídeo controlado pela rolagem", herói animado de site; e quando for preciso gerar um clipe por IA para um site (qual ferramenta, licença comercial, GPU).

**O que tem de mais útil:**
- `assets/scroll-sequencia.html`: página sem dependências, testada (canvas + sticky + inércia por tempo + carregamento grosso → fino + reduzir movimento + sem JS).
- `assets/SequenciaScroll.tsx`: o mesmo em Next.js + Motion 14.
- `scripts/testar-scroll.js`: confere quadro × progresso, texto sobre texto, reduzir movimento e sem JS; sai com código 1 se falhar.
- `scripts/preparar-quadros.sh`: render ou vídeo → WebP com alpha + *lite* + pôster + `manifest.json`.
- `references/ia-de-video.md`: o que é grátis **e** comercial em IA de vídeo (Wan 2.2, LTX-2.x), GPU necessária, prompts e pós-produção no ffmpeg.

**Testes feitos no container (4 CPUs, sem GPU, Chromium headless 1194, ffmpeg 6.1):**
- Agente B, página de teste (60 quadros WebP RGBA 1280×720, 400vh): quadro desenhado = esperado em p = 0,28 / 0,62 / 0,83 / 0,97 (18, 38, 50, 58); nenhum texto sobre texto; reduzir movimento → seção de 720 px com pôster; 60 rAF/s na rolagem contínua (renderização por software: não vale como medida de celular).
- Agente B, formatos (60 quadros): WebP q80 796 KB e 13,0 ms por quadro para carregar + decodificar; AVIF crf 32 294 KB e 32,6 ms; H.264 all-intra 432 KB × GOP 250 137 KB (3,2×); VP9 all-intra 629 KB.
- Agente A, ffmpeg: loop com crossfade, vídeo all-intra (todos os quadros keyframe), WebM VP9 e sequência WebP validados com vídeo sintético.
- Agente A, Hugging Face ZeroGPU sem login: LTX-2.3 recusado por cota (pede 150 s; o IP tinha 55 s); Wan 2.2 parou no limite anônimo. Nenhum vídeo, nenhum custo.
- Agente de transcrição (esta entrada):
  - `testar-scroll.js` generalizado rodou de novo no modelo HTML: mesmos quadros (18, 38, 50, 58), sem sobreposição, reduzir movimento ok (720 px, pôster), **sem JS ok** (pôster visível), código de saída 0. Com as faixas de B mexidas para se cruzar com A, o teste acusou "texto sobre texto".
  - `preparar-quadros.sh`: 60 PNG RGBA → 864 KB a 1280 px + *lite* (30 quadros, 1024 px) 376 KB; alpha preservado (canto com alpha 0); vídeo de 8 s a 24 fps → 192 quadros.
  - `SequenciaScroll.tsx`: `tsc --strict` ok (Motion 14.1.0, React 19.3); teste de fumaça no Chromium (bundle esbuild): canvas desenhado, pôster some, texto A dentro em 0,28 e fora em 0,62, B deslocado 333 px em 0,97, quadro 58 em 0,97; reduzir movimento sem canvas.

**Erros encontrados e corrigidos:**
- Filtro `rotate` do ffmpeg com `c=none` deixou sujeira dos quadros anteriores; use `c=black@0`.
- Com inércia, ler o estado logo depois do `scrollTo` dá o quadro antigo (10 em vez de 18): o teste agora espera `p ≈ alvo`.
- No componente proposto: o pôster ficaria **atrás** dos quadros com alpha (aparecendo duplicado) → agora some no 1º desenho; `padrao` nas dependências recarregaria tudo a cada render do pai → trocar de conjunto agora é pela `key`; misturar `x` animado com `translateY` no mesmo `motion.div` → centralização numa caixa externa em CSS.
- `getImageData` num canvas aberto por `file://` dá "tainted canvas": para inspecionar pixels no teste, sirva a pasta por HTTP.
- `ffprobe -of csv=s=' '` não aceita espaço: use `s=x`.

**Pendente:**
- Testar num iPhone e num Android reais (decodificação, memória, `svh/lvh`).
- Rodar o componente dentro de um app Next.js de verdade.
- Gerar um clipe de verdade por IA: o PC do Daniel tem GPU NVIDIA de ~4 GB (12/10/2026), abaixo do mínimo; usar Kaggle, Colab ou conta grátis no HF. Falta montar o notebook.
- A categoria `skills/sites/` ainda precisa da aprovação do Daniel.

## 12/10/2026: scroll sem travar (CANCELADO a pedido do Daniel)

O Daniel pediu foco em usar o scroll-vídeo **sem travar**. A pesquisa e o teste de velocidade foram interrompidos (≈ 40% prontos) e o trabalho foi **cancelado** pelo Daniel no mesmo dia. Nada disso entrou no `SKILL.md`; o material fica guardado só como conhecimento, caso ele peça de novo. O que já se sabe está em `research_notes/Caça de skills 2026-10/scroll-suave-PARCIAL/`. Resumo do que tem fonte ou medição:

- **Vídeo ligado à rolagem trava porque o navegador pula para o keyframe mais próximo.** A correção é codificar com todos os quadros como keyframe (`-g 1`). Medimos a distância até o keyframe: 45,5 quadros em média no GOP 250 e 1 no all-intra. O custo é o peso: 4,29 MB contra 1,52 MB em H.264 (720p, 3 s). Num teste, o VP9 all-intra ficou com cerca de metade do peso do H.264 na mesma qualidade (1,42 MB contra 2,91 MB); foi um vídeo sintético, repetir com quadros reais.
- **Para produto com fundo transparente no vídeo:** VP9 com alfa para Chrome e Firefox; HEVC com alfa só se codifica no Mac; o ffmpeg perde o alfa do HEVC sem avisar. A alternativa que roda em todo lugar é o vídeo "empilhado" (cor em cima, máscara embaixo).
- **Canvas com todos os quadros decodificados usa muita memória** (843 MB no pico contra 325 MB com vídeo, em 150 quadros): a ideia é a **janela deslizante** (`fetch` + `createImageBitmap`, `close()` ao sair da janela). Rascunho em `codigo-A/janela.js`.
- **Não validado:** nenhuma técnica foi comparada de verdade (2 de 373 execuções do teste), nada rodou em celular real nem no Safari.

**Pendências antigas, atualizadas:** a categoria `skills/sites/` foi aprovada e entrou (PR #21). O notebook para Kaggle/Colab foi **cancelado** pelo Daniel (ele mesmo vai escolher o serviço de geração de imagem e vídeo). Os últimos testes ficaram **dispensados pelo Daniel em 12/10/2026** e seguem anotados como **não testados**: o componente dentro de um app Next.js de verdade e a página num iPhone e num Android reais.
