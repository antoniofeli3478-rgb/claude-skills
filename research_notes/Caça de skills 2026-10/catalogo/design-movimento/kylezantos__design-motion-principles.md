---
nome: "design-motion-principles"
repositorio: "kylezantos/design-motion-principles (https://github.com/kylezantos/design-motion-principles)"
caminho_no_repo: "arquivos anti-checklist.md, motion-cookbook.md (pasta references/), creation-gotchas.md e accessibility.md; pasta exata dos dois últimos não verificada"
autor: "Kyle Zantos"
instalacoes: "11,4 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "4a9ca87 (30/05/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 8
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/references/revisao.md §3; references/coreografia.md §4; references/receitas.md; skills/design/button-animation/SKILL.md (§7 erros típicos de IA)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.4)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (tabela (fonte 4))", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.3)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§3 e §6)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.18)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.10)", "skills/design/motion-oficial/ORIGEM.md", "skills/design/button-animation/ORIGEM.md", "skills/design/motion-oficial/LICENSES/kylezantos-design-motion-principles-MIT.txt"]
---
# design-motion-principles

## O que é
Skill que combina três "lentes" de movimento (Emil = contenção, Jakub Krehel = polimento de produção, Jhey Tompkins = brincadeira) e traz a melhor lista do que parece "movimento feito por IA genérica". É o que separa o premium do genérico.

## O que aproveitamos
- Anti-checklist de "movimento de IA", com limites: pulsos/brilhos em loop (qualquer um); blur em toda entrada (3 ou mais componentes na tela); `hover:scale-105` em tudo (3 ou mais); cascata em toda lista (2 ou mais listas); mola com quique em ação utilitária (qualquer uma); mesmo fade em 4 ou mais componentes; animação ao montar em `h1`/`p`/`nav`.
- Pesos por tipo de projeto: painel SaaS não usa elástico; app infantil pode passar dos 300 ms; vídeo promocional pede o peso "polimento + brincadeira".
- Receita de entrada do Jakub Krehel: opacidade + `translateY` + `blur(4px)` com mola `duration 0.45, bounce 0`; saída mais sutil que a entrada; troca de ícone com `AnimatePresence mode="wait"`, escala 0,8 e blur 4 px.
- `creation-gotchas.md`: não usar uma duração só para tudo; respeitar o ritmo do código existente.
- Extras: `animation-fill-mode: backwards` na cascata; `@property` para animar variáveis CSS; limite de `will-change` (0–3 ok, 4–10 cuidado, 10+ repensar).

## O que NÃO aproveitamos e por quê
- Conflito: o `accessibility.md` sugere reset global `animation-duration: 0.01ms !important` em reduced motion. Emil e Blode dizem que isso exagera ("menos e mais suave, não zero"). Valeu Emil/Blode.
- A pesquisa de botões listou a skill como "não baixada" na A e baixada na B (MIT, mesmo commit): o conteúdo usado vem da B.

## Riscos e cuidados
- Só texto e exemplos; não executa nada. Lida como dado.
- Licença MIT: manter aviso de copyright.

## Transcrição: o que aprendemos (nas nossas palavras)
- Loops chamativos (CTA pulsando, anel brilhando sem parar, "respiração") só entram se o usuário pedir; em vídeo promocional podem.
- Nunca partir de `scale(0)`; comece de 0,9–0,97 com opacidade 0.
- Uma duração única para tudo é erro: elemento menor é mais rápido.
- Não aplique o estilo de um designer a tudo: ferramentas pedem contenção, coisas lúdicas pedem brincadeira.
- Se o projeto já usa molas de 500 ms, um componente de 150 ms vai parecer fora do lugar.
- Hover em todo card e fade-up em toda seção parecem gerados por IA; um momento orquestrado vale mais.

## Fontes
- https://skills.sh/kylezantos/design-motion-principles/design-motion-principles
- https://github.com/kylezantos/design-motion-principles
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.4)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (tabela (fonte 4))`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.3)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§3 e §6)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.18)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.10)`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/motion-oficial/LICENSES/kylezantos-design-motion-principles-MIT.txt`
