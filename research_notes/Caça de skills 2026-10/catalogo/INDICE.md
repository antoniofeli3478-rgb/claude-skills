# Catálogo de skills pesquisadas (caça de skills, outubro de 2026)

Uma ficha `.md` para cada skill, MCP ou fonte que avaliamos. Cada ficha tem: origem, licença, instalações, status, onde entrou na nossa estrutura, o que aproveitamos, o que não aproveitamos, riscos e a **transcrição** (o que aprendemos, nas nossas palavras).

**Como ler o status:**
- `instalada-intacta`: está no repositório em `skills/`, cópia sem alteração (o `ORIGEM.md` de cada skill diz de onde veio).
- `proposta-aguardando-aprovacao`: foi proposta e ainda espera a decisão do Daniel.
- `ideias-aproveitadas`: não copiamos, mas as ideias entraram numa skill nossa.
- `so-referencia`: avaliada e guardada só como referência.
- `descartada`: não serve, com o motivo na ficha.

Os status foram atualizados em 12/10/2026, depois da aprovação do Daniel (PR #21). Os caminhos de `fontes_locais` que começam com `scratchpad/` apontavam para uma pasta temporária da sessão; o conteúdo equivalente está em `research_notes/Caça de skills 2026-10/`.

Os arquivos `TRANSCRICAO.md` completos de cada tema ficam em `research_notes/Caça de skills 2026-10/<tema>/`. O índice das skills instaladas é o `guia/HABILIDADES.txt`.

**Total: 138 fichas.** Por status: ideias-aproveitadas 47, so-referencia 46, descartada 29, instalada-intacta 16.

## Movimento e botões (animação de interface) (46 fichas)

| Skill | Licença | Status | Nota | Entrou em |
|---|---|---|---|---|
| [frontend-design](design-movimento/anthropics__frontend-design.md) | Apache-2.0 | ideias-aproveitadas | "4 (motion B) / 5 (botões B)" | skills/design/button-animation/SKILL.md (§7 erros típicos de IA; ideia parafraseada) |
| [Animata (status-button, duolingo, swipe-button, shining-button)](design-movimento/codse__animata.md) | MIT | ideias-aproveitadas | 7 | skills/design/button-animation/SKILL.md (§4 receitas 12 botão 3D e 13 troca de texto no hover; ideia do botão de estados em assets/status-button.tsx) |
| [React Bits (ClickSpark, StarBorder, Magnet)](design-movimento/davidhdev__react-bits.md) | MIT + Commons Clause (não pode revender o código) | descartada | 5 | — |
| [animation-principles (buttons-ctas)](design-movimento/dylantarre__animation-principles.md) | MIT | ideias-aproveitadas | 5 | skills/design/button-animation/SKILL.md (§4 receita 4, tremor de erro: só os tempos) |
| [animate-expo](design-movimento/emilkowalski__animate-expo.md) | MIT (licença do repositório) | so-referencia | sem nota | — |
| [animate](design-movimento/emilkowalski__animate.md) | MIT | ideias-aproveitadas | 10 | skills/design/motion-oficial/SKILL.md (ordem de trabalho e "Nunca entregar"); skills/design/motion-oficial/references/receitas.md e references/molas-e-curvas.md; skills/design/button-animation/SKILL.md (§1 Antes de animar, §2 Números, §4 receitas 5 e 6, §6 e §7) |
| [animation-vocabulary](design-movimento/emilkowalski__animation-vocabulary.md) | MIT (licença do repositório) | instalada-intacta | sem nota | skills/design/animation-vocabulary/ (cópia intacta, MIT) (instalada na main em 12/10/2026) |
| [apple-design](design-movimento/emilkowalski__apple-design.md) | MIT | ideias-aproveitadas | 8 (motion A) / 10 (motion B e botões, nota do grupo Emil) | skills/design/motion-oficial/references/gestos.md e references/molas-e-curvas.md; skills/design/button-animation/SKILL.md (§2.3 Molas: padrão sem quique) |
| [emil-design-eng](design-movimento/emilkowalski__emil-design-eng.md) | MIT | ideias-aproveitadas | 10 (nota do grupo Emil, botões) | skills/design/button-animation/SKILL.md (§1 Antes de animar, §2 Números, §4 receitas, §6 Desempenho, §7 Nunca entregue) |
| [find-animation-opportunities](design-movimento/emilkowalski__find-animation-opportunities.md) | MIT (licença do repositório) | so-referencia | 10 (nota do grupo Emil, botões; não avaliada sozinha) | — |
| [improve-animations](design-movimento/emilkowalski__improve-animations.md) | MIT (licença do repositório) | so-referencia | 10 (nota do grupo Emil, motion B; não avaliada sozinha) | — |
| [review-animations](design-movimento/emilkowalski__review-animations.md) | MIT | instalada-intacta | 10 (nota do grupo Emil nas duas pesquisas) | skills/design/review-animations/ (cópia intacta, MIT, com STANDARDS.md); as ideias já estavam em motion-oficial/references/revisao.md e button-animation (instalada na main em 12/10/2026) |
| [motion-framer](design-movimento/freshtechbro__motion-framer.md) | não verificado | descartada | sem nota | — |
| [Material Design 3 (tokens de movimento e de estado)](design-movimento/google__material-3-tokens.md) | Apache-2.0 | ideias-aproveitadas | "7 (motion B) / 8 (botões B)" | skills/design/motion-oficial/references/molas-e-curvas.md §1–2; skills/design/button-animation/SKILL.md (§2.3 Molas; §3 Estados) |
| [GSAP skills oficiais (gsap-performance, gsap-timeline, gsap-core)](design-movimento/greensock__gsap-skills.md) | MIT | ideias-aproveitadas | "descartada (A) / 5 (B, só ideias de coreografia)" | skills/design/motion-oficial/references/api-motion.md §6 (só a ideia de sequência com posição relativa) |
| [hyperframes](design-movimento/heygen-com__hyperframes.md) | não verificado | descartada | sem nota | — |
| [baseline-ui](design-movimento/ibelick__baseline-ui.md) | MIT | ideias-aproveitadas | "8 (motion B, grupo ui-skills) / 6 (botões A)" | skills/design/button-animation/SKILL.md (regra de feedback até 200 ms) |
| [fixing-motion-performance](design-movimento/ibelick__fixing-motion-performance.md) | MIT | ideias-aproveitadas | "7 (motion A) / 8 (motion B, grupo ui-skills) / 6 (botões A)" | skills/design/motion-oficial/references/revisao.md §4; skills/design/button-animation/SKILL.md (§6 Desempenho) |
| [Motion Primitives (Magnetic, BorderTrail, GlowEffect, TextMorph)](design-movimento/ibelick__motion-primitives.md) | MIT | ideias-aproveitadas | 8 | skills/design/button-animation/SKILL.md (§4: botão magnético, troca de texto; cometa na borda para vídeo) |
| [transitions-polish / transitions-dev](design-movimento/jakubantalik__transitions-dev.md) | licença própria: pode usar em projetos, mas NÃO pode redistribuir a coleção | so-referencia | 3 | — |
| [better-accessibility](design-movimento/jakubkrehel__better-accessibility.md) | MIT | ideias-aproveitadas | 9 (nota do repositório jakubkrehel/skills, botões) | skills/design/button-animation/SKILL.md (§5 Acessibilidade) |
| [better-interface](design-movimento/jakubkrehel__better-interface.md) | MIT (licença do repositório) | so-referencia | sem nota | — |
| [better-ui](design-movimento/jakubkrehel__better-ui.md) | MIT | ideias-aproveitadas | 9 | skills/design/button-animation/SKILL.md (§2 Números, §3 Estados, §4 receitas de toque e troca de ícone, §5 Acessibilidade) |
| ["Springs and Bounces in Native CSS" (Josh Comeau)](design-movimento/joshcomeau__springs-bounces-linear.md) | sem licença aberta (só ideias, sem colar texto) | ideias-aproveitadas | 6 | skills/design/motion-oficial/references/molas-e-curvas.md §3 |
| [design-motion-principles](design-movimento/kylezantos__design-motion-principles.md) | MIT | ideias-aproveitadas | 8 | skills/design/motion-oficial/references/revisao.md §3; references/coreografia.md §4; references/receitas.md; skills/design/button-animation/SKILL.md (§7 erros típicos de IA) |
| [taste-skill](design-movimento/leonxlnx__taste-skill.md) | MIT | descartada | "descartada (A) / 4 (B)" | — |
| [Magic UI (shimmer-button, shine-border, ripple-button, rainbow-button)](design-movimento/magicuidesign__magicui.md) | MIT | ideias-aproveitadas | 9 | skills/design/button-animation/SKILL.md (§4: ripple, shimmer e borda de gradiente com máscara) |
| [ui-animation](design-movimento/mblode__ui-animation.md) | MIT | ideias-aproveitadas | 9 | skills/design/motion-oficial/SKILL.md; references/api-motion.md §7; references/molas-e-curvas.md; references/revisao.md §5; references/coreografia.md §6 |
| [framer-motion](design-movimento/mindrally__framer-motion.md) | não verificado | descartada | sem nota | — |
| [Documentação do Motion: acessibilidade](design-movimento/motion-dev__docs-acessibilidade.md) | documentação (só citar, não colar) | ideias-aproveitadas | 7 | skills/design/motion-oficial/SKILL.md (passo 7 Acessibilidade); skills/design/button-animation/SKILL.md (§5) |
| [motion (skill oficial do pacote motion-ai)](design-movimento/motiondivision__motion-skill-oficial.md) | MIT (campo license do package.json; o pacote não traz arquivo LICENSE) | ideias-aproveitadas | sem nota (é a base que as pesquisas tentam completar) | skills/design/motion-oficial/ (SKILL.md "Motion: o essencial"; references/api-motion.md, codex-mcp.md, molas-e-curvas.md §3–4, revisao.md §7; agents/motion-reviewer.md); skills/design/button-animation/SKILL.md (§0 e §8: instalação, imports, recursos do Motion) |
| [GRADIENT-BUTTON.txt (prompt original do componente Compose)](design-movimento/nosso__gradient-button-original.md) | sem licença declarada | ideias-aproveitadas | sem nota | skills/design/button-animation/assets/compose.tsx e compose-demo.tsx; SKILL.md §4 receitas 6 a 10 e 16 (popover com mola, anel de gradiente com @property, destaque deslizante, anel de progresso, pop, barra de rolagem) e §9 |
| [framer-motion-animator](design-movimento/patricio0312rev__framer-motion-animator.md) | não verificado | descartada | sem nota | — |
| [impeccable (animate, delight, polish, harden, craft-floor)](design-movimento/pbakaus__impeccable.md) | Apache-2.0 (com NOTICE.md) | ideias-aproveitadas | 5 | skills/design/motion-oficial/references/coreografia.md §1 (motion thesis, reescrita); skills/design/button-animation/SKILL.md (ideias: movimento reduzido é menos, não zero; evitar duplo envio) |
| [emilkowal-animations (dot-skills)](design-movimento/pproenca__emilkowal-animations.md) | MIT (2025) | descartada | "3 (motion B) / descartada (A)" | — |
| [userinterface-wiki](design-movimento/raphaelsalaja__userinterface-wiki.md) | MIT | ideias-aproveitadas | 7 | skills/design/motion-oficial/references/api-motion.md §7 |
| [Rauno Freiberg: "Web Interface Guidelines" (interfaces)](design-movimento/raunofreiberg__interfaces.md) | sem licença (só ideias) | ideias-aproveitadas | "5 (motion B) / 6 (botões B)" | skills/design/motion-oficial/references/receitas.md (troca de tema sem transição, press ~0,96); skills/design/button-animation/SKILL.md (escala proporcional ao gatilho; desativado sem tooltip) |
| [Remotion skills oficiais (remotion-markup, remotion-create)](design-movimento/remotion-dev__skills.md) | sem arquivo LICENSE no repositório (licença do Remotion): só ideias | ideias-aproveitadas | 6 | skills/design/motion-oficial/SKILL.md (seção Vídeo) e references/coreografia.md §5 |
| [Motion Lexicon (skill motion-lexicon)](design-movimento/ryan-yang125__motion-lexicon.md) | código MIT; texto e dados CC BY 4.0 (arquivo CONTENT-LICENSE). A pesquisa B de motion o listou só como MIT; corrigido na transcrição. | ideias-aproveitadas | "8 (motion B) / 7 (botões B)" | skills/design/motion-oficial/references/coreografia.md §2–3 e references/revisao.md §6; skills/design/button-animation/SKILL.md (tempos 240/150/150 ms, geometria estável, tremor de erro) |
| [shadcn (skill) e shadcn/ui Button (padrão de foco)](design-movimento/shadcn-ui__shadcn.md) | MIT (c) 2023 shadcn | ideias-aproveitadas | 5 (Button, botões B) | skills/design/button-animation/SKILL.md (§5 Acessibilidade: foco visível) |
| [ui-ux-pro-max](design-movimento/ui-ux-pro-max__ui-ux-pro-max.md) | não verificado | descartada | sem nota | — |
| [vercel-react-view-transitions](design-movimento/vercel-labs__vercel-react-view-transitions.md) | não verificado | descartada | sem nota | — |
| [web-design-guidelines](design-movimento/vercel-labs__web-design-guidelines.md) | não verificado | descartada | sem nota | — |
| [Web Interface Guidelines (seção Animation e controles)](design-movimento/vercel-labs__web-interface-guidelines.md) | MIT | ideias-aproveitadas | "7 (motion B) / 9 (botões B)" | skills/design/motion-oficial/SKILL.md (passos 3, 4 e 7); skills/design/button-animation/SKILL.md (§3 Estados, §5 Acessibilidade) |
| [microinteractions](design-movimento/wondelai__microinteractions.md) | não verificado | so-referencia | sem nota | — |
| [interaction-design](design-movimento/wshobson__interaction-design.md) | MIT | ideias-aproveitadas | "6,5 (botões A) / 6 (botões B)" | skills/design/button-animation/SKILL.md (§3 Estados; §4 receita 3, botão com estados; receita 4, tremor de erro) e assets/status-button.tsx (escrito por nós) |

## UI/UX, estilos premium e a lista do Daniel (33 fichas)

| Skill | Licença | Status | Nota | Entrou em |
|---|---|---|---|---|
| [Magic MCP (agora 21st MCP)](design-ui/21st-dev__magic-mcp-21st.md) | ISC (o repasse magic-mcp); MIT (o CLI). Serviço e componentes: pagos, licença de cada componente é do autor (não verificado) | so-referencia | sem nota | skills/sites/mcps-para-sites/ (só descrito, com a recomendação de NÃO ligar por ora: pago e envia contexto à 21st.dev) (instalada na main em 12/10/2026, PR #21) |
| [frontend-design](design-ui/anthropics__frontend-design.md) | Apache-2.0 (LICENSE.txt dentro da pasta da skill) | instalada-intacta | 9 | skills/design/frontend-design/ (cópia intacta) e ideias em skills/design/ui-ux-pro-max/ (SKILL.md e references/anti-padroes-ia.md) (instalada na main em 12/10/2026, PR #21) |
| [theme-factory](design-ui/anthropics__theme-factory.md) | Apache-2.0 (segundo a pesquisa B de sites, junto com web-artifacts-builder; arquivo da pasta não conferido por nós) | so-referencia | sem nota | — |
| [extract-design-system](design-ui/arvindrk__extract-design-system.md) | não verificado | so-referencia | sem nota | — |
| [awesome-design-skills (typeui.sh)](design-ui/bergside__awesome-design-skills.md) | MIT | descartada | sem nota | — (usada apenas como anti-exemplo) |
| [Chrome DevTools MCP](design-ui/chromedevtools__chrome-devtools-mcp.md) | Apache-2.0 | ideias-aproveitadas | sem nota | skills/sites/mcps-para-sites/ (instruções com as opções de privacidade; passo 10 do combo-site-premium) (instalada na main em 12/10/2026, PR #21) |
| [apple-design (ficha curta; ficha principal na área de movimento)](design-ui/emilkowalski__apple-design.md) | MIT (Copyright (c) 2026 Emil Kowalski) | ideias-aproveitadas | 9 | skills/design/ui-ux-pro-max/references/estilos-premium.md e SKILL.md (movimento, etapa 5) |
| [emil-design-eng](design-ui/emilkowalski__emil-design-eng.md) | MIT (Copyright (c) 2026 Emil Kowalski) | ideias-aproveitadas | 9 | skills/design/ui-ux-pro-max/SKILL.md (revisão e movimento) e references/estilos-premium.md |
| [pick-ui-library](design-ui/emilkowalski__pick-ui-library.md) | MIT (Copyright (c) 2026 Emil Kowalski) | ideias-aproveitadas | 9 | skills/design/ui-ux-pro-max/SKILL.md (lista de bibliotecas, regra 'uma recomendação, não um menu') |
| [scandinavian-design](design-ui/ericzakariasson__scandinavian-design.md) | não verificado | so-referencia | sem nota | — |
| [design.md (padrão aberto do Google e CLI @google/design.md)](design-ui/google-labs-code__design-md.md) | Apache-2.0 | ideias-aproveitadas | sem nota | skills/design/ui-ux-pro-max/references/modelo-DESIGN.md e SKILL.md (etapa 4: lint e export) |
| [design-md (stitch-skills)](design-ui/google-labs-code__stitch-skills-design-md.md) | não verificado | so-referencia | sem nota | — |
| [ui-skills (baseline-ui, create-design-md, fixing-accessibility, fixing-motion-performance)](design-ui/ibelick__ui-skills.md) | MIT | so-referencia | sem nota | — |
| [better-colors](design-ui/jakubkrehel__better-colors.md) | não verificado | so-referencia | sem nota | — |
| [oklch-skill](design-ui/jakubkrehel__oklch-skill.md) | não verificado | so-referencia | sem nota | — |
| [premium-frontend-design](design-ui/kv0906__premium-frontend-design.md) | não verificado | so-referencia | sem nota | — |
| [design-taste-frontend (Taste Skill v2)](design-ui/leonxlnx__design-taste-frontend.md) | MIT (Copyright (c) 2026 Leonxlnx; LICENSE lido) | instalada-intacta | 9 | skills/design/taste-skill/ (cópia intacta) e ideias em skills/design/ui-ux-pro-max/ (SKILL.md, references/estilos-premium.md, minimalismo.md, anti-padroes-ia.md) (instalada na main em 12/10/2026, PR #21) |
| [high-end-visual-design](design-ui/leonxlnx__high-end-visual-design.md) | MIT (Copyright (c) 2026 Leonxlnx) | ideias-aproveitadas | 9 | skills/design/ui-ux-pro-max/references/estilos-premium.md e SKILL.md (bisel duplo, arquétipos premium, espaços de seção) |
| [image-to-code](design-ui/leonxlnx__image-to-code.md) | MIT (mesmo LICENSE da Taste Skill, Copyright (c) 2026 Leonxlnx) | instalada-intacta | sem nota | skills/design/image-to-code/ (cópia intacta, opcional: exige gerador de imagem e gasta créditos) (instalada na main em 12/10/2026, PR #21) |
| [minimalist-ui](design-ui/leonxlnx__minimalist-ui.md) | MIT (Copyright (c) 2026 Leonxlnx) | ideias-aproveitadas | 9 | skills/design/ui-ux-pro-max/references/minimalismo.md |
| [redesign-existing-projects](design-ui/leonxlnx__redesign-existing-projects.md) | MIT (Copyright (c) 2026 Leonxlnx) | so-referencia | 9 | — |
| [playwright-cli](design-ui/microsoft__playwright-cli.md) | Apache-2.0 (Copyright Microsoft) | instalada-intacta | sem nota | skills/sites/playwright-cli/ (cópia intacta; o programa e o navegador são instalados por fora, com aprovação) (instalada na main em 12/10/2026, PR #21) |
| [ui-ux-pro-max (do nextlevelbuilder)](design-ui/nextlevelbuilder__ui-ux-pro-max.md) | MIT (Copyright (c) 2024 Next Level Builder); a versão paga uupm.cc fica com marca, logo e assets | ideias-aproveitadas | 7 | skills/design/ui-ux-pro-max/ (SKILL.md etapas 1, 3 e 4; references/checklist.md). A skill antiga nossa era só um ponteiro para este projeto; o ponteiro segue em original/ux-ui-disign-link |
| [impeccable](design-ui/pbakaus__impeccable.md) | Apache-2.0, com NOTICE.md (ios.md e android.md derivam de ehmo/platform-design-skills, MIT) | ideias-aproveitadas | 7 | skills/design/ui-ux-pro-max/ (SKILL.md, references/checklist.md, references/anti-padroes-ia.md) |
| [hig-doctor](design-ui/raintree-technology__hig-doctor.md) | MIT para o código; o material da Apple HIG continua sob os termos da Apple (diz o THIRD_PARTY_NOTICES do projeto) | so-referencia | sem nota | — |
| [refero-design (skill oficial do Refero)](design-ui/referodesign__refero-design-skill.md) | MIT (Copyright (c) 2026 Refero) | ideias-aproveitadas | sem nota | skills/design/ui-ux-pro-max/ (SKILL.md etapas 1 a 3; references/anti-padroes-ia.md; checklist.md seção 7) e skills/design/estilos-premium/ (adaptações em português). A skill inteira NÃO foi copiada |
| [Refero MCP (servidor oficial)](design-ui/referodesign__refero-mcp.md) | proprietária (serviço pago); termos de uso atualizados em 03/08/2026 | so-referencia | sem nota | — (só mencionado como opção paga em skills/design/estilos-premium/ e skills/design/ui-ux-pro-max/) |
| [shadcn/ui MCP](design-ui/shadcn-ui__shadcn-mcp.md) | MIT | ideias-aproveitadas | sem nota | skills/sites/mcps-para-sites/ (instruções para o computador do Daniel; a skill shadcn já cobre o mesmo pela linha de comando) (instalada na main em 12/10/2026, PR #21) |
| [ios-design](design-ui/uizze__ios-design.md) | não verificado | descartada | sem nota | — |
| [ui-taste](design-ui/uizze__ui-taste.md) | não verificado | descartada | sem nota | — |
| [web-design-guidelines (Web Interface Guidelines)](design-ui/vercel-labs__web-design-guidelines.md) | MIT. As regras: LICENSE MIT (Copyright (c) 2025 Vercel Labs). O repositório agent-skills não tem arquivo LICENSE; só o README diz MIT | instalada-intacta | 8 | skills/design/web-design-guidelines/ (cópia intacta + regras fixadas) e resumo em skills/design/ui-ux-pro-max/references/checklist.md (instalada na main em 12/10/2026, PR #21) |
| [awesome-design-md (Awesome Design)](design-ui/voltagent__awesome-design-md.md) | MIT (Copyright (c) 2026 VoltAgent). Marcas, logos e fontes continuam sendo dos donos | instalada-intacta | sem nota | skills/design/estilos-premium/references/marcas/ (15 de 74 fichas, sem alteração). Fatos pontuais de 16 marcas em skills/design/ui-ux-pro-max/references/. As outras 59 estão como linha de download em skills/sites/referencia-sites (baixar só a ficha da marca escolhida) |
| [tailwind-design-system](design-ui/wshobson__tailwind-design-system.md) | não verificado | so-referencia | sem nota | — |

## Blender e vídeo com IA (36 fichas)

| Skill | Licença | Status | Nota | Entrou em |
|---|---|---|---|---|
| [blender-motion-state-inspection (ECC)](blender/affaan-m__blender-motion-state-inspection.md) | MIT | ideias-aproveitadas | 2 | (ideia de método) verifica_curvas.py em skills/video/blender-ia (instalada na main em 12/10/2026, PR #21) |
| [MCP for Blender (antigo blender-mcp)](blender/ahujasid__mcp-for-blender.md) | MIT | ideias-aproveitadas | 7 | skills/video/blender-ia/SKILL.md (seção 'No computador do Daniel (com placa de vídeo e MCP)') |
| [GenesisCore (MCP de Blender)](blender/aigodlike__genesiscore.md) | não verificado (a pesquisa não registra) | descartada | 1 | — |
| [arjun988/blender-skills (94 skills para assets de jogo)](blender/arjun988__blender-skills.md) | MIT (conferida) | so-referencia | 2 | — |
| [blender-game-skills](blender/autor-nao-verificado__blender-game-skills.md) | não verificado (a pesquisa registra '—' na linha do grupo) | descartada | 1 a 2 (faixa do grupo; a nota individual não foi registrada) | — |
| [claymation (skill de Blender)](blender/autor-nao-verificado__claymation.md) | não verificado (a pesquisa registra '—' na linha do grupo) | descartada | 1 a 2 (faixa do grupo; a nota individual não foi registrada) | — |
| [gsap-timeline](blender/autor-nao-verificado__gsap-timeline.md) | não verificado (a pesquisa registra 'várias') | so-referencia | sem nota | — |
| [hyperframes-* (família de skills)](blender/autor-nao-verificado__hyperframes.md) | não verificado (a pesquisa registra 'várias') | so-referencia | sem nota | — |
| [Texturaizer](blender/autor-nao-verificado__texturaizer.md) | licença comercial separada (add-on pago) | so-referencia | sem nota | — |
| [Blender Lab MCP (MCP oficial do Blender)](blender/blender-lab__blender-mcp-oficial.md) | GPL-3.0 (cabeçalhos SPDX 'Blender Authors'; uma fonte de terceiros diz GPL-3.0-or-later) | ideias-aproveitadas | 4 | skills/video/blender-ia/SKILL.md (seção 'No computador do Daniel (com placa de vídeo e MCP)') |
| [bpy-dev/blender-mcp (Lab MCP turbinado)](blender/bpy-dev__blender-mcp.md) | GPL-3.0 | so-referencia | 5 | — (ORIGEM.md: reavaliar quando sair do preview) |
| [Dream Textures (Stable Diffusion dentro do Blender)](blender/carson-katri__dream-textures.md) | GPL-3.0 | descartada | 1 | — |
| [CLI-Anything Blender](blender/cli-anything__blender.md) | variada, não conferida | so-referencia | sem nota | — |
| [threejs-skills (threejs-animation e outras)](blender/cloudai-x__threejs-skills.md) | SEM LICENÇA | descartada | 3 | — (ORIGEM.md: não usado) |
| [blender-open-mcp](blender/dhakalnirajan__blender-open-mcp.md) | MIT | descartada | 2 | — |
| [animation-principles (12 princípios de animação)](blender/dylantarre__animation-principles.md) | MIT (2024) | so-referencia | sem nota | — |
| [blender-web-pipeline (claudedesignskills)](blender/freshtechbro__blender-web-pipeline.md) | MIT | so-referencia | 5 | — |
| [BlenderGPT (chat GPT-4 dentro do Blender)](blender/gd3kr__blendergpt.md) | MIT | descartada | 0 | — |
| [blenderwright (antigo blend-ai)](blender/holdmybeer-gg__blenderwright.md) | MIT | ideias-aproveitadas | 6 | skills/video/blender-ia/SKILL.md (seção 'No computador do Daniel (com placa de vídeo e MCP)') |
| [blender-agent-studio](blender/ifbars__blender-agent-studio.md) | não verificado (a pesquisa registra '—' na linha do grupo) | descartada | 1 a 2 (faixa do grupo; a nota individual não foi registrada) | — |
| [jithinolickal/blender](blender/jithinolickal__blender.md) | não verificado (a pesquisa registra '—' na linha do grupo) | descartada | 1 a 2 (faixa do grupo; a nota individual não foi registrada) | — |
| [kevinbadi/blender-skills (planos de câmera para produto)](blender/kevinbadi__blender-skills.md) | SEM LICENÇA (todos os direitos reservados) | ideias-aproveitadas | 7 como ideia; 0 para copiar | skills/video/blender-ia/SKILL.md (só o vocabulário de planos, reescrito do zero) |
| [LobzyJay motion](blender/lobzyjay__motion.md) | não verificado (a pesquisa registra '—' na linha do grupo) | descartada | 1 a 2 (faixa do grupo; a nota individual não foi registrada) | — |
| [major-ai-skills/blender](blender/major-ai-skills__blender.md) | variada, não conferida | so-referencia | sem nota | — |
| [meshy-3d-generation (Meshy, geração de 3D por IA)](blender/meshy-dev__meshy-3d-generation.md) | não verificado (a licença do serviço Meshy é tratada em ferramentas-video-ia.md) | so-referencia | sem nota | skills/video/blender-ia/SKILL.md (apenas a tabela de licenças de modelos 3D por IA) |
| [cc-blender-skill (30 skills de Blender)](blender/roble3__cc-blender-skill.md) | MIT (conferida no LICENSE) | ideias-aproveitadas | 7 | skills/video/blender-ia/SKILL.md (ideias de receitas, gates de qualidade, lentes e razão de luz; nada copiado) (instalada na main em 12/10/2026, PR #21) |
| [StableGen (texturização por IA via ComfyUI)](blender/sakalond__stablegen.md) | GPL-3.0 (add-on); a licença do modelo de IA vale à parte | descartada | 1 | — |
| [scenario-labs/skills (blender-expert, animation, rigging, lighting-rendering, hard-surface)](blender/scenario-labs__skills-blender.md) | MIT (conferida) | ideias-aproveitadas | 2 | skills/video/blender-ia (atualização Blender premium: lentes, luz, curvas) (instalada na main em 12/10/2026, PR #21) |
| [Flue (Blender)](blender/sfkislev__flue-blender.md) | MIT | descartada | 2 | — |
| [mixamo-llm-mocap](blender/squall01337__mixamo-llm-mocap.md) | não verificado (a pesquisa registra '—') | descartada | 1 | — |
| [make-blender-education-video](blender/sunxiayi__make-blender-education-video.md) | MIT | ideias-aproveitadas | 5 | skills/video/blender-ia/SKILL.md (regra de 1 quadro de aprovação em resolução cheia antes do render final; checklist) |
| [Dark Luxury Studio (Superhive Market)](blender/superhive__dark-luxury-studio.md) | produto pago (licença do vendedor, não verificada) | so-referencia | sem nota | — |
| [blender-developer-tools](blender/tmhsdigital__blender-developer-tools.md) | variada, não conferida | so-referencia | sem nota | — |
| [3dviz-pro-max](blender/viettranx__3dviz-pro-max.md) | MIT | so-referencia | 4 | — |
| [blender-mcp (skill de uso do MCP)](blender/vladmdgolam__blender-mcp-skill.md) | MIT | so-referencia | 4 | — (a lição do export sem janela está registrada na pesquisa A) |
| [blender-mcp-vxai](blender/vxasi__blender-mcp-vxai.md) | não verificado (a pesquisa não registra) | descartada | 1 | — |

## Sites com IA (23 fichas)

| Skill | Licença | Status | Nota | Entrou em |
|---|---|---|---|---|
| [web-quality-skills (pacote de 6: web-quality-audit, performance, core-web-vitals, accessibility, seo, best-practices)](sites/addyosmani__web-quality-skills.md) | MIT (LICENSE da raiz, Addy Osmani 2026; cada SKILL.md também traz license: MIT) | instalada-intacta | 9 | skills/sites/web-quality/<skill>/ (6 pastas lado a lado, cópia intacta) (instalada na main em 12/10/2026, PR #21) |
| [theme-factory](sites/anthropics__theme-factory.md) | Apache-2.0 | so-referencia | sem nota | — |
| [web-artifacts-builder](sites/anthropics__web-artifacts-builder.md) | Apache-2.0 | so-referencia | sem nota | — |
| [webapp-testing](sites/anthropics__webapp-testing.md) | Apache-2.0 (LICENSE.txt dentro da pasta da skill) | instalada-intacta | 8 | skills/sites/webapp-testing/ (cópia intacta) (instalada na main em 12/10/2026, PR #21) |
| [astro](sites/astrolicious__astro.md) | MIT | so-referencia | 5 | — |
| [web-perf](sites/cloudflare__web-perf.md) | Apache-2.0 | so-referencia | 6 | — |
| [ai-seo](sites/coreyhaines31__ai-seo.md) | MIT (LICENSE da raiz do repositório) | so-referencia | 5.5 | — |
| [analytics](sites/coreyhaines31__analytics.md) | MIT (LICENSE da raiz do repositório) | so-referencia | 6.5 | — |
| [cro (antiga page-cro)](sites/coreyhaines31__cro.md) | MIT (LICENSE da raiz do repositório) | instalada-intacta | 7.5 | skills/sites/cro/ (cópia intacta) (instalada na main em 12/10/2026, PR #21) |
| [programmatic-seo](sites/coreyhaines31__programmatic-seo.md) | MIT (LICENSE da raiz do repositório) | so-referencia | 5.5 | — |
| [schema (também citada como schema-markup)](sites/coreyhaines31__schema.md) | MIT (LICENSE da raiz do repositório) | so-referencia | 6 | — |
| [seo-audit](sites/coreyhaines31__seo-audit.md) | MIT (LICENSE da raiz do repositório) | instalada-intacta | 7.5 | skills/sites/seo-audit/ (cópia intacta) (instalada na main em 12/10/2026, PR #21) |
| [site-architecture](sites/coreyhaines31__site-architecture.md) | MIT (LICENSE da raiz do repositório) | instalada-intacta | 7.5 | skills/sites/site-architecture/ (cópia intacta) (instalada na main em 12/10/2026, PR #21) |
| [playwright-best-practices](sites/currents-dev__playwright-best-practices.md) | MIT | so-referencia | 7 | — |
| [shadcn (skill)](sites/shadcn-ui__shadcn.md) | MIT (LICENSE.md da raiz do repositório) | instalada-intacta | 9 | skills/sites/shadcn/ (cópia intacta) (instalada na main em 12/10/2026, PR #21) |
| [superpowers@landing-page-design](sites/varios__superpowers-landing-page-design.md) | não verificado (a pesquisa registra '–') | descartada | 0 | — |
| [agent-browser](sites/vercel-labs__agent-browser.md) | Apache-2.0 | so-referencia | 4 | — |
| [deploy-to-vercel](sites/vercel-labs__deploy-to-vercel.md) | MIT (declarada só no README do repositório; o repositório não tem arquivo LICENSE e o SKILL.md não tem campo de licença) | instalada-intacta | 8 | skills/sites/deploy-to-vercel/ (cópia intacta; aviso de hospedagem comercial no APRENDIZADOS.md) (instalada na main em 12/10/2026, PR #21) |
| [vercel-optimize](sites/vercel-labs__vercel-optimize.md) | MIT (declarada no README do repositório; o repositório não tem arquivo LICENSE) | so-referencia | 5 | — |
| [vercel-react-best-practices](sites/vercel-labs__vercel-react-best-practices.md) | MIT (declarada no README, linhas 237-239, e no campo license: do SKILL.md; o repositório não tem arquivo LICENSE) | instalada-intacta | 9 | skills/sites/vercel-react-best-practices/ (cópia intacta) (instalada na main em 12/10/2026, PR #21) |
| [next-dev-loop](sites/vercel__next-dev-loop.md) | MIT (repositório vercel/next.js) | so-referencia | 6.5 | — |
| [Skills do Next.js, as outras (next-bundle-optimizer, next-cache-components-adoption/optimizer, next-partial-prefetching-adoption/optimizer e mais)](sites/vercel__next-skills-outras.md) | MIT (repositório vercel/next.js) | so-referencia | sem nota | — |
| [nextjs-app-router-patterns](sites/wshobson__nextjs-app-router-patterns.md) | não conferida (a própria pesquisa diz isso) | descartada | 4 | — |

## Documentos de apoio (não são fichas de skill)

- [ferramentas-scroll.md](blender/ferramentas-scroll.md)
- [ferramentas-video-ia.md](blender/ferramentas-video-ia.md)
- [colecoes-de-sites.md](sites/colecoes-de-sites.md)
