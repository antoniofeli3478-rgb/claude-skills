---
nome: "userinterface-wiki"
repositorio: "raphaelsalaja/userinterface-wiki (https://github.com/raphaelsalaja/userinterface-wiki)"
caminho_no_repo: "regras com prefixos `exit-`, `presence-`, `mode-`, `container-`, `nested-`, `duration-`"
autor: "Raphael Salaja"
instalacoes: "4,4 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "256a954 (17/03/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 7
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/references/api-motion.md §7"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.6)", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.5)", "skills/design/motion-oficial/ORIGEM.md", "skills/design/motion-oficial/LICENSES/raphaelsalaja-userinterface-wiki-MIT.txt"]
---
# userinterface-wiki

## O que é
Uma "wiki" com 152 regras pequenas no formato Incorreto / Correto. Para nós valem as regras do Motion sobre saída e presença (`AnimatePresence`), que a skill oficial não detalha.

## O que aproveitamos
- `key` estável (nunca índice) dentro de `AnimatePresence`; o condicional fica dentro dele.
- `useIsPresent` no componente filho; `disabled={!isPresent}` enquanto sai; `safeToRemove` ao terminar a limpeza.
- `AnimatePresence propagate` nos dois níveis quando aninhado.
- `mode="wait"` quase dobra a duração (corte pela metade); `mode="popLayout"` ao reordenar listas.
- Padrão de duas divs: nunca medir e animar o mesmo elemento (a de fora anima altura; a de dentro é medida com ResizeObserver).
- Durações: hover/clique 120–180 ms; pequenas mudanças de estado 180–260 ms.

## O que NÃO aproveitamos e por quê
- Conflito: "encurte a duração antes de mexer na curva" (Salaja) contra "curva primeiro" (Blode). Solução: parece lento, encurte a duração; parece robótico, mude a curva.
- As outras ~140 regras da wiki (não ligadas ao Motion) não foram usadas.

## Riscos e cuidados
- Só texto; não executa nada. Lida como dado.
- A sintaxe de `propagate` ainda deve ser conferida na documentação do Motion antes do primeiro uso (aviso do `APRENDIZADOS.md`).

## Transcrição: o que aprendemos (nas nossas palavras)
- Na saída de um elemento, o erro mais comum é `AnimatePresence` sem `key` ou com o condicional do lado de fora.
- Enquanto o elemento sai, desative os cliques dele.
- Se o filho precisa saber que está saindo, o hook de presença vai nele, não no pai.
- Lista que muda de ordem pede `popLayout`; troca de tela com `wait` dura quase o dobro.
- Altura automática se anima com duas divs: uma mede, outra anima.

## Fontes
- https://skills.sh/raphaelsalaja/userinterface-wiki/userinterface-wiki
- https://github.com/raphaelsalaja/userinterface-wiki
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.6)`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.5)`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/motion-oficial/LICENSES/raphaelsalaja-userinterface-wiki-MIT.txt`
