# Relatório: Blender premium, sites com IA, scroll e vídeo com IA (12/10/2026)

**Situação:** tudo pronto e esperando a sua aprovação.
- **Já na `main` (conhecimento):** as pesquisas e as transcrições, em `research_notes/Caça de skills 2026-10/`.
- **Só na cópia de segurança** (branch `rascunho-caca-skills-design`, pasta `rascunhos/`): as skills novas e a atualização do Blender. Elas só entram na `main` depois do seu "pode".

Foram mais 12 agentes nesta rodada: 3 no Blender, 3 em sites, 2 em vídeo com IA e scroll, 1 transcrição do scroll e 1 para a sua lista de download. Além deles, eu mesmo pesquisei o Seedance.

---

## 1. Blender premium (atualização da skill `blender-ia`)

**Direção de animação**
- A curva do Blender agora sai igual à do Remotion: a diferença é de 0,00005%.
- A skill traz a duração e a curva de cada tipo de plano:
  - revelação do celular: 3 a 5 s;
  - câmera se aproximando: 3 a 5 s;
  - giro completo: 6 a 8 s.
- Regras para o movimento ficar suave:
  - a câmera nunca passa do ponto nem para no meio do caminho;
  - a câmera nunca acelera mais de 3 vezes a velocidade média;
  - o celular passa do ponto só 1 a 2%;
  - o "passa do ponto" padrão do Blender (10%) fica proibido, porque parece desenho animado.
- Um verificador confere o movimento em 2 segundos, antes de gastar tempo renderizando.
- O reflexo no vidro agora leva uns 0,8 s para cruzar a tela. Antes eram 0,27 s, rápido demais para perceber.

**Visual premium**
- Fundo escuro com um brilho suave só atrás do celular, ou transparente para o Remotion pôr o fundo.
- Luz de estúdio: uma luz grande e alta, duas faixas finas de contorno atrás e um reflexo diagonal discreto.
- As cores do app saem fiéis, porque o modo de cor foi trocado para um próprio de fotografia de produto.

**Modelagem**
- Celular genérico, sem nenhuma marca real: 8,2 mm de espessura, aro de metal, botões, USB-C, alto-falante, linhas de antena e câmera com duas lentes.
- A tela mostra a interface do app.

**Custo, aqui na nuvem sem placa de vídeo:** cerca de 24 segundos por quadro em 1080×1920. Um plano de 3 segundos (90 quadros) leva uns 36 minutos, em segundo plano.

**Não testado:** o render completo de 90 quadros, a tela real do Rotina, a cintilação do removedor de ruído numa sequência longa e o Blender no seu computador.

**Atenção:** os números de duração e velocidade são **estimativa nossa**. Não achei fontes de 2025–2026 com números de animadores de filmes de produto. O seu olho de editor é o teste que vale.

---

## 2. Sites com IA: a categoria nova `skills/sites/`

**Skills de terceiros** (todas de licença livre, copiadas sem alteração):

| Skill | Para que serve | De quem | Licença |
|---|---|---|---|
| `site-architecture` | planejar páginas, menu e mapa do site | Corey Haines | MIT |
| `shadcn` | peças do site com as cores e fontes do nosso `DESIGN.md` | shadcn/ui, oficial | MIT |
| `vercel-react-best-practices` | código rápido, com 70 regras | Vercel | MIT |
| `cro` | fazer a página vender mais | Corey Haines | MIT |
| `webapp-testing` | abrir o site, tirar prints no celular e no computador | Anthropic | Apache-2.0 |
| `playwright-cli` | o mesmo, pela linha de comando | Microsoft | Apache-2.0 |
| `web-quality` (pacote de 6) | velocidade, acessibilidade, SEO no código e segurança | Addy Osmani | MIT |
| `deploy-to-vercel` | colocar no ar, primeiro em link de teste | Vercel | MIT |
| `seo-audit` | SEO depois do lançamento | Corey Haines | MIT |

**Skills nossas:**
- **`referencia-sites`:** a base de referência de sites.
  - É um índice, e não um depósito de código. Cada coleção traz link, licença, tecnologia, estilo e quando usar.
  - Tem 8 modelos favoritos com licença MIT, presos numa versão fixa: Launch UI, o portfólio do Magic UI, AstroWind, SaaS Starter, Commerce, entre outros.
  - Um programa baixa o modelo escolhido para a pasta do projeto. Ele se recusa a baixar para a `main`.
  - O que é pago ou restrito fica só como link: Tailwind Plus, Aceternity, Cruip e React Bits.
- **`mcps-para-sites`:** como instalar, no seu computador, os 3 programas de conexão da sua lista. Está descrita na seção 3.
- **`scroll-cinematico`:** a animação que acompanha a rolagem. Veja a seção 4.
- **Combo `combo-site-premium`:** um site do zero, com 5 pontos de aprovação seus: o mapa do site, o texto, o visual, os prints e a hora de publicar.

**Cuidados:**
- **O plano grátis da Vercel não vale para uso comercial.** Site de cliente precisa do plano pago ou de outra hospedagem.
- A skill `shadcn` roda um comando sozinha quando é aberta, então só deve ser usada dentro da pasta de um projeto.
- O Next.js agora traz as instruções para a IA dentro do próprio projeto, e isso funcionou melhor nos testes do que skills de "boas práticas". Por isso essa parte não virou skill.

---

## 3. A sua lista (as duas imagens), item por item

| # | Item | Resultado | Onde fica |
|---|---|---|---|
| 1 | **Taste Skill** | MIT. Entra intacta, mas é a versão 2 experimental, com 89 KB, e cita uma pasta que não existe. Alguns padrões dela quebram as suas regras de composição (revelar texto com cortina ou com máscara). Fiz um mapa de leitura por seção. | `skills/design/` |
| 2 | **Web Design Guidelines** | MIT. A skill original manda buscar as regras na internet a cada uso. Acrescentei uma cópia fixa das regras, e o original ficou intacto. | `skills/design/` |
| 3 | **Awesome Design** | MIT. Tem 74 fichas de marcas, e 15 já estão na `estilos-premium`. As outras 59 ficam na lista de download, para baixar só a ficha que você precisar. | `referencia-sites` |
| 4 | **Image to Code** | MIT. É a skill `image-to-code` do repositório da Taste Skill. **Exige um gerador de imagem e gasta créditos**, então fica como opcional. Confirme que é essa que você viu no vídeo. | `skills/design/` |
| 5 | **Playwright CLI** | Apache-2.0. Entra intacta. | `skills/sites/` |
| 6 | **shadcn/ui MCP** | Grátis, sem chave. A skill `shadcn` já faz o mesmo pela linha de comando. | instruções em `mcps-para-sites` |
| 7 | **Magic MCP (21st.dev)** | Agora se chama **21st MCP**. É pago para código de componente e IA, e envia o contexto do seu projeto para a 21st.dev. **Recomendo não ligar por ora.** | só instruções |
| 8 | **Frontend Design** | Apache-2.0. Entra intacta. | `skills/design/` |
| 9 | **Chrome DevTools MCP** | Envia estatísticas ao Google por padrão. O comando que deixei já desliga isso. Só no seu computador. | instruções em `mcps-para-sites` |

Os 3 MCPs são programas que se conectam ao Claude. Por isso eu **não instalo nenhum aqui na nuvem**: a skill `mcps-para-sites` traz o comando para o seu computador, com os custos e o que cada um envia para fora. Nenhuma chave vai para o repositório.

O combo e o índice já foram atualizados com as peças novas.

---

## 4. Animação premium no scroll (`scroll-cinematico`)

A técnica das páginas de produto da Apple: a pessoa rola e o produto gira, abre ou monta.

- **Técnica principal:** uma sequência de 60 a 150 imagens desenhada num canvas, que avança com a rolagem.
  - Pesa de 3 a 6 MB no computador e de 1,5 a 2,5 MB no celular, com um conjunto próprio na vertical.
  - O formato WebP carrega mais rápido que o AVIF (13 ms contra 33 ms por quadro).
- **Outras técnicas, cada uma para o seu caso:** vídeo ligado à rolagem, CSS puro e as bibliotecas GSAP e Lenis. O GSAP agora é grátis até para uso comercial, mas a licença dele é proprietária, e isso está registrado.
- **Pipeline:** Blender, Remotion ou vídeo de IA geram os quadros, e o ffmpeg prepara as versões leves, com pôster e lista de arquivos.
- **Suas 4 regras valem no scroll:**
  - os textos não se sobrepõem, e cada um fica parado uns 54% da altura da tela;
  - o produto, com fundo transparente, fica por cima do texto;
  - o texto entra pela borda ou de trás do produto.
- **Quem pede "reduzir movimento"** ou está sem JavaScript vê uma imagem parada.
- **Testado:** uma página com 60 quadros, capturada no navegador em 4 pontos da rolagem. O quadro certo apareceu em todos, sem texto sobre texto. Um teste automático acusou o erro quando provoquei uma sobreposição de propósito.
- **Não testado:** o componente num app Next.js de verdade e num celular real.

---

## 5. IA de vídeo grátis

- **Serviços online grátis não servem para site de cliente.** Kling, Hailuo, Pika, Luma, Vidu, Higgsfield e o Google Flow colocam marca d'água e proíbem o uso comercial ou não deixam claro. Servem só para rascunho e para mostrar a ideia.
- **A sua chave do Google não gera vídeo de graça.** O Veo é pago (uns US$ 0,40 a 0,64 por clipe de 8 s). Não testei, para não gerar cobrança.
- **O caminho grátis e comercial são os modelos abertos:**
  - **Wan 2.2** (Apache 2.0, a licença mais livre);
  - **LTX-2.3 e 2.5**, grátis para quem fatura menos de US$ 10 milhões por ano, com fundo transparente e escolha do primeiro e do último quadro, o que ajuda no scroll.
- **A sua placa (NVIDIA, cerca de 4 GB) é pequena demais** para rodar esses modelos. Eles pedem de 8 a 12 GB ou mais.
  - O caminho é **Kaggle ou Google Colab grátis**: o Kaggle dá cerca de 30 horas por semana.
  - Outra opção é uma conta grátis no Hugging Face, que dá cerca de 1 clipe por dia.
  - Ainda preciso conferir qual tamanho de modelo cabe na placa grátis no dia do uso.
- **Aqui na nuvem** não dá para gerar vídeo por IA, porque não há placa de vídeo.
- **Seedance (ByteDance): não é open source.** A conta oficial da ByteDance no Hugging Face não tem nenhum modelo Seedance, e os guias dizem que ele só funciona pela nuvem. O uso comercial e o plano grátis dele não ficaram claros.
  - Os modelos **SeedVR2** da ByteDance são abertos (Apache 2.0), mas servem para melhorar um vídeo que já existe, e não para gerar um novo.

---

## 6. Para você decidir

**Aprovações** (responda "pode salvar tudo", ou os números):
1. **Criar a categoria `skills/sites/`.** Recomendo sim.
2. **As skills de terceiros de `sites`** da tabela da seção 2, incluindo `playwright-cli`.
3. **As skills nossas de `sites`** (`referencia-sites`, `mcps-para-sites`, `scroll-cinematico`) e o combo `combo-site-premium`.
4. **As 4 skills de terceiros de `design`:** `web-design-guidelines`, `frontend-design`, `taste-skill` e `image-to-code`. Mínimo recomendado: as duas primeiras.
5. **A atualização da `blender-ia`** (Blender premium).

**Respostas sobre o Blender:** na imagem 3 que mandei:
- O reflexo deveria demorar mais (30 a 40 quadros), ou está bom?
- O celular passa 1% do ponto. Está bom, ou prefere 1,5%?
- O `premium.py` pode ser o padrão para todo plano de destaque?

**Outras decisões:**
6. **Hospedagem dos sites:** a Vercel grátis não é para uso comercial. Quer que eu pesquise alternativas?
7. **21st MCP:** recomendo não assinar.
8. **Notebook para Kaggle ou Colab** com Wan/LTX, já pronto para abrir. Quer que eu monte?
9. **O "Image to Code" do vídeo é a skill `image-to-code` da Taste Skill?**

Depois do seu "pode", eu salvo na `main` com PR, atualizo o índice e o manual e confiro que o programa de skills encontra tudo.
