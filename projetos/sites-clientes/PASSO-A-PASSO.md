# PASSO-A-PASSO: sites para empresas (clientes do Antonio)

**Onde está o código:** branch `sites-clientes` deste repositório, pasta `sites-clientes/<empresa>/`.
**Dono:** Antonio (antoniofeli3478-rgb). Fale sempre em português do Brasil, de forma direta. Ele trabalha em vários projetos ao mesmo tempo: não peça o que dá para descobrir sozinho.

## 1. Como o Antonio quer trabalhar

1. Ele manda **só o nome da empresa e mais ou menos onde fica**. O Claude pesquisa tudo sozinho:
   - **dados:** endereço, horário, telefone/WhatsApp, avaliações;
   - **identidade:** logo, cores da loja;
   - **fotos:** do Instagram e do Google Maps.
2. O Claude já entrega o **site pronto**, específico para o ramo daquela empresa.
3. O site tem que ser **bonito e humano**, sem cara de IA, pensado nos mínimos detalhes.
4. **Interação que ele quer:**
   - ao passar o mouse, o elemento vem um pouco para frente e cresce um pouco;
   - ao rolar a página, as coisas vão aparecendo com animação.
5. **Interação que ele NÃO quer:** a "ferramenta interativa" (calculadora, montar pedido, "aberto agora?"). Ele reprovou a ideia.

## 2. Regras aprendidas com o que ele reprovou (30/10/2026, D'Fruta v1)

O primeiro site (`sites-clientes/dfruta/v1-reprovada.html`) foi reprovado com força. Motivos, nas palavras dele:

- **Cores inventadas.** O site saiu verde, mas devia usar **as cores da própria loja** (logo, fachada, embalagem, Instagram). Regra: **nunca escolher paleta antes de ver a identidade real da empresa.**
- **Ilustração no topo.** Os "picolés desenhados" no início pareceram genéricos. Regra: **usar fotos reais da empresa**, nunca ilustração no lugar de produto.
- **"Só faltou estar escrito: sou IA."** Evitar o kit pronto: frases de efeito, blocos e cards iguais, ícones genéricos, mapa desenhado, faixa de texto correndo.

## 3. Onde a pesquisa trava (ambiente na nuvem)

- O **Instagram**, o **iFood**, o **Google Maps** e o **Restaurant Guru** bloqueiam acesso automático (anti-robô). Não tente burlar.
- O site próprio da D'Fruta (`dfrutapicoleteria.com.br`) não é alcançado pela rede do ambiente na nuvem.
- **Solução:** trabalhar numa **sessão que roda no computador do Antonio** (app Claude Desktop, sessão local), usando o navegador do app ou a extensão Claude in Chrome. Assim o Claude abre o Instagram e o Google Maps como uma pessoa e pega logo, cores e fotos.

## 4. Empresa 1: D'Fruta Picoleteria (Ribeirão Preto, SP)

### O que já foi confirmado na internet
- **Nome:** D' Fruta Picoleteria (D'fruta Picoleteria Ltda).
- **Unidades:**
  - Rua Goiás, 1734, Campos Elíseos, Ribeirão Preto, SP. Fica perto do Teatro Municipal e do Jardim Japonês; a Rua Capitão Salomão é a próxima.
  - Rua Coronel Américo Batista, 832, Ipiranga, Ribeirão Preto, SP. No registro de empresas, esta é a sede (matriz) e a da Rua Goiás é filial.
- **História:** picoleteria artesanal e tradicional, desde **1977** (segundo a descrição no iFood).
- **Produto (descrição no Foursquare):**
  - picolés de frutas tropicais, frutas do Cerrado e frutas exóticas;
  - recheados (chocolate italiano);
  - sem adição de açúcar;
  - linha kids no formato de lápis;
  - mais de 150 sabores, feitos de fruta de verdade.
- **Avaliação:** 4,7 no Google, com cerca de 585 avaliações.
- **Frase real de cliente:** "É o melhor picolé de Ribeirão Preto disparado em quesito sabor".
- **Horário (Restaurant Guru, confirmar):** 10h às 22h. No iFood, a loja abre às 10h.
- **Telefones:**
  - (16) 3612-2370 (Restaurant Guru);
  - (16) 3021-4191 (Foursquare, ligado a um endereço antigo: Av. Dom Pedro I, 1728).
- **Delivery:** iFood, com uma página para cada unidade:
  - Campos Elíseos: https://www.ifood.com.br/delivery/ribeirao-preto-sp/d-fruta-picoleteria-campos-eliseos/a1ba5901-5cf3-449e-b4ca-0daa5cfb9923
  - Ipiranga: https://www.ifood.com.br/delivery/ribeirao-preto-sp/dfruta-picoleteria-ipiranga/28e24c49-3c15-4370-a457-e838b213dcae
- **Redes:**
  - Instagram **@dfrutapicoleterias**;
  - site dfrutapicoleteria.com.br;
  - Facebook: https://www.facebook.com/pages/dFruta-Picoleteria/2085662061678879
- **Fontes:**
  - restaurantguru.com.br/DFruta-Picoleteria-Ribeirao-Preto
  - pt.foursquare.com/v/dfruta-picoleteria/4eb58fc1e5fa17fc86d4e141
  - econodata.com.br (CNPJ 36.229.205/0002-71)

### Falta (fazer na sessão local, com navegador)
1. Abrir o Instagram @dfrutapicoleterias e o Google Maps:
   - pegar o **logo** e as **cores reais**;
   - baixar **fotos** da fachada, do balcão e dos picolés;
   - conferir o horário e o WhatsApp.
2. Abrir o iFood e pegar o cardápio real, com nomes dos sabores e linhas.
3. Refazer o site do zero (v2):
   - paleta tirada da loja;
   - fotos reais;
   - história desde 1977;
   - linhas de sabores (Cerrado, exóticas, recheados, sem açúcar, kids);
   - as duas unidades, com "Como chegar";
   - botão do iFood e do WhatsApp;
   - animações de hover e de rolagem.
4. Testar no celular e no computador. Publicar como Artifact e mandar o link ao Antonio.

## 5. Habilidades usadas
- `skills/combos/combo-site-premium/SKILL.md` (roteiro de site do zero)
- `skills/design/ui-ux-pro-max/SKILL.md`, `skills/design/estilos-premium/SKILL.md`
- `skills/sites/web-quality/web-quality-audit/SKILL.md` (qualidade antes de entregar)
- Fora deste repositório, mas úteis: `impeccable` e `taste-skill`, que são repositórios do Antonio no GitHub.

## 6. Histórico
- **30/10/2026:**
  - pesquisa da D'Fruta;
  - v1 publicada e **reprovada** (cores inventadas, ilustração no lugar de foto, cara de IA);
  - descoberto o bloqueio de Instagram, iFood e Google Maps na nuvem;
  - próximo passo: sessão local com navegador.
