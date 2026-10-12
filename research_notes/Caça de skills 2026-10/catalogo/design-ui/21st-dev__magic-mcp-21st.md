---
nome: "Magic MCP (agora 21st MCP)"
repositorio: "21st-dev/magic-mcp (hoje só um repasse de compatibilidade) e @21st-dev/cli"
caminho_no_repo: "pacote @21st-dev/magic 0.2.3 (09/09/2026); CLI @21st-dev/cli 1.18.0"
autor: "21st.dev"
licenca: "ISC (o repasse magic-mcp); MIT (o CLI). Serviço e componentes: pagos, licença de cada componente é do autor (não verificado)"
instalacoes: "6,0 mil estrelas (magic-mcp); a skill 21st-ui tem 128 instalações"
commit_ou_versao: "CLI 1.18.0; pacote @21st-dev/magic 0.2.3 (09/09/2026)"
data_verificada: "2026-10-12"
categoria_nossa: "mcp"
nota: "sem nota"
status: "so-referencia"
onde_entrou: "skills/sites/mcps-para-sites/ (só descrito, com a recomendação de NÃO ligar por ora: pago e envia contexto à 21st.dev) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md"]
---
# Magic MCP (agora 21st MCP)

## O que é
Item 7 da lista do Daniel. Busca e gera componentes React da 21st.dev (cerca de 12 mil componentes). O "Magic MCP" mudou de nome para 21st MCP, a chave antiga parou de funcionar (as chaves foram zeradas) e o código de componente passou a ser pago.

## O que aproveitamos
- Só o manual com o comando para o computador do Daniel: `npx @21st-dev/cli@1.18.0 init --client claude` (mostra a configuração antes de gravar). Não foi instalado nem testado.
- As coleções gratuitas equivalentes (Magic UI, Kokonut UI, via shadcn) já estão no `referencia-sites`.

## O que NÃO aproveitamos e por quê
- Recomendação: não ligar por enquanto. Custo, envio de dados e dependência de serviço externo não compensam, e não verificamos a licença de cada componente do catálogo.
- Nunca depender do serviço no site: copiar o código para dentro do projeto.

## Riscos e cuidados
- Pago: planos lidos em 21st.dev/pricing: Free (2 cópias por dia), Builder US$ 6/mês no anual, Builder + AI de US$ 15 a US$ 60/mês, Team US$ 7,50 por pessoa. As fontes discordam entre si (uma página de ajuda deu erro 404): confirmar antes de pagar.
- Precisa de chave nova em 21st.dev/mcp. Guardar em variável de ambiente (no container, `/root/.config/rotina/`); nunca no repositório.
- Envia à 21st.dev e a provedores de IA (Anthropic e OpenAI) tudo que a IA escrever nos pedidos, a chave e, com `--context auto`, o contexto de design do projeto (cores, fontes, tecnologia). A política guarda prompts e código por até 12 meses e diz que não treina modelos próprios sem consentimento.
- Telemetria do CLI sem opção de desligar.

## Transcrição: o que aprendemos (nas nossas palavras)
- Nem todo MCP famoso continua do mesmo jeito: este mudou de nome, de chave e de preço.
- Gerar componente por IA num serviço externo significa mandar parte do projeto para fora.
- Para sites, as coleções abertas via shadcn dão o mesmo tipo de ganho sem conta.
- Antes de pagar, conferir a página de planos atual.

## Fontes
- https://21st.dev (mcp e pricing), https://github.com/21st-dev/magic-mcp
- research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md (item 7 e tabela dos três MCPs)
