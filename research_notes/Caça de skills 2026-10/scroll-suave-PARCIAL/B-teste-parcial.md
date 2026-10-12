# Scroll-vídeo sem travar: teste de velocidade B, parcial (12/10/2026)

**Situação: INCOMPLETO.** O agente montou todo o equipamento de teste, mas rodou só **2 de 373** execuções planejadas (o "teste de fumaça"). Não há conclusão. O que existe vale para validar a montagem, e não para escolher técnica.

## O que ficou pronto

- **`codigo-B/gerar-conteudo.sh`:** gera o conteúdo de teste (fractal em zoom com grão e número do quadro desenhado, 150 quadros, 30 fps) em duas versões, desktop 1280×720 e celular 720×1280, e as variantes: H.264 (GOP longo, GOP 30, all-intra CRF 23 e 20), VP9 (GOP longo, GOP 30, all-intra), AV1 all-intra (SVT-AV1; o `libaom` trava com `-g 1`), WebP e AVIF por quadro. As variantes ficaram prontas (a geração levou 6 min 25 s no container).
- **`codigo-B/servidor.js`** e **`pagina.html`:** servidor local e a página de teste com as técnicas: vídeo (várias codificações), canvas sob demanda, canvas com todos os quadros, canvas com janela deslizante (±8, ±16, ±32) e WebCodecs, com e sem suavização (`lerp`).
- **`codigo-B/rodar.js`:** roda a matriz no Chromium com Playwright e protocolo CDP, com **entradas reais de roda do mouse** (não `scrollTo`), cenários *arremesso*, *constante*, *vai-e-volta* e *saltos*, CPU 1×, 4× e 6×, viewport de desktop (1440×900) e de celular (390×844, DPR 2), e mede: intervalo entre quadros, quadros longos, tempo de assentar, tempo de busca do vídeo, memória (PSS) e bytes baixados. É retomável (pula o que já foi gravado).
- **Matriz planejada (373 execuções):** principal 163, 1× 40, 6× 40, celular 60, carga inicial 20, ablação 48, fumaça 2. Estimativa nossa: cerca de 10 s por execução (9,5 s na fumaça), ou seja, uns 60 minutos de máquina para tudo.

## O que foi rodado (2 execuções, 1 repetição cada, desktop, rolagem em arremesso, CPU 4× mais lenta, 4 núcleos, Chromium sem placa de vídeo)

| Técnica | Intervalo típico (p50) | p95 | p99 | Pior | Quadros acima de 20 ms | Acima de 50 ms | Baixado | Memória no pico |
|---|---|---|---|---|---|---|---|---|
| Vídeo VP9 all-intra (150 quadros) | 16,7 ms | 16,8 | 33,3 | 33,4 | 4,1% | 0% | 23,4 MB | 325 MB |
| Canvas, todos os 150 quadros WebP na memória | 16,7 ms | 33,4 | 50,1 | 100 | 11,9% | 1,7% | 12,2 MB | 843 MB |

- No vídeo, o salto para o quadro novo levou cerca de **216 ms** (p50) e até **339 ms** (p95) para assentar. O quadro certo (150 de 150) apareceu ao final nas duas técnicas.
- **Não tire conclusão:** é uma execução de cada, sem placa de vídeo (renderização em software), com conteúdo sintético. A medida é o ritmo dos quadros da página, e não a sensação de quem rola.
- O H.264 não toca no Chromium do Playwright (sem codec), então as variantes H.264 só valem pelo tamanho; Safari e iPhone não foram testados.

## O que falta

- Rodar as 371 execuções restantes e resumir em tabela (mediana e p95 por técnica e cenário).
- Verificar se "janela ±16" entrega a meta nossa de 95% dos quadros abaixo de 20 ms a 4×.
- Testar com quadros reais do Blender, em celular de verdade (Android por USB; iPhone pelo Safari no Mac).
- Escrever `pesquisa-B.md`.
