# Hospedagem para sites de clientes (uso comercial)

Pesquisa feita em 12/out/2026, lendo páginas oficiais de cada serviço. Para quem não é programador: onde cada coisa "não confirmado" aparece, significa que eu NÃO vi aquilo escrito numa página oficial e não vou afirmar. Conteúdo de fóruns aparece só como "indício", nunca como regra.

---

## 1. Resposta rápida

- **A Vercel Hobby (grátis) NÃO serve para site de cliente.** Os termos oficiais dizem que times Hobby são "restricted to non-commercial personal use only" e definem uso comercial como qualquer site usado para ganho financeiro de **qualquer pessoa envolvida na produção, "including a paid employee or consultant writing the code"** e, entre os exemplos, "Receiving payment to create, update, or host the site". Ou seja: se o Daniel cobra para fazer o site, já é comercial, mesmo que a conta Hobby seja do próprio cliente. ([Vercel Fair Use Guidelines](https://vercel.com/docs/limits/fair-use-guidelines), atualizada em 14/set/2026)
- **Melhor opção grátis e legal para comercial (site estático ou Next.js exportado como estático): Cloudflare Pages.** Os termos que li não proíbem uso comercial no plano grátis (com duas cláusulas de cuidado, explicadas abaixo). Segunda opção: Netlify Free, que diz expressamente que pode ter projeto comercial.
- **Quando o site precisa de servidor de verdade (login, banco, SSR, ISR, otimização de imagem do Next.js sem configuração): Vercel Pro (US$ 20/mês)** ou Cloudflare Workers pago (US$ 5/mês, mais trabalho de configuração).

---

## 2. Tabela comparativa

Nota 0 a 10 = "agência/freelancer entrega site a cliente" (considera: grátis é permitido para comercial? dá para o cliente ser dono? Next.js funciona? risco de a conta ser suspensa?).

| Serviço | Grátis permite comercial? | Next.js | Plano pago mais barato | Cliente vira dono? | Nota |
|---|---|---|---|---|---|
| Vercel Hobby | NÃO (explícito) | Funções e otimização de imagem existem (detalhe de ISR não conferido) | -- | -- | 1 |
| Vercel Pro | Sim (é o plano comercial) | Funções e otimização de imagem pagas por uso | US$ 20/mês por vaga de desenvolvedor | Sim, transferência oficial entre times | 8 |
| Cloudflare Pages/Workers (grátis) | Termos não proíbem (ver cuidados) | Estático: sim. SSR/ISR: via Workers (vinext/OpenNext) | Workers Paid US$ 5/mês | Parcial (sem transferência oficial de projeto que eu tenha visto) | 8 |
| Netlify Free | Sim, dito no post oficial de lançamento | Não confirmado (página de preços não diz) | Personal US$ 9/mês | Sim, "Transfer project" documentado | 7 |
| Firebase Hosting (Spark) | Termos pressupõem uso de negócio; plano grátis não proíbe (não vi proibição) | Só estático no grátis; Next.js com servidor exige plano pago (Blaze) | Blaze (pague o que usar) | Não confirmado | 6 |
| Statichost.eu | Não confirmado | Estático (tem preset Next.js) | Starter 9 EUR/mês | Não confirmado | 6 |
| Hostinger (hospedagem compartilhada) | É um serviço pago (não há "grátis") | Estático: sim. Next.js com servidor: só planos Business/Cloud | Single R$ 5,99/mês promo (R$ 23,99 na renovação) | Fácil: o cliente contrata no nome dele | 7 |
| Locaweb | É um serviço pago | Não confirmado (página não cita Node.js) | Go R$ 5,90/mês promo (R$ 24,90 regular) | Fácil: cliente contrata no nome dele | 5 |
| Render | Não confirmado | Estático grátis; servidor grátis "não use em produção" | Não confirmado o preço do menor plano pago de servidor; plano Pro US$ 25/mês | Não confirmado | 5 |
| AWS Amplify | Não confirmado | SSR/ISR até Next.js 15 (16 não confirmado) | Pague pelo uso (ver abaixo) | Não confirmado | 5 |
| Bunny.net | Pago (sem plano grátis) | Estático (via Storage + CDN) | Mínimo US$ 1/mês | Não confirmado | 5 |
| GitHub Pages | NÃO é para "negócio" (ver abaixo) | Só estático | Não se aplica | Repositório pode ser do cliente (não conferido) | 3 |
| Railway | Não confirmado | Servidor (qualquer app Node) | Hobby US$ 5/mês | Não confirmado | 4 |
| Fly.io | Sem plano grátis para contas novas | Servidor (Docker) | A partir de US$ 2,19/mês (menor máquina) | Não confirmado | 4 |

---

## 3. Detalhe por serviço

### Vercel (Hobby x Pro)

- **Comercial no grátis?** Não. Trecho: "Hobby teams are restricted to non-commercial personal use only. All commercial usage of the platform requires either a Pro or Enterprise plan." Exemplos de comercial: pedir/processar pagamento no site, anunciar venda de produto/serviço, "Receiving payment to create, update, or host the site", anúncios (AdSense), afiliados. Doação não conta. Link: https://vercel.com/docs/limits/fair-use-guidelines. Se estiver em dúvida, a própria página manda falar com o suporte da Vercel.
- **Limites Hobby:** 100 GB de transferência/mês, 1 milhão de chamadas de função, 4 horas de CPU ativa, 5 mil otimizações de imagem/mês. Sem como comprar extra (https://vercel.com/pricing).
- **Pro:** US$ 20/mês por vaga de desenvolvedor, inclui US$ 20 de crédito de uso, 1 TB de transferência; passou disso, paga por uso (a partir de US$ 0,15/GB) (https://vercel.com/pricing).
- **Next.js:** a página de limites mede funções (SSR/API) e otimização de imagem como recursos da plataforma; suporte detalhado a ISR e middleware não foi conferido nesta pesquisa.
- **Dono:** documentação oficial de transferência: dá para passar o projeto de um time para outro "with zero downtime", por exemplo de Hobby para Pro; domínio é movido junto (domínio raiz como `example.com` é movido; subdomínio como `blog.example.com` é apenas "delegado" e a raiz fica no time de origem). Uso é zerado na transferência. Link: https://vercel.com/docs/projects/transferring-projects.
- **Risco:** subir site de cliente pago na Hobby é violação clara dos termos. Eles dizem que avisam antes de agir, mas pode suspender.

### Cloudflare Pages / Workers

- **Comercial no grátis?** Os termos de uso (Self-Serve Subscription Agreement, atualizado em 12/set/2025, https://www.cloudflare.com/terms/) **não têm cláusula proibindo uso comercial** no plano grátis (não encontrei). Mas têm duas cláusulas que importam para agência:
  1. Seção 2.2.1(h): proíbe "process or collect personal or business credit card information on any web property that is receiving Free Services". Ou seja: loja que coleta cartão no próprio site NÃO deve ficar no plano grátis. (Se o pagamento é feito em página de terceiros, como um checkout externo, eu não verifiquei se isso é aceito: não confirmado, pergunte à Cloudflare.)
  2. Seção 2.2.1(a): proíbe "sell access to the Services to any third party" e "sign up for the Services on behalf of a third party" sem permissão escrita. Tradução prática: **o Daniel não deve criar a conta Cloudflare "em nome" do cliente.** O cliente cria a própria conta e convida o Daniel como membro. Se o Daniel hospedar vários clientes dentro da conta dele e cobrar por "hospedagem", isso pode ser lido como revenda. Não é certeza, é risco: não confirmado.
  - Outra cláusula (Serviços específicos, atualizada em 28/set/2026, https://www.cloudflare.com/service-specific-terms-application-services/): quem não é Enterprise precisa usar serviços pagos como a Developer Platform para servir "video and other large files via the CDN". Site com muito vídeo próprio não deve ficar no grátis.
- **Limites (Pages, https://developers.cloudflare.com/pages/platform/limits/):** 500 builds/mês, 1 build por vez, 20.000 arquivos por site, 25 MiB por arquivo, 100 domínios por projeto. Banda: a página de limites não diz. Pedidos para arquivos estáticos: "On both free and paid plans, requests to static assets are free and unlimited" (https://developers.cloudflare.com/pages/functions/pricing/).
- **Funções/servidor no grátis:** 100.000 pedidos por dia, 10 ms de CPU por chamada (https://developers.cloudflare.com/workers/platform/pricing/). Pago: US$ 5/mês mínimo, 10 milhões de pedidos/mês incluídos.
- **Next.js:** estático (`output: 'export'`) funciona no Pages com preset pronto. Para SSR/ISR, a documentação da Cloudflare hoje recomenda o **vinext** (reimplementação do Next.js feita pela Cloudflare para Workers), com SSR, ISR, App Router e Server Actions marcados como suportados e otimização de imagem "parcial"; o adaptador OpenNext é a alternativa para quem já usa. Pelo que li, o vinext é beta: teste antes de prometer ao cliente (https://developers.cloudflare.com/workers/framework-guides/web-apps/nextjs/).
- **Pages x Workers:** a documentação de migração mostra Workers com mais recursos que Pages, mas **não diz** que Pages foi descontinuado.
- **Domínio e HTTPS:** domínio próprio: sim (limite de 100 por projeto). HTTPS automático: não confirmado nesta pesquisa.
- **Dono:** a comunidade da Cloudflare indica que não existe transferência de projeto Pages entre contas; a solução é o destinatário criar um novo projeto ligado ao mesmo repositório e refazer o domínio (indício de fórum, não documentação oficial). Para convidar o Daniel como membro, a conta do cliente precisa de um usuário com papel "Super Administrator" (https://developers.cloudflare.com/fundamentals/manage-members/manage/). Como o site estático vive no GitHub, mudar de dono é fácil na prática.

### Netlify

- **Comercial no grátis?** Post oficial de lançamento do plano: "On the Free plan, you can deploy commercial projects, personal sites, or other creative explorations" (https://www.netlify.com/blog/introducing-netlify-free-plan/). Os Termos de Uso (https://www.netlify.com/legal/terms-of-use/, 26/mar/2026) não tratam de uso comercial. **Atenção:** o acordo de assinatura (https://www.netlify.com/legal/self-serve-subscription-agreement/) diz que "Netlify may shut down Free Usage Tier website projects without notice for any reason or no reason" (o texto traz data de 2020). Indício de fórum (staff, não oficial) diz que não pode revender o plano grátis e que cobrar só pelo desenvolvimento e manutenção é aceito, mas outra resposta diz que precisa de Pro se cobrar de terceiros por sites hospedados lá. Ficou ambíguo: para cliente pagante com hospedagem incluída, pergunte ao suporte.
- **Limites (modelo novo por créditos):** Free = 300 créditos/mês, limite duro. Custos: deploy de produção 15 créditos; 1 GB de banda 20 créditos; 10.000 requisições 2 créditos (https://docs.netlify.com/manage/accounts-and-billing/billing/billing-for-credit-based-plans/credit-based-pricing-plans/). Conta minha com esses números: 10 deploys + 7,5 GB de banda já gastam os 300 créditos. Passou do limite, o site fica pausado até virar o mês (post oficial: "suspended for the remaining days in the calendar month").
- **Planos pagos:** Personal US$ 9/mês (1.000 créditos); Pro US$ 20/mês (3.000 créditos, membros ilimitados) (https://www.netlify.com/pricing/).
- **Next.js:** não confirmado nas páginas que li.
- **Domínio/HTTPS:** domínio próprio com SSL já no Free (confirmado na página de preços).
- **Dono:** transferência de projeto entre times documentada (https://docs.netlify.com/manage/projects/transfer-project/): quem transfere precisa ser Owner do time de origem e Owner ou Developer no destino; domínio é um processo separado.

### GitHub Pages

- **Comercial no grátis?** Não para negócio. Trecho que li: "GitHub Pages is not intended for or allowed to be used as a free web-hosting service to run your online business" (https://docs.github.com/en/pages/getting-started-with-github-pages/github-pages-limits). Para site institucional de cliente, é zona cinzenta; o risco é do Daniel e do cliente.
- **Limites:** site até 1 GB; banda "soft" de 100 GB/mês; 10 builds por hora (limites "soft").
- **Next.js:** só estático (o próprio Next.js tem modelo oficial de deploy para GitHub Pages).
- **Repositório privado:** no plano GitHub Free o repositório precisa ser público.
- **Nota:** ótimo para portfólio, documentação e site do próprio Daniel; evitar para cliente pagante.

### Firebase Hosting (Google)

- **Comercial no grátis?** Os termos (https://firebase.google.com/terms, 24/set/2026) pedem que quem aceita declare "my use of any Firebase service is for purposes related to my trade, business, craft, or profession", ou seja, o uso profissional é o esperado. Não vi proibição de uso comercial no plano grátis (Spark). Mesmo assim, a página de preços não fala do assunto: classifico como "provável, não confirmado".
- **Limites Spark (https://firebase.google.com/pricing):** 10 GB de armazenamento; 360 MB/dia de transferência (cerca de 10 GB/mês, e é limite diário). Domínio próprio e SSL incluídos.
- **Next.js com servidor:** é o produto "App Hosting", exige o plano pago Blaze (cartão de crédito); a integração antiga do Hosting com frameworks está fechada para novos usuários, segundo a busca na documentação oficial.
- **Dono:** não confirmado nesta pesquisa.

### AWS Amplify

- **Preço (https://aws.amazon.com/amplify/pricing/):** camada grátis de 12 meses (1.000 min de build, 5 GB de armazenamento, 15 GB servidos, 500 mil pedidos SSR); depois US$ 0,01/min de build, US$ 0,023/GB armazenado, US$ 0,15/GB servido, US$ 0,30 por milhão de pedidos SSR. A página também cita contas novas (desde 15/jul/2025) com plano grátis de 6 meses e até US$ 200 em créditos, e não explica como as duas ofertas se combinam. Na página geral da AWS, o plano grátis fecha a conta após 6 meses ou quando os créditos acabam.
- **Comercial:** a página da AWS não fala de restrição; termos não conferidos.
- **Next.js:** Amplify "fully manages" SSR para Next.js 12 a 15; ISR sim, **ISR sob demanda não**; streaming não; otimização de imagem sim (https://docs.aws.amazon.com/amplify/latest/userguide/ssr-amplify-support.html). O Next.js atual é o 16 (nextjs.org/docs): suporte a 16 não confirmado.
- **Observação:** mais complicado (console da AWS) e o plano grátis "expira". Não recomendo para o perfil do Daniel.

### Render

- **Preços (https://render.com/docs/outbound-bandwidth e pesquisa):** workspace Hobby US$ 0 + computação; Pro US$ 25/mês. Banda de saída incluída: Hobby 5 GB, Pro 25 GB; extra US$ 0,15/GB. Sem forma de pagamento cadastrada e estourando a banda, "Render spins down the workspace's services until the start of the next month".
- **Sites estáticos:** "Static sites are fast and free to deploy", mas contam na banda do workspace. Domínios próprios: Hobby 2, Pro 15; SSL automático (https://render.com/docs/static-sites).
- **Servidor grátis:** dorme após 15 min sem tráfego (volta em cerca de 1 minuto), 750 h/mês; a página diz: "Do not use them for production applications" (https://render.com/docs/free). Banco Postgres grátis expira em 30 dias.
- **Comercial no grátis:** não confirmado (não encontrei a página dos termos).

### Railway e Fly.io (servidores)

- **Railway** (https://railway.com/pricing): Trial com US$ 5 únicos por 30 dias; Free com US$ 1/mês de crédito (limites 1 vCPU / 0,5 GB); Hobby US$ 5/mês com US$ 5 de crédito; Pro US$ 20/mês por workspace. Termos sobre uso comercial: não confirmado. Para site estático é desnecessário; útil para back-ends.
- **Fly.io** (https://docs.fly.io/about/pricing): "New organizations don't have a free tier"; teste de até 2 h de máquina ou 7 dias; depois paga por uso, menor máquina US$ 2,19/mês, saída de dados US$ 0,02/GB (América do Norte/Europa) e US$ 0,04/GB (América do Sul). Exige Docker/linha de comando; não é para quem não programa.

### Statichost.eu e Bunny.net (baratas)

- **Statichost.eu** (https://www.statichost.eu/pricing/): Hobby grátis (1 site, 10 GB de banda, 100 min de build); Starter 9 EUR/mês (500 GB, 5.000 min); Professional 49 EUR/mês. Domínio próprio com HTTPS automático em todos os planos; preset de Next.js estático; tudo em provedores europeus. Comercial no grátis: **a página não diz** (não confirmado).
- **Bunny.net** (https://bunny.net/pricing/): sem plano grátis (teste de 14 dias), mínimo de US$ 1/mês, CDN a partir de US$ 0,01/GB (Europa/América do Norte). Preço do Storage e hospedagem de site estática: a página não mostra. Mais técnico: é CDN, não é "publicar com um clique".

### Hospedagem tradicional brasileira

- **Hostinger** (https://www.hostinger.com/br/hospedagem-de-sites): Single R$ 5,99/mês (1 site), Premium R$ 10,99 (3 sites), Unlimited R$ 13,99 (ilimitados), Cloud Startup R$ 39,99. Preço de renovação: R$ 23,99, R$ 38,99, R$ 64,99 e R$ 129,99. A página só mostra contrato de 48 meses; diz que dá para pagar mensalmente, mas recomenda planos longos. SSL em todos. Node.js listado como "Yes" nessa tabela; a página não cita Next.js. Pela documentação da Hostinger (busca), aplicativos Node.js/Next.js ficam nos planos **Business e Cloud**, com deploy via GitHub ou upload (https://www.hostinger.com/support/how-to-deploy-a-nodejs-website-in-hostinger/); o nome e o preço exatos do plano "Business" na página brasileira: não confirmado.
- **Locaweb** (https://www.locaweb.com.br/hospedagem-de-sites/): Go R$ 5,90/mês promo (regular R$ 24,90, 1 site, 15 GB); Hospedagem I R$ 10,90 (regular R$ 37,90, sites ilimitados, 35 GB); II R$ 16,90 (regular R$ 59,90). SSL ilimitado grátis. Preço promocional só "no primeiro ciclo de contratação"; pagamento "integral e antecipado". A página não cita Node.js: Next.js com servidor, não confirmado.
- **Para o cliente ser dono:** é o caminho mais simples, o cliente contrata no CPF/CNPJ dele e passa os dados de acesso ao Daniel. Desvantagem: para site estático do Next.js, o fluxo é "gerar a pasta `out` e enviar por upload/FTP", sem deploy automático a cada commit.

---

## 4. Recomendação em 3 níveis

### Nível 1: grátis e seguro para uso comercial (site estático ou Next.js exportado)

**Cloudflare Pages**, na conta do próprio cliente, com o Daniel convidado como membro.

- Por quê: termos sem proibição de comercial no plano grátis, pedidos de arquivo estático "free and unlimited", 500 builds/mês, domínio próprio.
- Regras de ouro: (1) o cliente cria a conta; (2) sem cobrança de cartão dentro do próprio site no plano grátis (cláusula 2.2.1(h)); (3) sem muito vídeo próprio; (4) não guarde vários clientes na sua conta para "revender hospedagem".
- Plano B grátis: **Netlify Free** (a Netlify diz que pode comercial; cuidado com os 300 créditos/mês e com a pausa do site se estourar).
- Evite para cliente pagante: GitHub Pages (termos barram "online business").

### Nível 2: barato (quando precisa de mais folga ou servidor leve)

- **Cloudflare Workers Paid, US$ 5/mês** por conta (10 milhões de pedidos incluídos): serve para Next.js com servidor via vinext/OpenNext, aceitando mais configuração (e o vinext está em beta segundo a página que li).
- **Netlify Personal, US$ 9/mês**, ou **Statichost Starter, 9 EUR/mês**, se o cliente quiser um painel simples.
- **Hostinger Single, R$ 5,99/mês na promoção** (renova a R$ 23,99), se o cliente prefere pagar em reais e ter suporte em português; bom para site estático institucional.
- **Railway Hobby US$ 5/mês ou Fly.io (a partir de US$ 2,19/mês)** apenas para back-end/servidor próprio.

### Nível 3: quando vale a Vercel Pro (US$ 20/mês)

- Next.js com SSR, ISR, middleware e otimização de imagem sem configuração extra.
- Cliente quer pré-visualização por link a cada alteração e fluxo "empurrou no GitHub, publicou".
- Cliente topa pagar US$ 20/mês de Vercel (ou o Daniel embute no valor de manutenção).
- Como entregar: criar o projeto num time Pro e, na entrega, usar "Transfer Project" para o time Pro do cliente (transferência oficial, sem queda). Lembre: o uso é zerado e integrações precisam ser refeitas.
- Regra: **nunca deixar cliente pagante, nem previews dele, na Hobby**: pela definição da Vercel, o ganho financeiro do consultor já torna o uso comercial.

---

## 5. Passo a passo curto: site Next.js estático no Cloudflare Pages (grátis)

Baseado em https://developers.cloudflare.com/pages/framework-guides/nextjs/deploy-a-static-nextjs-site/ e na documentação de exportação estática do Next.js (https://nextjs.org/docs/app/guides/static-exports).

1. **Cliente cria a conta Cloudflare** (grátis) e convida o Daniel como membro. Não crie a conta "em nome" do cliente (cláusula 2.2.1(a)).
2. **Repositório no GitHub** do cliente (ou do Daniel, e depois transfere): o site precisa estar lá.
3. **Configurar o Next.js como estático** no arquivo `next.config.js`: `output: 'export'`. Ao rodar `next build`, ele cria a pasta `out` com HTML/CSS/JS.
4. **Atenção ao que NÃO funciona em site estático** (lista oficial do Next.js): rotas dinâmicas sem `generateStaticParams`, cookies, redirects/rewrites/headers do `next.config`, ISR, Server Actions, Draft Mode, otimização de imagem padrão (precisa de "custom loader", por exemplo Cloudinary).
5. No painel da Cloudflare: **Workers & Pages > Create application > aba Pages > Import an existing Git repository**; escolha o repositório > Begin setup.
6. Escolha o preset **"Next.js (Static HTML Export)"**. Ele preenche: branch de produção `main`, comando de build `npx next build`, pasta de saída `out`.
7. **Save and Deploy.** O site sai em um endereço `*.pages.dev`. A cada commit novo, a Cloudflare refaz o site; cada pull request ganha uma prévia.
8. **Domínio próprio:** na aba do projeto, adicionar domínio personalizado (limite grátis: 100 por projeto). Passo a passo exato e HTTPS automático: não confirmado nesta pesquisa; conferir na documentação de Pages.
9. **Entrega:** o domínio fica registrado no nome do cliente (no Registro.br para .com.br; boa prática geral, não vem das páginas acima), a conta Cloudflare é do cliente e o Daniel permanece como membro enquanto fizer a manutenção.

Para site com servidor (SSR/ISR) na Cloudflare, use o vinext (a documentação recomenda como padrão) e leia o guia em https://developers.cloudflare.com/workers/framework-guides/web-apps/nextjs/. É beta, segundo a página.

---

## 6. O que mudar na skill deploy-to-vercel (parágrafo para o APRENDIZADOS dela)

Texto sugerido, em português, pronto para colar:

> **Aviso de uso comercial (conferido em 12/out/2026).** O plano Hobby (grátis) da Vercel é restrito a "non-commercial personal use only". Pela definição oficial (https://vercel.com/docs/limits/fair-use-guidelines), é uso comercial qualquer site usado para ganho financeiro de **qualquer pessoa envolvida em qualquer etapa da produção, inclusive consultor pago que escreve o código**, e "Receiving payment to create, update, or host the site" está na lista de exemplos. Portanto, esta skill NÃO deve publicar site de cliente pagante (nem as prévias dele) em conta Hobby, mesmo que a conta seja do cliente. Antes de qualquer deploy, perguntar: "este site é para um cliente que paga, tem anúncios, vende algo ou gera lucro para alguém?". Se sim: (a) usar Vercel Pro (US$ 20/mês por vaga de desenvolvedor) num time Pro e, na entrega, transferir o projeto para o time do cliente com Settings > General > Transfer Project (https://vercel.com/docs/projects/transferring-projects); ou (b) sugerir outra hospedagem grátis permitida para comercial (Cloudflare Pages ou Netlify Free; ver pesquisa de hospedagem) quando o site for estático. Se for portfólio, estudo ou projeto pessoal sem remuneração, Hobby continua valendo. Em caso de dúvida, o próprio texto da Vercel manda consultar o suporte. Preços e regras mudam: reconfirmar nas páginas oficiais antes de recomendar.

Mudanças sugeridas no fluxo da skill (para o Daniel aprovar antes de alterar qualquer skill):
1. Adicionar a pergunta de "uso comercial?" como primeiro passo.
2. Se "sim" e o site é estático: oferecer Cloudflare Pages (passo a passo da seção 5).
3. Se "sim" e precisa de servidor: oferecer Vercel Pro ou Cloudflare Workers.
4. Incluir a regra de transferência (projeto de um time para outro, sem queda).
5. Não guardar chaves, tokens ou senhas do cliente nos arquivos do projeto.

---

## 7. Dúvidas em aberto (para perguntar ao suporte antes de fechar contrato com cliente)

- Cloudflare: o Daniel pode ser membro na conta do cliente e cobrar manutenção sem violar "sell access"/"sign up on behalf of a third party"? Checkout de terceiros conta como "coletar cartão"? Não confirmado.
- Netlify: cobrar hospedagem embutida no valor do site no plano Free, tudo bem? A documentação oficial não responde; fóruns se contradizem.
- Vercel: previews de site de cliente em Hobby contam como comercial? Pela definição, tudo indica que sim, mas a Vercel manda confirmar com o suporte.
- Render, Railway, AWS Amplify, Firebase, Statichost: termos de uso comercial no plano grátis não foram confirmados.
- Datas das páginas de metadados de alguns sites (ex.: Netlify) divergiam da data escrita no texto; usei a data do texto.
- Preços podem mudar sem aviso; todos os valores acima são de páginas lidas em 12/out/2026.
