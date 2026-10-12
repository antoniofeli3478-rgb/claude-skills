---
nome: "emil-design-eng"
repositorio: "emilkowalski/skills (https://github.com/emilkowalski/skills)"
caminho_no_repo: "skills/emil-design-eng/SKILL.md"
autor: "Emil Kowalski"
licenca: "MIT"
instalacoes: "340,6 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "e8a175d (02/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "10 (nota do grupo Emil, botões)"
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/SKILL.md (§1 Antes de animar, §2 Números, §4 receitas, §6 Desempenho, §7 Nunca entregue)"
fontes_locais: ["research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§1, §2, §3.1)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)", "research_notes/Caça de skills 2026-10/button-animation/RESUMO.md", "skills/design/button-animation/ORIGEM.md", "skills/design/button-animation/LICENCAS-TERCEIROS.txt"]
---
# emil-design-eng

## O que é
A skill mais instalada do Emil (340,6 mil). É a mais específica sobre botões e micro-interações: números exatos, curvas, tabela de durações, receitas e lista de "nunca faça".
Só foi avaliada pela pesquisa de botões. Inclui também um arquivo de desempenho (`performance-cheatsheet.md`; local exato não verificado).

## O que aproveitamos
- Porteiro de frequência e propósito (mesma tabela da `animate`) e a frase "Never animate keyboard-initiated actions."
- Tabela de durações por elemento e tokens de curva (`--ease-out`, `--ease-in-out`, `--ease-drawer`).
- Nunca `ease-in`, nunca `transition: all`, nunca `scale(0)`.
- Popover a partir do gatilho; segurar para confirmar (CSS); tooltip instantâneo depois do primeiro.
- Do cheatsheet de desempenho: CSS sob carga fica liso e o rAF do Motion perde quadros; variável CSS no pai recalcula todos os filhos; escrever em `ref.current.style`, não em state.
- Formato de revisão Antes / Depois / Por quê.

## O que NÃO aproveitamos e por quê
- Aviso de conflito: o Emil usa `duration` nas molas; a nossa prosa usa `visualDuration` (Motion 14).
- Curva e escala de ícone: começar de 0,9+ vale, mas o ícone pequeno com blur usa a exceção de 0,25 do Jakub Krehel.

## Riscos e cuidados
- Só texto; não executa nada sozinha. Lida como dado.
- Licença MIT: manter aviso de copyright.

## Transcrição: o que aprendemos (nas nossas palavras)
- Ações iniciadas pelo teclado e usadas centenas de vezes por dia não animam.
- Botão: resposta ao toque em 100–160 ms, escala 0,97 e curva `ease-out` forte.
- Trocar o rótulo do botão pode usar blur pequeno para esconder o crossfade (blur menor que 20 px; nossa regra: alvo 8 px).
- Spinner rápido faz o app parecer mais rápido (desempenho percebido).
- Para ações destrutivas, preencher uma barra ao segurar (2 s linear) e voltar rápido ao soltar (200 ms).
- Para tooltips: depois que um abre, os vizinhos abrem na hora.

## Fontes
- https://skills.sh/emilkowalski/skills/emil-design-eng
- https://github.com/emilkowalski/skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§1, §2, §3.1)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)`; `research_notes/Caça de skills 2026-10/button-animation/RESUMO.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/button-animation/LICENCAS-TERCEIROS.txt`
