---
nome: "GRADIENT-BUTTON.txt (prompt original do componente Compose)"
repositorio: "nosso: skills/design/button-animation/original/GRADIENT-BUTTON.txt"
caminho_no_repo: "skills/design/button-animation/original/GRADIENT-BUTTON.txt"
autor: "sem autor declarado"
instalacoes: "—"
commit_ou_versao: "não verificado (arquivo guardado no repositório)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "sem nota"
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/assets/compose.tsx e compose-demo.tsx; SKILL.md §4 receitas 6 a 10 e 16 (popover com mola, anel de gradiente com @property, destaque deslizante, anel de progresso, pop, barra de rolagem) e §9"
fontes_locais: ["skills/design/button-animation/ORIGEM.md (Base nossa)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§6)", "skills/design/button-animation/APRENDIZADOS.md"]
---
# GRADIENT-BUTTON.txt (prompt original do componente Compose)

## O que é
Era a BASE antiga da skill `button-animation`: um prompt de integração de componente (shadcn + Tailwind + TypeScript) com o componente `Compose`. Não tem autor nem licença declarados. A skill atual adaptou o conteúdo para `motion/react`.

## O que aproveitamos
- As receitas do `Compose` (anel de gradiente com `@property`, popover com mola, destaque deslizante, anel de progresso, pop, barra de rolagem) e os arquivos `assets/compose.tsx` e `assets/compose-demo.tsx`.
- O bug do `{"\n"}` que tinha virado quebra de linha real (linha ~369) foi corrigido; conferido de novo em 11/10/2026 e os componentes passam no `tsc --strict` com `motion@14.0.0`, React 18.3.1 e TypeScript 5.

## O que NÃO aproveitamos e por quê
- O original importava de `framer-motion`; foi trocado por `motion/react`.
- Autor e licença desconhecidos: o arquivo original fica guardado só como registro, sem tratá-lo como fonte aberta.

## Riscos e cuidados
- Origem incerta: sem autor nem licença declarados.

## Transcrição: o que aprendemos (nas nossas palavras)
- Anel de gradiente girando atrás do botão, usando `conic-gradient` com `@property` (variável registrada, sem herdar).
- Popover com mola nascendo do gatilho; destaque deslizante em abas.
- O anel girando foi usado no vídeo Rotina v2 ("BAIXE AGORA"), com `rotate = quadro x 6°`.

## Fontes
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `skills/design/button-animation/ORIGEM.md (Base nossa)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§6)`; `skills/design/button-animation/APRENDIZADOS.md`
