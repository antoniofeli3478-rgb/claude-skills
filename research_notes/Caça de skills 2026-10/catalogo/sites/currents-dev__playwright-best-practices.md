---
nome: "playwright-best-practices"
repositorio: "currents-dev/playwright-best-practices-skill"
caminho_no_repo: "não verificado"
autor: "Currents (currents-dev)"
instalacoes: "92,3 mil (skills.sh find, 11/10/2026)"
commit_ou_versao: "283d5cb (21/07/2026; só o início do hash consta na pesquisa)"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: 7
status: "so-referencia"
onde_entrou: "—"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-A.md (tabela e seção 1)", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md (ficaram só como referência)", "research_notes/Caça de skills 2026-10/sites/RESUMO.md"]
---
# playwright-best-practices

## O que é
Skill de **testes de ponta a ponta robustos** com Playwright. Traz mais de 50 referências: organização dos testes (POM), CI, acessibilidade com axe-core e mocks (dados falsos para testar). Tem a segunda maior nota entre as que ficaram de fora (7).

## O que aproveitamos
- Nada copiado. Fica como referência para quando o Daniel tiver um site com muitos testes automáticos.
- Para ver o site e tirar prints, já basta a `webapp-testing` (ficha própria) e a ferramenta Playwright CLI (ficha de outra agente).

## O que NÃO aproveitamos e por quê
- O RESUMO.md lista "a de testes avançados com Playwright" entre as boas que não fazem falta agora. Pela nossa leitura, o fluxo atual só precisa de prints, cliques e leitura do console.
- Não está na proposta de `skills/sites/`.

## Riscos e cuidados
- Teste automático em CI e mocks envolvem mais ferramentas e Node; nada disso está instalado hoje no container (o Playwright nem está, segundo a pesquisa).
- Detalhes de execução e scripts: não verificado.
- Só instalar com o "sim" do Daniel.

## Transcrição: o que aprendemos (nas nossas palavras)
- A pesquisa a descreve como "testes E2E robustos": mais de 50 referências, POM, CI, axe-core, mocks.
- Tem 92,3 mil instalações e licença MIT; commit de 21/07/2026.
- Detalhes internos: não verificado.

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-A.md
- research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md
- research_notes/Caça de skills 2026-10/sites/RESUMO.md
