---
name: button-animation
description: Animation patterns for buttons and interactive UI (framer-motion + Tailwind + CSS): press/hover micro-interactions, spring popovers, rotating conic-gradient focus ring, progress ring, reduced-motion handling. Use when adding or polishing animations on buttons, inputs, menus or any interactive component in a React/Tailwind project.
---

# Button & UI Animation

Animation recipes taken from a polished `Compose` message box (`assets/compose.tsx`, demo in `assets/compose-demo.tsx`). The source file was saved as `GRADIENT-BUTTON.txt`, but it is really a full composer (textarea with `@mentions` and `/commands`). The reusable value is its motion design, listed below.

Dependencies: `framer-motion`, Tailwind CSS. Components go in `components/ui` (shadcn layout), TypeScript.

## Recipes

### 1. Press + hover micro-interaction (Tailwind only)
Primary button; arrow icon nudges on hover, whole button shrinks slightly on press.
```tsx
<button className="group inline-flex items-center gap-2 rounded-xl bg-zinc-900 px-3.5 py-2 text-[13px] font-semibold text-white shadow-sm transition-all duration-150 hover:bg-zinc-800 active:scale-[0.97] disabled:pointer-events-none disabled:opacity-40 dark:bg-white dark:text-zinc-900 dark:hover:bg-zinc-100">
  <svg className="transition-transform duration-200 group-hover:-translate-y-0.5 group-hover:translate-x-0.5" … />
  Send
</button>
```
Key values: `active:scale-[0.97]`, `duration-150` on the button, `duration-200` on the icon, `disabled:opacity-40`.

### 2. Rotating conic-gradient ring on focus (the "gradient" effect)
Animate a registered CSS custom property so a conic gradient spins; show only on `:focus-within`.
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
Put `.ring` as an absolutely positioned layer behind the control (slightly larger, rounded) so only a thin gradient edge shows. For a colorful gradient button, swap the grey stops for brand colors.

### 3. Spring popover with AnimatePresence
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
Sliding active-item highlight inside the list: a `motion.div` with `layoutId`, `transition={{ type: "spring", stiffness: 650, damping: 40, mass: 0.5 }}`.

### 4. Progress / counter ring (SVG)
Circle with `strokeDasharray = 2πr` and `strokeDashoffset = circ * (1 - pct)`, `transition: stroke-dashoffset .25s ease, stroke .25s ease`. Color: grey → amber at 80% → red when over.

### 5. One-shot "pop" feedback
`@keyframes pop { 0% { box-shadow: 0 0 0 3px rgba(113,113,122,.16) } 100% { box-shadow: 0 0 0 0 transparent } }` applied for .5s ease-out when an action lands (e.g. mention inserted).

### 6. Thin themed scrollbar
`scrollbar-width: thin` plus `::-webkit-scrollbar-thumb` with a transparent 4px border and `background-clip: padding-box`; slightly darker on hover/focus; separate `.dark` values.

## Spring cheat sheet

| Feel | stiffness / damping / mass |
|---|---|
| Snappy UI popover | 620 / 36 / 0.6 |
| Sliding highlight | 650 / 40 / 0.5 |
| Heavy, carousel-like | 400 / 50 / 3 (see `thumbnail-carousel`) |

## Rules when animating buttons

1. Always honor `prefers-reduced-motion` (`useReducedMotion()` in framer-motion, media query in CSS).
2. Animate `transform` and `opacity` only; keep durations 120–250 ms for micro-interactions.
3. Provide a visible `:focus-visible` state and `disabled` styling; never rely on motion alone.
4. Support light and dark (`dark:` variants).
5. Keep the effect subtle: scale ≥ 0.95 on press, ≤ 1.05 on hover.

## Integrating the full Compose component

Copy `assets/compose.tsx` to `components/ui/compose.tsx` (it injects its own `<style>`), `npm install framer-motion`, then see `assets/compose-demo.tsx`. Props: `value/defaultValue`, `onChange`, `onSubmit`, `onCommand`, `mentions`, `commands`, `placeholder`, `maxLength`, `submitLabel`, `autoFocus`, `className`.

## In Remotion videos

Port the values, not the code: replace hover/press with `spring()` on the frame (e.g. scale 1 → 0.97 → 1 around the click frame) and the ring with `rotate = interpolate(frame, [0, 150], [0, 360])` on a conic-gradient. See `remotion-video`.
