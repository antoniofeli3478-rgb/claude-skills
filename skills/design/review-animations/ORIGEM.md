# Origem desta skill

Cópia **sem nenhuma alteração** da skill `review-animations` de Emil Kowalski. Só `ORIGEM.md` e `APRENDIZADOS.md` foram escritos aqui; o `LICENSE` é o da raiz do repositório dele.

- **Origem:** https://github.com/emilkowalski/skills, pasta `skills/review-animations`, commit `e8a175de22ae1e49370fc144c1f3bb9aeedf988d` de 02/10/2026. Baixada para a pesquisa em 11/10/2026 e instalada em 12/10/2026.
- **Autor e licença:** Emil Kowalski, **MIT** (`LICENSE` da raiz do repositório, "Copyright (c) 2026 Emil Kowalski").
- **O que foi copiado:** a pasta inteira: `SKILL.md` (8 KB) e `STANDARDS.md` (10 KB).
- **Leitura de segurança (12/10/2026):** só texto. Sem comando, sem chamada de rede, sem pedido de chave. O único link externo são dois sites de curvas de easing (easing.dev e easings.co), citados como referência.
- **Detalhe do original:** o cabeçalho traz `disable-model-invocation: true`, ou seja, ela só roda quando o usuário a chama de propósito (o Claude não a dispara sozinho).
- **Instalação oficial equivalente:** `npx skills add emilkowalski/skills --skill review-animations` (não use: copiamos à mão).

## Como atualizar

```bash
git clone --depth 1 https://github.com/emilkowalski/skills.git
diff -r skills/review-animations <caminho>/skills/design/review-animations
```

Se houver novidade, mostre ao Daniel antes de substituir e anote aqui o commit novo.
