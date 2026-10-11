# Aprendizados: button-animation

Adendos datados. O mais novo fica embaixo. A skill original continua no `SKILL.md`.

## 09/10/2026: Motion 14 (ex-Framer Motion)

- **Versão:** a versão estável atual é a **`motion@14.0.0`**. O import correto é **`motion/react`**, e o `framer-motion` não deve ser usado nem ficar instalado junto. Pesquisa completa: `research_notes/Framer Motion 2026/notas.md`.
- **Teste:** os assets desta skill (`compose.tsx` e `compose-demo.tsx`) passam no TypeScript estrito com Motion 14 e React 18, só trocando o import.
- **Bug no `assets/compose.tsx`:** o `{"\n"}` da linha ~369 virou uma quebra de linha real, herdada do `skills/design/button-animation/original/GRADIENT-BUTTON.txt` original, e o arquivo não compila. A correção é `{"\n"}`. Ela entra na atualização da skill, se o usuário aprovar.
- **Skill oficial:** existe a `/motion` (MIT), instalada com `npx motion-ai`, mais o MCP grátis `https://mcp.motion.dev`. Ela é útil como consulta de boas práticas e da documentação atualizada.

## 09/10/2026: skill oficial do Motion adicionada

- A skill oficial do Motion está em `skills/design/motion-oficial/`. Para animar com Motion, leia primeiro o `best-practices/` dela e depois as receitas desta skill.

## 09/10/2026: atualização aplicada (aprovada pelo usuário)

- O `SKILL.md` foi atualizado para o Motion 14 (instalação, imports, molas com `visualDuration` e `bounce`, boas práticas oficiais e recursos novos).
- O `assets/compose.tsx` agora importa de `motion/react`, e o erro da quebra de linha foi corrigido: `{"\n"}` voltou a ser um texto válido.
- Os dois componentes (`compose.tsx` e `compose-demo.tsx`) foram testados no TypeScript estrito com `motion@14.0.0` e React 18, e passam sem erros.

## 09/10/2026: receitas usadas em vídeo (Rotina v2, Remotion)

- **Anel de gradiente girando (receita 2):** um `conic-gradient` girando atrás do botão "BAIXE AGORA", com `rotate = quadro × 6°`. Acelera no golpe final.
- **Toque (receita 1):** escala 1 → 0,94 → 1 com uma mola no quadro da palavra "agora", mais uma ondulação (círculo crescendo e sumindo).
- **Botão deslizante "Fixa | Pontual":** a mola do destaque deslizante, convertida para o Remotion como `spring({stiffness: 650, damping: 40, mass: 0.5})`. Desliza em ~8 quadros, sem passar do ponto.
