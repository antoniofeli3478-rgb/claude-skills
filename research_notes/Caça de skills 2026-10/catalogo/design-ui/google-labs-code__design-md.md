---
nome: "design.md (padrão aberto do Google e CLI @google/design.md)"
repositorio: "google-labs-code/design.md"
caminho_no_repo: "especificação e CLI (npm @google/design.md)"
autor: "Google Labs (Stitch)"
licenca: "Apache-2.0"
instalacoes: "não verificado (a skill parente google-labs-code/stitch-skills@design-md tem 63,4 mil instalações)"
commit_ou_versao: "commit 9bf8eae (27/07/2026); estado alpha; CLI 0.4.0 testado em 11/10/2026"
data_verificada: "2026-10-11"
categoria_nossa: "design"
nota: "sem nota"
status: "ideias-aproveitadas"
onde_entrou: "skills/design/ui-ux-pro-max/references/modelo-DESIGN.md e SKILL.md (etapa 4: lint e export)"
fontes_locais: ["research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md", "research_notes/Caça de skills 2026-10/ui-ux-pro-max/TRANSCRICAO.md", "skills/design/ui-ux-pro-max/ORIGEM.md", "skills/design/ui-ux-pro-max/APRENDIZADOS.md"]
---
# design.md (Google) e CLI

## O que é
O DESIGN.md virou um padrão aberto: um arquivo com cabeçalho YAML (cores, tipografia, cantos, espaços, componentes) seguido de 8 seções fixas em inglês (Overview, Colors, Typography, Layout, Elevation & Depth, Shapes, Components, Do's and Don'ts). O CLI `npx @google/design.md` confere o contraste, compara versões e exporta o tema para Tailwind v4. Achado-chave da pesquisa B. Seções desconhecidas são preservadas, então dá para acrescentar Movimento e Acessibilidade.

## O que aproveitamos
- O formato do arquivo (YAML mais seções na ordem da spec). O texto do modelo é nosso, em português, com as seções extras Movimento, Acessibilidade, Composição, Decision ledger e Decisões em aberto.
- Comandos: `lint` (contraste WCAG, referências quebradas), `diff` e `export --format css-tailwind|dtcg`.
- Testado: `npx -y @google/design.md@0.4.0 lint` no nosso modelo deu 0 erros e 0 avisos, e o `export` gerou o `@theme` do Tailwind v4.
- Pegadinhas já anotadas: no YAML use um único nome de fonte (pilha com vírgulas vira uma fonte só e gera CSS inválido); o lint avisa se faltar uma cor chamada `primary`.

## O que NÃO aproveitamos e por quê
- Nenhum arquivo da spec foi copiado; só o formato e os comandos.
- A skill do Stitch (`stitch-skills@design-md`) não foi lida; ver ficha própria.

## Riscos e cuidados
- Estado alpha: o formato pode mudar. Fixar a versão do CLI (0.4.0).
- `npx` baixa um pacote do npm; rodar só dentro da pasta do projeto. Telemetria do CLI: não verificado.

## Transcrição: o que aprendemos (nas nossas palavras)
- Padrão aberto reduz o risco de ficarmos presos a um formato nosso.
- O DESIGN.md tem duas camadas: valores (tokens) e prosa (por quê e quando).
- O `lint` pega erro de contraste antes de virar código.
- O `export` transforma o arquivo em tema pronto de Tailwind, sem digitar tudo à mão.
- Nomes de cor devem ser pelo propósito (`canvas`, `action`), não pela cor.

## Fontes
- https://github.com/google-labs-code/design.md e npm `@google/design.md`
- research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md (seções 0 e 1) e TRANSCRICAO.md (decisões do transcritor)
- skills/design/ui-ux-pro-max/ORIGEM.md e APRENDIZADOS.md
