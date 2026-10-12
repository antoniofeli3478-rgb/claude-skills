---
nome: "Remotion skills oficiais (remotion-markup, remotion-create)"
repositorio: "remotion-dev/skills (https://github.com/remotion-dev/skills)"
caminho_no_repo: "não verificado"
autor: "Remotion"
licenca: "sem arquivo LICENSE no repositório (licença do Remotion): só ideias"
instalacoes: "não verificado"
commit_ou_versao: "32b241b (07/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "video"
nota: 6
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/SKILL.md (seção Vídeo) e references/coreografia.md §5"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.12 e tabela (fonte 9))", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.9)", "skills/design/motion-oficial/ORIGEM.md"]
---
# Remotion skills oficiais (remotion-markup, remotion-create)

## O que é
As skills oficiais do Remotion, a ferramenta de vídeo do Daniel. O que importa: dentro de uma composição Remotion, CSS transition, classes `animate-*` do Tailwind e o Motion NÃO renderizam corretamente.

## O que aproveitamos
- Só a direção, reescrita: dirigir tudo por `useCurrentFrame()` + `interpolate()` / `spring()`.
- Traduções práticas: `Easing.bezier(0.23, 1, 0.32, 1)` no `interpolate()`; `spring()` do Remotion usa `{ stiffness, damping, mass }`; converter ms em quadros com `fps` de `useVideoConfig()` (250 ms a 30 fps são cerca de 8 quadros).
- Durações, curvas, coreografia e stagger das outras fontes valem para o vídeo.

## O que NÃO aproveitamos e por quê
- Nenhum texto copiado (sem LICENSE no repositório).

## Riscos e cuidados
- Licença não aberta: só ideias. Nada executado.

## Transcrição: o que aprendemos (nas nossas palavras)
- Vídeo é renderizado quadro a quadro: não existe "tempo real" de CSS, então todo movimento é calculado a partir do número do quadro.
- A mesma curva e a mesma mola das UIs servem, desde que sejam calculadas pelo `interpolate()`/`spring()`.

## Fontes
- https://github.com/remotion-dev/skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.12 e tabela (fonte 9))`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.9)`; `skills/design/motion-oficial/ORIGEM.md`
