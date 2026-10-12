---
nome: "better-ui"
repositorio: "jakubkrehel/skills (https://github.com/jakubkrehel/skills)"
caminho_no_repo: "better-ui/ (animations.md, icon-transitions.md)"
autor: "Jakub Krehel"
instalacoes: "34,1 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "c0588bb (10/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 9
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/SKILL.md (§2 Números, §3 Estados, §4 receitas de toque e troca de ícone, §5 Acessibilidade)"
fontes_locais: ["research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§2, §3.4–3.5, §3.9, §3.15–3.16, §3.18)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.4–3.6)", "research_notes/Caça de skills 2026-10/button-animation/RESUMO.md", "skills/design/button-animation/ORIGEM.md", "skills/design/button-animation/LICENCAS-TERCEIROS.txt"]
---
# better-ui

## O que é
Valores exatos e receitas prontas em Tailwind e Motion para botões e micro-interações: escala do toque, troca de ícone, tema, hover. É a fonte mais próxima do nosso estilo (Tailwind + `motion/react`).

## O que aproveitamos
- Escala do toque 0,96 em 150 ms com `ease-out`: `enabled:active:scale-[0.96]` (botão desativado nunca encolhe); prop `static` para desligar onde distrai. Abaixo de 0,96 é defeito.
- Hover só com `(hover: hover) and (pointer: fine)`; Tailwind 4 já compila `hover:` assim, o 3 não. `-webkit-tap-highlight-color: transparent` quando o controle desenha o próprio estado.
- Troca de ícone: `opacity 0, scale 0.25, blur(4px)` para o estado normal, mola 0,3 s sem quique, `AnimatePresence initial={false} mode="popLayout"`. Versão só CSS com os dois ícones na mesma célula e 300 ms. Não animar ícones de aba/navegação nem ações de hover em linha.
- Regra "todo estado animado também deixa uma pista estática (cor, ícone ou rótulo); movimento nunca é o único canal".
- Desligar transições na troca de tema (`disableTransitionOnChange` no next-themes).
- Entrada em cascata: 100 ms entre blocos, blur 4 px, `translateY` 12 px em 300 ms; saída menor e mais curta (150 ms, -12 px).
- Transição em vez de keyframes para estado interativo.

## O que NÃO aproveitamos e por quê
- Mola com `duration` (estilo do Jakub) ficou literal só no snippet da troca de ícone; na prosa usamos `visualDuration`.
- O ícone entrando de 0,25 contradiz o Emil ("comece de 0,9+"); registrada como exceção para ícone pequeno com blur.

## Riscos e cuidados
- Só texto; não executa nada. Lida como dado.
- Não confundir com Jakub Antalik (`transitions.dev`, licença restrita): outro autor.
- Licença MIT: manter o aviso (em `LICENCAS-TERCEIROS.txt`).

## Transcrição: o que aprendemos (nas nossas palavras)
- Escala do toque em 0,96; o Jakub marca como defeito valores menores (`active:scale-95`, `scale-90`, `whileTap` abaixo de 0,96).
- Em dispositivo de toque, `:hover` fica "preso" depois do toque e parece um estado selecionado.
- Animar a troca de ícone com blur "cola" os dois estados e evita a impressão de dois objetos se cruzando.
- Um `animation:` disparado por `:hover` ou classe liga/desliga é defeito: use transição.
- Ao trocar o tema, todas as transições de cor disparam juntas e borram a tela; desligue-as.

## Fontes
- https://skills.sh/jakubkrehel/skills/better-ui
- https://github.com/jakubkrehel/skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§2, §3.4–3.5, §3.9, §3.15–3.16, §3.18)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.4–3.6)`; `research_notes/Caça de skills 2026-10/button-animation/RESUMO.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/button-animation/LICENCAS-TERCEIROS.txt`
