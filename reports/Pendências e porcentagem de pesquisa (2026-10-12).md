# Pendências e porcentagem de pesquisa (12/10/2026)

Pedido do Daniel: pegar tudo o que foi coletado, salvar como planejado e trazer o que está incompleto, com porcentagem e percentual de pesquisa.

**Todas as porcentagens são estimativas nossas** e dizem como foram calculadas. Onde deu para contar nos arquivos, é contagem; onde não deu, é a regra "pronto = 1, parcial = 0,5, não feito = 0".

## O que foi salvo agora

- Material parcial do scroll sem travar: `research_notes/Caça de skills 2026-10/scroll-suave-PARCIAL/`.
- Adendo em `skills/sites/scroll-cinematico/APRENDIZADOS.md` (sem mexer no `SKILL.md`).
- Este relatório.
- Já estavam salvos: catálogo de fichas, pesquisa de hospedagem e todas as skills aprovadas (PR #21 e PR #22).

## Quadro geral

| # | Item | Pesquisa feita | O que falta |
|---|---|---|---|
| 1 | **Scroll-vídeo sem travar** (**cancelado** pelo Daniel em 12/10/2026) | **≈ 40%** (pesquisa 63%, teste 40%, proposta 17%) | 371 de 373 execuções do teste; texto final; proposta; celular real |
| 2 | **Catálogo de 138 fichas** | licença verificada **74%**; autor **88%**; nota **67%**; transcrição completa **34%** | 36 fichas sem licença verificada; 91 com transcrição curta |
| 3 | **Hospedagem de site de cliente** | **68%** dos fatos confirmados em página oficial (38 de 56) | 18 fatos "não confirmado" |
| 4 | **IA de vídeo grátis** | **≈ 86%** (6 pontos "não verificado" em 42 itens) | nenhum vídeo gerado (por escolha do Daniel) |
| 5 | **Blender premium** | pesquisa **100%**; validação **0 de 4** | render completo de 90 quadros; tela real do Rotina; cintilação do denoise numa sequência longa; Blender no PC do Daniel |
| 6 | **`scroll-cinematico` já na main** | testado em **1 de 3** ambientes (33%) | app Next.js de verdade; celular real (Android e iPhone) |
| 7 | **Estilos de marca (Awesome Design)** | **15 de 74** fichas guardadas (20%) | as outras 59 ficam como link de download, de propósito |
| 8 | **Notebook Kaggle/Colab** | 0% | **cancelado** pelo Daniel |

## 1. Scroll-vídeo sem travar (≈ 40%; cancelado pelo Daniel)

| Parte | Pronto | Detalhe |
|---|---|---|
| A. Pesquisa | 63% (5 de 8 etapas) | Prontas: causa do travamento, comparação de codecs, como medir. Parciais: janela deslizante, WebCodecs, suavização, qualidade adaptativa. Falta o texto final. |
| B. Teste de velocidade | 40% | Montagem 100%; execuções 0,5% (2 de 373); texto 0%. |
| C. Proposta da skill | 17% (1 de 6 etapas) | Só a cópia da skill atual. |

Nota geral = 0,3 × 63 + 0,4 × 40 + 0,3 × 17 = 40%. O que já se sabe com fonte ou medição:
- O vídeo trava porque o navegador pula para o keyframe mais próximo. A correção é codificar com todos os quadros como keyframe (`-g 1`).
- Custo: 4,29 MB contra 1,52 MB (H.264, 720p, 3 s). O VP9 all-intra ficou com metade do peso na mesma qualidade, em um vídeo sintético.
- Canvas com todos os quadros na memória usa mais que o dobro de memória do vídeo (843 MB contra 325 MB); a ideia é a janela deslizante.
- **Nenhuma técnica foi comparada de verdade.** Detalhes em `research_notes/Caça de skills 2026-10/scroll-suave-PARCIAL/`.

## 2. Catálogo de fichas (138)

Contagem feita nos próprios arquivos.

| Área | Fichas | Licença verificada | Transcrição completa (60 palavras ou mais) |
|---|---|---|---|
| Movimento e botões | 46 | 83% | 30% |
| UI/UX e estilos | 33 | 67% | 39% |
| Blender e vídeo com IA | 36 | **58%** | **25%** |
| Sites com IA | 23 | 91% | 48% |
| **Total** | **138** | **74%** | **34%** |

- A transcrição curta é, em boa parte, de fichas `descartada` (29), que não precisam de mais. As que merecem reforço são as 22 `ideias-aproveitadas` e as 39 `so-referencia` com transcrição curta.
- A área mais fraca é a de Blender e vídeo com IA: 16 de 36 fichas com licença ou autor "não verificado". Entre elas estão 13 que só têm nome, estrelas e nota (open-mcp, GenesisCore, vxai, 5 skills do grupo "nota 1 a 2", gsap-timeline, hyperframes e 3 pistas).
- **Sem ficha:** `apple-design` do Emil (é da área de movimento), uma skill de Unreal (fora do escopo), TerminalSkills (só o nome) e um curso pago (não é skill). O StableGen tem dois donos nas duas pesquisas; a divergência está na ficha.

## 3. Hospedagem

Das 14 hospedagens comparadas, 38 de 56 fatos da tabela foram confirmados em páginas oficiais (68%). Ficaram sem confirmação, entre outros: uso comercial no plano grátis de Render, Railway, Amplify, Statichost e Firebase; suporte ao Next.js no Netlify e na Locaweb; passo a passo de domínio próprio na Cloudflare; quem vira dono do projeto em Firebase, Amplify, Render e Railway. **Preço e termo mudam todo mês e isso não é parecer jurídico.**

## 4. IA de vídeo grátis

Pesquisa cobre modelos abertos (Wan 2.2, LTX-2.x e outros), 16 serviços online e onde rodar de graça. Pontos sem verificação: qual tamanho de modelo cabe na placa grátis do Kaggle/Colab no dia do uso, licença exata de alguns serviços e a Seedance (que não é open source). Nenhum vídeo foi gerado, e o Daniel disse que cuida dessa parte.

## 5. Decisões que dependem do Daniel

1. **`deploy-to-vercel`:** perguntar "é uso comercial?" antes de publicar, e não usar o plano Hobby se for (muda o `SKILL.md`).
2. **Emil Kowalski:** instalar `review-animations` e `animation-vocabulary` como skills separadas? (opcional)
3. **Renomear** `motion-oficial` para `animacao-ui`? (opcional)
4. **Image to Code:** confirmar se a skill `image-to-code` da Taste Skill é a do vídeo que ele viu.
5. **21st MCP:** recomendação é não assinar (pago e envia o contexto do projeto para fora).
6. **Blender:** o reflexo deve demorar mais (30 a 40 quadros)? O "passa do ponto" do celular fica em 1% ou 1,5%? O `premium.py` vira o padrão? (ficaram os valores padrão: 1% e `premium.py`.)
7. ~~Retomar o scroll sem travar~~ **cancelado** pelo Daniel em 12/10/2026; só volta se ele pedir.

## Limites desta contagem

- As porcentagens de "scroll sem travar", "IA de vídeo" e "Blender" usam a regra pronto/parcial/não feito, que é um julgamento.
- Uma ficha "completa" aqui só quer dizer que o campo foi preenchido, e não que o conteúdo foi conferido em duas fontes.
