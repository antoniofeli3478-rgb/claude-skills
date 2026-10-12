---
nome: "better-accessibility"
repositorio: "jakubkrehel/skills (https://github.com/jakubkrehel/skills)"
caminho_no_repo: "better-accessibility/ (hit-areas.md, motion-and-zoom.md, focus-and-keyboard.md)"
autor: "Jakub Krehel"
licenca: "MIT"
instalacoes: "25,7 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "c0588bb (10/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "9 (nota do repositório jakubkrehel/skills, botões)"
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/SKILL.md (§5 Acessibilidade)"
fontes_locais: ["research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§3.12–3.14)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.8)", "skills/design/button-animation/ORIGEM.md", "skills/design/button-animation/LICENCAS-TERCEIROS.txt"]
---
# better-accessibility

## O que é
Acessibilidade aplicada a botões e movimento: área de toque, movimento reduzido, foco e teclado.

## O que aproveitamos
- Tabela de movimento reduzido: DESLIGAR parallax e loops decorativos; TROCAR deslizar/escalar por crossfade de opacidade; MANTER spinner, progresso e feedback curto de toque.
- Armadilha: `<MotionConfig reducedMotion="user">` desliga transform e layout, mas o blur continua; onde houver `filter`, usar `useReducedMotion()`. Tailwind: `motion-safe:` e `motion-reduce:`.
- Área de toque mínima sem mudar o visual: 24x24 px (WCAG AA), 44x44 (AAA e Apple), 48x48 (Material), com pseudo-elemento `::after`; camadas decorativas com `pointer-events: none` e `aria-hidden`.
- Foco: manter o anel do navegador com respiro (`outline-offset: 2px`); conferir contraste em todo o contorno e em `forced-colors`; manter `border: 1px solid transparent` onde a sombra seria apagada.

## O que NÃO aproveitamos e por quê
- Nada descartado. A nota 9 é do repositório jakubkrehel/skills na pesquisa de botões, não específica desta skill.

## Riscos e cuidados
- Só texto; não executa nada. Lida como dado.
- Licença MIT: manter o aviso.

## Transcrição: o que aprendemos (nas nossas palavras)
- Movimento reduzido significa menos e mais suave, não zero: tire o deslocamento, mantenha opacidade e cor.
- Alvo pequeno de ícone precisa de área de toque maior que o desenho.
- Foco visível é obrigatório e precisa ter contraste.
- No modo de alto contraste, `box-shadow` some: contorno precisa de borda.

## Fontes
- https://skills.sh/jakubkrehel/skills/better-accessibility
- https://github.com/jakubkrehel/skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§3.12–3.14)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.8)`; `skills/design/button-animation/ORIGEM.md`; `skills/design/button-animation/LICENCAS-TERCEIROS.txt`
