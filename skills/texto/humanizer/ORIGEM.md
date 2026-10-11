# Origem desta skill

Cópia **sem nenhuma alteração** da skill `humanizer`. Os arquivos em inglês são os originais do autor. Só `ORIGEM.md` e `APRENDIZADOS.md` foram escritos aqui.

- **Origem:** https://github.com/Aboudjem/humanizer-skill (pasta `skills/humanizer` do repositório de origem), commit `a58df06` de 06/09/2026. Baixada em 09/10/2026.
- **Autor e licença:** Adam Boudjemaa, **MIT** (arquivo `LICENSE` nesta pasta).
- **O que foi copiado:** `SKILL.md`, `README.md`, `references/` (`patterns.md`, `patterns.zh.md` e `always-on-templates.md`) e `evals/evals.json`.
- **O que NÃO foi copiado:** a pasta `cli/` do repositório, uma ferramenta opcional em Node que calcula métricas do texto de forma determinística (burstiness, repetição e legibilidade). A skill funciona sem ela. Se for preciso medir de verdade, mostre ao usuário antes de instalar.
- **Não faz chamadas de rede.** O próprio `SKILL.md` diz "Pure Markdown, zero dependencies, no network calls". As ferramentas que ele usa são Read, Write, Edit, Grep, Glob e AskUserQuestion.

## Como atualizar

```bash
git clone --depth 1 https://github.com/Aboudjem/humanizer-skill.git
# compare humanizer-skill/skills/humanizer com esta pasta (skills/texto/humanizer)
```

Se houver novidade, mostre ao usuário antes de substituir e anote a versão e o commit aqui.
