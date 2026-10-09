---
name: button-animation
description: Padrões de animação para botões e interfaces interativas com Motion 14 (o antigo Framer Motion), Tailwind e CSS. Cobre micro-interações de toque e hover, popovers com mola, anel de gradiente girando no foco, anel de progresso, movimento reduzido e as boas práticas oficiais do Motion. Use ao criar ou polir animações em botões, campos, menus ou qualquer componente interativo em React/Tailwind.
---

# Animação de botões e interfaces

Receitas tiradas de uma caixa de mensagem bem acabada (`Compose`): o componente está em `assets/compose.tsx` e a demo em `assets/compose-demo.tsx`. O arquivo original foi salvo como `GRADIENT-BUTTON.txt`, mas é um compositor completo, com `@menções` e `/comandos`. O que vale reaproveitar é o design de movimento, listado abaixo. Os aprendizados novos ficam em `APRENDIZADOS.md`.

## Versão e instalação (out/2026)

- **Pacote:** o Framer Motion agora se chama **Motion**. Versão estável no npm: **14.0.0** (02/10/2026). A 14.1.0 já aparece no changelog de 09/10/2026, mas ainda não estava no npm. Funciona com React 18 e 19.
- **Instalação:** `npm install motion`. **Desinstale o `framer-motion`**, porque os dois nunca devem estar instalados juntos.
- **Imports:**
  - Em componentes de cliente (`"use client"`): `import { motion, AnimatePresence } from "motion/react"`.
  - Em server components: `import * as motion from "motion/react-client"`.
  - A função `animate`: vem de `"motion/react"` em arquivos React e de `"motion"` nos demais.
  - **Nunca** importe de `"framer-motion"`.
- **Para migrar um projeto antigo:** troque os imports de `framer-motion` por `motion/react`. Das versões 12 a 14, as mudanças que quebram coisas afetaram só APIs internas e a dependência opcional `@emotion/is-prop-valid`, removida na 13.0. Se ela for usada, configure via `<MotionConfig isValidProp={...}>`.
- **Testado em 09/10/2026:** os componentes desta skill e do `thumbnail-carousel` passaram na checagem estrita do TypeScript com `motion@14.0.0` e React 18, só trocando o import.

Dependências: `motion` e Tailwind CSS. Os componentes vão em `components/ui` (estrutura shadcn), em TypeScript.

## Receitas

### 1. Toque e hover (só Tailwind)

Botão principal: a seta se mexe um pouco no hover, e o botão inteiro encolhe levemente no toque.

```tsx
<button className="group inline-flex items-center gap-2 rounded-xl bg-zinc-900 px-3.5 py-2 text-[13px] font-semibold text-white shadow-sm transition-all duration-150 hover:bg-zinc-800 active:scale-[0.97] disabled:pointer-events-none disabled:opacity-40 dark:bg-white dark:text-zinc-900 dark:hover:bg-zinc-100">
  <svg className="transition-transform duration-200 group-hover:-translate-y-0.5 group-hover:translate-x-0.5" … />
  Enviar
</button>
```

Valores-chave: `active:scale-[0.97]`, `duration-150` no botão, `duration-200` no ícone, `disabled:opacity-40`.

**A mesma coisa com Motion** (mola interrompível, melhor quando o usuário toca rápido várias vezes):

```tsx
<motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }}
  transition={{ type: "spring", visualDuration: 0.2, bounce: 0.2 }} />
```

### 2. Anel de gradiente girando no foco (o efeito "gradient")

Anime uma propriedade CSS registrada para o gradiente cônico girar, e mostre o anel só no `:focus-within`.

```css
@property --ring-a { syntax: "<angle>"; initial-value: 0deg; inherits: false; }
@keyframes ring-spin { to { --ring-a: 360deg; } }
.ring { opacity: 0; transition: opacity .5s ease;
  background: conic-gradient(from var(--ring-a),
    rgba(161,161,170,0) 0deg, rgba(161,161,170,.42) 60deg,
    rgba(212,212,216,.62) 108deg, rgba(161,161,170,0) 168deg, rgba(161,161,170,0) 360deg); }
.root:focus-within .ring { opacity: 1; animation: ring-spin 5s linear infinite; }
@media (prefers-reduced-motion: reduce) { .root:focus-within .ring { animation: none; } }
```

Coloque o `.ring` como uma camada absoluta atrás do controle, um pouco maior e arredondada, para aparecer só uma borda fina de gradiente. Para um botão colorido, troque os cinzas pelas cores da marca.

### 3. Popover com mola e AnimatePresence

```tsx
const reduce = useReducedMotion();
<AnimatePresence>
  {open && (
    <motion.ul
      initial={{ opacity: 0, y: 6, scale: 0.98 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      exit={{ opacity: 0, y: 6, scale: 0.98 }}
      transition={reduce ? { duration: 0.12 } : { type: "spring", stiffness: 620, damping: 36, mass: 0.6 }} />
  )}
</AnimatePresence>
```

Destaque deslizante do item ativo dentro da lista: um `motion.div` com `layoutId` e `transition={{ type: "spring", stiffness: 650, damping: 40, mass: 0.5 }}`. A partir da 12.36, use `layout="x"` ou `layout="y"` para animar o layout só num eixo.

### 4. Anel de progresso ou contador (SVG)

Um círculo com `strokeDasharray = 2πr` e `strokeDashoffset = circ * (1 - pct)`, com `transition: stroke-dashoffset .25s ease, stroke .25s ease`. Cor: cinza, âmbar a partir de 80% e vermelho quando passa do limite.

### 5. "Pop" de confirmação

`@keyframes pop { 0% { box-shadow: 0 0 0 3px rgba(113,113,122,.16) } 100% { box-shadow: 0 0 0 0 transparent } }`, aplicado por 0,5 s com ease-out quando uma ação acontece (por exemplo, uma menção inserida).

### 6. Barra de rolagem fina com tema

`scrollbar-width: thin` mais `::-webkit-scrollbar-thumb` com borda transparente de 4 px e `background-clip: padding-box`. Um pouco mais escura no hover e no foco, com valores próprios em `.dark`.

## Molas

| Sensação | stiffness / damping / mass | ou, de forma mais previsível |
|---|---|---|
| Popover rápido | 620 / 36 / 0,6 | `visualDuration: 0.25, bounce: 0.1` |
| Destaque deslizante | 650 / 40 / 0,5 | `visualDuration: 0.25, bounce: 0` |
| Pesado, tipo carrossel | 400 / 50 / 3 (veja `thumbnail-carousel`) | `visualDuration: 0.6, bounce: 0` |

- **`visualDuration` + `bounce`** é a forma recomendada pelo Motion: você diz quanto tempo o movimento "parece" durar e quanto ele quica.
- **`bounce: 0`** não passa do ponto. Desde a 13.5, **`bounce` negativo** (até −1) deixa a mola ainda mais amortecida.
- **Escolha pelo tipo de produto:** num app sério (finanças, saúde), evite passar do ponto. Num app divertido, pode quicar mais.

## Boas práticas oficiais do Motion (da skill oficial, MIT)

- **Prefira animar `transform`** (`animate={{ transform: "scale(1.05)" }}`), que roda pelo WAAPI, acelerado. Use transformações independentes (`x`, `scale`) só quando:
  - cada uma tiver uma transição diferente;
  - vierem de MotionValues;
  - várias se somarem, como `animate={{ x }}` junto com `whileHover={{ scale }}`.
- **`will-change`:** use só ao animar com transição CSS ou com transformações independentes, e com moderação.
- **Molas e curvas:**
  - Prefira molas para movimentos físicos que podem ser interrompidos.
  - Para valores não numéricos, use `type: "spring", bounce: 0.2, visualDuration: 0.4`.
  - Curvas em camelCase: `easeOut`, `circOut` (não `ease-out`).
- **MotionValues:**
  - Nunca leia `value.get()` durante o render.
  - Use `value.on("change", fn)` (o `onChange` está obsoleto).
  - Encadeie `useTransform`, `useSpring` e `useVelocity` em vez de lógica imperativa.
  - Sintaxe do `useTransform`: `useTransform(value, [entrada], [saída])` ou `useTransform(() => a.get() * 2)`. A forma `useTransform(value, v => …)` está obsoleta.
- **Dentro de funções que rodam a cada quadro:** não crie objetos, use `for` em vez de `forEach`/`map`, e evite `Object.entries`.
- **Radix:**
  - Anime via `asChild` com um filho `motion.*`.
  - Para animação de saída, eleve o estado (`open`/`onOpenChange`) e coloque o componente com `forceMount` dentro do `AnimatePresence`.

## Recursos novos úteis para botões e UI (12.24 a 14.1)

- `propagate={{ tap: false }}` (12.33): o toque num botão interno não dispara o `whileTap` do card pai.
- `<MotionConfig reducedMotion="user">`: respeita automaticamente o "reduzir movimento" do sistema. `skipAnimations` (12.30) desliga as animações em testes.
- `AnimatePresence` com `anchorY` (12.28) e `root` (shadow DOM): posicionam elementos com `popLayout`.
- `useFollowValue` (12.28): valor que segue outro com qualquer transição, como um `useSpring` mais flexível.
- **Cores:** `oklch`, `oklab`, `color-mix` e `light-dark` animáveis (12.37). `backgroundColor` acelerado por hardware nos navegadores que suportam (12.43).
- `AnimateView` (13.4): transições de página com o `ViewTransition` do React 19.3.
- `Reorder` multidimensional, em grade, com RTL (13.1).
- `animateLayout` (14.1): animação de layout em JS puro.

## Regras ao animar botões

1. Respeite sempre o `prefers-reduced-motion`: `useReducedMotion()` ou `MotionConfig reducedMotion="user"` no Motion, e media query no CSS.
2. Anime só `transform` e `opacity`. Micro-interações duram entre 120 e 250 ms.
3. Tenha um estado `:focus-visible` visível e um estilo de `disabled`. Nunca dependa só do movimento.
4. Suporte tema claro e escuro (variantes `dark:`).
5. Seja sutil: no toque, escala de pelo menos 0,95; no hover, no máximo 1,05.

## Usar o componente Compose completo

1. Copie `assets/compose.tsx` para `components/ui/compose.tsx`. Ele injeta o próprio `<style>`.
2. Rode `npm install motion`.
3. Veja o uso em `assets/compose-demo.tsx`.

Props: `value/defaultValue`, `onChange`, `onSubmit`, `onCommand`, `mentions`, `commands`, `placeholder`, `maxLength`, `submitLabel`, `autoFocus`, `className`.

## Skill e MCP oficiais do Motion (opcional)

- **Instalação:** `npx motion-ai` instala a skill oficial `/motion` (MIT, código em github.com/motiondivision/ai-kit) e configura o MCP **https://mcp.motion.dev**, que busca na documentação e nos exemplos. Ele é gratuito e não precisa de conta.
- **Neste repositório:** a skill oficial já está em `skills/motion-oficial/SKILL.md`. Leia as regras dela primeiro e use as receitas desta skill depois.
- **O que a skill traz:**
  - boas práticas para JS puro, React, Vue, Base UI e Radix;
  - um gerador de molas em CSS (`linear()`);
  - prévia de curvas;
  - um agente "motion-reviewer".
- **Partes pagas (Motion+):** auditoria de desempenho MotionScore, código-fonte dos exemplos e Motion UI, e o editor visual.
- **`https://motion.dev/llms.txt`:** índice de toda a documentação, para consultar sem o MCP.

## Em vídeos do Remotion

Leve os valores, não o código:
- troque hover e toque por `spring()` no quadro (por exemplo, escala 1 → 0,97 → 1 em volta do quadro do clique);
- troque o anel por `rotate = interpolate(frame, [0, 150], [0, 360])` num gradiente cônico.

Veja `remotion-video`.
