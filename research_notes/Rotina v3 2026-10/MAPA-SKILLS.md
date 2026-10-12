# Mapa das skills para o vídeo v3 do Rotina (12/10/2026)

Feito a partir da leitura de todas as 39 skills do repositório (`/home/user/claude-skills-main`, branch main): o `SKILL.md` e o `APRENDIZADOS.md` de cada uma, mais as referências que importam para vídeo (`blender-ia/references/direcao-animacao.md`, `motion-oficial/references/coreografia.md` e `molas-e-curvas.md`, `review-animations/STANDARDS.md`, `estilos-premium/references/movimento-sites.md`, `padroes-premium.md` e a ficha `ferrari-DESIGN.md`, `ui-ux-pro-max/references/anti-padroes-ia.md`), os 3 combos e o `projetos/rotina/PASSO-A-PASSO.md`.

**Briefing que vale (versão atualizada):**
- Promo de 30 s, 1080×1920, 900 quadros a 30 fps, voz Gemini Leda a no máximo 1,25x (80 a 95 palavras), trilha feita em código.
- **Todo o 3D passa pelo Blender** (`blender-ia`, script `premium.py`): o celular de todas as cenas sai do Blender em PNG com fundo transparente e entra como camada no Remotion. O Remotion faz o fundo, os textos, os cartões, os efeitos e o áudio.
- **Vídeo novo:** da v2 só se reaproveita a infraestrutura (o `voz.json` como fonte dos tempos, as camadas separadas, a lógica das entradas que cumprem as regras). Visual, cenas, transições, textos e coreografia são novos.
- **Celular sempre reto ou inclinado para cima** (o topo se afasta da câmera). Nunca com a tela voltada para baixo. Nada de "capotar", como o celular fazia na seção Financeiro da v2.
- As 4 regras do editor valem em todo quadro. Nada pago sem o "sim" do Daniel. Nenhuma chave em arquivo.

**Legenda das fontes** (usada na parte 2): [remotion] `remotion-video` · [blender] `blender-ia` · [mv] `motion-video` · [mo] `motion-oficial` · [btn] `button-animation` · [ux] `ui-ux-pro-max` · [ep] `estilos-premium` · [taste] `taste-skill` · [fd] `frontend-design` · [rev] `review-animations` · [voc] `animation-vocabulary` · [wdg] `web-design-guidelines` · [voz] `voz-pt-br` · [trilha] `trilha-e-mixagem` · [a11y] `accessibility` (pacote web-quality) · [combo] `combo-video-explicativo`. Quando escrevo **INFERÊNCIA**, é conclusão minha que ainda precisa de teste ou do olho do Daniel.

---

## 1. As 39 skills: o que entra neste vídeo

Resumo: **14 sim, 8 parcial, 17 não.** As 17 que ficam de fora são quase todas de site; para cada uma digo como aproveitar depois (página do "link na bio", capa, revisão).

### 1a. Vídeo, áudio e texto (8)

| # | Skill | Usa? | Passo | Como, de forma concreta | Conflito com as 4 regras |
|---|---|---|---|---|---|
| 1 | **remotion-video** | **sim** | animação, revisão, entrega | É o motor 2D e a montagem final. (1) O `voz.json` manda em todos os tempos. (2) Composição final: fundo → sombra do Blender → textos e cartões → PNG do celular do Blender (`<Img>` do quadro). (3) Entradas pela borda e de trás do celular: refazer com visual novo a lógica do `Entra` (mola 300/26), do `Atras` (170/20) e do título dividido (320/38). (4) Blur direcional em SVG e granulação por cima. (5) Zonas seguras do Reels, palavras-chave no quadro da fala, rascunho em `--scale=0.5` e folhas de quadros. (6) As telas do app são renderizadas aqui, em 1080×2309, e passadas ao Blender como textura. | A skill é a dona das regras. Atenção: a íris em `clipPath` da v2 vem de antes das regras. Só vale uma forma sólida crescendo, sem conteúdo dentro (parte 2.7). |
| 2 | **blender-ia** | **sim (papel central)** | design, animação, entrega | Todo o celular sai do `premium.py`: celular genérico de 8,2 mm, estúdio com sheen, `Khronos PBR Neutral`, câmera 85 mm com pivô, `--anim heroi\|turntable\|parado`, `--tela_seq` com a UI do Remotion, `--mb`, `--sombra`/`--so_sombra`, `caixas.json` (a caixa do celular em cada quadro, para os textos) e `qa.json`. O `verifica_curvas.py` confere cada plano antes do render (vmax, overshoot, paradas). O `folha.sh` faz a folha de quadros. A direção de animação está na parte 2.4. **Planos-herói** (revelação e fechamento): parte 2.8. | Seção própria na skill: não há texto no Blender, o quadro é renderizado inteiro com alpha e nunca recortado, e o logo entra pela borda. Regra nova a acrescentar: o celular só fica reto ou inclinado para cima (parte 2.5). |
| 3 | **motion-video** | parcial | animação, música | Não usar o motor em canvas. Aproveitar: (a) as 9 regras de movimento (nada parado, chegada de cauda longa, saída que continua a deriva, eventos sobrepostos de 4 a 12 quadros, transição que nasce de um elemento); (b) as curvas `E.smooth (.45,0,.12,1)`, `E.push (.72,0,.1,1)` e `E.cam (.33,0,.67,1)`; (c) a gramática do estilo `tech-premium`: frase curta (3 a 5 palavras) seguida de uma demonstração que a prova, e o clímax "botão → ponto → check → aviso"; (d) os sons do `sfx.py`, que já estão no mixer da `trilha-e-mixagem`. | Os ajudantes `words()` (palavra surgindo com blur no lugar) e `logo()` fazem o texto "brotar" no meio da tela, o que quebra a regra 4. O `phone()` é um iPhone (regra da Apple) e o 3D agora vem do Blender. |
| 4 | **voz-pt-br** | **sim** | voz | `gemini_tts.py --voz Leda --falas falas.txt --juntar` (um pedido só, porque a cota é de 10 por dia). Depois `alinhar_roteiro.py`, `apertar_pausas.py --estimar --alvo 30` (meta de 1,0 a 1,25x; com 80 a 95 palavras cabe sem acelerar muito), `atempo` (nunca `rubberband`), `apertar_pausas.py` → `voz.json`, `conferir_voz.py` ≥ 95%. Nome dos arquivos: `<modelo>_<voz>_<trecho>`. Apagar o `.wav` vazio se a API falhar. | Nenhum. |
| 5 | **trilha-e-mixagem** | **sim** | música, efeitos, entrega | Trilha nova com `exemplo_trilha_v2.py`, com a grade vinda do `voz.json`: escolher o BPM e a fase para a virada cair na palavra da revelação. **Sugestão de algo novo:** 100 BPM (18 quadros por batida), mais "com peso" que os 120 da v2. Intro 9 dB mais baixa, riser, uma batida de silêncio antes da virada, pausa antes do CTA, golpe final. Efeitos: lista no código do vídeo (`sons.ts`), whoosh com pico no quadro de pouso, impacto no contato, nunca mais de 1 quadro adiantado, efeitos em início de palavra 3 a 4 dB mais baixos. Mixagem: `mixar_com_efeitos.py --linha-do-tempo voz.json`, voz ≥ 12 dB acima do fundo, −14 LUFS e −1,5 dBTP. Lyria e música paga: não. | Nenhum. |
| 6 | **copywriting** | **sim** | roteiro | Roteiro para o ouvido, uma fala por momento, gancho em 3 s, clareza antes de esperteza, benefício concreto ("veja para onde vai cada real"), CTA única no fim. Lista "No AI Tells": nada de "não é X, é Y", listas de negação, pergunta que a própria frase responde, travessão. Faltou prova? `[NEED: ...]`. | Nenhum. Os textos na tela saem do roteiro aprovado: 3 a 5 palavras por linha. |
| 7 | **humanizer** | **sim** | roteiro | `rewrite --purpose marketing`, voz `casual` (Reels com energia, combina com a Leda). Antes, um `detect`; se já soar humano, não mexer. Nunca inventar fato. A "alma" não pode tirar a clareza nem a CTA. | Nenhum. |
| 8 | **find-skills** | não (opcional) | (fora do vídeo) | Só se faltar algo (por exemplo, uma skill de capa de Reels): `npx -y skills@1.7.2 find <tema>` numa pasta temporária, mostrar ao Daniel e **não instalar** sem o "sim". | Nenhum. |

### 1b. Design e animação (11)

| # | Skill | Usa? | Passo | Como, de forma concreta | Conflito com as 4 regras |
|---|---|---|---|---|---|
| 9 | **motion-oficial** | **sim** | design, animação, revisão | O método: (1) "deve animar?" e (2) "para quê?" (feedback, estado, explicação, continuidade, encanto). Tese da cena em 4 linhas (momento focal, continuidade, feedback, orçamento de 3 a 5 beats) e tabela de beats (`coreografia.md`). Curvas e molas: parte 2.3. Ponte para o Remotion: ms → quadros (`ms/1000×30`). Peso do projeto: "vídeo promo = brincadeira", com 500 a 1000 ms e expo-out, mas o Rotina mexe com dinheiro e saúde, então fica em "premium com peso" (parte 2.3). | A skill traz as 4 regras. O scroll reveal com `clip-path` das receitas não vale em vídeo. |
| 10 | **button-animation** | **sim** | animação (telas do app) | Valores, não o código (seção 10): toque 1 → 0,94–0,96 → 1; troca de rótulo **em sequência** ("Salvar séries" → "Salvando…" → "✓ Séries salvas"); destaque deslizante "Fixa \| Pontual" com 650/40/0,5 (8 quadros); ripple no ponto do toque (600 ms, opacidade 0,3); estados com pista estática (cor + ícone + texto); botão 3D que afunda (receita 12, "ótimo para apps de hábito") no CTA final, no lugar do anel girando da v2. Detalhe na parte 2.6. | Na seção 10: crossfade de rótulo = texto sobre texto (fazer em sequência); shimmer e troca de texto deslizante usam `overflow:hidden` (só se o corte for a borda do botão); botão nunca nasce de `scale` no meio da tela. |
| 11 | **ui-ux-pro-max** | **sim** | design | Escrever um **DESIGN.md do vídeo** pelo `references/modelo-DESIGN.md`. Arquétipo **Palco escuro** (cor só no produto e no dado), com motivo escrito: o fundo `#080808` é da marca. Regras: um acento com um papel só, no máximo 2 famílias e 3 pesos, título de peso 300 a 600, contraste ≥ 4,5:1 (texto ≥ 24 px: 3:1). Detector anti-IA antes de entregar. "Vídeo do app usa o mesmo DESIGN.md do app." | A skill traduz as 4 regras para telas (seção própria). |
| 12 | **estilos-premium** | **sim** | design | Trava da referência em 6 linhas antes de animar. Referência principal: a ficha `ferrari-DESIGN.md` (MIT), porque é o vermelho único sobre quase preto: o vermelho fica escasso (só no CTA e no destaque), o título com peso 500 e **nunca negrito**, sem sombras, a profundidade vem da "foto" (aqui, do render do Blender). **Não copiar** nada da marca Ferrari. Personalidade de movimento "premium com peso": curvas `(.52,.01,0,1)` e `(.72,0,.12,1)`, sem mola em texto. Tabela web → quadros e "vídeo de agência: 18 a 36 quadros". | A seção 6 de `movimento-sites.md` lista os padrões que quebram as regras e o que usar no lugar (fenda, letras no lugar, palavra que troca, header sobre título). Atenção: a skill diz "Rotina = Galeria clara", mas isso vale para o **site**; o vídeo segue a marca escura (parte 1d). |
| 13 | **frontend-design** | **sim** | design, revisão | Plano curto antes de animar: 4 a 6 hex com nome, fontes e papéis, layout do quadro em ASCII e princípios. Revisar o plano contra o briefing ("sairia igual para qualquer app?"). **Gastar a ousadia num lugar só** (o plano-herói do Blender) e tirar um acessório no fim. Guia de texto de UI: a ação mantém o nome ("Salvar séries" → "Séries salvas"). | Ela lista "quase preto com um acento vermelhão" como um dos 5 visuais típicos de IA. Aqui é a marca, e "o briefing sempre ganha". Em troca, evitar os outros sinais (parte 2.9). |
| 14 | **taste-skill** | parcial | design, revisão | Ler só as seções do mapa do `APRENDIZADOS.md`. "Design Read" de uma linha. Botões: variância 5, movimento 6, densidade 3 (Reels premium, um assunto por quadro). Proibições úteis: preto puro `#000`, brilho neon, texto em gradiente, travessão (zero, inclusive no texto na tela), nomes e números falsos ("99,9%"), fonte serifada por padrão. | Seção 10: *Text Mask Reveal* e *Curtain Reveal* (recorte e texto sobre imagem em movimento); seções 4 e 5: pilha presa na rolagem. Não usar. |
| 15 | **review-animations** | **sim (adaptada)** | revisão | Os 10 padrões viram o checklist de vídeo (parte 2.10), na saída dela: tabela **Antes / Depois / Por quê** e veredito **Block/Approve**, com quadro e arquivo:linha. Ordem de correção: apagar → reduzir → curva → origem → … → acessibilidade. | A tabela "UI < 300 ms" só vale **dentro da tela do app**. Para o vídeo valem os tempos da `blender-ia` e da `remotion-video` (o próprio APRENDIZADOS dela diz isso). |
| 16 | **animation-vocabulary** | parcial | roteiro de planos, revisão | Dar nome certo a cada movimento na lista de planos e nas notas para o Daniel: *Slide in*, *Stagger*, *Origin-aware animation*, *Follow-through*, *Anticipation*, *Line drawing*, *Tabular numbers*, *Press / Tap feedback*, *Ripple*, *Continuity transition*. | O glossário tem *Reveal*, *Clip-path* e *Mask*: conhecer o nome não autoriza usar. |
| 17 | **web-design-guidelines** | parcial | design (telas), revisão | Regras de **tipografia e números** nas telas recriadas: "…" de verdade em "Salvando…", aspas curvas, `tabular-nums` em kg, repetições e R$, `Intl.NumberFormat('pt-BR')` para "R$ 1.890,00", `text-wrap: balance` nos títulos. Usar a cópia fixada `regras-fixadas/command.md` e não buscar na internet. | As regras de animação dela (interromper, `prefers-reduced-motion`) não se aplicam ao render. |
| 18 | **thumbnail-carousel** | parcial | animação (dentro da tela) | Só os números: a mola pesada `mass 3, stiffness 400, damping 50` (cerca de 0,6 s, sem quique) para a troca de aba **dentro da tela** (Hábitos → Treinos → Financeiro), e a escala 0,95 (ativa) contra 0,85 (inativa) se aparecerem as 3 telas lado a lado no fechamento. | Nenhum, desde que as telas laterais entrem da borda do quadro. |
| 19 | **image-to-code** | não | — | Exige gerador de imagem (créditos ou cota) e é feita para sites. **Outro jeito:** gerar 3 ou 4 "quadros de estilo" do vídeo antes de animar, só com o "sim" do Daniel e um teto de imagens. Nunca copiar texto da imagem gerada. | Os padrões de movimento que ela sugerir passam pelas 4 regras. |

### 1c. Sites (17) e o combo de site

Nenhuma entra na produção do vídeo. Elas servem para a **página do "link na bio"**, que recebe quem assistiu ao vídeo, e para conferências.

| # | Skill | Usa? | Por que não, e outro jeito de usar |
|---|---|---|---|
| 20 | **scroll-cinematico** | parcial (divulgação) | Os PNG do Blender (com alpha) já são a matéria-prima de uma sequência de quadros no site: `preparar-quadros.sh` → WebP + versão *lite* + pôster. A página do link na bio repete o vídeo na rolagem (60 a 150 quadros, 200 a 400vh). O "um progresso só" dela é a mesma ideia do `voz.json` mandar em tudo. Pede render vertical próprio para celular. |
| 21 | **accessibility** (web-quality) | parcial (revisão) | No vídeo: contraste (texto comum ≥ 4,5:1, texto grande ≥ 3:1), "vídeo tem legenda" (palavras-chave na tela, mais o arquivo de legenda no post) e a seção de movimento. Regra nossa, a conferir: nenhum flash forte mais de 3 vezes por segundo nos impactos. |
| 22 | **cro** | parcial (CTA, divulgação) | Conferir o cartão final como "página": uma ação só ("Baixe o Rotina agora"), proposta de valor clara e nenhuma prova inventada (`[FALTA: ...]`). Depois, a landing do link na bio. |
| 23 | referencia-sites | não | É para templates de site. Depois: template MIT para a landing do link na bio. |
| 24 | site-architecture | não | Planejamento de páginas. Depois: a landing de 1 página. |
| 25 | shadcn | não | Componentes de site. Roda `npx` ao carregar: só dentro da pasta do site. |
| 26 | vercel-react-best-practices | não | Desempenho de React no navegador. O Remotion renderiza offline, então as regras não importam. |
| 27 | webapp-testing | não | Não está instalada no container. Outro jeito: tirar prints do app real para conferir se as telas recriadas estão fiéis (se houver cópia local do app). |
| 28 | playwright-cli | não | Não está instalada. Outro jeito: `video-start` gravando o app real como referência das micro-interações. Nunca conectar ao Chrome pessoal. |
| 29 | mcps-para-sites | não | MCPs só no computador do Daniel. O 21st é pago. |
| 30 | web-quality-audit | não | Auditoria de site. Depois: a landing. |
| 31 | performance | não | Depois: peso do vídeo e do pôster na landing. |
| 32 | core-web-vitals | não | Depois: o vídeo ou a sequência no hero não pode estourar o LCP. |
| 33 | seo | não | Depois: `og:image` com a capa do vídeo na landing. |
| 34 | best-practices | não | Segurança de site. |
| 35 | deploy-to-vercel | não | Para a landing: preview primeiro. O Rotina é comercial, então nada de plano Hobby: Cloudflare Pages (decisão do `combo-site-premium`). |
| 36 | seo-audit | não | SEO depois que a página estiver no ar. |
| 37 | combo-site-premium | não | Para a landing do link na bio, depois do vídeo. |

### 1d. Combos (os 2 que entram)

| # | Combo | Usa? | Como |
|---|---|---|---|
| 38 | **combo-copy-humanizada** | **sim** | Passo do roteiro: copywriting → autoconferência → humanizer (`casual`) → conferir fatos, clareza e CTA → aprovação do Daniel. Lista dos tiques em pt-BR ("Diga adeus a", "sem X, sem Y", "O resultado? ..."). Fechar o texto **antes** da voz. |
| 39 | **combo-video-explicativo** | **sim** | É o esqueleto do processo: briefing → roteiro ✋ → voz → grade da música → telas e animação → efeitos e mixagem → legendas → rascunho e folhas ✋ → render → conferências → salvar o aprendizado. **Muda neste vídeo:** a camada 3D deixa de ser R3F e passa a ser Blender, com tempos novos (parte 2.8). |

**Conflitos entre skills que achei, e como resolver neste vídeo:**
1. **Tamanho da textura da tela:** a `remotion-video` (R3F) usa 1080×2340, mas o `premium.py` exige **1080×2309**. Renderize as telas do app em 1080×2309 [blender].
2. **"Nada parado"** [remotion, mv] contra "elementos que nunca aparecem parados" é cara de IA [ux]. No vídeo, a deriva é mínima: alguns px, ±1°, ou o Noise de 0,3° na câmera do Blender. Nunca balanço visível.
3. **Blur:** a v2 usava blur em toda entrada de letra [remotion], mas "blur em 3 ou mais componentes vira cara de IA" [mo]. Use blur **só proporcional à velocidade** (blur de movimento) e nunca como enfeite.
4. **Fundo escuro com vermelho** é um "visual típico de IA" [fd, ux], mas é a marca. Fica, com motivo escrito no DESIGN.md, e sem os outros sinais.
5. **Estilo do Rotina:** a `estilos-premium` diz "Galeria clara" para o **site**. O vídeo segue o Palco escuro da marca. Vale perguntar ao Daniel se a landing também será escura.
6. **Roxo do app:** o "Fixa" usa `#A78BFA` nas telas. É cor de dado do app e fica **só dentro da tela**; nunca em texto do vídeo.

---

## 2. Receita de animação e design para o v3

### 2.1 Paleta e tokens do "palco escuro" (para o DESIGN.md do vídeo)

| Token | Valor | Papel (onde usar / onde nunca) | Fonte |
|---|---|---|---|
| `fundo` | `#080808` | O palco inteiro. Nunca `#000` puro. | marca; [taste 9.A], [ux] |
| `fundo-halo` | radial `#141414` → `#080808` atrás do celular | Halo neutro (≤ 40/255), nunca vermelho | [blender] fundo; [ux] proíbe glow |
| `superficie-1/2/3` | `#101010` `#151515` `#1D1D1D` | Cartões que saem de trás do celular (degraus de +3 a 6%) | tokens do app; [ux] |
| `linha` | `#262626` | Borda de 1 px dos cartões. Sem sombra | app; [ux] "sem sombra em card", [ep] |
| `texto` | `#F5F5F7` | Textos do vídeo. Dentro da tela do app vale o `#FFFFFF` do app | [ux] "no escuro, nunca #fff" |
| `texto-2` | `#A3A3A3` | Texto secundário (≈ 8:1 sobre o fundo) | app (`muted`) |
| `vermelho` | `#CC0000` | **Preenchimento**: CTA, círculo do check, mancha da marca. Branco sobre ele ≈ 5,9:1. Como **texto** sobre o fundo dá só ≈ 3,4:1: só em título ≥ 24 px | marca; contraste calculado por mim |
| `vermelho-claro` | `#FF5A5A` | **Texto** vermelho: a palavra que a voz está dizendo (≈ 6,5:1). No máximo uma por cena | marca; [ux] "1 acento, 1 papel" |
| `grao` | `feTurbulence`, opacidade 0,06 a 0,09, `overlay`, `seed = quadro % 12` | Por cima de tudo, fora do blur | [mv] 6%, [remotion] 0,09 |

- **Proporção:** 80% fundo e neutros, 10 a 15% texto, ≤ 5 a 10% vermelho [ux, ep]. Verde, âmbar, azul e roxo só **dentro** da tela do app; trava de cor única no vídeo [taste 4.2].
- **Profundidade:** vem da luz do Blender (key alta, rims e sheen) e do halo. Sem sombra nos cartões 2D; só a sombra do celular, renderizada em camada (`--so_sombra`) [blender, ep].

### 2.2 Tipografia

- **Barlow Condensed** nos títulos e **Barlow** nos textos de apoio. O motivo, escrito no DESIGN.md: são as fontes do próprio app, então vídeo e tela falam a mesma língua [ux "a fonte com motivo escrito", ep "pegue a linguagem"]. Elas não estão nas listas de "fontes de IA" [ux, taste]. Nada de serifa nem de uma palavra em outra família [taste 4.1].
- **Pesos:** 400, 500 e 600 (no máximo 3). Título em **600, nunca 700** [ux regra 6; ficha Ferrari "nunca negrito"].
- **Títulos:** caixa alta em Barlow Condensed 600, de 120 a 170 px na largura de 1080, entrelinha de 0,95 a 1,0, tracking de +0,01 a +0,02 em (caixa alta pede tracking positivo [ux, ep: palco escuro "caixa alta espaçada"]). De 3 a 5 palavras por linha, no máximo 2 linhas [remotion].
- **Apoio:** Barlow 500, de 44 a 56 px, em caixa normal (sem caixa alta em rótulo pequeno [fd]).
- **Números** (kg, repetições, R$, dias de sequência) com `tabular-nums` e formato pt-BR [wdg]. Um número muda de uma vez e ganha um "pop" de mola; nunca conta quadro a quadro com motion blur [remotion].
- **Zona segura do texto:** fora dos 269 px de cima, dos 672 px de baixo e dos 65 px de cada lado (sobra uma caixa de ~950×980) [remotion]. O celular pode passar dessas faixas; o texto não.
- **Leitura:** de 2 a 3 palavras por segundo, até 20 caracteres por segundo, e a mensagem principal na tela nos 3 primeiros segundos [remotion].
- **Proibido na tela:** travessão [taste 9.G, combo-copy], "01 / 02 / 03" (as três áreas não são uma sequência [fd]), emoji, rótulo em caixa alta acima de cada título [fd, ux].

### 2.3 Curvas, molas e durações (Remotion, 30 fps; 1 quadro ≈ 33 ms)

**Personalidade:** "premium com peso" [ep]: chega pousando, muda mais de cor que de posição, sem quique em texto, um momento de encanto (o plano-herói), o resto contido. O peso do projeto [mo, coreografia §4] diria "brincadeira" para vídeo promo, mas o app trata de dinheiro e saúde [btn §1], então fica no meio.

**Curvas cubic-bezier**

| Movimento | Curva | Duração | Fonte |
|---|---|---|---|
| Título ou cartão chegando da borda | expo-out `(0.16, 1, 0.3, 1)` ou `(0.19, 1, 0.22, 1)` | **14 a 20 quadros** (cauda longa até perto do próximo evento) | [remotion], [ep] "vídeo de agência 18 a 36 q", [blender] "entrada e pouso 15 a 25 q" |
| Peso de marca (o celular "puxa" o texto) | "puxada de foco" `(0.52, 0.01, 0, 1)` ou "encaixe" `(0.72, 0, 0.12, 1)` | 18 a 24 q | [ep] premium com peso |
| UI dentro da tela do app | ease-out forte `(0.23, 1, 0.32, 1)` | 6 a 9 q (≤ 300 ms) | [mo], [btn], [rev] |
| Mover algo que já está na tela do app | `(0.77, 0, 0.175, 1)` | 6 a 9 q | [mo] |
| Gaveta ou sheet no app | `(0.32, 0.72, 0, 1)` | até 15 q | [mo] |
| Toque (pressionar) | press `(0.25, 0.46, 0.45, 0.94)` | 3 a 5 q | [mo molas-e-curvas] |
| Saída de texto | continua a direção da deriva e acelera: `(0.7, 0, 0.84, 0)` com blur; ou M3 `(0.3, 0, 0.8, 0.15)` (só saída) | ~20% mais curta que a entrada: **8 a 12 q** | [remotion], [mo], [mv] |
| Câmera 2D (deriva lenta do fundo) | Easy Ease `(0.33, 0, 0.67, 1)` | a cena inteira | [remotion], [mv] `E.cam` |
| Whip curto e decidido | `(0.83, 0, 0.17, 1)` ou `E.push (0.72, 0, 0.1, 1)` | ~10 q (5 + 5 em volta do corte) | [remotion], [mv] |
| Progresso, turntable, desenhar a barra | `linear` | — | [mo], [blender] |
| **Nunca** | `ease-in` em entrada; o padrão `{100, 10}` do Remotion; `easeOutBack (0.34, 1.56, 0.64, 1)` (passa 9,8%) | — | [rev], [remotion], [blender] |

**Molas** (`spring({fps, frame, config})`; ζ = damping ÷ (2·√(stiffness·mass)))

| Uso | config | Pouso / overshoot | Fonte |
|---|---|---|---|
| Texto entrando da borda (regra 4) | `{300, 26}` | 7 q, ζ 0,75 | [remotion] `Entra` |
| Título em duas metades vindo das bordas | `{320, 38}` | sem passar do ponto: as metades não se cruzam (regra 1) | [remotion] |
| Cartão saindo de trás do celular | `{170, 20}` | 9 q, ~2% | [remotion] `Atras` |
| Chegada premium de UI | `{380, 31}` | ~12 q, ~1,5% | [remotion], M3 Expressive [btn] |
| Painel grande e macio | `{80, 14}` | ζ 0,78, sedosa | [remotion], [mv] |
| Destaque deslizante "Fixa \| Pontual" | `{650, 40, mass 0.5}` | ~8 q, sem quique | [btn] |
| Troca de aba dentro da tela | `{400, 50, mass 3}` | ~0,6 s, sem quique | [thumbnail-carousel], [btn] |
| Opacidade e cor | M3 effects `{1600, 80}` ou curva | **0% sempre** | [btn], [mo], [blender] |
| Pop pequeno (check, chama da sequência) | ζ ≈ 0,7 (2 a 5%), por exemplo `{400, 28}` | ~4% | faixa da [blender] "objeto pequeno 2–4%"; o número é conta minha |
| **Evitar** | `{200, 15}` em texto ou check | ζ 0,53, quica 14%: cara de brinquedo | [remotion] |

**Ritmo**
- **Stagger:** de 2 a 4 quadros entre irmãos (palavras, itens, cartões) [remotion]; linhas a 75 ms ≈ 2 q, blocos de 100 a 175 ms ≈ 3 a 5 q [ep]; dentro do app, de 30 a 80 ms (1 a 2 q), total < 300 ms [mo, rev]. Um momento com stagger por cena, não em toda lista [mo].
- **Sobreposição:** o evento seguinte começa de 4 a 12 quadros antes de o anterior terminar [remotion, mv].
- **Leitura:** todo texto fica parado pelo menos de 8 a 10 quadros entre o pouso e a saída. Com voz natural, ele fica enquanto a palavra é dita [remotion].
- **Câmera 2D:** empurrão de 1,00 → 1,05 a 1,12 **só no fundo e na camada de texto**. O celular tem o próprio dolly no Blender; escalar o PNG no Remotion tira nitidez (INFERÊNCIA).
- **Beats:** um ator principal e no máximo dois de apoio por beat, de 3 a 5 beats por momento; o apoio entra depois que o principal assenta [mo coreografia].
- **Sons:** o swish começa 1 quadro antes da entrada, o impacto cai no quadro de pouso (+7 com a mola 300/26, +9 com a 170/20) [remotion, trilha].

### 2.4 Suavização: tudo o que as skills dizem (Blender + Remotion + Motion)

**No Blender (o celular)** [blender, `references/direcao-animacao.md`]

| Plano | Duração | Quanto mover | Curva | Overshoot |
|---|---|---|---|---|
| Revelação-herói (o reflexo varre o vidro) | 3 a 5 s (90 a 150 q) | 25 a 40° de rotação relativa entre celular e câmera; **a varredura do reflexo (10→90%) dura ≥ 20 a 30 q** | mola ζ 0,85, response de 2 a 2,5 s, defasada 6 q | celular 1 a 2% |
| Push-in (dolly, nunca zoom) | 3 a 5 s | 15 a 30% da distância | `(0.65,0,0.35,1)` se parte parado; `(0.3,0.15,0.2,1)` se entra no corte andando | câmera **0%** |
| Órbita ou arco | 3 a 6 s | 20 a 40° (7 a 13°/s), câmera filha de um pivô | `(0.3,0.15,0.2,1)` (vmax 2,5×) | 0% |
| Turntable | 6 a 8 s por volta | 45 a 60°/s | linear + `CYCLES` com `REPEAT_OFFSET` | — |
| Entrada e pouso | 15 a 25 q | da borda do quadro | expo-out `(0.16,1,0.3,1)` ou mola ζ 0,8 | 2 a 4% |
| Detalhe macro | 1,5 a 2,5 s | 2 a 5% do quadro por segundo | `(0.33,0,0.67,1)` | — |
| Pausa antes de corte ou de texto | ≥ 8 a 10 q | deriva residual, nunca congelado | Noise na rotação da câmera: scale 40, strength 0,3°, depth 0 | — |

- **Câmera:** vmax ≤ 3× a média; 4 a 6× parece UI ou trailer. Não empilhar dois ease-out rápidos no mesmo sentido (as velocidades somam).
- **1 movimento = 2 chaves por canal.** Para passar por um ponto: `AUTO_CLAMPED` + `CONT_ACCEL`, nunca um ease por trecho (a câmera para na chave do meio).
- **Canais defasados de 4 a 8 quadros:** a câmera começa e o celular responde 6 quadros depois.
- **Entrada num corte:** já em movimento (≈ 0,5× a velocidade média). **Saída:** cortar com 5 a 15% do movimento ainda acontecendo, ou chegar parado e segurar ≥ 8 a 10 q.
- Overshoot de pouso na chave de **partida** (`BACK`, `back = 0.5` → 0,8%). O `BACK` padrão passa 10%.
- **Luzes paradas, o celular gira:** o reflexo varrendo é o que vende. Lente de 85 mm constante (100 mm no detalhe), `TRACK_TO` num empty de foco.
- **Motion blur:** `motion_blur_shutter = 0.5` (180°), +3 a 4% de CPU. Não pôr outro blur por cima no Remotion (blur duplo, INFERÊNCIA).
- **Conferir:** `verifica_curvas.py` em cada plano (vmax, entrada, overshoot, paradas) e, na folha, quantos quadros a varredura leva.
- **Mesma assinatura no 2D e no 3D:** a curva do Remotion vira handles do Blender com erro de 0,00005%. Use as mesmas curvas nos dois.

**No Remotion (textos, cartões, transições)** [remotion, mo, mv]
- Tudo vem de `useCurrentFrame()`, sem transição CSS; `interpolate` sempre com clamp dos dois lados; uma função `tween(quadro, intervalo, saída, curva)`.
- **Blur de movimento barato:** `<feGaussianBlur stdDeviation="X 0">` (horizontal) ou `"0 Y"` (vertical) só na camada que se move, com força proporcional à velocidade do quadro. Use `filterUnits="userSpaceOnUse"` e uma região maior que o quadro, porque blur também corta (regra 2).
- Fundos parados ficam **fora** do blur (as amostras criam faixas). O `CameraMotionBlur` (180°, 8 a 10 amostras) multiplica o render por 10: não vale aqui, porque o celular já vem com blur do Blender.
- Blur de enfeite: alvo de 8 px, teto de 20 px [mo APRENDIZADOS]. Para disfarçar um crossfade, `blur(2px)` [rev].

### 2.5 O celular: um aparelho só, sempre reto ou para cima

- **Um único `.blend` base** (`premium.py --salvar`), com o mesmo celular, o mesmo estúdio de luz, o HDRI `studio_small_09`, `Khronos PBR Neutral`, a mesma exposição e a lente de 85 mm em todas as cenas. Muda só a animação de cada plano. Assim não parecem dois aparelhos. Se algum plano precisar de outra luz, mude a **câmera**, nunca as lâmpadas [blender: luzes paradas].
- **Hierarquia sugerida (INFERÊNCIA, testar):** um empty `Inclina` (rotação X fixa entre **−4° e −12°**, nunca positiva) → filho `Gira` (giro em Z, o eixo longo) → o celular. Assim a inclinação para trás continua valendo durante um giro de 360°. Com uma rotação Euler `XYZ` simples, o celular inclinado em X e girando em Z ficaria **inclinado para a frente** quando estivesse de costas. Na convenção do `premium.py` (câmera em −Y, celular em pé), **−6° em X** já deixa a tela levemente para cima e o topo se afastando.
- **Câmera:** na altura do centro do celular ou um pouco abaixo (contra-plongée leve). Se ficar acima, a inclinação do celular precisa ser maior que o ângulo da câmera olhando para baixo + 3° (INFERÊNCIA).
- **Proibido:** capotar em X (o "celular capotando" da v2), tela voltada para o chão, topo vindo na direção da câmera.
- **QA automático (novo, código do projeto):** em cada quadro, conferir no Blender que (eixo longo do celular · direção da câmera) ≥ 0 (o topo nunca vem na direção da câmera) e, com a tela de frente, (normal da tela · "para cima" da câmera) ≥ 0. Imprimir os quadros que falham, junto com o `qa.json`.
- **Ponte com o 2D** (no lugar do `rig.ts` da v2): o `caixas.json` do `premium.py` dá a caixa do celular em cada quadro. É com ela que os textos param em área livre e saem de trás do aparelho (`Atras`). Proposta: exportar também os 4 cantos projetados da tela em cada quadro, para cartões saírem do ponto exato da tela.
- **Telas do app:** compor em 1080×2309, renderizar no Remotion como sequência e passar ao Blender com `--tela --tela_seq N`. Cada toque e cada troca de tela fica preso a uma palavra do `voz.json` **antes** do render do Blender: mudar a UI depois obriga a renderizar de novo os quadros do celular.

### 2.6 Micro-interações que valem mostrar (só funções reais do app)

Todas acontecem **dentro da tela** (a borda da tela é borda real, então o recorte é permitido) e seguem as regras de vídeo da [btn §10].

| Momento (função real) | Micro-interação | Números |
|---|---|---|
| **Hábitos:** marcar um hábito e ver a sequência crescer (`HabitsList`: `Check`, `Flame`, "Sequência") | Toque → círculo enche de `#CC0000` → check desenhado (*line drawing*) → o número da sequência troca (11 → 12) de uma vez, com pop, e a chama dá um pop | Toque: 1 → 0,96 → 1 (pressionar 4 q, voltar 6 q); enchimento do círculo 6 q `(0.23,1,0.32,1)`; check desenhado em 8 a 10 q; pop ζ ≈ 0,7; ripple no ponto do toque: 18 q, opacidade 0,3 → 0 [btn 11] |
| **Treino:** salvar as séries (`LogSets`: "Salvar séries", `SAVE_TAP`) | **"Salvar séries" → "Salvando…" → "✓ Séries salvas"**, sempre em sequência (o rótulo velho sai por completo antes de o novo entrar). Depois o gráfico de evolução se desenha e o último ponto dá um pop | Botão com largura fixa; cada troca: sai em 5 q, entra em 6 q, escala 0,8 → 1 + blur 4 px → 0 [btn 2.4]; "Salvando…" fica ≥ 10 a 15 q (o mínimo de 300 a 500 ms do spinner) [btn 3]; verde + ✓ + texto como pista estática; o gráfico desenha em 24 a 34 q |
| **Treino:** estilo da semana (`Styles`: força, hipertrofia, resistência, deload) | Selecionar um estilo: o destaque desliza até ele e o check aparece | `{650, 40, 0.5}`, ~8 q, sem quique |
| **Treino:** montar o dia (`WeekPlan`: "+ Adicionar exercício") | Toque com afundar, e a linha nova entra pela borda de baixo da lista | Entrada 6 a 9 q `(0.23,1,0.32,1)`, deslocamento de 8 px |
| **Financeiro:** despesa fixa x pontual (`Expenses`: "Fixa · todo mês \| Pontual · uma vez") | Segmentado com destaque deslizante | `{650, 40, 0.5}` |
| **Financeiro:** para onde vai cada real (`FinOverview`, `Transactions`) | Barras por categoria crescendo da base (borda real), valores em R$ com `tabular-nums` | Stagger de 2 q, 9 a 12 q cada |
| **CTA final** | Botão "Baixe o Rotina" **que afunda** (receita 12): a borda de baixo de 4 → 2 px e o texto desce 2 px, num toque no quadro de "agora". É novo em relação ao anel girando da v2 | Afundar 3 q, voltar 5 q; um loop só no máximo (a regra de 1 loop por tela [ep]) |

- **Pista estática** em todo estado (cor + ícone + texto, nunca só movimento) [btn §3]. Botão desativado nunca encolhe [btn].
- **O indicador de toque** é um círculo, sem mão nem emoji, e nunca passa por cima de um texto que ainda está sendo lido [btn §10].
- O aviso de confirmação dentro do app entra pela borda de baixo da tela e sai por onde entrou [mo receitas, mv clímax].

### 2.7 Transições entre seções (todas sem máscara no meio da tela)

1. **Giro de 360° no eixo longo** (Blender) para trocar de seção: de 18 a 24 quadros, curva in-out de câmera `(0.83,0,0.17,1)` [remotion], com a inclinação para cima mantida (2.5). A tela troca no quadro em que a traseira está de frente para a câmera (2 a 3 quadros) [remotion]. O texto da seção anterior sai **no mesmo sentido do giro**, com blur direcional.
2. **Entrar na tela:** o dolly do Blender leva o celular até a tela cobrir o quadro. A tela mostra a mesma arte do quadro 2D seguinte e, no quadro seguinte, entra o 2D: emenda invisível. A volta é um **afastamento** (a moldura entra pelas bordas do quadro), nunca um recorte [remotion].
3. **Whip lateral** de ~10 quadros, com o blur no máximo bem no corte [remotion].
4. **Forma sólida que cresce** a partir de um elemento (o botão do CTA, o ponto do check) até cobrir o quadro inteiro, **sem conteúdo dentro dela**. A cena seguinte começa sobre a cor cheia. Assim não há máscara (regra 2) e a transição nasce de um elemento [remotion, mv].
- **Proibidos:** fade genérico [remotion], íris com a cena nova aparecendo dentro do círculo (máscara com borda no meio da tela), *curtain* e *text mask reveal* [taste], palavra que troca no mesmo lugar [ep].

### 2.8 Onde o plano-herói do Blender rende mais (esqueleto de 30 s, a ajustar ao `voz.json`)

| Trecho (aprox.) | Cena | Plano do Blender | Texto (camada entre o fundo e o celular) |
|---|---|---|---|
| 0 a 3 s | Gancho (a vida espalhada em vários apps e planilhas) | O celular entra pela borda de baixo, reto, com mola ζ 0,8, de 15 a 25 q | A frase do gancho entra da borda lateral **antes** de o celular subir; nunca fica embaixo dele |
| 3 a 7 s | **Revelação (plano-herói 1, onde o Blender rende mais)** | `--anim heroi`: 90 a 120 q, órbita drift de −20° a 0°, dolly de 18%, mola do celular ζ 0,85 com response de 2,35 s defasada 6 q, **varredura do reflexo de 24 q**, `--comp` só aqui | O nome "Rotina" sai **de trás do celular** depois que a varredura termina. Logo nunca sobre texto |
| 7 a 13 s | Hábitos | Órbita lenta de 20 a 25° + push-in de 15%, celular a −6° | 1 título curto da borda + 1 cartão saindo de trás do celular |
| 13 a 19 s | Treino | Giro de 360° no eixo longo para entrar; depois, detalhe macro na tela (100 mm) para o "Salvando… → ✓" | idem |
| 19 a 25 s | Financeiro | Entrar na tela → afastamento; o celular **não capota** | idem |
| 25 a 30 s | "Um app só" + **CTA (plano-herói 2)** | Turntable lento a 45°/s ou push-in dolly até a tela com o botão que afunda; pausa de 8 a 10 q antes do golpe final | "Baixe o Rotina agora. Link na bio." entra da borda, em área livre, na zona segura |

**Custo e ordem (medidos na skill, 4 CPUs):** final em 1080×1920 ≈ 24 s por quadro com tudo ligado, ≈ 18,5 s sem compositor e sem motion blur, menos quando o celular ocupa menos do quadro. Rascunho a 25%: ≈ 1,5 s por quadro.
- Com cerca de 750 quadros de celular, o rascunho completo leva ≈ 20 min e o final, de 4 a 5 h em segundo plano.
- **Ordem:**
  1. Animatic 2D com o celular em silhueta, usando o `caixas.json` do rascunho.
  2. Rascunho do Blender a 25% em todos os planos e `verifica_curvas.py`.
  3. Folhas de quadros e aprovação do Daniel.
  4. Final por plano (`--frames a-b`), um render por vez.
  5. `--comp` só nos 2 planos-herói.
- **Economia a testar (INFERÊNCIA):** nos planos em que o celular fica parado e só a UI muda, renderizar o celular uma vez e compor a UI no Remotion pelos 4 cantos projetados. A borda da tela é borda real, então o recorte é permitido. O risco é perder o reflexo sobre o vidro; compensar com um passe de reflexo.

### 2.9 O que evitar ("cara de IA" e quebras de regra)

- Texto em gradiente, brilho passando no texto, glow ou neon, vidro como enfeite [ux, taste 9.A, ep §5 "brilho" = gradiente].
- Preto puro `#000`, índigo ou violeta fora da tela do app, um segundo acento decorativo [taste, ux].
- Fade-up igual em tudo, texto centralizado com tudo entrando em fade, hover e pulso em loop, blur em toda entrada [remotion §7, mo, fd, btn §7].
- A mola padrão `{100,10}`, `scale(0)`, `ease-in` em entrada, `easeOutBack` [remotion, rev, blender].
- Uma palavra destacada em outra cor "para dar gosto": o vermelho só marca a palavra que a voz diz [fd, ux].
- Rótulos "01 · HÁBITOS", eyebrow em caixa alta em tudo, emoji, travessão, "99,9%", nomes falsos [fd, taste 9.D/9.G, ux].
- Texto que brota no lugar com blur (`words()` do motion-video), letras subindo de uma fenda, *text mask reveal*, *curtain*, palavra que troca no mesmo lugar [mv, ep §6, taste §10].
- Interface falsa feita de `<div>`: as telas precisam ser fiéis ao app (o código em `src/explainer/screens`), sem função inventada [taste 9.E, briefing].
- Número contando sob motion blur [remotion]; o celular cobrindo um texto antes de ele ser lido [remotion]; celular inclinado para baixo [briefing].
- O iPhone (ilha dinâmica, câmeras em diagonal): o celular é genérico [blender, remotion].

### 2.10 Checklist de revisão final (a `review-animations` adaptada ao vídeo, mais as 4 regras)

Revise no rascunho em meia resolução e em folhas de quadros **a cada 6 quadros** (`folha.sh` para o Blender e `ffmpeg select` para o vídeo) [remotion, blender, combo]. Saída no formato da [rev]: tabela **Antes / Depois / Por quê** com o número do quadro, e veredito **Block** ou **Approve**.

**A. Os 10 padrões da review-animations, versão vídeo**
- [ ] 1. **Movimento com motivo:** cada movimento cabe numa palavra (feedback, estado, explicação, continuidade, encanto) [rev 1, mo].
- [ ] 2. **Orçamento de encanto:** só os 2 planos-herói "brilham"; o resto é contido; de 3 a 5 beats por momento [rev 2, mo coreografia].
- [ ] 3. **Curvas:** entradas em expo-out ou ease-out forte; saídas acelerando com blur; nenhum `ease-in` em entrada; nenhuma curva fraca nativa [rev 3, remotion].
- [ ] 4. **Durações:** UI dentro da tela ≤ 9 q; textos de 14 a 20 q; saídas de 8 a 12 q; planos do Blender dentro da tabela 2.4 [rev 4, blender, ep].
- [ ] 5. **Origem:** tudo entra da borda, de trás do celular ou do gatilho; nada de `scale(0)` (o ripple é a única exceção) [rev 5, btn].
- [ ] 6. **Continuidade:** eventos se sobrepõem de 4 a 12 q; nenhum elemento fica 100% parado esperando (a deriva é mínima) [remotion, mv].
- [ ] 7. **Custo:** nenhum blur duplo sobre o PNG do Blender; nenhum número contando sob blur; os fundos parados ficam fora do blur [remotion].
- [ ] 8. **Acessibilidade:** contraste ≥ 4,5:1 (≥ 3:1 em título ≥ 24 px); palavras-chave legíveis e legenda no post; nenhum flash forte mais de 3 vezes por segundo (regra nossa); texto na zona segura [a11y, ux, remotion].
- [ ] 9. **Assimetria:** a saída é ~20% mais curta que a entrada; o toque é mais rápido que a resposta do app [rev 9, mo].
- [ ] 10. **Coesão:** uma personalidade ("premium com peso"), um acento vermelho, as mesmas curvas no 2D e no 3D [rev 10, ep, blender].

**B. As 4 regras do editor (em cada folha, também no meio do movimento)**
- [ ] Nenhum texto sobre texto nem logo sobre texto; as metades do título não se cruzam.
- [ ] Nenhuma linha de corte no meio da tela (inclusive blur cortado e íris com conteúdo dentro).
- [ ] O celular está sempre por cima do texto; o texto está entre o fundo e o celular; o celular só cobre um texto depois de lido.
- [ ] Todo texto entra da borda do quadro ou de trás do celular.

**C. Específico deste vídeo**
- [ ] **O celular nunca está inclinado para baixo** (QA da parte 2.5 sem falhas) e nunca capota.
- [ ] `verifica_curvas.py` em cada plano: câmera com vmax ≤ 3× e overshoot 0%; celular com 1 a 2%; nenhuma parada no meio; varredura do reflexo ≥ 20 q.
- [ ] Cada texto fica parado ≥ 8 a 10 q; o tempo de leitura bate com a fala.
- [ ] Sons no quadro (o swish 1 q antes, o impacto no pouso); voz ≥ 12 dB acima do fundo em todas as falas; −14 LUFS e −1,5 dBTP; transcrição do áudio final ≥ 95% [trilha, voz].
- [ ] As telas mostram só funções reais; os números são plausíveis (nada de "99,9%"); valores em pt-BR com `tabular-nums` [wdg, taste].
- [ ] Detector anti-IA da `ui-ux-pro-max` e "tirar um acessório" da `frontend-design`.
- [ ] O Daniel ouve a versão final (o Claude não ouve áudio) [combo].

---

## 3. Ideias de skills e combos novos (para o Daniel criar depois)

| Nome | Para que serve | Junta | Por que vale |
|---|---|---|---|
| **combo-promo-3d-blender** | O passo a passo de um promo com **todo o 3D no Blender** e a montagem no Remotion: roteiro → voz → grade → animatic com silhueta → rascunho do Blender a 25% → aprovação → final por plano → composição → revisão | combo-copy-humanizada, voz-pt-br, trilha-e-mixagem, blender-ia, remotion-video, motion-oficial, button-animation, review-animations | O `combo-video-explicativo` foi escrito para o R3F. O v3 é o primeiro vídeo inteiro em Blender, com outra ordem (a UI é travada antes do 3D) e outros custos (horas de render em segundo plano). |
| **ponte-blender-remotion** (skill) | Exportar do Blender, em cada quadro, a caixa do celular, os 4 cantos da tela, a normal da tela e o teste "inclinado para cima", num JSON que o Remotion lê (`Atras`, cartões saindo da tela, conferência da regra 3) | blender-ia, remotion-video | É o `rig.ts` da v2 para o Blender. Sem ela, os textos não acham as áreas livres nem saem do ponto exato do celular. |
| **design-de-video** (skill, com modelo `DESIGN-VIDEO.md`) | Tokens de vídeo: paleta com papéis, fontes em px para 1080×1920, zona segura, curvas e molas **em quadros**, durações por tipo de movimento, som por evento | ui-ux-pro-max (modelo DESIGN.md), estilos-premium, motion-oficial, remotion-video, trilha-e-mixagem | O `modelo-DESIGN.md` é para web (ms, hover, breakpoints). O vídeo precisa da mesma disciplina com quadros e zonas seguras. |
| **revisao-de-video** (skill) | O revisor de vídeo: folhas a cada 6 quadros + as 4 regras + a review-animations adaptada + conferências automáticas (texto sob o celular pelo `caixas.json`, tempo de leitura, pitch do celular, contraste, flashes, voz ≥ 12 dB, LUFS, transcrição), com tabela Antes/Depois/Por quê e Block/Approve | review-animations, remotion-video, blender-ia (`verifica_curvas`, `folha.sh`), trilha-e-mixagem, voz-pt-br, accessibility | Hoje a revisão está espalhada em 5 skills. A v2 achou 5 falhas só no segundo rascunho. |
| **micro-interacoes-em-video** (skill com componentes Remotion) | Componentes guiados pelo quadro: `Toque` (pressionar + ripple), `CheckDesenhado`, `BotaoEstados` ("Salvar → Salvando… → ✓ Salvo" em sequência), `Segmentado`, `GraficoDesenhando`, `NumeroPop`, `BotaoQueAfunda` | button-animation, motion-oficial, remotion-video, animation-vocabulary (nomes) | A `button-animation` diz "leve os valores, não o código", mas cada vídeo reescreve os mesmos componentes. |
| **transicoes-sem-mascara** (skill) | Catálogo de transições que cumprem as 4 regras: giro no eixo longo, entrar na tela e afastar, whip com blur `userSpaceOnUse`, forma sólida crescendo do botão, empurrão; com código Remotion e o equivalente no Blender | remotion-video, motion-video, blender-ia, estilos-premium (`movimento-sites.md` §6) | As transições "bonitas" da internet quase todas usam máscara. Uma lista aprovada pelo Daniel economiza revisões. |
| **capa-e-cortes-reels** (skill) | Capa do Reels a partir do quadro-herói do Blender + cortes de 15 s (gancho + 1 seção + CTA) e versões 4:5 e 1:1, trocando só o `voz.json` e a lista de cenas | blender-ia, remotion-video, ui-ux-pro-max, copywriting, cro | O PASSO-A-PASSO já planeja "versões menores". A capa decide o clique e ainda não tem skill. |
| **combo-lancamento-app** | Vídeo + landing do "link na bio" com a mesma sequência do Blender na rolagem, CTA testado e publicação comercial correta | combo-promo-3d-blender, scroll-cinematico, combo-site-premium, cro, web-quality, deploy (Cloudflare Pages) | O vídeo manda para a bio; os PNG com alpha do Blender já servem para o `scroll-cinematico`, então o custo extra é pequeno. |
| **regra-pose-do-celular** (adendo a propor em `blender-ia` e `remotion-video`) | Registrar a regra nova do Daniel (reto ou para cima, nunca capotar), a hierarquia `Inclina → Gira` e o teste automático | blender-ia, remotion-video | É uma regra de editor nova, como as 4 da v2. Precisa entrar nas skills (com aprovação) para não se perder. |

**Propostas de ajuste nas skills (só com aprovação do Daniel, nada foi editado):**
- `blender-ia`: ligar o Noise de micro-deriva no `premium.py` (hoje só está na referência); adicionar a regra de pose e o teste.
- `remotion-video`: avisar que a textura da tela é 1080×2309 quando o celular vem do Blender.
- `estilos-premium`: separar "Rotina no site" (Galeria clara) de "Rotina em vídeo" (Palco escuro da marca).
- `motion-video`: anotar no APRENDIZADOS que `words()` e `logo()` quebram a regra 4.
