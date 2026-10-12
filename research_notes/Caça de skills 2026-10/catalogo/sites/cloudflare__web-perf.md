---
nome: "web-perf"
repositorio: "cloudflare/skills"
caminho_no_repo: "não verificado"
autor: "Cloudflare"
licenca: "Apache-2.0"
instalacoes: "96,3 mil (skills.sh find, 11/10/2026)"
commit_ou_versao: "c430dd9 (10/10/2026; só o início do hash consta na pesquisa)"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: 6
status: "so-referencia"
onde_entrou: "—"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-A.md (tabela e 'Não recomendadas agora')", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md (ficaram só como referência)", "research_notes/Caça de skills 2026-10/sites/RESUMO.md"]
---
# web-perf

## O que é
Skill da Cloudflare para analisar o **desempenho** de um site usando o Chrome DevTools MCP. Exige esse MCP instalado.

## O que aproveitamos
- Nada copiado. A ideia de medir desempenho com o Chrome DevTools já aparece no pacote web-quality do Addy Osmani, que entra na proposta.

## O que NÃO aproveitamos e por quê
- O pacote do Addy (ficha `addyosmani__web-quality-skills`) cobre o mesmo assunto e ainda tem um caminho alternativo sem o MCP (a ferramenta Lighthouse ou `scripts/analyze.sh`).
- Exige o Chrome DevTools MCP (`chrome-devtools-mcp@latest`). MCP só com aprovação do Daniel.
- RESUMO.md: a de desempenho da Cloudflare ficou entre as "boas, mas que não fazem falta agora".

## Riscos e cuidados
- Depende do Chrome DevTools MCP, instalado via `@latest` (sem versão fixa), e do Chrome.
- Outros riscos (rede, scripts): não verificado.
- Só instalar com o "sim" do Daniel.

## Transcrição: o que aprendemos (nas nossas palavras)
- Pelo resumo da pesquisa, ela depende do Chrome DevTools MCP para medir o desempenho.
- Nota 6, abaixo do pacote do Addy (9), que mede e também ensina a corrigir.
- Licença Apache-2.0 e 96,3 mil instalações; commit de 10/10/2026.
- Detalhes internos: não verificado.

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-A.md
- research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md
- research_notes/Caça de skills 2026-10/sites/RESUMO.md
