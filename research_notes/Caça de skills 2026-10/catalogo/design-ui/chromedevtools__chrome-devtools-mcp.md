---
nome: "Chrome DevTools MCP"
repositorio: "ChromeDevTools/chrome-devtools-mcp"
caminho_no_repo: "pacote chrome-devtools-mcp; o repositório traz 7 skills próprias (úteis: debug-optimize-lcp e a11y-debugging; não copiadas)"
autor: "Chrome DevTools (Google)"
licenca: "Apache-2.0"
instalacoes: "53,3 mil estrelas; a skill 'chrome-devtools' tem 8 mil instalações"
commit_ou_versao: "f08dbe1 (09/10/2026); v1.10.1"
data_verificada: "2026-10-12"
categoria_nossa: "mcp"
nota: "sem nota"
status: "ideias-aproveitadas"
onde_entrou: "skills/sites/mcps-para-sites/ (instruções com as opções de privacidade; passo 10 do combo-site-premium) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md", "research_notes/Caça de skills 2026-10/sites/pesquisa-B.md"]
---
# Chrome DevTools MCP

## O que é
Item 9 da lista do Daniel. Liga a IA a um Chrome de verdade para medir desempenho (LCP, INP, CLS), rodar Lighthouse e ler console e rede. É o mais útil dos três MCPs da lista: fecha o passo "medir, corrigir, medir de novo" do combo de sites.

## O que aproveitamos
- Manual na skill nossa `mcps-para-sites`, com comando para o computador do Daniel e já com as flags de privacidade: `claude mcp add --scope user --env CHROME_DEVTOOLS_MCP_NO_UPDATE_CHECKS=1 chrome-devtools -- npx -y chrome-devtools-mcp@1.10.1 --isolated --no-usage-statistics --no-performance-crux --redact-network-headers`.
- Não precisa de chave e é grátis.
- Recomendação da pesquisa: ligar, no computador do Daniel, com essas flags.

## O que NÃO aproveitamos e por quê
- As 7 skills do repositório não foram copiadas, só listadas.
- Nunca usar `--autoConnect`, `--browserUrl` nem `--wsEndpoint` (ligam ao Chrome pessoal).

## Riscos e cuidados
- Manda estatísticas de uso ao Google por padrão e URLs de trace ao CrUX: desligar com `--no-usage-statistics` e `--no-performance-crux`; a consulta de versão ao npm, com a variável `CHROME_DEVTOOLS_MCP_NO_UPDATE_CHECKS=1`.
- Dá acesso total ao navegador e roda JavaScript na página. Só em perfil isolado (`--isolated`), com cabeçalhos de rede ocultados (`--redact-network-headers`).
- Ainda não foi instalado nem testado; os comandos vêm da documentação oficial e do código lido.

## Transcrição: o que aprendemos (nas nossas palavras)
- Dá para medir velocidade de verdade (não só olhar) com um Chrome controlado pela IA.
- Há telemetria ligada por padrão; ler as flags de privacidade antes de usar.
- Perfil isolado evita misturar o site em teste com os logins pessoais.
- O resultado alimenta o ciclo: medir, corrigir, medir de novo.

## Fontes
- https://github.com/ChromeDevTools/chrome-devtools-mcp
- research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md (item 9 e tabela dos três MCPs)
- research_notes/Caça de skills 2026-10/sites/pesquisa-B.md (item 14)
