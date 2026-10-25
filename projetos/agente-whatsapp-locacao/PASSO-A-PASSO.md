# Agente de WhatsApp para locação de brinquedos: passo a passo

## Objetivo
Um agente de IA no WhatsApp para locadoras de brinquedos de festa, com clima de festa. É **um
molde só, com um perfil por empresa** (tom, emojis, catálogo, frete e regras), e cada empresa fica
diferente seguindo o padrão dela.

## Combinado com o usuário
- Sempre avisar quando terminar uma tarefa: resumo final + notificação (PushNotification).
- Base do agente: manter o código próprio em `agente-locacao/`. O Chatwoot (central da equipe) fica para uma fase 2, se o dono aprovar.

## O que foi feito
- **24/10/2026:** pesquisa de repositórios de referência. Os 5 melhores foram baixados para
  `research_notes/Agente WhatsApp locacao 2026-10/referencias/` (leia antes o `LEIA-ME.md` da pasta).
  - Comando: `git clone --depth 1 <url>` e depois `rm -rf .git`. Do cookbook, só a pasta `tool_use`, com sparse-checkout.
  - O plano aprovado está resumido abaixo.
- **25/10/2026:** dossiê da 1ª empresa, a **Anima Kids** (Anima Kids Park, Ribeirão Preto). Está
  em `research_notes/Agente WhatsApp locacao 2026-10/anima-kids/`:
  - `DOSSIE.md`: tudo o que foi achado e a lista do que falta perguntar ao dono;
  - `catalogo-rascunho.yaml`: 52 itens, 38 com foto;
  - `dados-brutos/`: texto de 65 páginas e 23 posts, mais o índice de fotos.
  - Como foi feito: a API pública do WordPress do site, `/wp-json/wp/v2/pages|posts|media`.
    Os scripts ficaram no scratchpad e não foram para o repositório.
  - O Instagram bloqueia a leitura automática: o usuário mandou prints (ver `instagram.md`).
  - **Regra do usuário:** a Anima Kids não tem endereço fixo (os do Maps são só para SEO). O agente nunca informa endereço.
- **25/10/2026 (2):** Instagram pelos prints do usuário (`anima-kids/instagram.md` e `prints/`).
  - Aviso contra golpe vira regra de pagamento do agente.
  - Aventura na Fazenda e Celeiro são o mesmo brinquedo.
  - Dual Splash entrou no catálogo.
  - O usuário definiu o foco: infláveis diferenciados ("na região não tem igual"), mais cama
    elástica e piscina de bolinhas. Sem personagem fantasiado; recreação só no futuro. Jogos,
    touro e pipoca ficaram `ativo: false`.
  - Obs.: "Daniel" é só o nome que veio no fork do claude-skills, não é o usuário.
- **25/10/2026 (3): 1ª versão do agente construída** (o usuário pediu para montar já, mesmo
  genérica, para mostrar ao dono e ganhar a confiança dele antes de pedir preços e regras).
  - **Onde:** repositório `whatsapp-agentkit`, branch `claude/whatsapp-toy-rental-agent-9jfi4l`,
    pasta `agente-locacao/`. Comece pelo `agente-locacao/LEIA-ME.md`.
  - Commits `2574d60` e `fd1b79f` enviados ao GitHub em 25/10. No começo o push deu 403;
    resolveu quando o usuário liberou o `whatsapp-agentkit` no app do Claude
    (github.com/settings/installations → Claude → Configure → Repository access).
  - **Regra de ouro (definida pelo usuário):** a IA atende e a equipe fecha.
    - A IA nunca confirma data, reserva ou valor, e nunca dá endereço.
    - Ela anota a ficha e passa para a equipe.
    - Depois disso fica pausada: volta pelo painel, pelo comando `#bot voltar <telefone>` ou
      sozinha depois de 12 h.
  - **Nome do agente:** "Anima Kids Park Bot" (o usuário pediu; troca no `perfil.yaml`).
  - **Ferramentas (tool use):**
    - `sugerir_brinquedos`: filtra por idade, espaço, água e tema;
    - `ver_brinquedo`;
    - `enviar_foto`: a foto real sai do site da empresa;
    - `anotar_pedido`;
    - `passar_para_equipe`.
  - **Travas no código:** promessa de data ou reserva, valor e endereço. A IA ganha uma segunda
    chance; se errar de novo, a conversa vai para a equipe.
  - **Zernio:**
    - manda foto com `attachmentUrl` + `attachmentType: image`;
    - o evento `message.sent` com `sentVia: human` significa que a equipe respondeu pelo celular,
      e a IA pausa.
    - Fonte: docs.zernio.com (send-inbox-message e webhooks/inbox).
  - **Testes:**
    - 37 testes com pytest e um Claude falso, sem gastar API;
    - `tests/test_local.py`: conversa no terminal;
    - `tests/rodar_conversas.py`: 12 cenários com a API de verdade (falta a chave da Anthropic).
  - **Teste de ponta a ponta:** feito com o `uvicorn`. O webhook responde 200 na hora, chama a
    API e trata erros de chave sem cair.
  - **Modelo:** `claude-sonnet-5`, o padrão do molde do AgentKit; troca pelo `ANTHROPIC_MODEL`.
- **Habilidades usadas nesta etapa:**
  - skill de plataforma `claude-api` (tool use, cache do system prompt, effort);
  - o tom de festa veio do Instagram e do site (`anima-kids/instagram.md`).

## Plano aprovado
1. Partir do código do `whatsapp-agentkit` (CLAUDE.md, seção 3), traduzido para pt-BR.
2. Criar a pasta `empresas/<slug>/` com `perfil.yaml`, `catalogo.yaml`, `regras.yaml` e `faq.md`. A variável `EMPRESA=<slug>` escolhe o perfil.
3. Ligar o `tools.py` ao ciclo de ferramentas do Claude, com estas ferramentas:
   - consultar_disponibilidade
   - montar_orcamento
   - sugerir_combo
   - criar_pre_reserva
   - chamar_atendente
4. O código calcula o preço, o frete e a confirmação (padrão do `whatsapp-ai-agent`).
5. A agenda e o estoque por item ficam numa tabela própria. O Google Calendar fica opcional.
6. Fazer uma bateria de conversas de festa para testar cada perfil.

## Habilidades usadas
- `skills/combos/combo-copy-humanizada/SKILL.md`: personalidade de cada empresa (voz `casual`).
- Skill da plataforma `claude-api`: uso de ferramentas (tool use) com o Claude.

## Próximo passo
1. A chave da API da Anthropic (fica só no `.env` do container) → rodar `tests/test_local.py` e
   `tests/rodar_conversas.py` e ajustar o tom pelas transcrições.
2. Mostrar ao dono. Depois, preencher com os dados dele: preços, frete, sinal/Pix,
   cancelamento e chuva, estoque, horário e número da equipe (`EQUIPE_WHATSAPP`).
3. Deploy na Railway (Root Directory `agente-locacao`) e webhook da Zernio (eventos
   message.received e message.sent).

Também falta decidir até onde vai a reserva e o provedor (Zernio ou Meta).
Onde fica o código: ainda não foi decidido (repositório próprio ou branch de projeto, nunca a `main`).
