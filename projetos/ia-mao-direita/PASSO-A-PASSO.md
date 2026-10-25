# IA "mão direita" do dono: passo a passo

Projeto final do usuário: uma IA da empresa que ajuda em todas as áreas (atendimento, vendas,
marketing, financeiro e operação) e funciona como a mão direita do dono.

**Status em 25/10/2026:** só a visão e o roteiro. Nenhum código ainda.

## A visão

Não é uma IA gigante que faz tudo sozinha. É uma **equipe de IAs especialistas**, uma por área,
com uma **IA "chefe"** que:
- conversa com o dono;
- chama a especialista certa;
- junta as respostas e manda relatórios.

Todas as IAs olham para **uma base de dados única** da empresa: clientes, pedidos, agenda,
brinquedos e financeiro.

```
                    DONO (WhatsApp ou painel)
                             │
                   IA "mão direita" (chefe)
                             │
  Atendimento · Vendas · Marketing · Financeiro · Operação
                             │
     DADOS DA EMPRESA: clientes · pedidos · agenda · estoque · financeiro
```

**Regra de ouro:** a IA sugere e prepara, o dono aprova. É a mesma regra do agente de
atendimento ("a IA atende, a equipe fecha"). O que mexe com dinheiro, cliente ou agenda passa
pelo ok de uma pessoa.

## O que o dono faria no dia a dia (exemplos)

| O dono pede | Quem responde | O que acontece |
|---|---|---|
| "Quais festas tenho no sábado e quem monta cada uma?" | operação | responde pela agenda |
| "Quem ainda não pagou o sinal?" | financeiro | lista e sugere a mensagem de cobrança |
| "Faz um post do Big Shark para o fim de semana" | marketing | escreve a legenda no tom da marca e prepara a arte |
| "Como foi o mês?" | a chefe | junta faturamento, festas, brinquedos mais pedidos e orçamentos que não fecharam |

Além disso, todo dia de manhã, sem ninguém pedir, ela manda um resumo: festas do dia,
pendências e oportunidades.

## Os degraus

Cada degrau já é útil sozinho e pode ser vendido como um pacote à parte.

| Degrau | O que entra | Por que nessa ordem |
|---|---|---|
| 1. Atendimento ✅ | Agente de WhatsApp que atende, sugere brinquedos e passa a ficha para a equipe | Traz os clientes e as fichas (projeto `agente-whatsapp-locacao`) |
| 2. Agenda e pedidos | A ficha vira reserva quando a equipe confirma; agenda de festas; estoque dos brinquedos | É a fundação: sem dados organizados, nenhuma IA ajuda |
| 3. Mão direita v1 | O dono conversa com a IA pelo WhatsApp sobre agenda e pedidos, mais o resumo diário | O dono sente o valor cedo |
| 4. Financeiro | Sinais, Pix recebidos, contas, relatório do mês | Precisa da agenda e dos pedidos prontos |
| 5. Vendas | Retomar quem pediu orçamento e sumiu; lembrar clientes antigos em datas especiais | Usa o histórico dos degraus anteriores |
| 6. Marketing | Posts, legendas e campanhas no tom da marca; ideias pela época do ano | Usa as skills do repositório (abaixo) |

## Como fazer (técnico, resumido)

- **Mesma base do degrau 1:** Claude com ferramentas (tool use). Cada especialista tem as
  ferramentas da área dela; a chefe tem ferramentas que chamam as especialistas.
- **Banco de dados único:** hoje o agente de atendimento já guarda as fichas (tabela `pedidos`,
  em `whatsapp-agentkit/agente-locacao/agent/memory.py`). O degrau 2 transforma isso em reservas,
  agenda e estoque.
- **Conexões possíveis:** Google Agenda, Planilhas e Drive (conectores do Claude), Instagram,
  banco/Pix e WhatsApp da equipe.
- **Central da equipe:** o Chatwoot (open source, licença MIT) pode entrar como a caixa de
  entrada única da equipe.
- **Custo:** cresce degrau por degrau (API da IA + servidor). Cada degrau pode virar um pacote
  que o dono paga à parte.

## Habilidades que entram

- Degrau 1: `projetos/agente-whatsapp-locacao/PASSO-A-PASSO.md` (código em
  `whatsapp-agentkit`, pasta `agente-locacao/`).
- Marketing (degrau 6):
  - textos: `skills/combos/combo-copy-humanizada/SKILL.md`;
  - vídeos: `skills/combos/combo-video-explicativo/SKILL.md`;
  - design: `skills/design/ui-ux-pro-max/SKILL.md`.
- Plataforma: `claude-api` (tool use, cache, agentes).

## Próximo passo

Terminar bem o degrau 1:
1. o usuário coloca US$ 5 de crédito e manda a chave da API da Anthropic;
2. testes com a IA de verdade, correções e novos testes;
3. o usuário conversa com a IA;
4. demonstração ao dono da Anima Kids.

Com o dono aprovando, ele vira cliente pagante e isso financia o degrau 2.
