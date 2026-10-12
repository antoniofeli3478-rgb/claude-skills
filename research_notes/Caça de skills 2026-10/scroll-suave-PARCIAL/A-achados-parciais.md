# Scroll-vídeo sem travar: achados parciais da pesquisa A (12/10/2026)

**Situação: INCOMPLETO.** O agente de pesquisa foi parado antes de escrever o `pesquisa-A.md` final. Este texto foi montado depois, a partir do que ele leu e mediu. Cada fato diz de onde veio. O que **não** foi confirmado está marcado.

Tudo em palavras nossas. Os comandos do ffmpeg são fatos técnicos curtos. Os testes usaram vídeo **sintético** (fractal com grão, 90 quadros, 1280×720, 30 fps) no container (4 CPUs, sem placa de vídeo). Servem para comparar opções entre si, não para prometer números num celular.

## 1. Por que o vídeo ligado à rolagem trava

- **`video.currentTime = x` não entrega o quadro x.** O navegador pula para o quadro de referência (keyframe) mais próximo. Num vídeo normal há um keyframe a cada 2 a 10 segundos (250 quadros é um padrão comum), então a rolagem só alcança poucas imagens diferentes. Fonte: hontran.dev, "Scroll-Scrubbed Video Stutters? Fix the Keyframes" (09/07/2026). A resposta da equipe do GSAP num fórum (31/08/2022) diz o mesmo: a suavidade depende muito de como o vídeo foi codificado.
- **Nossa medição confirma.** Distância média até o keyframe anterior, em quadros: GOP 250 = 45,5 (pior caso 90); GOP 15 = 8; GOP 8 = 4,4; all-intra (`-g 1`) = 1.
- **Correção da fonte:** codificar com todos os quadros como keyframe.
  - H.264 (Safari): `ffmpeg -i in.mov -vf scale=1280:-2 -r 30 -movflags faststart -vcodec libx264 -crf 23 -g 1 -pix_fmt yuv420p -an out.mp4`
  - VP9 (colocar primeiro nas fontes): `ffmpeg -i in.mov -vf scale=1280:-2 -r 30 -c:v libvpx-vp9 -crf 32 -b:v 0 -g 1 -pix_fmt yuv420p -an -row-mt 1 out.webm`
  - `-pix_fmt yuv420p` evita caixa preta no Safari; `muted`, `playsinline` e `preload="auto"` no `<video>`; nunca chamar `.play()`.
- **Custo:** all-intra pesa bem mais. A fonte diz que um clipe 1080p pode passar de 100 MB; a regra dela é nunca codificar maior do que o maior tamanho em que o vídeo aparece. Na nossa medição (720p, 3 s): H.264 GOP 250 = 1,52 MB; GOP 8 = 2,17 MB; all-intra = 4,29 MB (2,8× o GOP 250).
- **Problema de fonte ainda aberto:** a fonte não fala de `requestAnimationFrame`, suavização nem do evento `seeked`. Isso ficou para o teste B.

## 2. Qual codec compensa (nossa medição, vídeo sintético, 90 quadros 1280×720)

| Arquivo | Tamanho | Distância média ao keyframe |
|---|---|---|
| H.264 GOP 250 (CRF 23) | 1,52 MB | 45,5 quadros |
| H.264 GOP 15 | 1,93 MB | 8 |
| H.264 GOP 8 | 2,17 MB | 4,4 |
| H.264 all-intra | 4,29 MB | 1 |
| VP9 (CRF 32) all-intra | 7,10 MB | 1 |
| VP9 (CRF 32) GOP 8 | 2,52 MB | 4,4 |
| AV1 (SVT-AV1) all-intra | 2,97 MB | 1 |
| HEVC all-intra | 3,61 MB | 1 |
| VP9 com transparência, all-intra | 7,64 MB | 1 |
| H.264 "empilhado" (cor em cima, transparência embaixo, 1280×1440) | 4,94 MB | 1 |
| 90 quadros WebP (q80) | 4,88 MB (54 KB por quadro) | não se aplica |
| 90 quadros WebP com transparência | 3,36 MB (37 KB por quadro) | não se aplica |
| 90 quadros JPG | 14,2 MB (158 KB por quadro) | não se aplica |

- **Cuidado ao comparar:** os CRFs acima não dão a mesma qualidade. O VP9 CRF 32 saiu bem mais nítido (SSIM 0,9966) que o H.264 CRF 23 (0,9766).
- **Na mesma qualidade (SSIM ≈ 0,97), com all-intra:** VP9 CRF 63 = 1,42 MB (SSIM 0,9725) contra H.264 CRF 28 = 2,91 MB (0,9706). O VP9 all-intra ficou com cerca de metade do tamanho. Foi **um** vídeo sintético; repita com uma sequência real do Blender antes de decidir.
- **Decodificação em software, 1 thread do ffmpeg** (aproximação; o navegador usa decodificador de hardware): H.264 GOP 250 = 5,3 ms por quadro; H.264 all-intra = 7,2; AV1 = 7,9; VP9 all-intra = 10,9; HEVC = 13,1 ms.
- **AV1 pelo `libaom` com `-g 1` travou o ffmpeg** (falha de segmentação) nesta versão (6.1). O `SVT-AV1` funcionou. O agente B anotou a mesma coisa.
- **Transparência:** o decodificador nativo do ffmpeg joga fora o canal alfa do VP9; use o `libvpx-vp9` na entrada para conferir. O arquivo VP9 com alfa traz a marca `alpha_mode=1`.

## 3. Transparência em vídeo (para o produto com fundo transparente)

- **Chrome e Firefox:** WebM VP9 com alfa (`-pix_fmt yuva420p`, `-auto-alt-ref 0` obrigatório). O Chrome aceita alfa em WebM desde a versão 31 (blog do Chrome).
- **Safari:** HEVC com alfa, marca `hvc1`, e o codificador `hevc_videotoolbox` **só existe no macOS**. O ffmpeg **perde o alfa do HEVC sem avisar** e o `ffprobe` não enxerga (hontran.dev, 01/09/2026). Para a lista de fontes: o MP4 `hvc1` vem **antes** do WebM.
- **Alternativa sem depender do Mac:** vídeo "empilhado" (cor em cima, máscara embaixo) lido por um pequeno WebGL (Jake Archibald, 05/08/2024; licença da página não declarada, confira a do pacote `stacked-alpha-video`). Medimos 4,94 MB para 90 quadros 1280×1440.
- **Não confirmado:** a página da Apple sobre HEVC com alfa veio sem texto; versões exatas de Safari/iOS ficam por conferir.

## 4. Sequência de quadros no canvas (a técnica principal da skill)

- **Estudo do CSS-Tricks (22/05/2020, Jurn van Wissen):** 148 quadros, laço com `requestAnimationFrame`, pré-carga em lista de imagens. Pesa: 56 MB quando há várias instâncias na página; um comentário cita cerca de 31 KB por quadro; a Apple serve uma imagem única de reserva quando a conexão é lenta; guardar as imagens numa lista evita pedidos novos.
- **`createImageBitmap(blob)`** decodifica sem travar a tela e libera memória com `close()`. **`img.decode()`** garante que a imagem está decodificada antes de entrar na tela (MDN; a página não diz se sai da thread principal).
- **Safari no iPhone:** o erro de vazamento de memória do `createImageBitmap` (WebKit 229825, 2021) está marcado como **corrigido** (01/11/2024). O limite total de memória de canvas no iOS (a busca apontou para erros do WebKit, e o número 384 MB **não foi confirmado**).
- **Nossa medida (a 4× de CPU mais lenta, rolagem rápida):** deixar todos os 150 quadros decodificados na memória usou 843 MB no pico, contra 325 MB com vídeo. Isso reforça a ideia da **janela deslizante** (só os quadros perto do atual).
- **Rascunho de código nosso** em `codigo-A/janela.js` (janela deslizante com `fetch` + `createImageBitmap` e cancelamento), `suave.js` (suavização com o tempo real de cada quadro), `adaptar.js` (reduz qualidade se o aparelho for lento) e `medir.js` (intervalo entre quadros e quadros longos). Os quatro passaram na checagem de sintaxe; só a janela foi rodada no navegador, e o teste achou um erro (a primeira carga não disparava), já corrigido no arquivo.
- **Saída parcial do navegador (rascunho, cenário ainda mal definido; não vale como resultado):** a 1× de CPU, praticamente todos os quadros em menos de 20 ms; a 4×, 78,6% (p95 33 ms, p99 50 ms, pior 83 ms); a 6×, só 32,5% (p95 83 ms). **Ponto a conferir:** sem o medidor ligado o laço ficou parado (0 ciclos por segundo, como deve ser); com o medidor ligado apareceram 60 ciclos por segundo parado, e não ficou claro se o culpado é o próprio medidor.
- **Rede lenta:** o teste `rodar-rede.js` foi interrompido; quatro pedidos ficaram em voo enquanto nenhum quadro estava pronto. Sem conclusão.

## 5. Suavização e bibliotecas

- **Efeitos ligados à rolagem em JavaScript chegam atrasados:** o navegador mostra a nova posição antes de o evento de rolagem alcançar o código (documentação do Firefox). A recomendação: CSS scroll-driven ou `position: sticky`. Segundo a nota do WebKit (20/06/2025), animações ligadas à rolagem estavam no Safari 26 **beta**; não confirmado no Safari 26 final.
- **GSAP ScrollTrigger:** `scrub: true` liga direto; `scrub: 1` (número) é o tempo, em segundos, que a animação leva para alcançar a barra. O GSAP é grátis para uso comercial desde 30/04/2025, mas a licença é proprietária (já registrado na skill).
- **Lenis (MIT, v1.3.26, último commit 01/10/2026):** `lerp` padrão 0,1; `syncTouch` desligado por padrão; respeita "reduzir movimento" (força `lerp` = 1).
- **scrolly-video (MIT, v0.0.25, último commit 07/03/2025):** tem caminho com WebCodecs e plano B; parado há mais de 19 meses. Serve como leitura, não como dependência.
- **`motion.dev` `useSpring`:** liga a um valor de rolagem; os valores padrão da mola **não** estão na página lida.
- **Não confirmado:** o repositório `frame-scroll-animation` tem arquivo LICENSE, mas o texto não foi lido.

## 6. WebCodecs (decodificar quadro a quadro sem `<video>`)

- Suporte para `VideoDecoder` (dados do MDN baixados pelo agente): Chrome 94, Firefox 130 (não no Firefox Android), Safari 16.4 (iOS também). `requestVideoFrameCallback`: Chrome 83, Firefox 132, Safari 15.4. `fastSeek`: **não existe no Chrome**; Firefox 31; Safari 8; e perde precisão. `ImageDecoder`: Safari só em "preview".
- Cada `VideoFrame` precisa de `close()` logo que não servir mais, senão a decodificação pode **parar** (rascunho do W3C de 07/10/2026). O limite de quadros abertos não está no texto.
- O vídeo precisa ser separado (demux) por código nosso ou por biblioteca. Não houve teste com WebCodecs.

## 7. Como medir (para a skill)

- **Quadro longo:** a API *Long Animation Frames* marca quadros com mais de 50 ms. Código: `new PerformanceObserver(cb).observe({ type: 'long-animation-frame', buffered: true })` (Chrome).
- **O INP não conta rolagem**; por isso ele não serve para medir o scroll-vídeo (web.dev, 02/09/2025).
- **Simular celular lento:** o DevTools oferece 4× e 20× (página de 23/10/2024) e a calibração por aparelho; o próprio Chrome avisa que não simula o celular de verdade. Nosso objetivo "95% dos quadros abaixo de 20 ms a 4×" é uma meta nossa, não uma norma.
- **Celular de verdade:** Android via `chrome://inspect` com cabo USB; iPhone via Web Inspector do Safari no Mac. Nenhum dos dois foi testado.
- **Outros números úteis:** imagens começam com prioridade baixa no Chrome e as 5 primeiras grandes passam a média (Chrome 117); use `fetchpriority="high"` no primeiro quadro. O HTTP/2 recomenda no mínimo 100 pedidos simultâneos (RFC 9113), então baixar muitos quadros em paralelo é aceitável. `navigator.deviceMemory` devolve a memória arredondada para potência de 2 e limitada; serve de pista para escolher o conjunto leve.

## 8. O que se viu na página da Apple (só o HTML, sem rodar)

Nas páginas do AirPods Pro e do iPhone 17 Pro o HTML mostra vídeos `<video preload="none" muted playsinline>` (a lista de reprodução é HLS, `.m3u8`), marcações `data-anim-keyframe` que ativam classes CSS (ex.: "t - 70vh") e imagens de começo e fim com versão separada para telas até 734 px. Isso mostra **o que está no HTML**, e não como a animação inteira funciona.

## O que falta nesta pesquisa

- Texto final `pesquisa-A.md` e as receitas verificadas em formato de skill.
- Testes de janela deslizante com rolagem real, rede lenta e quadros reais do Blender.
- Todo o teste com WebCodecs.
- Safari/iOS e Android reais.
