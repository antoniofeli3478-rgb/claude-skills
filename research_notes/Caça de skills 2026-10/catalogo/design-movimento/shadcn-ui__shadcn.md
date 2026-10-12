---
nome: "shadcn (skill) e shadcn/ui Button (padrão de foco)"
repositorio: "shadcn-ui/ui (https://github.com/shadcn-ui/ui)"
caminho_no_repo: "não verificado"
autor: "shadcn"
instalacoes: "370,4 mil (skill shadcn, skills.sh, 11/10/2026)"
commit_ou_versao: "não verificado"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "5 (Button, botões B)"
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/SKILL.md (§5 Acessibilidade: foco visível)"
fontes_locais: ["research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§1)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§1 (fonte 14), §2.16)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)", "skills/design/button-animation/ORIGEM.md", "skills/design/button-animation/LICENCAS-TERCEIROS.txt"]
---
# shadcn (skill) e shadcn/ui Button (padrão de foco)

## O que é
Duas coisas na mesma ficha. A skill `shadcn` (370,4 mil) trata de componentes, não de animação: descartada. O componente Button (visto via Magic UI) tem o padrão de foco que adotamos.

## O que aproveitamos
- Padrão de foco: `outline-none focus-visible:ring-[3px] focus-visible:ring-ring/50` e estado `aria-invalid` (classes copiadas, MIT).

## O que NÃO aproveitamos e por quê
- A skill `shadcn` em si: foco em componentes, fora do tema de animação.

## Riscos e cuidados
- Nada executado. Licença MIT (aviso em `LICENCAS-TERCEIROS.txt`).

## Transcrição: o que aprendemos (nas nossas palavras)
- Foco visível com anel de 3 px e opacidade 50% da cor do anel; use `:focus-visible`, não `:focus`.
- Estado inválido também ganha anel.

## Fontes
- https://ui.shadcn.com
- https://github.com/shadcn-ui/ui
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§1)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§1 (fonte 14), §2.16)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)`; `skills/design/button-animation/ORIGEM.md`; `skills/design/button-animation/LICENCAS-TERCEIROS.txt`
