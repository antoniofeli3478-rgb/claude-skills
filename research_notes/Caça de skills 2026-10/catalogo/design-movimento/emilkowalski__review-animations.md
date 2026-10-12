---
nome: "review-animations"
repositorio: "emilkowalski/skills (https://github.com/emilkowalski/skills)"
caminho_no_repo: "skills/review-animations/ (com STANDARDS.md)"
autor: "Emil Kowalski"
licenca: "MIT"
instalacoes: "215,3 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "e8a175d (02/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "10 (nota do grupo Emil nas duas pesquisas)"
status: "instalada-intacta"
onde_entrou: "skills/design/review-animations/ (cópia intacta, MIT, com STANDARDS.md); as ideias já estavam em motion-oficial/references/revisao.md e button-animation (instalada na main em 12/10/2026)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.1 (item k))", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.1)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§2, §5)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.2)", "research_notes/Caça de skills 2026-10/button-animation/RESUMO.md", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§5)", "skills/design/motion-oficial/ORIGEM.md", "skills/design/button-animation/ORIGEM.md"]
---
# review-animations

## O que é
Skill de REVISÃO de animações: pega um trecho de interface e devolve o que está errado, em tabela, com veredito. Traz o `STANDARDS.md` com as durações por elemento.
É um revisor grátis, que complementa o agente `motion-reviewer`, cuja auditoria depende do MotionScore (pago, Motion+).

## O que aproveitamos
- Formato de revisão em tabela Before | After | Why, com veredito por camadas e decisão Block/Approve.
- Hierarquia de correções: a primeira possível é "apagar a animação".
- `STANDARDS.md`: durações por elemento (clique 100–160 ms; tooltip/popover 125–200; dropdown 150–250; modal/gaveta 200–500; marketing pode ser maior).
- Depuração da "sensação": rodar a 2–5 vezes a duração, painel Animations do DevTools, aparelho real.
- Conteúdo entrou como `references/revisao.md` (motion-oficial) e na lista "nunca entregue" (button-animation).

## O que NÃO aproveitamos e por quê
- Não foi instalada como skill separada. As duas pesquisas sugerem instalá-la; o Daniel ainda decide (RESUMO do button-animation, "Para você decidir", item 2).
- A pesquisa avisa que ela (e a `animation-vocabulary`) começa com uma "Initial Response" fixa, que talvez convenha remover num adendo antes de instalar.

## Riscos e cuidados
- Texto e instruções; não executa nada sozinha. Lida como dado nas pesquisas.
- A "Initial Response" fixa muda o jeito de a IA responder se for instalada sem adendo.
- Licença MIT: manter o aviso de copyright.

## Transcrição: o que aprendemos (nas nossas palavras)
- Revise sempre em tabela: o que está hoje, o que deveria ser, por que.
- Dê o veredito em camadas e termine com Block ou Approve.
- Antes de consertar, pergunte se dá para simplesmente tirar a animação.
- Compare cada duração com a tabela do `STANDARDS.md` (regra geral da fonte: animações de UI ficam abaixo de 300 ms).
- Para julgar sensação, nunca olhe só em velocidade normal: diminua a velocidade e olhe quadro a quadro.

## Fontes
- https://skills.sh/emilkowalski/skills/review-animations
- https://github.com/emilkowalski/skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.1 (item k))`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.1)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§2, §5)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.2)`; `research_notes/Caça de skills 2026-10/button-animation/RESUMO.md`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§5)`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/button-animation/ORIGEM.md`
