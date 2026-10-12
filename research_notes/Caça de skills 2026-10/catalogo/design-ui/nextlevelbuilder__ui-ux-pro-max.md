---
nome: "ui-ux-pro-max (do nextlevelbuilder)"
repositorio: "nextlevelbuilder/ui-ux-pro-max-skill"
caminho_no_repo: "skill ui-ux-pro-max (o repositório traz 7 skills: ui-ux-pro-max, design, design-system, brand, banner-design, slides, ui-styling)"
autor: "Next Level Builder"
licenca: "MIT (Copyright (c) 2024 Next Level Builder); a versão paga uupm.cc fica com marca, logo e assets"
instalacoes: "390,7 mil instalações (skills.sh, 11/10/2026); cópias por outros autores têm de 1,2 mil a 4,7 mil"
commit_ou_versao: "67803ea (11/10/2026); repositório v2.13.0; CLI ui-ux-pro-max-cli 2.15.0"
data_verificada: "2026-10-11"
categoria_nossa: "design"
nota: 7
status: "ideias-aproveitadas"
onde_entrou: "skills/design/ui-ux-pro-max/ (SKILL.md etapas 1, 3 e 4; references/checklist.md). A skill antiga nossa era só um ponteiro para este projeto; o ponteiro segue em original/ux-ui-disign-link"
fontes_locais: ["research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md", "research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md", "research_notes/Caça de skills 2026-10/ui-ux-pro-max/RESUMO.md", "skills/design/ui-ux-pro-max/ORIGEM.md"]
---
# ui-ux-pro-max (nextlevelbuilder)

## O que é
O projeto externo para o qual a nossa skill antiga apontava. É um catálogo genérico: tabelas CSV (88 estilos, 192 paletas, 74 pares de fontes, 1.934 Google Fonts, 119 regras de UX, 22 stacks) mais uma busca em Python. A nossa `ui-ux-pro-max` de hoje é outra coisa: uma skill nossa, em português, "Frankenstein" de sete skills públicas.

## O que aproveitamos
- Os três controles de variação, movimento e densidade (no script: `--variance`, `--motion`, `--density`).
- Tokens em três camadas: primitivo (`--color-blue-600`) → semântico (`--color-primary`) → componente (`--button-bg`).
- Ideia de `MASTER.md` mais páginas que sobrepõem; antes de regerar, ler o que existe.
- Tabela de prioridades de UX e checklist de entrega: testar em 375 px e na horizontal, com movimento reduzido; alvos de toque de 44 pt; sem emoji como ícone; uma família de ícones.
- Escala de 4/8 pontos, breakpoints 375/768/1024/1440, z-index em escala, `min-h-dvh`.
- Só ideias; nenhum CSV nem script entrou.

## O que NÃO aproveitamos e por quê
- Os CSVs e scripts Python (cerca de 2,5 MB, genéricos): pesados e tendem ao "genérico".
- As fontes que ela recomenda (Inter, Playfair, DM Sans, Space Grotesk) batem de frente com as regras anti-IA da impeccable e da taste. O estilo "Minimalism" dela usa `#000` e `#fff` puros.
- As cópias por outros autores (kimny1143, sickn33, davila7 e outros) são repostagens; vale a original.
- Da versão paga (uupm.cc): nada.

## Riscos e cuidados
- Exige Python 3. O pacote é pesado. Não executamos nada dele.
- Marca e assets da versão paga são do dono; a licença MIT cobre o repositório aberto.

## Transcrição: o que aprendemos (nas nossas palavras)
- Um catálogo grande de estilos e paletas ajuda a explorar, mas gera resultado médio se usado sem briefing.
- Tokens em camadas evitam hex solto no componente.
- Guardar o design system num arquivo-mãe e deixar cada página só mudar o necessário mantém o projeto coerente.
- Prioridade de UX: acessibilidade, toque, desempenho, depois o resto.
- Para entregar, rodar um checklist mecânico (tamanhos, contraste, ícones, movimento reduzido).

## Fontes
- https://github.com/nextlevelbuilder/ui-ux-pro-max-skill
- https://skills.sh/nextlevelbuilder/ui-ux-pro-max-skill/ui-ux-pro-max
- research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md (seções 1, 2, 3.1 a 3.5, 3.7) e pesquisa-B.md (seção 1)
- skills/design/ui-ux-pro-max/ORIGEM.md
