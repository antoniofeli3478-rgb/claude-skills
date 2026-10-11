---
name: thumbnail-carousel
description: Reusable image carousel composition with drag-to-swipe, auto-advance, spring scaling of the active slide and a clickable thumbnail strip (React + Next.js + Tailwind + Motion 14, formerly Framer Motion). Use when a project needs a gallery, hero slider, portfolio/product showcase or image carousel.
---

# Thumbnail Carousel

Drag/swipe carousel with a thumbnail navigator. Active slide scales up (0.95 vs 0.85), the active thumbnail gets a ring and `scale-110`, and it auto-advances every 5 s (paused while dragging).

Files:
- `assets/thumbnail-carousel.tsx` — the component (copy to `components/ui/thumbnail-carousel.tsx`)
- `assets/demo.tsx` — usage example

## Requirements

- shadcn-style structure, Tailwind CSS, TypeScript. If missing, set up with the shadcn CLI (`npx shadcn@latest init`) / install Tailwind.
- Components live in `components/ui`. Keep that path: shadcn imports (`@/components/ui/...`) and the CLI assume it.
- Dependencies: `npm install motion` (the new name of Framer Motion; imports come from `motion/react`, never `framer-motion`) and **Next.js** (`next/image`). Not on Next? Replace `Image` with `<img className="absolute inset-0 h-full w-full object-cover">`.

## Integrating

1. Copy `assets/thumbnail-carousel.tsx` into `components/ui/`.
2. Install dependencies.
3. Replace the `imgs` array with real images (the bundled URLs are placeholder CDN images; use Unsplash URLs you know exist, and allow the host in `next.config` `images.remotePatterns`).
4. Render `<ThumbnailCarousel />`.

## Customizing (constants at the top of the file)

| Constant | Effect |
|---|---|
| `imgs` | Image URLs |
| `AUTO_DELAY` | ms between auto-advances (5000) |
| `DRAG_BUFFER` | px drag needed to change slide (50) |
| `SPRING_OPTIONS` | feel of the slide motion (mass 3, stiffness 400, damping 50) |
| `SLIDE_SIZE_CLASSES` / `SLIDE_WIDTH_CLASSES` | slide size (Tailwind) |
| `ACTIVE_SCALE` / `INACTIVE_SCALE` | active vs inactive slide scale |
| `THUMB_SIZE_CLASSES`, `ACTIVE_THUMB_RING_CLASSES` | thumbnail size and active ring |

## Improvements to make when reusing

The original hardcodes `imgs` in the module. For a reusable composition, promote it to props: `images: {src: string; alt?: string}[]`, `autoDelay?`, and pass them down to `Images` and `ThumbnailIndicators`. Also consider `useReducedMotion()` to disable auto-advance, and meaningful `alt` text.

## Questions to ask before integrating

- Where do the images come from, and what is the alt text?
- Responsive behavior: keep the 320px/384px slides or fit a container?
- Where in the page does it go (hero, product page, gallery)?
- Using Next.js? (`next/image` dependency)

## Use in Remotion videos

Not directly (it is interactive). To show a carousel in a video, reuse the idea: translate X by `interpolate`/`spring` on the frame and scale the active slide (see `remotion-video`).
