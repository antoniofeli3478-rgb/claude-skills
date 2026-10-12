---
nome: "animate"
repositorio: "emilkowalski/skills (https://github.com/emilkowalski/skills)"
caminho_no_repo: "skills/animate/ (SKILL.md e RECIPES.md)"
autor: "Emil Kowalski (criador do Sonner e do Vaul)"
instalacoes: "132,5 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "e8a175d (02/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 10
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/SKILL.md (ordem de trabalho e \"Nunca entregar\"); skills/design/motion-oficial/references/receitas.md e references/molas-e-curvas.md; skills/design/button-animation/SKILL.md (§1 Antes de animar, §2 Números, §4 receitas 5 e 6, §6 e §7)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.1)", "research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.1–3.7)", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.1)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§3.1–3.7, §3.10)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.1–2.2, §2.10)", "skills/design/motion-oficial/ORIGEM.md", "skills/design/button-animation/ORIGEM.md", "skills/design/motion-oficial/LICENSES/emilkowalski-skills-MIT.txt"]
---
# animate

## O que é
É a skill do Emil Kowalski que ensina a DECIDIR o movimento de uma interface: se algo deve animar, por quê, com qual curva e duração.
É a fonte mais citada do setor (Kyle Zantos e Matthew Blode derivam dela) e a espinha das nossas duas skills de animação. Vem com um arquivo de receitas prontas (`RECIPES.md`).

## O que aproveitamos
- Porta "deve animar?" por frequência de uso: 100+ vezes/dia = nunca anima; dezenas = quase imperceptível; ocasional = animação padrão; raro ou primeira vez = onde cabe o encanto.
- Propósito em uma palavra (feedback, consistência espacial, estado, evitar salto brusco, explicação, encanto). Regra da fonte: "Can't name it? Don't build it."
- Ferramenta mais barata: CSS transition, `@starting-style`, CSS animation, WAAPI e só por último o Motion.
- Curvas fortes com nome (`--ease-out`, `--ease-in-out`, `--ease-drawer`) e a regra de nunca usar `ease-in` em UI.
- Tabela de durações por elemento (clique 100–160 ms; tooltip 125–200; dropdown 150–250; modal/gaveta 200–500) e teto de 300 ms para UI.
- Física de entrada: nunca `scale(0)`; `transform-origin` no gatilho (exceto modal); clique com `scale(0.97)`.
- Transições (não keyframes) para o que dispara várias vezes; tempo assimétrico (devagar ao segurar, rápido ao soltar).
- Tabela "Never Ship" (13 linhas) como checklist final e receitas de botão, dropdown, tooltip, modal, gaveta, toast, acordeão, cascata, hold-to-confirm, aba, scroll reveal e arrastar para dispensar.

## O que NÃO aproveitamos e por quê
- `{ type: "spring", duration, bounce }` do Emil foi trocado por `visualDuration` (API oficial do Motion 14).
- "Use sempre a string `transform`" foi conciliado com a regra oficial: `transform` por padrão; `x/y/scale` quando há transforms compostos, MotionValues ou gestos.
- Receitas com `clip-path` (scroll reveal, indicador de aba) ficaram restritas pela regra 2 do Daniel (nenhum recorte no meio da tela): só quando a borda do recorte é borda real do componente. Em vídeo/promo, scroll reveal com `clip-path` é proibido.
- Teto de 300 ms: o feedback de interação ficou em até 200 ms (ibelick/Rauno); marketing e vídeo podem ir de 500 a 1000 ms.

## Riscos e cuidados
- É só texto e exemplos de código: não executa nada sozinha. Nas pesquisas o repositório foi clonado e lido como dado, sem rodar nada.
- Sem chave, conta ou plano pago. Telemetria: nenhuma citada nas pesquisas (não verificado a fundo).
- Licença MIT: copiar exige manter o aviso de copyright (guardado em `LICENSES/` e em `LICENCAS-TERCEIROS.txt`).
- As receitas novas ainda não foram testadas com TypeScript (aviso do `APRENDIZADOS.md` da motion-oficial).

## Transcrição: o que aprendemos (nas nossas palavras)
- Primeiro pergunte quantas vezes por dia a pessoa vê aquilo. Quanto mais frequente, menos (ou nenhum) movimento.
- Toda animação precisa ter um motivo que caiba em uma palavra; "encanto" só vale em momentos raros.
- Use a ferramenta mais simples que resolve; Motion só para molas, layout, saída e gestos.
- Só animar `transform` e `opacity` (e `clip-path` quando a borda do recorte é real); `height` só em acordeão.
- Entrar e sair usa `ease-out` forte; mover na tela usa `ease-in-out` forte; cor e hover usam `ease`; barra de progresso usa `linear`.
- Nunca começar do zero absoluto: partir de 0,9 a 0,97 com opacidade 0.
- Popover e dropdown nascem do gatilho; o modal fica centralizado.
- Se o usuário pode disparar de novo no meio, use transição (ela parte do valor atual); keyframes recomeçam do zero.
- "Reduced motion" é menos e mais suave, não zero. Hover só em `(hover: hover) and (pointer: fine)`.
- Para checar a sensação: tocar a 2–5 vezes a duração, ver quadro a quadro, testar no celular de verdade e olhar de novo no dia seguinte.

## Fontes
- https://skills.sh/emilkowalski/skills/animate
- https://github.com/emilkowalski/skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.1)`; `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.1–3.7)`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.1)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§3.1–3.7, §3.10)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.1–2.2, §2.10)`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/motion-oficial/LICENSES/emilkowalski-skills-MIT.txt`
