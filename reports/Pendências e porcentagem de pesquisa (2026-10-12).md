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
| 2 | **Catálogo de 138 fichas** | autor verificado **88%**; nota **67%**; transcrição completa **34%** (a licença saiu do catálogo, veja abaixo) | 91 com transcrição curta; 16 sem autor verificado |
| 3 | **Hospedagem de site de cliente** (**adiada** pelo Daniel: "depois a gente vê") | **68%** dos fatos confirmados em página oficial (38 de 56) | 18 fatos "não confirmado" |
| 4 | **IA de vídeo grátis** | **≈ 86%** (6 pontos "não verificado" em 42 itens) | nenhum vídeo gerado (por escolha do Daniel) |
| 5 | **Blender premium** | pesquisa **100%**; testes finais **dispensados** pelo Daniel | ficam anotados como "não testado": render completo de 90 quadros, tela real do Rotina, cintilação do removedor de ruído, Blender no PC dele |
| 6 | **`scroll-cinematico` já na main** | testado em **1 de 3** ambientes (33%); testes finais **dispensados** pelo Daniel | ficam anotados como "não testado": app Next.js de verdade, celular real (Android e iPhone) |
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

| Área | Fichas | Autor verificado | Transcrição completa (60 palavras ou mais) |
|---|---|---|---|
| Movimento e botões | 46 | 98% | 30% |
| UI/UX e estilos | 33 | 79% | 39% |
| Blender e vídeo com IA | 36 | 81% | **25%** |
| Sites com IA | 23 | 96% | 48% |
| **Total** | **138** | **88%** | **34%** |

**Licença saiu do catálogo (12/10/2026, pedido do Daniel):** a licença não decide mais se uma skill pode ser usada. O campo foi tirado das 138 fichas, do `MODELO.md`, das listas e do `INDICE.md`, e a regra "licença não trava o uso" entrou no `CLAUDE.md`. Seis fichas que citavam a licença como motivo de exclusão foram reescritas sem esse motivo; nenhuma mudou de status.

- A transcrição curta é, em boa parte, de fichas `descartada` (29), que não precisam de mais. As que merecem reforço são as 22 `ideias-aproveitadas` e as 39 `so-referencia` com transcrição curta.
- A área mais fraca é a de Blender e vídeo com IA: 13 fichas só têm nome, estrelas e nota (open-mcp, GenesisCore, vxai, 5 skills do grupo "nota 1 a 2", gsap-timeline, hyperframes e 3 pistas).
- **Sem ficha:** uma skill de Unreal (fora do escopo), TerminalSkills (só o nome) e um curso pago (não é skill). A `apple-design` do Emil já tem ficha, na área de movimento. O StableGen tem dois donos nas duas pesquisas; a divergência está na ficha.

## 3. Hospedagem

Das 14 hospedagens comparadas, 38 de 56 fatos da tabela foram confirmados em páginas oficiais (68%). Ficaram sem confirmação, entre outros: uso comercial no plano grátis de Render, Railway, Amplify, Statichost e Firebase; suporte ao Next.js no Netlify e na Locaweb; passo a passo de domínio próprio na Cloudflare; quem vira dono do projeto em Firebase, Amplify, Render e Railway. **Preço e termo mudam todo mês e isso não é parecer jurídico.**

## 4. IA de vídeo grátis

Pesquisa cobre modelos abertos (Wan 2.2, LTX-2.x e outros), 16 serviços online e onde rodar de graça. Pontos sem verificação: qual tamanho de modelo cabe na placa grátis do Kaggle/Colab no dia do uso, licença exata de alguns serviços e a Seedance (que não é open source). Nenhum vídeo foi gerado, e o Daniel disse que cuida dessa parte.

## 5. Decisões (fechadas em 12/10/2026, a pedido do Daniel: "termine as pendências")

Seguiu-se a recomendação de cada uma. Se alguma estiver errada, é só dizer.

1. **Uso comercial na hospedagem:** aplicado no passo 11 do `combo-site-premium` (não usar o plano Hobby da Vercel para cliente pagante; oferecer Cloudflare Pages ou Vercel Pro). A `deploy-to-vercel` continua cópia intacta.
2. **Skills do Emil Kowalski:** `review-animations` e `animation-vocabulary` instaladas, sem alteração, em `skills/design/`.
3. **Nome `motion-oficial`:** mantido.
4. **Image to Code:** assumido que é a skill `image-to-code` da Taste Skill.
5. **21st MCP:** não assinar.
6. **Blender:** valores mantidos (o celular passa do ponto 1%; `premium.py` é o padrão). Os testes finais foram dispensados pelo Daniel.
7. **Scroll sem travar** e **notebook Kaggle/Colab:** cancelados.
8. **Licença:** não trava o uso de nenhuma skill (regra nova no `CLAUDE.md`).
9. **Hospedagem:** a segunda rodada de confirmação de fatos fica para depois.

## Limites desta contagem

- As porcentagens de "scroll sem travar" e "IA de vídeo" usam a regra pronto/parcial/não feito, que é um julgamento.
- Uma ficha "completa" aqui só quer dizer que o campo foi preenchido, e não que o conteúdo foi conferido em duas fontes.
