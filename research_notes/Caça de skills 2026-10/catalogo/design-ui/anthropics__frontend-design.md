---
nome: "frontend-design"
repositorio: "anthropics/skills"
caminho_no_repo: "skills/frontend-design"
autor: "Anthropic"
licenca: "Apache-2.0 (LICENSE.txt dentro da pasta da skill)"
instalacoes: "975,5 mil instalações (skills.sh, 12/10/2026); 180,6 mil estrelas no repositório"
commit_ou_versao: "dbd4588 (09/10/2026)"
data_verificada: "2026-10-12"
categoria_nossa: "design"
nota: 9
status: "instalada-intacta"
onde_entrou: "skills/design/frontend-design/ (cópia intacta) e ideias em skills/design/ui-ux-pro-max/ (SKILL.md e references/anti-padroes-ia.md) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md", "research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md", "research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md", "skills/design/ui-ux-pro-max/ORIGEM.md"]
---
# frontend-design (Anthropic)

## O que é
A skill oficial da Anthropic para fazer interface com cara própria, sem o visual genérico de IA. É curta (cerca de 1.500 palavras, 9 KB) e só lê, não executa nada. É a skill de design mais instalada do catálogo. O texto atual é uma reescrita: plano de design, revisão contra o briefing e lista dos 5 visuais típicos de IA.

## O que aproveitamos
- Plano em duas passadas antes do código: 4 a 6 cores nomeadas, famílias de fonte e seus papéis, layout (frase mais desenho em ASCII) e princípios. Depois, revisar o plano contra o briefing e dizer o que mudou.
- "Gaste a ousadia num lugar só" e, no fim, "tire um acessório".
- Os 5 visuais típicos de IA (creme com serifa e terracota; quase preto com acento ácido; estilo jornal com linha fina e raio 0; kit de cards SaaS; rótulos em caixa alta espaçada com setinhas). Foram para `references/anti-padroes-ia.md`.
- Um momento de movimento bem orquestrado, em vez de fade-up em tudo.
- Regras de texto de interface (ponto de vista do usuário, voz ativa, botão específico, mesma palavra no fluxo todo, erro que explica como resolver).
- Entrou na etapa de revisão do plano do fluxo de 6 etapas da `ui-ux-pro-max`. Nenhum trecho literal foi copiado.

## O que NÃO aproveitamos e por quê
- Ela não traz tabelas de números nem receitas de código. Os números vêm de outras fontes (Apple, Material, awesome-design-md).
- Cópia intacta ainda não está na `main`. O ganho de instalar é pequeno, já que as ideias estão na `ui-ux-pro-max`, mas ela é barata e é o texto oficial.

## Riscos e cuidados
- Quase nenhum: curta, só lê, sem telemetria nem chave.
- Licença Apache-2.0: ao copiar trechos, manter o aviso e dizer o que mudou. O README do repositório trata esta skill como código aberto (só as de documentos são "source-available").

## Transcrição: o que aprendemos (nas nossas palavras)
- Planejar antes de codar: um plano compacto de cor, tipo, layout e princípios.
- Revisar o plano contra o briefing: se serviria para qualquer página parecida, refazer.
- Premium não pode virar receita fixa. Até "escuro com acento ácido" e "jornal com raio 0" já são cara de IA.
- Escolha uma ou duas famílias de fonte; se duas, bem diferentes.
- Gaste a ousadia num ponto só e tire um enfeite no final.
- Depois de construir, tire screenshot e critique.
- Texto de interface: palavra única por ação, erro útil, tela vazia como convite.

## Fontes
- https://github.com/anthropics/skills/tree/main/skills/frontend-design
- https://skills.sh/anthropics/skills/frontend-design
- research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md (seções 2, 3.1, 3.8, 3.9) e pesquisa-B.md (seção 1)
- research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md (item 8)
- skills/design/ui-ux-pro-max/ORIGEM.md
