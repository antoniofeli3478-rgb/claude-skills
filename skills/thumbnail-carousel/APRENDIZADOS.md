# Aprendizados: thumbnail-carousel

Adendos datados. O mais novo fica embaixo.

## 09/10/2026: migração para o Motion 14

- **O que mudou:** o import do `assets/thumbnail-carousel.tsx` passou de `framer-motion` para **`motion/react`**, e a dependência para `npm install motion`. O resto do componente não mudou.
- **Testado:** compila no TypeScript estrito com `motion@14.0.0` e React 18. O `next/image` foi simulado com uma declaração de tipo, então falta um teste dentro de um projeto Next.js de verdade.
- **Ideia para quando for reutilizar:** transformar a lista `imgs` em props e respeitar `useReducedMotion()` (veja o `SKILL.md`).
