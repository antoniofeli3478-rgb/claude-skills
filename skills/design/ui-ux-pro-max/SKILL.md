---
name: ui-ux-pro-max
description: Pointer to the external "UI/UX Pro Max" design-intelligence skill (styles, color palettes, font pairings, UX guidelines, chart types, 20+ stacks). Use when designing or reviewing a UI/UX, picking a design system, palette or typography, or building landing pages, dashboards and app screens.
---

# UI/UX Pro Max (external skill)

Source: https://github.com/nextlevelbuilder/ui-ux-pro-max-skill (MIT license). The saved link in this repo (`skills/design/ui-ux-pro-max/original/ux-ui-disign-link`) points here. This skill does not copy that project; it tells you how to install and use it.

## What it provides (per its README)

- ~79 searchable UI styles (glassmorphism, minimalism, brutalism…)
- 192 color palettes, 74 font pairings
- 25 chart types for dashboards
- 119 UX guidelines (best practices, accessibility, anti-patterns)
- 192 industry-specific reasoning rules to generate a tailored design system
- 22 stacks (React, Next.js, Vue, Svelte, Angular, Tailwind, SwiftUI, Flutter, Jetpack Compose…)

## Install

Requires Python 3.x (search script).

```bash
# Claude Code marketplace
/plugin marketplace add nextlevelbuilder/ui-ux-pro-max-skill
/plugin install ui-ux-pro-max@ui-ux-pro-max-skill

# or via CLI, per project
npm install -g ui-ux-pro-max-cli
cd /path/to/project && uipro init --ai claude
# globally for all projects
uipro init --ai claude --global      # installs in ~/.claude/skills/
```

## Use

It activates on natural UI/UX requests ("build a landing page for my SaaS", "dashboard for healthcare analytics"). Direct search:

```bash
python3 .claude/skills/ui-ux-pro-max/scripts/search.py "beauty spa wellness" --design-system -p "Serenity Spa"
python3 .claude/skills/ui-ux-pro-max/scripts/search.py "glassmorphism" --domain style
```

## If it is not installed

Say so, ask the user whether to install it, and meanwhile apply basics: pick one style, a 3–5 color palette with accessible contrast (WCAG AA), two font families max, consistent spacing scale, visible focus states, and `button-animation` for interactions.

## Combining with the other skills

- `button-animation` / `thumbnail-carousel`: ready UI + motion pieces.
- `remotion-video`: use its palette/typography choices in video scenes.
