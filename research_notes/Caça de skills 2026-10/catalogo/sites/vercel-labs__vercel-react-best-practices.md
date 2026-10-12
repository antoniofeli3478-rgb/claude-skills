---
nome: "vercel-react-best-practices"
repositorio: "vercel-labs/agent-skills"
caminho_no_repo: "skills/react-best-practices (na nossa proposta a pasta se chama vercel-react-best-practices, igual ao name: do SKILL.md)"
autor: "Vercel"
licenca: "MIT (declarada no README, linhas 237-239, e no campo license: do SKILL.md; o repositório não tem arquivo LICENSE)"
instalacoes: "789,1 mil (skills.sh find, 11/10/2026)"
commit_ou_versao: "063bee94c3f4df8453406c830b0a7df0f2860278 (28/08/2026); skill versão 1.0.0"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: 9
status: "instalada-intacta"
onde_entrou: "skills/sites/vercel-react-best-practices/ (cópia intacta) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-A.md (tabela e seção 3.1)", "research_notes/Caça de skills 2026-10/sites/pesquisa-B.md (item 1 e 6)", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md (conferências)", "scratchpad/hunter/sites/PROPOSTA/skills/sites/vercel-react-best-practices/ORIGEM.md, APRENDIZADOS.md, LICENCA-NOTA.md"]
---
# vercel-react-best-practices

## O que é
Um livro de regras da Vercel para escrever código React e Next.js que carrega rápido. São 70 regras em 8 grupos, da mais grave para a mais leve. Cada regra tem um exemplo "errado e certo". Entra no passo "código" do combo de site.

## O que aproveitamos
- Entra **intacta** na proposta: `SKILL.md`, `AGENTS.md`, `README.md`, `metadata.json` e `rules/` (70 regras + 2 arquivos de apoio).
- Regras críticas que valem sempre: `Promise.all` em vez de `await` em fila, Suspense, sem "barrel imports", `next/dynamic`, analytics carregado depois da hidratação, autenticar server actions.
- Uso econômico: ler **só a regra necessária** em `rules/`. O `AGENTS.md` junta tudo (cerca de 13 mil palavras) e gasta muitos créditos.
- Onde entra: passo 5 "código" do `combo-site-premium` e na revisão antes de publicar.

## O que NÃO aproveitamos e por quê
- Só serve para React e Next.js. Para outro framework, não vale.
- Para dúvidas da **API do Next.js**, manda a documentação que vem dentro do pacote (Next 16.2 ou mais novo). Esta skill é só de **desempenho**.
- Animação não é com ela: a `motion-oficial` tem a revisão de desempenho de animação.
- Uma regra cita a biblioteca `better-all` (da Vercel). É opcional.
- As outras skills do mesmo repositório não foram instaladas: `web-design-guidelines` (já é fonte da `ui-ux-pro-max`), `composition-patterns`, `react-view-transitions`, `react-native-skills`, `vercel-optimize`, `vercel-cli-with-tokens` e `writing-guidelines`.

## Riscos e cuidados
- Só texto: sem script, sem binário, sem serviço pago, sem telemetria citada na pesquisa.
- A licença MIT está só no README e no cabeçalho, sem arquivo LICENSE. Foi anotado em `LICENCA-NOTA.md` na proposta. Conferir de novo ao atualizar.
- Pasta grande (76 arquivos), mas é só texto.
- Nada é instalado sem o "sim" do Daniel.

## Transcrição: o que aprendemos (nas nossas palavras)
- O maior ganho de velocidade em React vem de **não esperar uma coisa de cada vez**: buscas de dados em paralelo (`Promise.all`) e telas que aparecem aos poucos (Suspense).
- O segundo ganho é **mandar menos JavaScript** ao navegador: importar direto do arquivo certo (sem "barrel"), carregar peças pesadas só quando precisam (`next/dynamic`) e deixar o analytics para depois da hidratação.
- No servidor: guardar resultado repetido (`React.cache`), fazer trabalho que não bloqueia a resposta com `after()` e sempre conferir quem pode chamar uma server action.
- Depois vêm re-render, renderização e JavaScript puro, que são ganhos menores.
- Os 8 grupos estão em ordem de impacto: comece pelo topo.
- Pesquisa B notou que o Next 16.2+ traz a documentação dentro do pacote e que, nos testes da Vercel, ela acertou 100% das tarefas contra no máximo 79% das skills de conhecimento. Por isso esta skill fica só no tema desempenho.

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-A.md
- research_notes/Caça de skills 2026-10/sites/pesquisa-B.md
- research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md
- https://github.com/vercel-labs/agent-skills (link citado nas pesquisas; não reaberto)
