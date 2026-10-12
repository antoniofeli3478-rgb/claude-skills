---
nome: "interaction-design"
repositorio: "wshobson/agents (https://github.com/wshobson/agents)"
caminho_no_repo: "plugins/ui-design, references/microinteraction-patterns.md"
autor: "Seth Hobson (wshobson)"
instalacoes: "15,5 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "46891e7 (04/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "\"6,5 (botões A) / 6 (botões B)\""
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/SKILL.md (§3 Estados; §4 receita 3, botão com estados; receita 4, tremor de erro) e assets/status-button.tsx (escrito por nós)"
fontes_locais: ["research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§2 e §3.8)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.4, §2.11)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.5, §5)", "skills/design/button-animation/ORIGEM.md", "research_notes/Caça de skills 2026-10/button-animation/RESUMO.md"]
---
# interaction-design

## O que é
O único achado com o botão de estados completo: ocioso, carregando, sucesso, erro. A IDEIA é boa, mas o código tem vícios.

## O que aproveitamos
- Só a ideia do botão ocioso, carregando, sucesso e erro (voltando ao ocioso em 2 s) e do tremor de erro.
- Em cima dela escrevemos `assets/status-button.tsx` com troca de ícone do Jakub e bloqueio de duplo envio.

## O que NÃO aproveitamos e por quê
- O código importa de `framer-motion` (nosso padrão é `motion/react`), entra o ícone de `scale: 0, rotate: -180` (o Emil proíbe) e não tem movimento reduzido. Nada do código foi copiado.

## Riscos e cuidados
- Código de terceiros com bibliotecas antigas: não usar como está.
- Só texto na leitura; nada executado.

## Transcrição: o que aprendemos (nas nossas palavras)
- Botão com quatro estados: ocioso, carregando (desabilitado, com spinner e "Enviando…"), sucesso, erro; sucesso e erro voltam ao ocioso depois de ~2 s.
- O tremor de erro e o loading precisam de pista estática e de acessibilidade (`aria-busy`, anúncio em `aria-live`).
- Sempre prevenir o duplo envio.

## Fontes
- https://skills.sh/wshobson/agents/interaction-design
- https://github.com/wshobson/agents
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§2 e §3.8)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.4, §2.11)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.5, §5)`; `skills/design/button-animation/ORIGEM.md`; `research_notes/Caça de skills 2026-10/button-animation/RESUMO.md`
