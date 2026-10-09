# Aprendizados: button-animation

Adendos datados. O mais novo fica embaixo. A skill original continua no `SKILL.md`.

## 09/10/2026: Motion 14 (ex-Framer Motion)

- **Versão:** a versão estável atual é a **`motion@14.0.0`**. O import correto é **`motion/react`**, e o `framer-motion` não deve ser usado nem ficar instalado junto. Pesquisa completa: `research_notes/Framer Motion 2026/notas.md`.
- **Teste:** os assets desta skill (`compose.tsx` e `compose-demo.tsx`) passam no TypeScript estrito com Motion 14 e React 18, só trocando o import.
- **Bug no `assets/compose.tsx`:** o `{"\n"}` da linha ~369 virou uma quebra de linha real, herdada do `GRADIENT-BUTTON.txt` original, e o arquivo não compila. A correção é `{"\n"}`. Ela entra na atualização da skill, se o usuário aprovar.
- **Skill oficial:** existe a `/motion` (MIT), instalada com `npx motion-ai`, mais o MCP grátis `https://mcp.motion.dev`. Ela é útil como consulta de boas práticas e da documentação atualizada.
