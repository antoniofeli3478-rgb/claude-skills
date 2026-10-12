---
nome: "playwright-cli"
repositorio: "microsoft/playwright-cli"
caminho_no_repo: "skill playwright-cli (SKILL.md e 10 arquivos de referência, uns 70 KB, sem binários); programa @playwright/cli"
autor: "Microsoft"
licenca: "Apache-2.0 (Copyright Microsoft)"
instalacoes: "181,1 mil instalações (skills.sh, 12/10/2026); 14,0 mil estrelas"
commit_ou_versao: "b85c7a7 (28/09/2026); programa @playwright/cli 0.1.22"
data_verificada: "2026-10-12"
categoria_nossa: "sites"
nota: "sem nota"
status: "instalada-intacta"
onde_entrou: "skills/sites/playwright-cli/ (cópia intacta; o programa e o navegador são instalados por fora, com aprovação) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md"]
---
# playwright-cli (Microsoft)

## O que é
Item 5 da lista do Daniel. Deixa a IA usar um navegador por comandos curtos: prints, cliques, formulário, console, vídeo. A busca também mostrou `openai/skills@playwright` (7,9 mil instalações) e o `agent-browser` da Vercel (deixado de fora em 11/10/2026).

## O que aproveitamos
- Para o `combo-site-premium` (passo 9): prints em celular e computador (`open --mobile`, `resize`), tema claro e escuro, movimento reduzido (`set-reduced-motion`), cliques, formulário, console, vídeo, e `show --annotate` (o Daniel marca no site o que quer mudar).
- Regra de escolha anotada: `playwright-cli` para prints e cliques (gasta menos créditos); `webapp-testing` (Anthropic) quando precisar subir o servidor junto.
- Proposta: copiar a pasta inteira da skill e o LICENSE, intactos.

## O que NÃO aproveitamos e por quê
- Nada descartado, mas só entra com aprovação, e o programa fica fora do repositório.

## Riscos e cuidados
- O cabeçalho libera comandos sem perguntar (`allowed-tools`) e a skill tem `run-code` e `eval` (rodam JavaScript). Usar só no projeto do site.
- Nunca usar `attach --cdp=chrome` nem `attach --extension` (ligam ao Chrome pessoal do Daniel), nem `--persistent`/`--profile` com perfil pessoal.
- `state-save` grava cookies em arquivo: é como uma senha; guardar fora do repositório.
- O programa e o navegador não estão no container. Instalar baixa um navegador (centenas de MB): só na pasta do projeto ou no computador do Daniel, com o "sim" dele.
- Uma consulta por dia ao registro do npm (desligável com `NO_UPDATE_NOTIFIER=1`). Sem telemetria e sem chave.

## Transcrição: o que aprendemos (nas nossas palavras)
- Dá "olhos" ao agente: ele abre o site, tira print e confere o que construiu.
- Comandos curtos; a pesquisa anota que gasta menos créditos que a `webapp-testing` para prints e cliques.
- Testes visuais úteis: celular e desktop, claro e escuro, movimento reduzido.
- Gravar vídeo e anotar na tela ajudam o Daniel a apontar mudanças.
- A mesma ferramenta pode acessar o navegador pessoal; por isso existem regras de "nunca fazer".

## Fontes
- https://github.com/microsoft/playwright-cli
- https://skills.sh (catálogo; contagem de 12/10/2026)
- research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md (item 5)
