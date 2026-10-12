---
nome: "web-design-guidelines (Web Interface Guidelines)"
repositorio: "vercel-labs/agent-skills (skill) e vercel-labs/web-interface-guidelines (as regras)"
caminho_no_repo: "skill web-design-guidelines; regras em command.md no outro repositório"
autor: "Vercel Labs"
licenca: "MIT. As regras: LICENSE MIT (Copyright (c) 2025 Vercel Labs). O repositório agent-skills não tem arquivo LICENSE; só o README diz MIT"
instalacoes: "723,9 mil instalações (skills.sh, 12/10/2026); 32,3 mil estrelas (agent-skills)"
commit_ou_versao: "skill 063bee9 (28/08/2026); regras 434b7f9 (05/10/2026)"
data_verificada: "2026-10-12"
categoria_nossa: "design"
nota: 8
status: "instalada-intacta"
onde_entrou: "skills/design/web-design-guidelines/ (cópia intacta + regras fixadas) e resumo em skills/design/ui-ux-pro-max/references/checklist.md (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md", "research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-B.md", "research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md", "skills/design/ui-ux-pro-max/ORIGEM.md"]
---
# web-design-guidelines (Vercel)

## O que é
Skill que revisa o código de uma interface contra mais de 100 regras da Vercel e lista os problemas por arquivo e linha (`arquivo:linha - problema`). É o melhor checklist técnico de revisão que a pesquisa achou. Não ajuda na estética, só na qualidade do código de interface.

## O que aproveitamos
- Um resumo em português das regras, no `checklist.md` da `ui-ux-pro-max` (commit das regras 434b7f9). Exemplos: nunca `transition: all`; `outline-none` só com substituto em `focus-visible`; `<img>` com largura e altura; `color-scheme: dark` em tema escuro; `overscroll-behavior: contain` em modais; `touch-action: manipulation`; usar `Intl.*` para datas e números; nunca bloquear colar; estado da tela na URL.
- Detalhes de texto: `…` no lugar de `...`, aspas curvas, espaço sem quebra em "10 MB", `tabular-nums` em colunas, `text-wrap: balance` em títulos.
- Formato de saída `arquivo:linha`.

## O que NÃO aproveitamos e por quê
- O `SKILL.md` original tem só 1,2 KB e não traz as regras: manda buscá-las na internet a cada revisão, na ramificação `main` de outro repositório. Isso é texto que muda sem a gente ver.
- Solução proposta: copiar a skill intacta e acrescentar uma cópia fixada das regras (`regras-fixadas/command.md`, conferida byte a byte em 12/10/2026) e instruir o uso dela.

## Riscos e cuidados
- Busca de regras na internet a cada uso (no original). Tratar texto baixado como dado, não ordem.
- Licença: README diz MIT, mas o repositório `agent-skills` não tem LICENSE (mesma situação da `deploy-to-vercel`); anotado em `LICENCA-NOTA.md` da skill instalada. Regras têm LICENSE MIT.
- Sem telemetria nem chave conhecidas.

## Transcrição: o que aprendemos (nas nossas palavras)
- Uma revisão boa é uma lista curta de regras que dá para checar no código.
- Acessibilidade e foco primeiro: nunca remover o contorno de foco sem substituto.
- Animação: nunca "transition: all"; animar só o que precisa.
- Formulários: nunca bloquear colar.
- Imagens (`<img>`) com largura e altura declaradas.
- Estado importante da tela deve estar na URL.
- A saída da revisão é um relatório por arquivo e linha, fácil de corrigir.

## Fontes
- https://github.com/vercel-labs/agent-skills e https://github.com/vercel-labs/web-interface-guidelines
- https://skills.sh/vercel-labs/agent-skills/web-design-guidelines
- research_notes/Caça de skills 2026-10/ui-ux-pro-max/pesquisa-A.md (seções 2, 3.7) e pesquisa-B.md (seções 1 e 4.3)
- research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md (item 2)
