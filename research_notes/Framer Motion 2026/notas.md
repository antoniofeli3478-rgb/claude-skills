# Framer Motion (agora Motion): versão atual e novidades (pesquisa de 09/10/2026)

**Fontes:**
- npm (`npm view motion` / `framer-motion`);
- changelog oficial: https://raw.githubusercontent.com/motiondivision/motion/main/CHANGELOG.md;
- README do repositório motiondivision/motion;
- pacote npm `motion-ai` 14.1.0 (skill oficial).

## Versões

- **Novo nome:** o Framer Motion passou a se chamar **Motion**. O pacote novo é `motion`, importado de `motion/react`. O pacote `framer-motion` continua publicado com a mesma versão, mas a orientação oficial é **não usar**.
- **Versão estável no npm** (09/10/2026): **14.0.0**, de 02/10/2026, tanto em `motion` quanto em `framer-motion`.
  - Compatível com React `^18.0.0 || ^19.0.0`.
  - A **14.1.0** consta no changelog com data de 09/10/2026, mas ainda não estava no npm. O pacote `motion-ai` já estava na 14.1.0.
- **Mudanças que quebram coisas:**
  - **14.0:** só a remoção de APIs internas. Daqui para frente, as dependências internas ficam fixadas.
  - **13.0:** saiu a dependência opcional `@emotion/is-prop-valid`. Se ela for usada, configure `<MotionConfig isValidProp={isPropValid}>`.
  - O uso público com `motion/react` não muda.

## Novidades relevantes (12.24 → 14.1)

- **14.1:**
  - `animateLayout`: layout em JS puro.
  - Correções de `whileHover` e `whileTap` presos depois de Fast Refresh, e de rolagem elástica.
- **13.5:**
  - `spring` aceita `bounce` negativo (0 a −1), para molas ainda mais amortecidas.
  - O componente `<m>` ficou 20% menor e o `useSpring` 10% menor.
- **13.4:**
  - `AnimateView`: transições de visualização com o `ViewTransition` do React 19.3.
- **13.3:**
  - Ganchos para o Motion Editor.
  - `animate` 10% menor e 20% mais rápido para iniciar.
- **13.2:**
  - `animate.addEffect()`.
  - `threeEffect` (`motion/three`) anima objetos do Three.js.
  - `vgpuEffect` (`motion/vgpu`).
- **13.1:**
  - `Reorder` multidimensional (em grade), com eixo detectado automaticamente e suporte a RTL.
- **12.43:**
  - Aceleração por hardware de `backgroundColor` e de elementos SVG.
- **12.41:**
  - `animateView` (view transitions) entra na biblioteca principal.
- **12.40:**
  - Opção `path` na transition e `arc()` para movimento em arco.
- **12.39:**
  - `repeatType` e `repeatDelay` em sequências.
- **12.37:**
  - Cores `oklch`, `oklab`, `lab`, `lch`, `color-mix` e `light-dark`.
  - Offsets `start`/`end` acelerados no `scroll`.
- **12.36:**
  - `layout="x"` / `layout="y"`.
  - `skipInitialAnimation` no `useSpring`.
  - `dragSnapToOrigin` por eixo.
- **12.33:**
  - `propagate.tap` impede que o toque se propague para o elemento pai.
- **12.30:**
  - `MotionConfig` com a opção `skipAnimations`.
- **12.28:**
  - `useFollowValue` / `followValue`.
  - `anchorY` no `AnimatePresence`.
- **12.24:**
  - **Servidor MCP.**
  - `resize()` e `usePageInView`.
  - `delayChildren: stagger()`.
  - `root` (shadow DOM) no `AnimatePresence`.

## IA: skill oficial e MCP

- **Skill oficial:** `npx motion-ai` instala a skill **`/motion`**. Ela é **MIT**, o código está em github.com/motiondivision/ai-kit e o pacote npm `motion-ai` estava na 14.1.0. O instalador configura os MCPs para Claude Code, Cursor, Amp, OpenCode, Gemini CLI e Copilot.
- **Conteúdo do pacote:**
  - `skills/motion/SKILL.md`;
  - guias de boas práticas para React, JS puro, Vue e Base UI;
  - `css-spring` (molas em CSS `linear()`);
  - `performance-audit` (MotionScore, recurso do Motion+);
  - `transition-preview`;
  - `codex` (busca na documentação);
  - o agente `motion-reviewer` e a regra `motion.mdc`.
  - São cerca de 30 KB no total.
- **Servidor MCP:** `https://mcp.motion.dev` busca na documentação e nos metadados dos exemplos. É grátis e não precisa de conta.
- **Índice da documentação:** `https://motion.dev/llms.txt`.
- **Partes pagas (Motion+):**
  - auditorias MotionScore;
  - código-fonte dos exemplos e do Motion UI;
  - o editor visual de transições.

## Boas práticas oficiais (resumo da skill oficial)

- **Imports:**
  - Nunca use `framer-motion`.
  - Em componentes de cliente, use `motion/react`. Em server components, `motion/react-client`.
  - Não deixe `framer-motion` e `motion` instalados juntos.
- **Transformações:** prefira animar `transform`, que roda pelo WAAPI. Use `x`/`scale` quando houver transições diferentes, MotionValues ou transformações que se somam.
- **`will-change`:** só para transição CSS ou transformações independentes, e com moderação.
- **Molas:** use molas para movimento físico interrompível. Para valores não numéricos, `type: "spring", bounce: 0.2, visualDuration: 0.4`. Em interfaces "sérias", sem passar do ponto.
- **MotionValues:** nunca leia `.get()` no render. Use `.on("change")`, porque o `onChange` está obsoleto. A forma `useTransform(value, fn)` também está obsoleta.
- **Curvas:** nomes em camelCase (`easeOut`).
- **Radix:** anime via `asChild`, use `forceMount` dentro do `AnimatePresence` e eleve o estado.

## Teste de compatibilidade (MEDIDO, 09/10/2026)

- **Ambiente:** `motion@14.0.0` com React 18.3.1, `@types/react` 18.3.3, TypeScript 5.5.4, `strict`, `moduleResolution: bundler`.
- **Resultado:** os componentes `skills/design/button-animation/assets/compose.tsx`, `compose-demo.tsx` e `skills/design/thumbnail-carousel/assets/thumbnail-carousel.tsx` passam na checagem só trocando `"framer-motion"` por `"motion/react"`. O `next/image` foi simulado com uma declaração de tipo.
- **Bug encontrado no `compose.tsx`:** ele já vinha do `GRADIENT-BUTTON.txt` original. O `{"\n"}` virou uma quebra de linha de verdade dentro da string, o que impede a compilação. A correção é voltar para `{"\n"}`.

## Feito depois

- A skill oficial foi adicionada em `skills/design/motion-oficial/` (09/10/2026, com aprovação do usuário), como cópia fiel do pacote `motion-ai` 14.1.0.

- As atualizações das skills `button-animation` (com a correção do `compose.tsx`) e `thumbnail-carousel` foram aplicadas em 09/10/2026, com aprovação do usuário.
- O servidor `https://mcp.motion.dev` foi testado: responde daqui, sem sessão, com uma só ferramenta (`search-motion-docs`).

## PENDENTE / NÃO PESQUISADO

- **Página de upgrade do React:** ler a `react-upgrade-guide` completa no motion.dev, que segundo a skill oficial deve ser lida inteira e seguida na ordem.
- **14.1.0:** testar quando sair no npm.
- **MCP oficial:** registrar no Claude Code (`claude mcp add`) e testar como ferramenta nativa. A chamada direta já funciona.
- **Partes pagas:** avaliar se vale a pena para o usuário (auditoria e editor).
