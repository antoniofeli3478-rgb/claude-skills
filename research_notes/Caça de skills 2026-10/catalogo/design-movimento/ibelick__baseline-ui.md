---
nome: "baseline-ui"
repositorio: "ibelick/ui-skills (https://github.com/ibelick/ui-skills)"
caminho_no_repo: "não verificado"
autor: "Julien Thibeaut (ibelick)"
instalacoes: "22 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "d036a68 (11/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "\"8 (motion B, grupo ui-skills) / 6 (botões A)\""
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/SKILL.md (regra de feedback até 200 ms)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (tabela (fonte 2) e §5)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (tabela 2 e §3.2, §4 (conflitos))", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.2, §4)", "skills/design/button-animation/ORIGEM.md", "skills/design/motion-oficial/LICENSES/ibelick-ui-skills-MIT.txt"]
---
# baseline-ui

## O que é
Regras de base para interface feitas pelo ibelick. A pesquisa só aproveita o que fala de movimento: teto de duração e posição contra curvas e gradientes personalizados.

## O que aproveitamos
- Regra de feedback de interação: "NEVER exceed `200ms` for interaction feedback" (pesquisas A de botões e B de motion). Entrou como "feedback até 200 ms".

## O que NÃO aproveitamos e por quê
- "NEVER introduce custom easing curves unless explicitly requested" conflita com o Emil ("curvas nativas são fracas"). Valeu o Emil, mais a regra "se o projeto já tem tokens de curva, use os do projeto".
- "NEVER use gradients unless explicitly requested": não muda nada para o Rotina, porque o anel de gradiente é um efeito pedido e intencional. Vale a nota de usar o anel como foco ou destaque, não como enfeite.

## Riscos e cuidados
- Só texto; não executa nada. Lida como dado.
- Cuidado: regras absolutas ("NEVER") podem bloquear efeitos que o Daniel pede de propósito.

## Transcrição: o que aprendemos (nas nossas palavras)
- Feedback de clique ou toque: no máximo 200 ms.
- Curvas: a regra do ibelick evita invenção, mas as curvas padrão do CSS são fracas; por isso adotamos tokens fortes nomeados e deixamos o projeto mandar quando já tem os dele.
- Gradientes: só quando pedido; no nosso caso o pedido existe.

## Fontes
- https://skills.sh/ibelick/ui-skills/baseline-ui
- https://github.com/ibelick/ui-skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (tabela (fonte 2) e §5)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (tabela 2 e §3.2, §4 (conflitos))`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.2, §4)`; `skills/design/button-animation/ORIGEM.md`; `skills/design/motion-oficial/LICENSES/ibelick-ui-skills-MIT.txt`
