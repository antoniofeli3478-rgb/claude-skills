---
nome: "Magic UI (shimmer-button, shine-border, ripple-button, rainbow-button)"
repositorio: "magicuidesign/magicui (https://github.com/magicuidesign/magicui)"
caminho_no_repo: "componentes shimmer-button, shine-border, ripple-button, rainbow-button, subscribe; skill própria em skills/magic-ui"
autor: "Magic UI"
instalacoes: "não verificado"
commit_ou_versao: "cdb348c (05/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 9
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/SKILL.md (§4: ripple, shimmer e borda de gradiente com máscara)"
fontes_locais: ["research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.5–2.9, §5)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)", "research_notes/Caça de skills 2026-10/button-animation/RESUMO.md", "skills/design/button-animation/ORIGEM.md", "skills/design/button-animation/LICENCAS-TERCEIROS.txt"]
---
# Magic UI (shimmer-button, shine-border, ripple-button, rainbow-button)

## O que é
Biblioteca de componentes com receitas "premium" de botão prontas, já em `motion/react` e Tailwind v4: brilho passando (shimmer), borda que brilha, onda no clique e botão arco-íris.

## O que aproveitamos
- Shimmer: feixe cônico (`--spread: 90deg`, `--speed: 3s`, `blur-[2px]`) dentro de um container que desliza; fundo interno recuado em `--cut` (0,05em) para só a borda brilhar; sombra interna que muda no hover e no active. Keyframes `shimmer-slide` e `spin-around` copiados (MIT).
- Borda de gradiente "de verdade" com `mask-composite: exclude`, pintando só a borda, com `motion-safe:animate-shine`.
- Ripple na posição do clique (cálculo de tamanho e posição; 600 ms, escala 0 a 2).
- Rainbow button: borda com `background-clip` em 3 camadas e brilho borrado embaixo (só menção).

## O que NÃO aproveitamos e por quê
- O original NÃO respeita movimento reduzido: foi acrescentado `motion-safe:` nas classes `animate-*`.
- Usa `transition-all` no shimmer e no rainbow: trocado por propriedades explícitas ao portar.
- Shimmer, rainbow e pulsating são loops infinitos: ficam só em landing page e vídeo, não em app de uso diário. Pulsating/rainbow não viraram receita própria.

## Riscos e cuidados
- Código de terceiros: foi copiado só CSS/JSX curto sob MIT, com aviso. Nada instalado nem executado (nem a skill `magic-ui`).
- A skill própria `magic-ui` do repositório não foi avaliada em detalhe.

## Transcrição: o que aprendemos (nas nossas palavras)
- O brilho passando é um feixe cônico girando dentro de um quadro que desliza; só a borda aparece porque o fundo interno é recuado.
- Para pintar só a borda de um elemento, use máscara com `mask-composite: exclude` em vez de uma camada maior atrás.
- A onda no clique nasce na posição do ponteiro, cresce e some em 600 ms; remova o elemento depois.
- Efeitos em loop são ótimos em vídeo e landing, ruins em app diário.

## Fontes
- https://github.com/magicuidesign/magicui
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.5–2.9, §5)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)`; `research_notes/Caça de skills 2026-10/button-animation/RESUMO.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/button-animation/LICENCAS-TERCEIROS.txt`
