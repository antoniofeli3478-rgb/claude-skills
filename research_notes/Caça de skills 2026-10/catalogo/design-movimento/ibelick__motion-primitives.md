---
nome: "Motion Primitives (Magnetic, BorderTrail, GlowEffect, TextMorph)"
repositorio: "ibelick/motion-primitives (https://github.com/ibelick/motion-primitives)"
caminho_no_repo: "não verificado"
autor: "ibelick (Julien Thibeaut)"
instalacoes: "não verificado"
commit_ou_versao: "120f64f (28/09/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 8
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/SKILL.md (§4: botão magnético, troca de texto; cometa na borda para vídeo)"
fontes_locais: ["research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.7–2.8, §2.15)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)", "skills/design/button-animation/ORIGEM.md", "skills/design/button-animation/LICENCAS-TERCEIROS.txt"]
---
# Motion Primitives (Magnetic, BorderTrail, GlowEffect, TextMorph)

## O que é
Primitivas de animação para React com `motion/react`: botão magnético, rastro/cometa que percorre a borda, brilho e texto que se transforma letra a letra.

## O que aproveitamos
- Magnético: mola `{ stiffness: 26.7, damping: 4.1, mass: 0.2 }`, intensidade 0,6, raio de 100 px; o botão volta a 0 ao sair do raio.
- BorderTrail: um quadrado percorre o contorno com `offset-path: rect(0 auto auto 0 round 60px)`; `offsetDistance` de 0% a 100% em 5 s linear. Em vídeo Remotion, `offsetDistance` vira função do quadro.
- TextMorph: cada letra é um `motion.span` com `layoutId`, `AnimatePresence mode="popLayout"`, mola `stiffness 280, damping 18, mass 0.3`; `aria-label` no texto inteiro e `aria-hidden` nas letras.

## O que NÃO aproveitamos e por quê
- O Magnetic original escuta `mousemove` no `document` o tempo todo: ligar o ouvinte só durante o hover.
- Desligar com `useReducedMotion()` e com `(pointer: coarse)`. O efeito é decorativo: serve para landing page, não para app financeiro.

## Riscos e cuidados
- Código de terceiros sob MIT (JSX do BorderTrail e constante da mola copiados com aviso). Nada executado.
- Efeitos decorativos em loop: só landing e vídeo.

## Transcrição: o que aprendemos (nas nossas palavras)
- O "puxão" magnético é uma mola solta (baixa rigidez e baixo amortecimento) em x/y, proporcional à distância do cursor.
- Um cometa na borda usa o caminho do próprio retângulo arredondado; fica ótimo em vídeo de produto.
- Texto que se transforma: cada letra tem identidade (`layoutId`), então as letras comuns deslizam em vez de piscar.

## Fontes
- https://github.com/ibelick/motion-primitives
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.7–2.8, §2.15)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)`; `skills/design/button-animation/ORIGEM.md`; `skills/design/button-animation/LICENCAS-TERCEIROS.txt`
