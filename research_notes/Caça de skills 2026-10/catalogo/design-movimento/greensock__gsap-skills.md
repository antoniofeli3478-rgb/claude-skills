---
nome: "GSAP skills oficiais (gsap-performance, gsap-timeline, gsap-core)"
repositorio: "greensock/gsap-skills (https://github.com/greensock/gsap-skills)"
caminho_no_repo: "não verificado"
autor: "GreenSock (oficial do GSAP)"
instalacoes: "60 mil+ (skills.sh, 11/10/2026, segundo a pesquisa A)"
commit_ou_versao: "aed9cfd (21/04/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "\"descartada (A) / 5 (B, só ideias de coreografia)\""
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/references/api-motion.md §6 (só a ideia de sequência com posição relativa)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§1 (descartadas sem clonar))", "research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.8 e tabela (fonte 8))", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.9)", "skills/design/motion-oficial/ORIGEM.md"]
---
# GSAP skills oficiais (gsap-performance, gsap-timeline, gsap-core)

## O que é
Skills oficiais da biblioteca GSAP. É outra biblioteca: o nosso caminho é o Motion. A pesquisa A descartou; a B aproveitou só a ideia de coreografia.

## O que aproveitamos
- Sequenciar com timeline e "position parameter" (por exemplo `"-=0.2"` e labels) em vez de somar `delay`.
- No Motion, o equivalente é `animate([...sequência])` com `at` e `stagger()`, e `delayChildren`/`staggerChildren` em variants (sintaxe a conferir no codex antes de escrever).

## O que NÃO aproveitamos e por quê
- A biblioteca GSAP em si: fora do escopo. Só serviria para migração.

## Riscos e cuidados
- Nada executado. Licença MIT.

## Transcrição: o que aprendemos (nas nossas palavras)
- Em vez de encadear atrasos soltos, trate a animação como uma linha do tempo com pontos de entrada relativos ao que veio antes.

## Fontes
- https://github.com/greensock/gsap-skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§1 (descartadas sem clonar))`; `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.8 e tabela (fonte 8))`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.9)`; `skills/design/motion-oficial/ORIGEM.md`
