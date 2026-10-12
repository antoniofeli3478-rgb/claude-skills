---
nome: "motion (skill oficial do pacote motion-ai)"
repositorio: "motiondivision/ai-kit (https://github.com/motiondivision/ai-kit; pacote npm motion-ai)"
caminho_no_repo: "package/content/skills/motion/ (dentro do pacote npm)"
autor: "Matt Perry (Motion)"
instalacoes: "não verificado"
commit_ou_versao: "motion-ai 14.1.0, baixado em 09/10/2026"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "sem nota (é a base que as pesquisas tentam completar)"
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/ (SKILL.md \"Motion: o essencial\"; references/api-motion.md, codex-mcp.md, molas-e-curvas.md §3–4, revisao.md §7; agents/motion-reviewer.md); skills/design/button-animation/SKILL.md (§0 e §8: instalação, imports, recursos do Motion)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/RESUMO.md", "research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (diagnóstico e §3)", "research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§1)", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§1)", "skills/design/motion-oficial/ORIGEM.md", "skills/design/button-animation/ORIGEM.md", "skills/design/motion-oficial/APRENDIZADOS.md (motion-oficial/APRENDIZADOS.md)"]
---
# motion (skill oficial do pacote motion-ai)

## O que é
É a skill oficial do Motion (antigo Framer Motion), entrada na base em 09/10/2026 como cópia intacta. Em 11/10/2026, com autorização do Daniel, foi traduzida e reorganizada dentro da `motion-oficial`, junto com as outras fontes.

## O que aproveitamos
- Regras de API: imports certos (`motion/react`, nunca `framer-motion`), `transform` (WAAPI) contra atalhos `x/y/scale`, `will-change` com moderação, MotionValues, `useTransform`, React, Vue, JS puro, Base UI e Radix com `forceMount`.
- Busca na documentação via servidor MCP gratuito e `motion.dev/llms.txt`; migração; molas CSS com `linear()`; editor visual; agente revisor.
- O conteúdo dos arquivos `best-practices/`, `codex/`, `css-spring/`, `performance-audit/` e `transition-preview/` foi absorvido em `references/`; as pastas antigas deixaram de existir.

## O que NÃO aproveitamos e por quê
- `rules/motion.mdc` (só serve para o Cursor) ficou de fora.
- Partes pagas (Motion+): auditoria MotionScore, código-fonte de exemplos e Motion UI, editor visual. A skill diz com clareza que são pagas e não as reconstrói de memória.
- Lacunas dela, que as outras fontes preencheram: não diz quando NÃO animar, não tem tabela de durações nem curvas com valores, não fala de `prefers-reduced-motion` em nenhum arquivo, e a única regra de design é "bolsa sem overshoot, casamento mais suave".

## Riscos e cuidados
- Servidor MCP externo (`https://mcp.motion.dev`): as consultas vão para fora. Testado em 09/10/2026: responde, não pede sessão e expõe só `search-motion-docs`. Ainda não registrado de forma permanente neste repositório.
- `npx motion-ai` instala o pacote em outro projeto (comando citado no `APRENDIZADOS.md`); não foi executado nas pesquisas.
- Parte paga (Motion+): não prometer nada dela.

## Transcrição: o que aprendemos (nas nossas palavras)
- Use `motion/react` e prefira `transform` em string para rodar via WAAPI; abreviações `x/y/scale` só em casos compostos ou gestos.
- Para consultar a documentação, use a busca do servidor MCP ou o `llms.txt`.
- O pacote oficial dá a API; a direção do movimento (quando animar, quanto, qual curva) veio das outras fontes.
- Valores não numéricos não usam física de mola.

## Fontes
- https://github.com/motiondivision/ai-kit
- https://motion.dev/llms.txt
- https://mcp.motion.dev
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/RESUMO.md`; `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (diagnóstico e §3)`; `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§1)`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§1)`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/motion-oficial/APRENDIZADOS.md (motion-oficial/APRENDIZADOS.md)`
