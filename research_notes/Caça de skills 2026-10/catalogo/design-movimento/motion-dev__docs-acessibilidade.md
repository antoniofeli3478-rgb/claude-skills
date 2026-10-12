---
nome: "Documentação do Motion: acessibilidade"
repositorio: "https://motion.dev/docs/react-accessibility (documentação, não repositório)"
caminho_no_repo: "—"
autor: "Motion (Matt Perry)"
licenca: "documentação (só citar, não colar)"
instalacoes: "não verificado"
commit_ou_versao: "2026 (data exata não verificada)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 7
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/SKILL.md (passo 7 Acessibilidade); skills/design/button-animation/SKILL.md (§5)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.10 e tabela (fonte 6))", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§2)", "skills/design/motion-oficial/ORIGEM.md"]
---
# Documentação do Motion: acessibilidade

## O que é
Página oficial do Motion sobre movimento reduzido. É o jeito certo de respeitar a preferência do sistema nas animações do Motion.

## O que aproveitamos
- `<MotionConfig reducedMotion="user">` desliga animações de transform e layout e mantém opacidade e cor.
- `useReducedMotion()` serve para trocar `x` por `opacity`, desligar autoplay de vídeo e zerar parallax.

## O que NÃO aproveitamos e por quê
- Texto não colado: é documentação, só citada.

## Riscos e cuidados
- Nenhum de execução. Cuidado: `MotionConfig` não desliga `filter` (blur), então onde houver blur use `useReducedMotion()` (armadilha vinda do Jakub Krehel).

## Transcrição: o que aprendemos (nas nossas palavras)
- A media query CSS não alcança o Motion; a configuração tem que ser feita no próprio Motion.
- Reduzir é trocar movimento por fade, não apagar tudo.

## Fontes
- https://motion.dev/docs/react-accessibility
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.10 e tabela (fonte 6))`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§2)`; `skills/design/motion-oficial/ORIGEM.md`
