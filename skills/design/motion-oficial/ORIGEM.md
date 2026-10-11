# Origem desta skill

Esta pasta é a **skill oficial `/motion`** do Motion (o antigo Framer Motion), copiada sem nenhuma alteração. Os arquivos em inglês são os originais dos autores. Só `ORIGEM.md` e `APRENDIZADOS.md` foram escritos aqui.

- **Origem:** pacote npm `motion-ai`, versão **14.1.0**, baixado em 09/10/2026.
  - Código-fonte: https://github.com/motiondivision/ai-kit
  - Autor: Matt Perry (https://motion.dev)
- **Licença:** **MIT**, segundo o `package.json` do pacote.
- **O que foi copiado:** `SKILL.md`, `best-practices/`, `codex/`, `css-spring/`, `performance-audit/` e `transition-preview/`, mais o agente revisor em `agents/motion-reviewer.md`.
- **O que ficou de fora:** a regra `rules/motion.mdc`, que só serve para o editor Cursor.
- **Sobre `performance-audit/`:** ela está aqui porque o `SKILL.md` aponta para ela. A auditoria MotionScore em si é uma parte **paga** (Motion+). O arquivo só explica como pedi-la.

## Como atualizar

```bash
npm pack motion-ai          # baixa o pacote mais novo
tar -xzf motion-ai-*.tgz    # os arquivos ficam em package/content/skills/motion/
```

Compare com esta pasta e, se houver novidade, mostre ao usuário antes de substituir, como manda o `CLAUDE.md`. Anote a versão nova aqui.
