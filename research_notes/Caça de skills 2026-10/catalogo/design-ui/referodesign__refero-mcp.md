---
nome: "Refero MCP (servidor oficial)"
repositorio: "referodesign (servidor remoto https://api.refero.design/mcp; listado no MCP Registry como io.github.referodesign/refero)"
caminho_no_repo: "não se aplica (serviço remoto; o plugin e o .mcp.json vêm no repositório referodesign/refero_skill)"
autor: "Refero"
instalacoes: "não verificado"
commit_ou_versao: "não verificado (serviço remoto)"
data_verificada: "2026-10-11"
categoria_nossa: "mcp"
nota: "sem nota"
status: "so-referencia"
onde_entrou: "— (só mencionado como opção paga em skills/design/estilos-premium/ e skills/design/ui-ux-pro-max/)"
fontes_locais: ["research_notes/Caça de skills 2026-10/refero-estilos/RELATORIO-minimal.md", "research_notes/Caça de skills 2026-10/refero-estilos/RELATORIO-premium.md", "research_notes/Caça de skills 2026-10/refero-estilos/TRANSCRICAO.md", "skills/design/estilos-premium/ORIGEM.md"]
---
# Refero MCP (oficial)

## O que é
O caminho oficial para uma IA pesquisar no acervo do Refero (mais de 150 mil telas e mais de 6 mil fluxos de produtos reais). É um servidor remoto que o Claude Code liga por login (OAuth) ou token. É a única via permitida para pesquisa automática em volume.

## O que aproveitamos
- Nada foi instalado. Fica como opção para o Daniel, se um dia assinar.
- Ferramentas anotadas: `refero_search_site`, `refero_search_app` (iOS), `refero_search_styles`, `refero_get_style` (até 10 UUIDs; recomenda 3 a 4), `refero_search_screens`, `refero_get_screen`, `refero_get_similar_screens`, `refero_get_screen_image`, `refero_search_flows` e `refero_get_flow`.
- Comando de ligação (no computador do Daniel, não no container): `claude mcp add --transport http refero https://api.refero.design/mcp`, depois `/mcp` para entrar.

## O que NÃO aproveitamos e por quê
- Exige plano Pro, Team ou Lifetime (o Free não tem MCP), com limite de 8.000 chamadas por mês. Uso em produto ou alto volume pede plano Business.
- Sem a assinatura, a pesquisa usa links, prints e o awesome-design-md (MIT).

## Riscos e cuidados
- Pago. As buscas saem do computador para os servidores do Refero.
- O token nunca vai para o repositório (regra nossa; os termos do Refero também proíbem publicá-lo).
- Os termos proíbem robôs, raspagem e montar "repositório" ou dataset com o conteúdo; o robots.txt do site de estilos bloqueia robôs de IA. Não repetir coletas automáticas.
- Antes de os termos serem lidos, agentes baixaram bastante coisa; esse material fica só na pasta temporária e não deve ser salvo no repositório.
- A API pública e o CLI não estão documentados para uso livre.

## Transcrição: o que aprendemos (nas nossas palavras)
- O MCP dá ao agente busca de estilos, telas e fluxos reais, com imagens, de forma legal e com limite de uso.
- É o complemento "ao vivo" da skill oficial, que é só método.
- Servidor remoto: precisa de login/token e rede; não funciona offline.
- Para nós, o custo-benefício só aparece se o Daniel pesquisar referências com frequência.

## Fontes
- https://doc.refero.design (termos de uso, atualizados em 03/08/2026) e https://api.refero.design/mcp
- research_notes/Caça de skills 2026-10/refero-estilos/RELATORIO-minimal.md (seção 6) e TRANSCRICAO.md (seção 6)
- skills/design/estilos-premium/ORIGEM.md
