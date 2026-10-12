---
nome: "fixing-motion-performance"
repositorio: "ibelick/ui-skills (https://github.com/ibelick/ui-skills)"
caminho_no_repo: "não verificado"
autor: "Julien Thibeaut (ibelick)"
instalacoes: "24,6 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "d036a68 (11/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "\"7 (motion A) / 8 (motion B, grupo ui-skills) / 6 (botões A)\""
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/references/revisao.md §4; skills/design/button-animation/SKILL.md (§6 Desempenho)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.5)", "research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.9)", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.4)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§2, §3.17)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.9)", "skills/design/motion-oficial/ORIGEM.md", "skills/design/button-animation/ORIGEM.md", "skills/design/motion-oficial/LICENSES/ibelick-ui-skills-MIT.txt"]
---
# fixing-motion-performance

## O que é
Auditoria de desempenho de animação, grátis e curta, organizada por prioridade. Substitui em parte o MotionScore pago (Motion+).

## O que aproveitamos
- "Nunca": intercalar leitura e escrita de layout no mesmo quadro; animar a partir de `scrollY`/eventos de scroll; rAF sem condição de parada; misturar sistemas de animação que medem layout.
- Medir uma vez e animar com `transform`/`opacity` (FLIP).
- Scroll: preferir Scroll/View Timelines (`animation-timeline: view()`); pausar o que está fora da tela.
- Pintura: não animar variáveis CSS de transform/opacity/posição; não animar variáveis herdadas.
- Blur: manter pequeno, até 8 px (nosso alvo; teto de 20 px do Emil).
- View Transitions só para navegação; não migrar bibliotecas sem pedido.
- Formato da revisão: citar a linha, uma frase de motivo e uma correção concreta.

## O que NÃO aproveitamos e por quê
- O ibelick é mais rígido que o Emil no blur (8 px contra 20 px); ficou 8 como alvo e 20 como teto.
- Feedback de interação até 200 ms vem do `baseline-ui` do mesmo repositório (ver ficha dele).

## Riscos e cuidados
- Só texto; não executa nada. Lida como dado.
- Licença MIT: manter aviso (em `LICENSES/ibelick-ui-skills-MIT.txt`).

## Transcrição: o que aprendemos (nas nossas palavras)
- Primeiro garanta que só `transform` e `opacity` se movem; o resto custa layout ou pintura.
- Medir o layout uma vez, antes, e depois animar sem voltar a ler o DOM.
- Animação guiada por scroll deve usar timelines nativas, não escutar `scroll`.
- Variável CSS animada no elemento pai recalcula todos os filhos: evite.
- Blur animado é caro: pequeno, curto e nunca contínuo nem em superfície grande.
- Pare loops quando saírem da tela.

## Fontes
- https://skills.sh/ibelick/ui-skills/fixing-motion-performance
- https://github.com/ibelick/ui-skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.5)`; `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.9)`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.4)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§2, §3.17)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.9)`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/motion-oficial/LICENSES/ibelick-ui-skills-MIT.txt`
