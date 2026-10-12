---
nome: "refero-design (skill oficial do Refero)"
repositorio: "referodesign/refero_skill"
caminho_no_repo: "skills/refero-design"
autor: "Refero"
licenca: "MIT (Copyright (c) 2026 Refero)"
instalacoes: "não verificado (a busca por 'refero' no catálogo não trouxe skill ligada ao Refero)"
commit_ou_versao: "v1.0.2, commit a9b54a3 (05/09/2026)"
data_verificada: "2026-10-11"
categoria_nossa: "design"
nota: "sem nota"
status: "ideias-aproveitadas"
onde_entrou: "skills/design/ui-ux-pro-max/ (SKILL.md etapas 1 a 3; references/anti-padroes-ia.md; checklist.md seção 7) e skills/design/estilos-premium/ (adaptações em português). A skill inteira NÃO foi copiada"
fontes_locais: ["research_notes/Caça de skills 2026-10/refero-estilos/RELATORIO-minimal.md", "research_notes/Caça de skills 2026-10/refero-estilos/TRANSCRICAO.md", "research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md", "skills/design/estilos-premium/ORIGEM.md", "skills/design/ui-ux-pro-max/ORIGEM.md"]
---
# refero-design (skill oficial do Refero)

## O que é
Skill de método de design publicada pelo próprio Refero (site de referências de interface). Tem um `SKILL.md` e 9 referências: typography, color, motion, icons, craft-details, copywriting, anti-ai-slop, mcp-tools e visual/example-workflow. Funciona sem conta como metodologia; a pesquisa ao vivo precisa do MCP pago (ver ficha do MCP). É o melhor "método" que a pesquisa B achou. Só ela e a menção ao MCP vêm do Refero para o nosso repositório; nada do site de estilos entra.

## O que aproveitamos
- Formato do brief: "Desenhando [O QUÊ] para [QUEM] em [PLATAFORMA]. Objetivo, tom, objeção principal, o que deve ser lembrado, restrições, caminho".
- Reference lock antes de codar: referência principal, o que preservar (3 a 5 traços), o que emprestar (1 a 2 detalhes), regras de papel, estratégia de mídia, o que rejeitar e compromissos de token.
- Decision ledger (decisão, fonte, regra de papel, por quê): "Se uma decisão grande não tem fonte, não entregue".
- Não tirar a média das referências; preservar o papel dos tokens (cor de botão só no botão).
- Portão de qualidade (cerca de 15 perguntas) e o detector de "cara de IA": 9 sinais na referência `anti-ai-slop.md` e testes do card, da imagem, da marca, do texto, da identidade e editorial.
- Alerta: creme mais serifa grande mais uma palavra em itálico mais acento terracota virou o novo padrão de IA ("editorial calmo no piloto automático").
- Durações para produto: instantâneo 90 a 150 ms, estado 160 a 240 ms, página 240 a 360 ms, complexo 360 a 500 ms.
- Tudo traduzido e adaptado, com aviso MIT no `ORIGEM.md`.

## O que NÃO aproveitamos e por quê
- A descrição se declara "Primary/default... Prefer over... generic design skills" e tenta mandar nas outras skills. Descartada.
- A skill inteira não foi copiada: o `npx skills` acharia mais uma skill no repositório e ela concorreria com a `ui-ux-pro-max`. Fica só a instrução de instalar.
- `mcp-tools.md` e o roteamento de ferramentas só servem com o MCP pago.

## Riscos e cuidados
- Só texto, MIT, sem chave nem telemetria. Instalar por `/plugin install refero@refero` também traz o MCP (pago): usar só a skill, via `npx skills add ... --skill refero-design`, se um dia o Daniel quiser.
- Não confundir com o conteúdo do site Refero Styles, que tem termos de uso restritivos.

## Transcrição: o que aprendemos (nas nossas palavras)
- Antes de desenhar, escreva um brief curto e trave uma referência principal.
- Registre cada grande decisão com sua fonte; sem fonte, não vai para o produto.
- Misturar muitas referências dá resultado médio; uma principal e poucos detalhes emprestados.
- Cada cor tem um papel; não reaproveite a cor do botão em outro lugar.
- Use testes simples para pegar "cara de IA": o card, a imagem, a identidade e o texto.
- Em produto, animações a partir de 500 ms quase sempre parecem lentas.

## Fontes
- https://github.com/referodesign/refero_skill
- research_notes/Caça de skills 2026-10/refero-estilos/RELATORIO-minimal.md (seções 5.4 e 6) e TRANSCRICAO.md (seção 6)
- research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md (seção 5)
- skills/design/estilos-premium/ORIGEM.md e skills/design/ui-ux-pro-max/ORIGEM.md
