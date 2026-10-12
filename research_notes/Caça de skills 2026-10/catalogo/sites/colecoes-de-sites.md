# Coleções de sites e templates (não são skills)

Área: SITES COM IA. Data da pesquisa: 11/10/2026. Fonte única: `research_notes/Caça de skills 2026-10/sites/pesquisa-B.md` (seção 3 e 6), mais os commits fixados em `scratchpad/hunter/sites/PROPOSTA/skills/sites/referencia-sites/fontes.txt`. Nada foi pesquisado de novo, nada foi baixado ou executado nesta tarefa.

Como ler: cada seção diz **licença**, **pode copiar?**, **stack**, **estilo** e uma observação. "Pode copiar" significa copiar o código mantendo o `LICENSE` e o crédito. Na proposta, **nenhuma cópia vai para a main**: tudo entra por link e por um script que baixa para a pasta do projeto do site, no commit fixado.

Resumo rápido:

| Grupo | Coleções |
|---|---|
| Pode copiar (licença livre) | Launch UI, Magic UI grátis e portfólio, Once UI core e starter, Next.js SaaS Starter, Next.js Commerce, vercel/examples, shadcn/ui blocks, shadcn/studio (parte MIT), Page UI, landings shadcn da comunidade, AstroWind, AstroPaper, HTML5 UP (com crédito), Start Bootstrap, HyperUI, Flowbite (núcleo), Kokonut UI, listas awesome (CC0 e MIT) |
| Só link (pago ou restrito) | Once UI magic-portfolio, Magic UI Pro, React Bits, shadcnblocks, Tailark, Aceternity UI, Cruip, Tailwind Plus, Preline, listas sem licença |
| Conferir caso a caso | Galeria de templates da Vercel, Temas do Astro, Diretório de registries do shadcn |
| Inspiração sem código | Godly, Lapa Ninja, Land-book, One Page Love, Awwwards, Codrops |

---

## 1. Pode copiar (licença livre)

### Launch UI
- **Link:** https://github.com/launch-ui/launch-ui (site: launchuicomponents.com)
- **Licença:** MIT. Existe versão Pro paga (essa não é MIT).
- **Pode copiar?** Sim (cerca de 5 MB, 60 arquivos). Commit fixado na proposta: `b0d4d5b`.
- **Stack:** Next 16, React 19, Tailwind 4, shadcn. Também é registry `@launchui`.
- **Estilo:** premium e limpo, animações leves. Um template de landing com muitas seções (hero, logos, recursos, preços, FAQ, CTA).

### Magic UI (componentes) e portfólio
- **Link:** https://github.com/magicuidesign/magicui e https://github.com/dillionverma/portfolio
- **Licença:** MIT nos dois.
- **Pode copiar?** Sim, o grátis: mais de 150 componentes animados e 1 template de portfólio (1,9 MB). Commit fixado do portfólio: `5ef12e4`. Os templates "startup" e "saas" são do **Magic UI Pro, pago**: só link (ver seção 2).
- **Stack:** Next, Tailwind, Motion.
- **Estilo:** animado, "design engineer"; o portfólio é minimalista e premium.

### Once UI (core e starter)
- **Link:** https://github.com/once-ui-system/core e https://github.com/once-ui-system/nextjs-starter
- **Licença:** MIT (`core` e `nextjs-starter`).
- **Pode copiar?** Sim, o starter (296 KB). Commit fixado: `78dcb8d`. **Não** o `magic-portfolio` (ver seção 2).
- **Stack:** Next 16, Once UI.
- **Estilo:** elegante e minimalista.

### Next.js SaaS Starter (oficial)
- **Link:** https://github.com/nextjs/saas-starter
- **Licença:** MIT.
- **Pode copiar?** Sim (556 KB). Commit fixado: `6e33e58`.
- **Stack:** Next 15, Postgres, Drizzle, Stripe, shadcn.
- **Estilo:** funcional e neutro. Um app SaaS com login, pagamentos e painel.

### Next.js Commerce (oficial)
- **Link:** https://github.com/vercel/commerce
- **Licença:** MIT.
- **Pode copiar?** Sim (812 KB). Commit fixado: `3761e52`.
- **Stack:** Next 15, Shopify, Tailwind 4.
- **Estilo:** loja moderna e minimalista.

### vercel/examples
- **Link:** https://github.com/vercel/examples
- **Licença:** MIT.
- **Pode copiar?** Sim, mas a pesquisa recomenda só link para aprender integrações.
- **Stack:** variada. Cerca de 250 exemplos (52 boilerplates de framework, 36 middlewares, 36 "solutions" etc.).
- **Estilo:** técnico, pouco visual.

### shadcn/ui blocks e templates
- **Link:** https://ui.shadcn.com/blocks (repositório `shadcn-ui/ui`)
- **Licença:** MIT.
- **Pode copiar?** Sim, mas é melhor instalar pela ferramenta do shadcn do que copiar.
- **Stack:** React, Tailwind 4. Cerca de 27 a 30 blocos de **app** (painel, login, cadastro, 16 sidebars) e 10 templates de partida (Next, Vite, Astro, React Router, Start). **Não há blocos de landing.**
- **Estilo:** limpo e neutro.

### shadcn/studio
- **Link:** https://github.com/shadcnstudio/shadcn-studio
- **Licença:** MIT na parte aberta; há parte paga.
- **Pode copiar?** Sim, só a parte MIT.
- **Stack:** shadcn. Componentes, blocos e alguns templates.
- **Estilo:** moderno.

### Page UI (Shipixen)
- **Link:** https://github.com/danmindru/page-ui
- **Licença:** MIT.
- **Pode copiar?** Pode, mas é grande: 42 MB e 857 arquivos. A pesquisa B diz "só link". Commit fixado na proposta: `bb4951f` (baixa sob demanda).
- **Stack:** React, Tailwind, shadcn. Muitos componentes de landing (hero, depoimentos, preços).
- **Estilo:** bom, com cara de SaaS.

### Landings shadcn da comunidade
- **Link:** https://github.com/leoMirandaa/shadcn-landing-page e https://github.com/nobruf/shadcn-landing-page
- **Licença:** MIT nos dois.
- **Pode copiar?** Sim (cerca de 650 KB). Commit fixado do primeiro: `ae3716d`.
- **Stack:** React ou Next, shadcn (o primeiro usa Tailwind 3).
- **Estilo:** bom, mas genérico. Uma landing por repositório.

### AstroWind
- **Link:** https://github.com/onwidget/astrowind
- **Licença:** MIT.
- **Pode copiar?** Sim (2,5 MB). Commit fixado: `14e1a69`.
- **Stack:** Astro 7, Tailwind 4.
- **Estilo:** profissional e limpo; o tema Astro mais famoso. Várias páginas (landing, blog, SaaS, startup).

### AstroPaper
- **Link:** https://github.com/satnaing/astro-paper
- **Licença:** MIT.
- **Pode copiar?** Sim.
- **Stack:** Astro.
- **Estilo:** minimalista e tipográfico. Um blog.

### HTML5 UP
- **Link:** https://html5up.net (licença em https://html5up.net/license)
- **Licença:** CC BY 3.0: pode copiar e modificar, inclusive em uso comercial, **dando crédito**.
- **Pode copiar?** Sim, com crédito (manter o aviso CC BY). Não está no `fontes.txt`: baixa-se o zip do site.
- **Stack:** HTML, CSS e jQuery (tecnologia antiga). 44 templates.
- **Estilo:** clássico e bonito; bom para estudar layout.

### Start Bootstrap
- **Link:** https://github.com/StartBootstrap (`startbootstrap-landing-page`, `-agency`, `-creative` e outros)
- **Licença:** MIT (conferida em 3 repositórios).
- **Pode copiar?** Sim, mas o valor visual é baixo.
- **Stack:** Bootstrap 5. Dezenas de templates grátis, um repositório por template (número não conferido).
- **Estilo:** simples e "corporativo".

### HyperUI
- **Link:** https://github.com/markmead/hyperui
- **Licença:** MIT.
- **Pode copiar?** Sim, copiando só trechos.
- **Stack:** Tailwind puro, HTML. Muitos componentes (marketing, app, loja).
- **Estilo:** limpo.

### Flowbite
- **Link:** https://github.com/themesberg/flowbite
- **Licença:** MIT no núcleo; os Flowbite Blocks Pro são pagos.
- **Pode copiar?** Sim, só o grátis.
- **Stack:** Tailwind. Componentes e blocos.
- **Estilo:** corporativo.

### Kokonut UI
- **Link:** https://github.com/kokonut-labs/kokonutui
- **Licença:** MIT.
- **Pode copiar?** Sim, só trechos.
- **Stack:** shadcn, Motion (registry `@kokonutui`).
- **Estilo:** animado e premium.

### Listas "awesome" (inspiração)
- **Links:** `aniftyco/awesome-tailwindcss` (CC0), `one-aalam/awesome-astro` (MIT), `birobirobiro/awesome-shadcn-ui` (MIT), `nordicgiant2/awesome-landing-page` (sem licença), `unicodeveloper/awesome-nextjs` (sem licença).
- **Pode copiar?** As CC0 e MIT, sim. As **sem licença: só link**.
- **Estilo:** centenas de links de inspiração, sem código próprio.

---

## 2. Só link (pago, restrito ou sem licença)

### Once UI magic-portfolio
- **Link:** repositório `magic-portfolio` do Once UI (a pesquisa B não traz a URL completa)
- **Licença:** CC BY-NC 4.0, que **proíbe uso comercial**.
- **Pode copiar?** Não. Só link.
- **Stack/estilo:** Next 16, Once UI; elegante e minimalista.

### Magic UI Pro
- **Link:** https://pro.magicui.design
- **Licença:** paga. Traz os templates "startup" e "saas".
- **Pode copiar?** Não. Só link. (A proposta registra que `pro.magicui.design` respondeu 200 ao teste de link.)
- **Stack/estilo:** Next, Tailwind, Motion; animado.

### React Bits
- **Link:** https://github.com/DavidHDev/react-bits
- **Licença:** MIT + Commons Clause (não pode vender o software em si).
- **Pode copiar?** Usar em sites é permitido, mas **não guardar cópia** no repositório; só link. Foi baixado só para consulta.
- **Stack:** React. Muitos componentes animados (número não conferido).
- **Estilo:** muito animado e chamativo.

### shadcnblocks (parte grátis)
- **Link:** https://github.com/shadcnblocks/shadcn-ui-blocks
- **Licença:** MIT + Commons Clause. O site completo é pago (mais de 1.400 blocos).
- **Pode copiar?** Não. Só link.
- **Stack/estilo:** shadcn; premium; parte grátis pequena.

### Tailark
- **Link:** https://tailark.com
- **Licença:** não informada no site. Há "Get full access" (pago); a pesquisa não achou repositório público.
- **Pode copiar?** Não. Só link.
- **Stack/estilo:** shadcn e Next; blocos de marketing e kits; premium e minimalista.

### Aceternity UI
- **Link:** https://ui.aceternity.com/licence
- **Licença:** própria; **proíbe redistribuir os arquivos-fonte**, mesmo modificados.
- **Pode copiar?** Não. Só link.
- **Stack/estilo:** Next, Motion; componentes grátis e templates pagos; muito animado.

### Cruip (templates grátis)
- **Link:** https://github.com/cruip/open-react-template e `tailwind-landing-page-template`
- **Licença:** GPL-3.0, e o README pede "não republicar nem redistribuir".
- **Pode copiar?** Não. Só link.
- **Stack/estilo:** Next, Tailwind; premium; alguns grátis e muitos pagos.

### Tailwind Plus (antigo Tailwind UI)
- **Link:** https://tailwindcss.com/plus
- **Licença:** paga (cerca de US$ 299 pessoal e US$ 979 time, pagamento único, nos preços vistos). **Proíbe publicar repositório com os componentes.**
- **Pode copiar?** Não. Só link.
- **Stack/estilo:** React, HTML, Tailwind; premium, referência do setor. Uma fonte secundária (Untitled UI) diz que as vendas pararam em 09/2026: **não confirmado**.

### Preline UI
- **Link:** https://github.com/htmlstreamofficial/preline
- **Licença:** dupla: MIT + "Preline UI Fair Use License".
- **Pode copiar?** Cuidado: ler a Fair Use antes. Por enquanto, só link.
- **Stack/estilo:** Tailwind; moderno.

### Repositórios sem licença
- `awesome-nextjs` e `awesome-landing-page` (ver seção 1, listas). Sem licença, só link.

---

## 3. Conferir caso a caso

### Galeria de templates da Vercel
- **Link:** https://vercel.com/templates
- **Licença:** cada template tem a sua.
- **Pode copiar?** Depende; só link por padrão.
- **Stack/estilo:** variada (Next, Astro, Nuxt, Svelte); centenas; de básico a premium.

### Temas do Astro (catálogo)
- **Link:** https://astro.build/themes
- **Licença:** cada tema tem a sua. Há grátis e pagos; os destaques são pagos.
- **Pode copiar?** Depende; só link por padrão.
- **Stack/estilo:** Astro; centenas (o site não mostra o total).

### Diretório de registries do shadcn
- **Link:** https://ui.shadcn.com/docs/directory
- **Licença:** cada registry tem a sua. São 435 registries.
- **Pode copiar?** Só link; é o melhor índice vivo de blocos. Instalar com a ferramenta do shadcn e **ler o código antes** (o próprio shadcn avisa).
- **Stack/estilo:** shadcn; de básico a premium.

---

## 4. Inspiração sem código

Godly, Lapa Ninja, Land-book, One Page Love, Awwwards e Codrops. Só para olhar e citar como link; **nunca copiar nem raspar**. As demos do Codrops têm termos próprios.

Também ficam fora do repositório: o **Refero** (os termos proíbem copiar; só pelo caminho oficial da `estilos-premium`).

## Observação

As coleções de fichas de design de marcas (awesome-design-md) têm ficha própria, feita por outra agente, e não aparecem aqui.
