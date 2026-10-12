---
nome: "design-taste-frontend (Taste Skill v2)"
repositorio: "leonxlnx/taste-skill"
caminho_no_repo: "pasta taste-skill (skill design-taste-frontend, v2 experimental)"
autor: "Leonxlnx"
instalacoes: "593,7 mil instalações (skills.sh, 12/10/2026); 94,8 mil estrelas no repositório"
commit_ou_versao: "d237dd3 (11/10/2026), cópia da lista do Daniel; a ui-ux-pro-max usou 717446e (09/10/2026)"
data_verificada: "2026-10-12"
categoria_nossa: "design"
nota: 9
status: "instalada-intacta"
onde_entrou: "skills/design/taste-skill/ (cópia intacta) e ideias em skills/design/ui-ux-pro-max/ (SKILL.md, references/estilos-premium.md, minimalismo.md, anti-padroes-ia.md) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md", "research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md", "research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md", "skills/design/ui-ux-pro-max/ORIGEM.md"]
---
# design-taste-frontend (Taste Skill v2)

## O que é
A skill "anti-genérico" mais famosa do catálogo. Faz a IA ler o briefing e desenhar interface sem a cara padrão de IA. Tem três botões de 1 a 10 (variação, movimento e densidade), regras duras de hero, menu e bento, e listas de coisas proibidas. É a v2 experimental: o autor avisa que ainda muda. É muito grande (89 KB, uns 22 mil tokens).

## O que aproveitamos
- Os três controles com presets (minimalista: 5-6 / 3-4 / 2-3; premium tipo Apple: 7-8 / 5-7 / 3-4; confiança e regulado: 3-4 / 2-3 / 4-5).
- Linha "Design Read" antes de qualquer código e a regra de fazer uma única pergunta se o briefing for ambíguo.
- Regras de hero (cabe na primeira tela, título de até 2 linhas, subtítulo de até 20 palavras, no máximo 4 elementos de texto), menu de 64 a 72 px, bento sem célula vazia, eyebrow no máximo 1 a cada 3 seções.
- Trava de forma (uma escala de raio só) e trava de cor (um acento, saturação abaixo de 80%).
- Paletas banidas (creme e latão genéricos) e clichês de texto ("Elevate", "Seamless").
- Rolagem sem `addEventListener('scroll')`; pré-voo final.
- Tudo reescrito em português na `ui-ux-pro-max`; ver `ORIGEM.md` dela.

## O que NÃO aproveitamos e por quê
- As proibições de Inter, Lucide e botão em pílula: conflitam com os visuais Apple e Samsung. Não adotadas.
- Travessão proibido em todo texto: em português tem uso normal. Adotamos só "evitar como enfeite".
- Vocabulário de animação com recorte ou máscara (Curtain-Reveal Hero, Text Mask Reveal) e pilhas de cards presas na rolagem: batem com as 4 regras de composição do Daniel. Marcado como "não usar sem passar pelas 4 regras".
- A seção 12 promete uma pasta `blocks/` que não existe. Ignorar.
- Tamanho: ler por seção, só quando o Daniel pedir (custa crédito).

## Riscos e cuidados
- Não executa nada sozinha, sem telemetria e sem chave.
- Manda usar `picsum.photos` e `cdn.simpleicons.org` como imagem e ícone provisórios: pedidos de rede a terceiros; trocar antes de publicar.
- O arquivo mudou desde o commit usado na `ui-ux-pro-max`. Papel sugerido da cópia intacta: consultor e revisor opcional.
- O repositório tem mais 11 skills (v1, gpt-taste, soft, minimalist, brutalist, redesign, output, stitch e três só de imagem); só 4 delas aparecem avaliadas aqui (ver as outras fichas leonxlnx__).

## Transcrição: o que aprendemos (nas nossas palavras)
- Entender o pedido antes de desenhar: página, público, clima, família estética.
- Três botões controlam o resultado: quanto foge do comum, quanto se mexe, quão cheia é a tela.
- Hero enxuto: título curto, subtítulo curto, poucos elementos, logos de clientes depois.
- Um acento só e uma escala de cantos só, valendo a página inteira.
- Evite paletas "bege com latão" e frases de marketing batidas.
- Na rolagem, use APIs próprias (ScrollTrigger, IntersectionObserver, `animation-timeline`), nunca o evento de scroll cru.
- Antes de entregar, rode o pré-voo: conte eyebrows, confira o colapso no celular.
- Atenção: é dogmática e às vezes se contradiz com as outras skills do mesmo repositório.

## Fontes
- https://github.com/leonxlnx/taste-skill
- https://skills.sh/leonxlnx/taste-skill/design-taste-frontend
- research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md (seções 2, 3, 4)
- research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md (item 1)
- skills/design/ui-ux-pro-max/ORIGEM.md
