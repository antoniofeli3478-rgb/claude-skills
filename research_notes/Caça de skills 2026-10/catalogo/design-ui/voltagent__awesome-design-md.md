---
nome: "awesome-design-md (Awesome Design)"
repositorio: "VoltAgent/awesome-design-md"
caminho_no_repo: "design-md/<marca>/DESIGN.md"
autor: "VoltAgent"
licenca: "MIT (Copyright (c) 2026 VoltAgent). Marcas, logos e fontes continuam sendo dos donos"
instalacoes: "120,4 mil estrelas (não é skill; sem contagem oficial de instalações)"
commit_ou_versao: "13be5c0 (05/10/2026)"
data_verificada: "2026-10-12"
categoria_nossa: "design"
nota: "sem nota"
status: "instalada-intacta"
onde_entrou: "skills/design/estilos-premium/references/marcas/ (15 de 74 fichas, sem alteração). Fatos pontuais de 16 marcas em skills/design/ui-ux-pro-max/references/. As outras 59 estão como linha de download em skills/sites/referencia-sites (baixar só a ficha da marca escolhida)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md", "research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md", "research_notes/Caça de skills 2026-10/refero-estilos/RESUMO.md", "skills/design/estilos-premium/ORIGEM.md", "skills/design/ui-ux-pro-max/ORIGEM.md"]
---
# awesome-design-md (VoltAgent / Awesome Design)

## O que é
Coleção com 74 fichas `DESIGN.md` de marcas conhecidas (o README ainda diz 73): cores, fontes, tamanhos e regras de sites como Apple, BMW, Ferrari, Lamborghini, Nike, SpaceX, Tesla, Linear, Vercel e Stripe, no formato do Google Stitch. Não há Samsung. São análises "inspiradas", não guias oficiais. Item 3 da lista do Daniel. É a alternativa com licença livre ao Refero.

## O que aproveitamos
- 15 fichas copiadas sem alteração (só o nome do arquivo mudou) para a `estilos-premium`: apple, bmw, ferrari, lamborghini, bugatti, tesla, nike, spacex, linear, vercel, stripe, notion, superhuman, raycast e framer. Com o LICENSE MIT ao lado.
- Fatos pontuais de 16 marcas (hex, tamanhos, pesos, raios) na receita "tipo Apple" e no `minimalismo.md` da `ui-ux-pro-max`.
- Comando para baixar a ficha de UMA marca para dentro do projeto (nunca para o `claude-skills`).
- Procedência conferida: os arquivos não citam o Refero, não têm links, scripts nem instruções embutidas.

## O que NÃO aproveitamos e por quê
- As outras 59 fichas ficam de fora (BMW M, PlayStation, Spotify, Airbnb, ElevenLabs, Cursor etc.). Proposta: uma linha no `fontes.txt` do `referencia-sites` e uma seção F com dois comandos testados para baixar uma ficha só (`curl` do arquivo no commit fixado, 24 KB, ou `git sparse-checkout`, 44 KB).
- O script `baixar-referencia.sh` baixa a coleção inteira (2,9 MB); por isso não foi alterado nem usado para ficha única.
- O README faz propaganda de kits pagos do getdesign.md (só link).

## Riscos e cuidados
- Marcas, logos, fotos e fontes são dos donos; as fichas podem estar defasadas. Conferir cores no site real.
- Acento do Linear diverge entre capturas de datas diferentes (lavanda `#5e6ad2` aqui; lima `#e4f222` em outra fonte): conferir no site.
- Só texto; nenhuma execução.

## Transcrição: o que aprendemos (nas nossas palavras)
- Um DESIGN.md bom junta valores exatos com explicação de quando usar cada um.
- O formato é um README do design system para agentes: cabeçalho YAML com tokens e seções de prosa.
- Para "tipo Apple" ou "tipo Linear", baixe a ficha da marca para dentro do projeto e use como ponto de partida, não como cópia.
- A ficha de marca é referência; a identidade do projeto continua no `DESIGN.md` do projeto.

## Fontes
- https://github.com/VoltAgent/awesome-design-md e https://getdesign.md
- research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md (item 3)
- research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md (seção 1)
- skills/design/estilos-premium/ORIGEM.md e skills/design/ui-ux-pro-max/ORIGEM.md
