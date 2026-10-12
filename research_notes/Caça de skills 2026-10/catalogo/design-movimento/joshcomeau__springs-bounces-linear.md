---
nome: "\"Springs and Bounces in Native CSS\" (Josh Comeau)"
repositorio: "https://www.joshwcomeau.com/animation/linear-timing-function/ (artigo, não repositório)"
caminho_no_repo: "—"
autor: "Josh Comeau"
licenca: "sem licença aberta (só ideias, sem colar texto)"
instalacoes: "não verificado"
commit_ou_versao: "atualizado em 10/2025"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 6
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/references/molas-e-curvas.md §3"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.10 e tabela (fonte 7))", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.9)", "skills/design/motion-oficial/ORIGEM.md"]
---
# "Springs and Bounces in Native CSS" (Josh Comeau)

## O que é
Artigo sobre molas e quiques em CSS puro com `linear()`. Complementa a pasta de molas CSS da skill oficial do Motion.

## O que aproveitamos
- Só ideias, reescritas com nossas palavras.
- Colocar a mola `linear()` dentro de `@media (prefers-reduced-motion: no-preference)`.
- Fallback com `@supports (animation-timing-function: linear(0, 1))`, trocando uma variável CSS.
- Quantidade de pontos: cerca de 50 convencem; cerca de 11 parecem robóticos.
- `linear()` não carrega velocidade numa interrupção.

## O que NÃO aproveitamos e por quê
- Nenhum texto ou código copiado (sem licença que permita).

## Riscos e cuidados
- Licença restrita (sem licença aberta): não copiar.

## Transcrição: o que aprendemos (nas nossas palavras)
- Uma mola em CSS é uma curva `linear()` com muitos pontos; poucos pontos denunciam a artificialidade.
- Como a curva é fixa, ela não herda a velocidade de um gesto interrompido: para gesto, use mola do Motion.
- Sempre proteja com reduced motion e com `@supports`.

## Fontes
- https://www.joshwcomeau.com/animation/linear-timing-function/
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.10 e tabela (fonte 7))`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.9)`; `skills/design/motion-oficial/ORIGEM.md`
