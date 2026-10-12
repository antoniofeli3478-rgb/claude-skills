---
nome: "next-dev-loop"
repositorio: "vercel/next.js"
caminho_no_repo: "skills/ (branch canary; pasta de skills do repositório do Next.js)"
autor: "Vercel (equipe do Next.js)"
licenca: "MIT (repositório vercel/next.js)"
instalacoes: "19,2 mil (skills.sh find, 11/10/2026)"
commit_ou_versao: "d8c1c1b (10/10/2026; só o início do hash consta na pesquisa)"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: 6.5
status: "so-referencia"
onde_entrou: "—"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-A.md (tabela e 'Não recomendadas agora')", "research_notes/Caça de skills 2026-10/sites/pesquisa-B.md (itens 2 e 3)", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md"]
---
# next-dev-loop

## O que é
Skill oficial do Next.js que **confere cada mudança no `next dev` em execução**, usando o canal `/_next/mcp` e o `agent-browser`. A pesquisa A a chamou de "ótima", mas ela só funciona com versões muito novas das ferramentas.

## O que aproveitamos
- Nada copiado. Ideia anotada: fechar o ciclo "mudar o código, olhar o resultado no navegador, ler os erros do servidor".
- Para o mesmo objetivo (ver erros do `next dev`), a pesquisa B cita o `next-devtools-mcp` (MCP; vai para a skill nossa `mcps-para-sites`, não para esta ficha).

## O que NÃO aproveitamos e por quê
- Exige **Next.js 16.3 ou mais novo com Turbopack** e o `agent-browser` na versão 0.31.1 ou mais nova. A própria skill manda parar se faltar algo.
- O `agent-browser` também ficou só como referência (ficha própria).
- Por isso ficou para depois, não está na proposta.

## Riscos e cuidados
- Depende de programas extras (agent-browser, Chrome). Nada instalado sem o "sim" do Daniel.
- Que dados o `/_next/mcp` expõe e para quem: não verificado (a pesquisa B diz que ele mostra erros, rotas, logs e metadados da página).

## Transcrição: o que aprendemos (nas nossas palavras)
- A equipe do Next.js mudou as skills do repositório antigo `vercel-labs/next-skills` para dentro do repositório do Next.js (pasta `skills/`).
- A `next-dev-loop` é uma skill de **tarefa**, não de ensino do framework: cada mudança é conferida contra o `next dev`.
- Nota 6,5: boa, mas com pré-requisitos pesados.
- Ver também a ficha `vercel__next-skills-outras` (otimização depois que o site está pronto).

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-A.md
- research_notes/Caça de skills 2026-10/sites/pesquisa-B.md
- https://github.com/vercel/next.js/tree/canary/skills (citado na pesquisa B; não reaberto)
