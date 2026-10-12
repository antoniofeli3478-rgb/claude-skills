---
nome: "emil-design-eng"
repositorio: "emilkowalski/skills"
caminho_no_repo: "emil-design-eng"
autor: "Emil Kowalski"
instalacoes: "não verificado (a pesquisa só dá a contagem do apple-design: 201 mil)"
commit_ou_versao: "e8a175d (02/10/2026)"
data_verificada: "2026-10-11"
categoria_nossa: "design"
nota: 9
status: "ideias-aproveitadas"
onde_entrou: "skills/design/ui-ux-pro-max/SKILL.md (revisão e movimento) e references/estilos-premium.md"
fontes_locais: ["research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md", "skills/design/ui-ux-pro-max/ORIGEM.md"]
---
# emil-design-eng (Emil Kowalski)

## O que é
Outra skill do repositório do Emil Kowalski, sobre engenharia de interface com atenção ao detalhe. A nota 9 da pesquisa vale para o conjunto (apple-design, emil-design-eng e pick-ui-library). Se a área de movimento tiver ficha própria desta skill, a dela prevalece para durações e curvas.

## O que aproveitamos
- Formato de revisão em tabela "Antes | Depois | Por quê".
- "A frequência decide se anima": ação repetida mais de 100 vezes por dia, ou disparada pelo teclado, não anima.
- Durações de apoio: clique em botão 100 a 160 ms, tooltip 125 a 200 ms, dropdown 150 a 250 ms, modal ou gaveta 200 a 500 ms.
- Três curvas nomeadas: ease-out `(0.23,1,0.32,1)`, ease-in-out `(0.77,0,0.175,1)` e gaveta `(0.32,0.72,0,1)`. Nunca `ease-in` em UI e nunca começar em `scale(0)` (usar 0.95 com opacidade 0).
- A pesquisa não separa qual dessas ideias vem de qual skill do repositório; os números são do repositório como um todo.

## O que NÃO aproveitamos e por quê
- Animação de componente em detalhe: já cobrida por `motion-oficial` e `button-animation`; a regra é apontar para elas, não duplicar.

## Riscos e cuidados
- MIT, só texto. Sem execução, chave ou telemetria conhecidos (não verificado individualmente).

## Transcrição: o que aprendemos (nas nossas palavras)
- Revisões ficam claras em formato Antes, Depois e Por quê.
- O movimento serve a quem usa: quanto mais frequente a ação, menos animação.
- Cada tipo de elemento tem uma faixa de duração própria; botão é o mais rápido, modal e gaveta os mais lentos.
- Use poucas curvas, nomeadas, em vez de inventar uma por componente.

## Fontes
- https://github.com/emilkowalski/skills
- research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md (seções 3.1 e 3.6)
- skills/design/ui-ux-pro-max/ORIGEM.md
