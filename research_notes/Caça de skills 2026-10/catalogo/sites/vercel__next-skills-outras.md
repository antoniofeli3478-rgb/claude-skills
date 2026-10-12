---
nome: "Skills do Next.js, as outras (next-bundle-optimizer, next-cache-components-adoption/optimizer, next-partial-prefetching-adoption/optimizer e mais)"
repositorio: "vercel/next.js (antes vercel-labs/next-skills)"
caminho_no_repo: "skills/ (branch canary)"
autor: "Vercel (equipe do Next.js)"
instalacoes: "não verificado (só a next-dev-loop tem número: 19,2 mil)"
commit_ou_versao: "não verificado"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: "sem nota"
status: "so-referencia"
onde_entrou: "—"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-B.md (resumo ponto 1 e itens 1 a 3)", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md (ficaram só como referência)", "research_notes/Caça de skills 2026-10/sites/RESUMO.md"]
---
# Skills do Next.js (as outras) e a documentação dentro do pacote

## O que é
Depois que o repositório `vercel-labs/next-skills` avisou que "as skills se mudaram", as skills do Next.js passaram a ficar no próprio repositório do Next.js. Servem para **tarefas** (otimizar bundle, adotar cache e prefetch), não para ensinar o framework. Esta ficha agrupa as do grupo que não são a `next-dev-loop` (que tem ficha própria). Instala-se com `npx skills add vercel/next.js`.

## O que aproveitamos
- Nada copiado. A **lição principal** da pesquisa B vale para todo site em Next.js:
  - Desde o Next 16.2, a documentação vem dentro do pacote, em `node_modules/next/dist/docs/`.
  - O `create-next-app` cria um `AGENTS.md` e um `CLAUDE.md` que apontam para ela. A partir da 16.3 o `next dev` também mantém esse `AGENTS.md`.
  - Nos testes da Vercel, a documentação no pacote acertou 100% das tarefas, contra no máximo 79% das skills de conhecimento.
  - Consequência: **não instalar skill de "boas práticas de Next"**. Isso foi para o `APRENDIZADOS.md` da `vercel-react-best-practices`.
- Ficam como referência para otimizar **depois** que o site está pronto.

## O que NÃO aproveitamos e por quê
- As antigas `next-best-practices` e `next-upgrade` **deixaram de existir**: o conteúdo foi para a documentação e para o `npx @next/codemod@latest upgrade`.
- As outras citadas (`next-bundle-optimizer`, `next-cache-components-adoption`/`optimizer`, `next-partial-prefetching-adoption`/`optimizer`) são de otimização avançada; não fazem falta agora e não estão na proposta.

## Riscos e cuidados
- Não lidas em detalhe: scripts, rede e permissões das skills: não verificado.
- A pesquisa B anota que, na versão 16.4.0, o guia ainda cita `agentFeedback` e `agentUpgrade`; o `create-next-app` pode ser desligado com a opção `agentRules`.
- Só instalar com o "sim" do Daniel.

## Transcrição: o que aprendemos (nas nossas palavras)
- Em Next.js atual, a fonte de verdade sobre a API é a documentação que vem no pacote, não uma skill.
- As skills do Next.js foram reorganizadas: agora fazem tarefas específicas de otimização e de adoção de recursos (cache, prefetch).
- A pesquisa B as vê como boas para otimizar depois que o site está pronto.
- Existe também o `next-devtools-mcp` (MCP, MIT por `package.json`), que mostra erros, rotas e logs do `next dev`. Ele pertence à skill nossa `mcps-para-sites`.

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-B.md
- research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md
- https://nextjs.org/docs/app/guides/ai-agents (citado na pesquisa B; não reaberto)
- https://github.com/vercel/next.js/tree/canary/skills (citado; não reaberto)
