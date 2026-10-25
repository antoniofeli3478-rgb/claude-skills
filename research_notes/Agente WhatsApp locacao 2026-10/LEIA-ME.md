# Agente de WhatsApp para locação de brinquedos: referências (24/10/2026)

Material de estudo para o agente de locação de brinquedos para festas (um perfil por empresa).
Projeto: `projetos/agente-whatsapp-locacao/PASSO-A-PASSO.md`.

Base do agente: o molde `whatsapp-agentkit`, que é um repositório à parte (FastAPI + Claude +
Zernio/Meta, memória e deduplicação). O que falta nele: o Claude **não chama** as ferramentas do
`tools.py`, e isso é essencial para consultar datas, montar orçamentos e fazer pré-reservas.

## Empresas

- `anima-kids/`: dossiê da Anima Kids Park, de Ribeirão Preto (comece pelo `DOSSIE.md`).

## O que tem em `referencias/` e o que aproveitar

| Pasta | Licença | Abrir primeiro | O que aproveitar |
|---|---|---|---|
| `whatsapp-ai-agent/` | MIT | `app/conversation/agent.py`, `tools.py`, `messages.py`; `app/booking/base.py`, `internal.py`; `evals/`; `examples/demo-clinic/business.yaml` | **Melhor referência.** É feito com o Claude e chama as ferramentas num ciclo (no máximo 6 passos). O código, e não a IA, escreve a confirmação e o "reserva feita". Se a IA disser "reservei" sem ter rodado a ferramenta, a frase é corrigida e nunca chega ao cliente. A reserva tem uma interface `BookingProvider` (agenda própria ou Google Calendar). Os dados do negócio ficam num YAML (`business.yaml` + FAQ + guia de estilo). A bateria de testes repete conversas pelo mesmo caminho da produção. |
| `rental-business-whatsapp-agent/` | não informada | `app/tools.py`, `app/agent.py`, `data/demo_business.json` | Fluxo de **locação**: checa a disponibilidade contra as reservas confirmadas antes de orçar, manda o orçamento em PDF, só reserva depois do "sim" explícito, avisa o dono e tem um `/admin` com fotos. Feito com LangChain/Groq: copiar a ideia, não o código. |
| `booking-agent/` | não informada | `code/backend/app/tools.py` | O código resolve as datas relativas ("sábado que vem") com o fuso horário, sem deixar isso para a IA. Controla quantos clientes cabem em cada horário. Tem um painel React simples. |
| `claude-cookbooks-tool_use/` | MIT (Anthropic) | `customer_service_agent.ipynb`, `tool_use_with_pydantic.ipynb`, `parallel_tools.ipynb`, `memory_cookbook.ipynb` | O padrão oficial do Claude para usar ferramentas. Usar junto com a skill de plataforma `claude-api`. |
| `langgraph-whatsapp-bot/` | não informada | `src/` | Só para leitura: o Google Calendar usado como ferramenta. |

Cada pasta tem um `ORIGEM.md` com a URL, o commit e a licença.
**Não rode nada dessas pastas sem revisar antes:** é código de terceiros.

## Descartados

- **Evolution API e Baileys:** WhatsApp não oficial, com risco de banimento permanente do número da empresa.
- **n8n:** o projeto de agendamento encontrado está pausado.
- **Produtos pagos (LendControl, DeskCaller, KaiCalls):** servem só como ideia de funcionalidades.
- **Projeto aberto de locação de brinquedos:** não foi encontrado nenhum em 24/10/2026. A parte "festa" é nossa.

## Lições para o molde de locação

1. Preço, frete, sinal e confirmação são **calculados pelo código**. A IA só conversa.
2. Pré-reserva só depois do "sim" explícito, com um resumo escrito pelo código.
3. O estoque fica por item e por data (pode haver 2 pula-pulas iguais). Considerar o tempo de montagem e de desmontagem.
4. Um perfil por empresa em arquivos (tom, emojis, catálogo, regras). O código é o mesmo para todas.
5. Uma bateria de conversas de teste para cada perfil antes de colocar no ar.
