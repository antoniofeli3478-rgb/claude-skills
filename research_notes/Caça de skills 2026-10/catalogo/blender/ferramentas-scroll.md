---
nome: "Ferramentas e técnicas de animação ligada à rolagem do site (não são skills)"
data_verificada: "2026-10-11 (pesquisa B de scroll; suporte e licenças conferidos em out/2026 pela própria pesquisa)"
categoria_nossa: "sites"
status: "so-referencia"
onde_entrou: "skills/sites/scroll-cinematico (já na main, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/video-ia/pesquisa-B.md", "research_notes/Caça de skills 2026-10/video-ia/TRANSCRICAO.md", "research_notes/Caça de skills 2026-10/video-ia/RESUMO.md", "reports/Blender premium, sites com IA, scroll e vídeo com IA (2026-10-12).md"]
---
# Ferramentas e técnicas de scroll cinematográfico

Estas ferramentas NÃO são skills, por isso não têm ficha própria. Cada uma tem uma seção curta: licença, uso comercial, requisitos de GPU e nota.

**Como ler**
- Não existe nota numérica da pesquisa para estas ferramentas. Em vez dela vai o papel que cada uma tem na proposta `scroll-cinematico`.
- 'Uso comercial no grátis': aqui todas são bibliotecas ou recursos do navegador, grátis. A única com licença não livre é o GSAP.
- A pesquisa de scroll avaliou ferramentas e técnicas, e não skills de terceiros. As únicas skills de animação de site citadas (`gsap-timeline`, `hyperframes-*` e `threejs-animation`, esta dentro de `cloudai-x__threejs-skills`) têm ficha própria nesta pasta.
- Teste feito: página com 60 quadros em canvas, no Chromium sem janela (Playwright), em 4 posições da rolagem; o quadro certo apareceu em todas, sem texto sobre texto, e 'reduzir movimento' mostrou o pôster parado. O componente Next.js com Motion NÃO foi testado num app real nem num celular.

---

## Técnicas e bibliotecas

### Sequência de imagens num canvas (técnica principal, sem biblioteca)
- **Licença:** não se aplica (HTML, CSS e JavaScript próprios). **Uso comercial:** livre.
- **GPU:** nenhum requisito especial (canvas 2D). Cuidado com a memória: cada quadro decodificado ocupa largura × altura × 4 bytes (1920×1080 = 8,3 MB; 150 quadros = 1,2 GB), então não segurar todos na memória.
- **Números:** 60 a 150 quadros; no máximo 20 a 25 px de rolagem por quadro; pin de 200 a 400vh por cena (acima de 5 telas vira sequestro de rolagem); orçamento total de 3 a 6 MB no desktop e 1,5 a 2,5 MB no celular, com render vertical próprio de 720 a 900 px.
- **Formato:** WebP com alpha é o padrão (13,3 KB por quadro, carrega em 13 ms). AVIF fica 2 a 3 vezes menor, mas decodifica 2,5 vezes mais devagar (32,6 ms), o que pesa no scroll.
- **Referência da Apple:** AirPods Pro (2019) usava 148 JPGs num canvas; fontes de 2025-26 citam 60 a 180 imagens por sequência.
- **Nota:** é a técnica principal da proposta. Boa para produto que gira, abre ou monta; permite alpha (produto por cima do texto).

### Vídeo com `currentTime` ligado ao scroll
- **Licença:** não se aplica. **GPU:** decodificação de vídeo do aparelho.
- **Quando usar:** cena longa (acima de 150 quadros), fotográfica ou vídeo de IA.
- **Cuidado:** recodificar em all-intra (`-g 1`, todo quadro é keyframe); senão o vídeo pula em blocos. All-intra pesa de 3 a 5 vezes mais (1280 px, 30 fps: cerca de 8 MB em MP4 e 13 MB em WebM). No iOS o `preload` costuma ser ignorado. Alpha em vídeo não funciona igual em todo navegador (VP9 com alpha no Chrome/Firefox, HEVC com alpha no Safari): se o produto precisa ficar por cima do texto, preferir a sequência WebP.

### CSS scroll-driven (`animation-timeline: scroll()/view()`) e `ScrollTimeline`
- **Licença:** padrão da web. **GPU:** roda no compositor (só `transform` e `opacity`).
- **Suporte (out/2026):** Chrome e Edge 115, Chrome Android 115, Safari e Safari iOS 26. Firefox: só 'preview' (MDN marca 'Limited availability'). `animation-trigger`: só Chromium 146+. Precisa de `@supports` e de um fallback em JavaScript.
- **Limite:** não desenha canvas. Em CSS puro, sequência de quadros só com sprite sheet e `steps(N)` (até cerca de 30 quadros pequenos, por causa do limite de textura de uns 16 mil px). Se os textos estão em CSS, o canvas NÃO pode ter inércia, senão texto e quadro descolam.
- **Nota:** para textos, cartões, parallax e barra de progresso.

### GSAP e ScrollTrigger (scrub, pin, snap, markers)
- **Licença:** 'Standard No Charge License' da Webflow, em vigor desde 30/04/2025 (atualizada em 30/05/2025). PROPRIETÁRIA, não é MIT. Versão no npm: 3.15.0.
- **Uso comercial:** liberado, com todos os plugins (inclui ScrollTrigger e SplitText). É proibido usar o GSAP para construir uma ferramenta visual de animação sem código que concorra com o Webflow. Gerar código GSAP com IA é permitido.
- **GPU:** nenhum requisito. **Cuidado:** não copiar texto da documentação (licença proprietária); os trechos da nossa proposta são próprios. Com GSAP o pin é do próprio GSAP: não misturar com `position: sticky` nem com `height: 400vh`. Em React, usar `useGSAP()`.
- **Nota:** opcional, para coreografia complexa com muitas faixas e para depuração (`markers`); `scrub: 0.5` dá inércia. Adiciona peso à página.

### Lenis (rolagem suave)
- **Licença:** MIT (darkroom.engineering, versão 1.3.x). **Uso comercial:** livre. **GPU:** nenhum.
- **Uso:** 'sensação premium' no desktop, com `lerp` perto de 0,1; integra com o ScrollTrigger (`lenis.on('scroll', ScrollTrigger.update)`).
- **Cuidado:** desligar quando o usuário pede 'reduzir movimento'; não pode quebrar a rolagem por teclado nem o iOS.
- **Nota:** opcional.

### Motion (`useScroll`, `useTransform`, `useSpring`)
- **Licença:** MIT. **Uso comercial:** livre. **GPU:** nenhum.
- **Uso:** caminho natural em projeto React/Next.js (o nosso padrão). Combina com a skill `motion-oficial`. Mola `{stiffness: 200, damping: 40}` fica sem overshoot e dá o mesmo progresso suavizado para quadros e textos.
- **Cuidado:** roda na thread principal quando o valor vai para JavaScript (o canvas). O componente `SequenciaScroll.tsx` proposto não foi testado num app Next.js real.
- **Nota:** padrão para React/Next.js na proposta.

### Three.js, React Three Fiber e drei (`ScrollControls`)
- **Licença:** MIT. **Uso comercial:** livre.
- **GPU:** usa a GPU do aparelho do visitante; no celular pode pesar, e a aparência depende do aparelho. Qualidade abaixo do Cycles do Blender.
- **Quando vale:** quando a pessoa deve girar o produto com o dedo, ou quando um GLB de 1 a 3 MB sai mais leve que 150 quadros. Para beleza cinematográfica, quadros pré-renderizados no Blender ganham.
- **Nota:** só para produto interativo.

### View Transitions (mesmo documento)
- **Licença:** padrão da web. Baseline desde out/2025 (Chrome 125, Safari 18, Firefox 144).
- **Limite:** NÃO é scrub de rolagem. Serve para troca de estado ou de página (por exemplo, cartão que vira página do produto). **Nota:** complemento, não técnica principal.

---

## Acessibilidade e desempenho (valem para todas)
- Quem pede 'reduzir movimento' (`prefers-reduced-motion`) vê uma versão PARADA: pôster mais textos lado a lado, sem pin, sem scrub, sem Lenis e sem parallax. Sem JavaScript também aparece o pôster.
- Texto real no DOM; o canvas leva `role="img"` e `aria-label`. Rolagem nativa (teclado e leitor de tela funcionam). Pin de no máximo 3 a 5 telas. WCAG 2.3.3 e 2.2.2.
- No scroll, ler só `scrollY` (passivo) e usar um `requestAnimationFrame`. Só `transform` e `opacity` nos textos.
- Celular: conjunto vertical próprio de quadros; com `Save-Data`, conjunto leve ou só o pôster; palco em `100svh` e seção em `lvh`.

## As 4 regras do editor aplicadas ao scroll (resumo)
1. Nada de texto sobre texto (faixas de progresso que não se sobrepõem).
2. Nenhum recorte no meio da tela (sem `clip-path`; o `overflow: clip` fica só na borda do palco).
3. O elemento principal (produto com fundo transparente) fica por cima do texto.
4. O texto entra pela borda da tela ou de trás do produto.

## Fontes
- `research_notes/Caça de skills 2026-10/video-ia/pesquisa-B.md` (seções 1, 2, 3 e 5)
- `research_notes/Caça de skills 2026-10/video-ia/TRANSCRICAO.md` e `RESUMO.md`
- `reports/Blender premium, sites com IA, scroll e vídeo com IA (2026-10-12).md`
- Links citados pela pesquisa: https://gsap.com/community/standard-license/ , https://developer.mozilla.org/en-US/docs/Web/CSS/animation-timeline , https://developer.chrome.com/docs/css-ui/scroll-driven-animations , https://motion.dev/docs/react-use-scroll , https://web.dev/blog/same-document-view-transitions-are-now-baseline-newly-available
