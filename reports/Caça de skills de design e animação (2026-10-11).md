# Relatório: caça de skills de design e animação (11/10/2026)

Pedido do Daniel: usar o caçador de skills para melhorar as nossas skills "estilo Frankenstein", começando pela pasta de design, com 2 agentes de pesquisa e 1 de transcrição por skill. Depois vieram mais três pedidos:
- pesquisar o site Refero;
- deixar o carrossel de fora;
- pesquisar animação com IA no Blender.

**Situação:** está tudo pronto e esperando a sua aprovação. Nada foi para a branch principal (`main`). Uma cópia de segurança está na branch `rascunho-caca-skills-design` do GitHub.

---

## 1. O que foi feito

| Rodada | Skill | Agentes | Resultado |
|---|---|---|---|
| 1 | `motion-oficial` (animação de interface) | 2 pesquisa + 1 transcrição | **Reescrita** |
| 1 | `button-animation` (botões e micro-interações) | 2 pesquisa + 1 transcrição | **Reescrita** |
| Refero | sites premium, animados e minimalistas | 3 pesquisa + 1 transcrição | **Skill nova** `estilos-premium` |
| 2 | `ui-ux-pro-max` (design de telas e sites) | 2 pesquisa + 1 transcrição | **Reescrita** (antes era só um link) |
| 2 | animação com IA no Blender | 2 pesquisa + 1 transcrição | **Skill nova** `blender-ia` |
| – | `thumbnail-carousel` | – | Fora, como você pediu |

No total foram 19 agentes. Dois limites de uso da sessão interromperam o trabalho, mas os agentes foram retomados de onde pararam, sem perder nada.

---

## 2. As 5 skills, uma por uma

### 2.1 `motion-oficial` → "como animar interfaces" (reescrita)

- **Antes:** a skill ensinava só a escrever código com o Motion (o antigo Framer Motion), em inglês.
- **Agora:** ensina a **decidir o movimento**:
  - se algo deve mesmo animar: o que a pessoa usa 100 vezes por dia não anima;
  - quanto tempo dura: interface até 300 ms;
  - qual curva e qual mola usar;
  - como respeitar quem pede "reduzir movimento";
  - como revisar, com uma revisão de desempenho grátis no lugar da paga;
  - como evitar o "movimento com cara de IA".
- **Também traz:**
  - as suas 4 regras adaptadas para telas;
  - uma ponte para o Remotion: as mesmas curvas e tempos nos vídeos.
- **Fontes principais:** Emil Kowalski (o designer de animação mais citado, MIT), Matthew Blode (MIT), Kyle Zantos (MIT), Material 3 do Google (Apache), Vercel (MIT) e Motion Lexicon (CC BY).
- **Arquivos:** o `SKILL.md` com 208 linhas, mais 7 referências e um agente revisor.
- **Atenção:** os arquivos antigos em inglês saem. O conteúdo deles foi traduzido e reorganizado. A pasta é trocada inteira.

### 2.2 `button-animation` → botões premium (reescrita)

- **Todos os estados do botão:** parado, mouse em cima, foco, apertado, desligado, carregando, deu certo e deu erro.
- **Receitas novas:**
  - "Salvando… → ✓ Salvo";
  - ícone que se transforma;
  - "segure para excluir";
  - tremidinha de erro;
  - brilho que passa pelo botão e borda brilhante;
  - onda no clique;
  - botão 3D que afunda;
  - botão "magnético".
- **Dois botões prontos e testados no TypeScript:** `status-button` e `hold-to-confirm`.
- **Corrigido:** o exemplo principal usava `transition-all`, que todas as fontes condenam, e o botão desligado ainda encolhia ao ser tocado.
- **Mudança de padrão:** o botão **não quica** mais ao ser tocado. O quique fica para apps divertidos.
- **Fontes:** Emil Kowalski, Jakub Krehel, Vercel, Magic UI, Motion Primitives e Animata (todas MIT), mais o Material 3.

### 2.3 `ui-ux-pro-max` → design de telas e sites premium (reescrita)

- **Antes:** era só um bilhete apontando para a skill de outra pessoa.
- **Agora:** é uma skill nossa, em português, com **6 passos**:
  1. entender o pedido;
  2. escolher **uma** referência (✋ você aprova);
  3. escrever a ficha de design `DESIGN.md` (✋ você aprova);
  4. transformar a ficha em código com a ferramenta oficial do Google, que também confere o contraste das cores;
  5. montar os componentes;
  6. revisar com prints no celular e no computador.
- **Quatro estilos para escolher:**
  - "galeria clara" (tipo Apple);
  - "palco escuro" (tipo Ferrari);
  - "instrumento de precisão" (tipo Linear e Stripe);
  - "minimalista editorial".
- **Receitas** "tipo Apple" e "tipo Samsung".
- **Uma lista de sinais de "cara de IA"** para evitar: roxo por padrão, cards em tudo, emoji como ícone.
- **Fontes:** Anthropic (frontend-design), taste-skill, Emil Kowalski (apple-design), Vercel, a skill oficial do Refero (MIT), a coleção VoltAgent (MIT), o padrão DESIGN.md do Google (Apache) e os números públicos da Apple e do Material.
- **Testado:** o modelo de ficha passou no verificador do Google sem nenhum erro.

### 2.4 `estilos-premium` → biblioteca de estilos (skill nova)

- **Para quando você pedir:** "no estilo da Apple", "tipo Samsung", "premium" ou "minimalista".
- **O que traz:**
  - as 5 famílias de visual premium e como escolher uma;
  - 15 fichas de marca com licença livre: Apple, BMW, Bugatti, Ferrari, Lamborghini, Tesla, Nike, SpaceX, Linear, Vercel, Stripe, Notion, Superhuman, Raycast e Framer;
  - um índice de 64 estilos estudados, com resumos nossos e os links;
  - uma **resenha didática** do que aprendemos: o que dá "cara de caro" e o que dá "cara de IA";
  - receitas de animação para sites, testadas e ajustadas às suas regras.
- **O principal aprendizado:**
  - **Caro é tirar, não pôr.** 22 de 27 sites premium quase não têm cor, e 23 de 27 não usam sombra.
  - **Uma cor, uma função.** Na Apple, o azul só aparece no botão de comprar.
  - **Letra grande e fina.**
  - **Muito espaço:** cerca de 96 px entre as seções.
  - **Movimento discreto:** muda a cor, não a posição.
- **A Samsung não está no Refero.** A receita "tipo Samsung" foi montada com medidas de outra fonte.

### 2.5 `blender-ia` → animação 3D com IA no Blender (skill nova)

- **O que faz:** o Claude escreve as instruções do Blender sozinho, sem abrir o programa, e entrega o **celular, produto ou logo 3D com fundo transparente**. Isso entra como camada no vídeo do Remotion, entre o fundo e os textos, seguindo as suas regras de camadas.
- **Quando usar:** em 1 ou 2 cenas de destaque por vídeo, de 3 a 5 segundos. Aqui na nuvem não há placa de vídeo, então o Blender fica umas 20 vezes mais lento que o Remotion.
- **Tempos medidos:**
  - rascunho pequeno: 1 a 2 segundos por quadro;
  - versão final: cerca de 15 segundos por quadro;
  - 2 segundos de vídeo levam cerca de 15 minutos.
- **Testado:** 5 scripts funcionaram no Blender 5.2.2. Funcionaram o fundo transparente, a sombra separada e a tela do celular mostrando as telas do app quadro a quadro.
- **Seguindo as suas regras:** o agente pegou um logo que "brotava" no meio da tela (regra 4) e o corrigiu. Agora o logo entra pela borda de baixo; veja a imagem `folha_logo.png`.
- **No seu computador:** o "Blender MCP" (licença MIT) deixa o Claude mexer no Blender aberto. A skill explica como instalar com segurança, com a telemetria desligada.
- **Licenças:**
  - Blender e Poly Haven: livres para anúncio;
  - modelos 3D feitos por IA: cada serviço tem uma regra, e há uma tabela na skill;
  - usar sempre um celular genérico, nunca um iPhone.
- **Sinceridade:** o teste ainda não tem cara premium (a tela do celular é um degradê liso). Isso depende da direção de arte de cada vídeo, que a skill ensina.

---

## 3. O que aconteceu com o site Refero (importante)

- **Os termos de uso do Refero** proíbem:
  - robôs e coleta automática de páginas;
  - transformar o conteúdo deles num repositório, num banco de dados ou num recurso para IA;
  - usar o conteúdo para treinar IA.

  O site também bloqueia robôs de IA.
- **Os agentes só descobriram os termos no meio do trabalho.** Antes disso, já tinham feito cerca de 1.650 acessos ao site, e um deles leu o começo de todas as 1.342 páginas. Isso não deveria ter acontecido. Está registrado nas transcrições e não vai se repetir.
- **O que eu fiz:**
  - mandei parar a coleta;
  - tirei as cópias do GitHub e reescrevi a branch de rascunho sem elas;
  - deixei nas skills só o que os termos permitem: o que **aprendemos**, escrito por nós, e a **skill oficial do Refero**, que tem licença livre (MIT).
- **As fichas de marca vêm de outra fonte,** a coleção aberta VoltAgent, que tem licença MIT.
- **O material bruto do Refero está só aqui no computador temporário.** Recomendo apagar.
- **O repositório agora é privado** (conferido).

---

## 4. Decisões para você

1. **Aprovar as 5 skills?** Responda "pode salvar tudo" ou os números:
   1. `motion-oficial` (reescrita);
   2. `button-animation` (reescrita);
   3. `ui-ux-pro-max` (reescrita);
   4. `estilos-premium` (nova, em `skills/design/`);
   5. `blender-ia` (nova, em `skills/video/`).
2. **As 15 fichas de marca da VoltAgent:** guardar no repositório? **Recomendo sim**, porque têm licença livre e o repositório agora é privado. A outra opção é baixar só a da marca escolhida em cada projeto.
3. **Juntar os dois modelos de ficha `DESIGN.md` num só?** **Recomendo sim,** dentro da `ui-ux-pro-max`. A `estilos-premium` ficaria com a biblioteca de marcas e a `ui-ux-pro-max` com as regras.
4. **Sua regra 4 em sites:** num site, um texto que "sobe e aparece" quando você rola a página conta como "surgir do nada"? A proposta faz o texto entrar pela borda de baixo da tela, junto com a rolagem. Isso serve?
5. **Instalar também duas skills do Emil Kowalski como skills separadas?** São a `review-animations`, que revisa animações, e a `animation-vocabulary`, que ensina o nome certo de cada efeito. **Opcional.**
6. **Trocar o nome da `motion-oficial` para `animacao-ui`?** **Opcional.** Hoje ela não é mais "oficial", é nossa.
7. **Apagar o material bruto do Refero** deste computador temporário? **Recomendo sim.**

Depois da aprovação, eu:
- salvo na `main` com PR;
- atualizo o índice `guia/HABILIDADES.txt` e o manual;
- copio as pesquisas para `research_notes/`;
- confiro que o programa de skills encontra todas as skills.
