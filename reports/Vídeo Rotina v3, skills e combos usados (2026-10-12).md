# Vídeo Rotina v3: skills e combos usados (12/10/2026)

Este relatório diz **o que foi usado, onde e como** no vídeo v3 do Rotina (30 s, 1080×1920), e o que dá para virar skill ou combo novo. Nada foi mudado nas skills; as propostas do fim esperam o seu "sim".

- **Vídeo:** `rotina-video/rotina-v3.mp4` (branch `rotina-video`): 30 s, 1080×1920, −14,0 LUFS, transcrição do áudio final com 95,6% de acerto.
- **Mapa completo das 39 skills** (o que cada uma diz e por que entrou ou ficou de fora): `research_notes/Rotina v3 2026-10/MAPA-SKILLS.md`.
- **Passo a passo para refazer:** `projetos/rotina/PASSO-A-PASSO.md`, item 6.

## 1. A linha de produção, na ordem

| # | Etapa | Quem fez | Skills e combos | Saída |
|---|---|---|---|---|
| 1 | Leitura de todas as skills | agente "mapa" | as 39 (leitura completa) | `MAPA-SKILLS.md`: 14 entram, 8 em parte, 17 ficam para o site |
| 2 | Roteiro | agente "roteiro" | **combo-copy-humanizada** = **copywriting** → **humanizer** (`casual`) | 13 falas, 91 palavras, ângulo "Segunda eu começo" |
| 3 | Voz | eu | **voz-pt-br** (Gemini, voz Leda, 1 pedido só) | voz 1,1x, 28,3 s, palavras alinhadas no `voz.json` |
| 4 | Design do vídeo | eu | **ui-ux-pro-max**, **estilos-premium**, **frontend-design**, **taste-skill**, **web-design-guidelines** | "Palco escuro": `#080808`, um vermelho só, Barlow Condensed 600 |
| 5 | Telas do app (dentro do celular) | eu | **button-animation**, **motion-oficial**, **animation-vocabulary**, **thumbnail-carousel** (só números) | 900 telas em 1080×2309, cada toque preso a uma palavra |
| 6 | Coreografia do celular | eu | **blender-ia** (direção de animação), **motion-video** (9 regras), **remotion-video** | `rig.ts` → `poses-v3.json`, com teste "nunca inclinado para baixo" |
| 7 | Render 3D | Blender | **blender-ia** (`premium.py` + script novo `v3_pose.py`) | 900 PNG com fundo transparente |
| 8 | Animação 2D e montagem | eu | **remotion-video**, **motion-oficial**, **estilos-premium** | títulos, cartões, selos, "+1", LINK NA BIO |
| 9 | Trilha | eu | **trilha-e-mixagem** (`exemplo_trilha_v2.py` adaptado) | trilha original de 100 BPM, virada em "junta" |
| 10 | Efeitos e mixagem | eu | **trilha-e-mixagem** (`mixar_com_efeitos.py`), **motion-video** (sons) | 96 efeitos, −14,0 LUFS no vídeo final, voz ≥ 12,8 dB acima do fundo |
| 11 | Revisão | eu | **review-animations** (adaptada ao vídeo), **accessibility**, as 4 regras + a regra do celular | silhueta antes do Blender, rascunho por terços durante o render; 5 correções (seção 5) |
| 12 | Entrega e registro | eu | **combo-video-explicativo** (esqueleto do processo) | este relatório, PASSO-A-PASSO, APRENDIZADOS |

## 2. Cada skill: onde entrou e como

### Entraram (14)

1. **combo-copy-humanizada** (combo): o agente do roteiro seguiu o combo inteiro. Escreveu com a copywriting, conferiu sozinho, passou pelo humanizer e conferiu fatos, clareza e CTA. Arquivos: `roteiro/1-copy.md` e `roteiro/2-humanizado.md`.
2. **copywriting**: roteiro para o ouvido, uma ideia por fala, gancho em 2 s ("Segunda eu começo."), benefício concreto em cada seção, uma CTA só no fim. Lista "No AI Tells": sem "não é X, é Y", sem travessão.
3. **humanizer**: `rewrite --purpose marketing`, voz `casual`. Tirou o tom de anúncio ("Aí cada coisa fica num app diferente", "cai direto no gráfico").
4. **voz-pt-br**: Gemini TTS com a voz Leda, roteiro inteiro num pedido só (a cota é de 10 por dia). `alinhar_roteiro.py` (WhisperX), `apertar_pausas.py`, `atempo` 1,1x (nunca `rubberband`), `voz.json` com o quadro de cada palavra. A chave 1 deu erro 401; o script passou sozinho para a chave 2.
5. **trilha-e-mixagem**: o gerador de trilha foi copiado e mudado para **100 BPM** (uma batida = 18 quadros), com a grade presa no `voz.json`: virada em "junta" (quadro 250), seções em "Você planeja" (430), "Entrou salário" (574), pausa antes do CTA (772) e golpe final logo depois de "bio" (862). Os efeitos saem de uma lista no código (`sons.ts`) com os mesmos quadros da animação. A mixagem mediu a voz contra o fundo em cada fala.
6. **remotion-video**: o motor da montagem. Camadas: fundo com halo → textos e cartões → celular do Blender por cima (regra 3). Entradas pela borda (`Titulo`) e de trás do celular (`Cartao`, `Selos`, `RotinaTitulo`, `MaisUm`, `LinkNaBio`). Blur só proporcional à velocidade. Granulação e vinheta. Zona segura do Reels (y 269 a 1249 para textos).
7. **blender-ia**: todo o 3D. A cena base saiu do `premium.py` (celular genérico, estúdio, câmera 85 mm, Cycles, `Khronos PBR Neutral`, motion blur). Script novo `v3_pose.py`: lê as poses do Remotion, troca a tela pela sequência de telas e renderiza **só a região do celular** em cada quadro (`--borda`), o que baixou de 18 para cerca de 10 s por quadro. A direção de animação (2 chaves por movimento, canais defasados, chegada sem parar, deriva mínima) foi seguida no `rig.ts`.
8. **motion-oficial**: método "deve animar? para quê?", tese de cada cena, curvas e molas convertidas em quadros (`ms ÷ 1000 × 30`), personalidade "premium com peso".
9. **button-animation**: as micro-interações das telas, pelos valores dela: toque 1 → 0,96 → 1 com ondulação, check desenhado, rótulo em sequência "Salvar séries → Salvando… → ✓ Séries salvas" (sem texto sobre texto), seletor "Fixa | Pontual" com mola 650/40/0,5, botão "LINK NA BIO" que afunda (receita 12).
10. **ui-ux-pro-max**: arquétipo "Palco escuro" com motivo escrito (o fundo escuro é da marca), um acento com um papel só (o vermelho marca a palavra-chave), 2 famílias de fonte, contraste conferido.
11. **estilos-premium**: trava da referência antes de animar (vermelho único sobre quase preto, título sem negrito, sem sombras, profundidade vinda do render 3D) e as curvas "premium com peso" `(0.52, 0.01, 0, 1)`.
12. **frontend-design**: plano curto antes de animar e "gastar a ousadia num lugar só" (o giro de revelação do celular). Texto de UI: a ação mantém o nome ("Salvar séries" → "Séries salvas").
13. **review-animations** (adaptada ao vídeo): os 10 padrões viraram o checklist das folhas de quadros, com veredito Block/Approve. Ordem de correção: apagar → reduzir → curva → origem.
14. **combo-video-explicativo** (combo): o esqueleto do processo (briefing → roteiro → voz → grade da música → telas e animação → efeitos e mixagem → rascunho e folhas → render → conferências → registro), trocando o 3D em React por Blender.

### Entraram em parte (8)

15. **motion-video**: não o motor dele, mas as 9 regras de movimento (nada parado, eventos sobrepostos de 4 a 12 quadros) e os sons (o mixer da trilha-e-mixagem usa os sons dele).
16. **taste-skill**: as proibições úteis: nada de `#000` puro no vídeo, nada de neon, de texto em gradiente, de travessão, de número falso.
17. **animation-vocabulary**: os nomes certos nos comentários do código e neste relatório (*Slide in*, *Stagger*, *Anticipation*, *Follow-through*, *Line drawing*, *Press feedback*, *Ripple*, *Continuity transition*).
18. **web-design-guidelines**: números nas telas: "…" de verdade, `R$ 4.800,00` no formato brasileiro, kg e repetições alinhados.
19. **thumbnail-carousel**: só a mola pesada sem quique como referência para a troca de tela dentro do celular.
20. **accessibility**: contraste dos textos e nada de flash forte mais de 3 vezes por segundo.
21. **scroll-cinematico** e 22. **cro**: não entram no vídeo; ficaram anotados para a página do "link na bio" (os PNG do Blender com fundo transparente já servem para a rolagem).

### Ficaram de fora (17)

As 15 skills de site (referencia-sites, site-architecture, shadcn, vercel-react-best-practices, webapp-testing, playwright-cli, mcps-para-sites, web-quality-audit, performance, core-web-vitals, seo, best-practices, deploy-to-vercel, seo-audit, combo-site-premium), mais **find-skills** (não faltou nada) e **image-to-code** (pede gerador de imagem pago). Cada uma tem o "como usar depois" no MAPA.

## 3. Combos usados neste vídeo

1. **combo-copy-humanizada** (já existe): roteiro.
2. **combo-video-explicativo** (já existe): processo inteiro, com o 3D trocado para Blender.
3. **Combo novo, usado de fato:** "promo com 3D no Blender e montagem no Remotion". Ordem que funcionou:
   1. roteiro (agente) → voz → `voz.json`;
   2. telas do app no Remotion (1080×2309) → textura;
   3. coreografia do celular em código (`rig.ts`), com teste automático da pose;
   4. silhueta do celular no Remotion para conferir o layout antes de gastar horas de Blender;
   5. Blender em segundo plano (só a região do celular) enquanto trilha, efeitos e mixagem são feitos;
   6. montagem final, folhas de quadros, render e conferências.
4. **Combo novo, usado de fato:** "ponte Blender ↔ Remotion": a mesma câmera (0,62 m, 24°) nos dois lados, conferida com 0 px de diferença. Assim os textos saem exatamente de trás do celular 3D.

## 4. Propostas de skills e combos novos (esperam o seu "sim")

Em ordem de prioridade. As 4 primeiras já têm código pronto no projeto, então viram skill rápido.

| # | Nome | Tipo | Para que serve | Junta | Por que vale |
|---|---|---|---|---|---|
| 1 | **ponte-blender-remotion** | skill | A coreografia do celular escrita em código (TypeScript), exportada como uma pose por quadro e aplicada no Blender com a **mesma câmera** do Remotion. Inclui o render só da região do celular (`--borda`) e o teste "nunca inclinado para baixo". | blender-ia, remotion-video | **Testado no v3** (`blender/v3_pose.py`, `src/v3/camera.ts`, `rig.ts`, `exportar_poses.ts`). Sem ela, os textos não saem do ponto exato do celular 3D. |
| 2 | **combo-promo-3d-blender** | combo | O passo a passo do v3: roteiro (agente) → voz → `voz.json` → telas do app → coreografia + teste de pose → **silhueta** → Blender em segundo plano → trilha, efeitos e mix em paralelo → revisão por terços → final → conferências. | combo-copy-humanizada, voz-pt-br, trilha-e-mixagem, blender-ia, remotion-video, motion-oficial, button-animation, review-animations | O `combo-video-explicativo` foi escrito para o 3D em React. O Blender muda a ordem (a tela do app trava antes do 3D) e os custos (horas de render). |
| 3 | **revisao-de-video** | skill | O revisor: folhas a cada 5 ou 6 quadros, as 4 regras + a regra do celular, a review-animations adaptada e as conferências automáticas (texto atrás do celular, pose, voz ≥ 12 dB, LUFS, transcrição ≥ 95%, tremido do reflexo). Saída: tabela Antes/Depois/Por quê e Block/Approve. | review-animations, remotion-video, blender-ia, trilha-e-mixagem, voz-pt-br, accessibility | Hoje a revisão está espalhada em 5 skills. No v3 ela achou 5 problemas, 3 deles só com o celular real. |
| 4 | **micro-interacoes-em-video** | skill (componentes Remotion) | Componentes prontos guiados pelo quadro: `Toque` (pressionar + ondulação), `CheckDesenhado`, botão "Salvar → Salvando… → ✓ Salvo", seletor com mola, gráfico desenhando, número que sobe, botão que afunda, **tela de bloqueio com notificações**. | button-animation, motion-oficial, remotion-video, animation-vocabulary | Cada vídeo reescreve os mesmos componentes. Os do v3 estão em `src/v3/Tela.tsx`. |
| 5 | **design-de-video** | skill (modelo `DESIGN-VIDEO.md`) | Tokens de vídeo: paleta com papéis, fontes em px para 1080×1920, zona segura do Reels, curvas e molas **em quadros**, duração por tipo de movimento, som por evento. | ui-ux-pro-max, estilos-premium, motion-oficial, remotion-video, trilha-e-mixagem | O modelo de DESIGN.md atual é para site (ms, hover). O `src/v3/theme.ts` já é um rascunho dele. |
| 6 | **transicoes-sem-mascara** | skill | Catálogo de transições que cumprem as 4 regras: giro no eixo, entrar na tela e afastar, deslize com blur de velocidade, forma sólida crescendo do botão, cruzamento sem recorte; com o código Remotion e o equivalente no Blender. | remotion-video, motion-video, blender-ia, estilos-premium | Quase toda transição "bonita" da internet usa máscara. Uma lista aprovada por você economiza revisões. |
| 7 | **capa-e-cortes-reels** | skill | Capa do Reels a partir do quadro-herói do Blender + cortes de 15 s (gancho + 1 seção + CTA) e versões 4:5 e 1:1, trocando só o `voz.json` e a lista de cenas. | blender-ia, remotion-video, ui-ux-pro-max, copywriting, cro | A capa decide o clique e ainda não tem skill. |
| 8 | **combo-lancamento-app** | combo | Vídeo + página do "link na bio" com a mesma sequência do Blender na rolagem, CTA conferido e publicação comercial (Cloudflare Pages). | combo-promo-3d-blender, scroll-cinematico, combo-site-premium, cro, web-quality, deploy | Os PNG do Blender com fundo transparente já servem para o `scroll-cinematico`. |
| 9 | **regra-pose-do-celular** | adendo (em blender-ia e remotion-video) | Registrar a sua regra (reto ou inclinado para cima, nunca de tela para baixo) e o teste automático. | blender-ia, remotion-video | É uma regra de editor, como as 4 da v2. Hoje ela só está no código do v3. |

**Ajustes propostos em skills que já existem** (mudam o `SKILL.md` ou scripts, então esperam o seu "sim"):
- **blender-ia:** levar o `v3_pose.py` (poses por quadro + `--borda`) para `scripts/`; ligar a micro-deriva no `premium.py`; marcar como **testados** o render longo (895 quadros), a tela real do app em sequência e o tremido do OIDN (0,4/255 nos quadros parados).
- **trilha-e-mixagem:** deixar o BPM como parâmetro no `exemplo_trilha_v2.py`. Ao mudar de 120 para 100 BPM, além do `beat_t`, 6 tempos fixos em segundos (0,125, 0,25, 0,375, passo do rufar, delay) tiveram de ser trocados à mão.
- **voz-pt-br:** atualizar a estimativa da Leda para ~2,2 palavras por segundo a 1,0x (era 2,67, medida com outra montagem).
- **remotion-video:** avisar que a textura da tela é 1080×2309 quando o celular vem do Blender, e que o Remotion deve pular exatamente os quadros que o Blender não grava.
- **estilos-premium:** separar "Rotina no site" (galeria clara) de "Rotina em vídeo" (palco escuro).
- **motion-video:** anotar no APRENDIZADOS que `words()` e `logo()` fazem o texto brotar no meio da tela (quebra a regra 4). Isso é conhecimento, então já vai nesta entrega.

## 5. Aprendizados deste vídeo

1. **Coreografia em código e render no Blender** deixam o 2D e o 3D na mesma conta: os textos saem de trás do celular sem ajuste manual.
2. **A sua regra da pose virou teste automático**: o exportador para com erro se algum quadro ficar inclinado para baixo.
3. **Silhueta antes do Blender e revisão por terços durante o render** pegaram 5 problemas sem refazer o render inteiro:
   - o celular apagado era um retângulo preto e virou tela de bloqueio;
   - a troca da data usava recorte e virou cruzamento;
   - o "R" de ROTINA vazava pela lateral do celular;
   - a metade direita ficava vazia em "Cada dia cumprido";
   - o Remotion e o Blender discordavam sobre os quadros sem celular.
4. **Celular apagado em palco escuro some.** A tela acesa dá forma ao aparelho e já conta a história.
5. **Quem assiste sem som** precisa de uma palavra na tela ou de uma ação clara no celular em cada fala.
6. **Custos medidos:** Blender 2 h 39 min (895 quadros, ~10,7 s por quadro com 4 CPUs e a máquina dividida com os rascunhos); vídeo final no Remotion 4 min 22 s; rascunho de 10 s em meia resolução ~1 min.
7. **O que o Claude não consegue julgar:** o som (ritmo da trilha, volume, se "anotou peso" saiu claro) e o "sentir" do movimento. Isso fica com o seu ouvido e o seu olho de editor.
