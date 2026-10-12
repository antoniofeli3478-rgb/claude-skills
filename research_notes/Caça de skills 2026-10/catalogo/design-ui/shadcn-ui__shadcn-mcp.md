---
nome: "shadcn/ui MCP"
repositorio: "shadcn-ui/ui (comando 'shadcn mcp')"
caminho_no_repo: "pacote shadcn 4.21.4; documentação em ui.shadcn.com/docs/mcp"
autor: "shadcn (shadcn-ui)"
licenca: "MIT"
instalacoes: "125,4 mil estrelas no repositório; a skill 'shadcn' do mesmo projeto tem 372,3 mil instalações"
commit_ou_versao: "02cad61 (11/10/2026); shadcn 4.21.4"
data_verificada: "2026-10-12"
categoria_nossa: "mcp"
nota: "sem nota"
status: "ideias-aproveitadas"
onde_entrou: "skills/sites/mcps-para-sites/ (instruções para o computador do Daniel; a skill shadcn já cobre o mesmo pela linha de comando) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md", "research_notes/Caça de skills 2026-10/sites/pesquisa-B.md"]
---
# shadcn/ui MCP

## O que é
Item 6 da lista do Daniel. É um MCP (programa que a IA liga) que busca e instala componentes de qualquer registro shadcn. A CLI reconhece 435 registros da comunidade, entre eles @magicui, @launchui, @tailark, @aceternity, @kokonutui e @react-bits. Não é uma skill para copiar: o que fazemos é um manual nosso.

## O que aproveitamos
- Manual na skill nossa `mcps-para-sites`: comando para o computador do Daniel, dentro do projeto: `claude mcp add --scope project shadcn -- npx -y shadcn@4.21.4 mcp`. Também `npx shadcn@latest mcp init --client claude` aparece na pesquisa B.
- Não precisa de chave (registros públicos) e é grátis.

## O que NÃO aproveitamos e por quê
- Não traz nada que a skill `shadcn` (já na proposta de sites) não faça pela linha de comando `npx shadcn`. Fica como opção.

## Riscos e cuidados
- Instala código de terceiros no projeto: ler o componente antes de aceitar.
- Baixa pacote com `npx` (versão fixada no comando). Os pedidos vão aos registros listados no projeto.
- Nada a desligar (sem telemetria conhecida).

## Transcrição: o que aprendemos (nas nossas palavras)
- O MCP conecta a IA ao catálogo de componentes shadcn e instala por nome.
- Muitos registros comunitários (animados, de landing, de marketing) usam o mesmo comando.
- Regra de segurança: componente de terceiros é código de terceiros; ler antes.
- Para o nosso fluxo, a skill `shadcn` e a linha de comando já bastam.

## Fontes
- https://ui.shadcn.com/docs/mcp e https://github.com/shadcn-ui/ui
- research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md (item 6 e tabela dos três MCPs)
- research_notes/Caça de skills 2026-10/sites/pesquisa-B.md (item 5 da tabela)
