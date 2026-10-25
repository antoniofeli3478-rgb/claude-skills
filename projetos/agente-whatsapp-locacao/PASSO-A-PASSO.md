# Agente de WhatsApp para locação de brinquedos: passo a passo

## Objetivo
Um agente de IA no WhatsApp para locadoras de brinquedos de festa, com clima de festa. É **um
molde só, com um perfil por empresa** (tom, emojis, catálogo, frete e regras), e cada empresa fica
diferente seguindo o padrão dela.

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
  - O Instagram bloqueia a leitura automática: é preciso pedir prints ao Daniel.
  - **Regra do Daniel:** a Anima Kids não tem endereço fixo (os do Maps são só para SEO). O agente nunca informa endereço.

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
Esperando o Daniel (ou o dono da Anima Kids) responder a lista "Falta perguntar ao dono" do
`DOSSIE.md`: preços, frete, sinal/Pix, cancelamento e chuva, estoque, horário, número da equipe
e prints do Instagram e de conversas.

Também falta decidir até onde vai a reserva e o provedor (Zernio ou Meta).
Onde fica o código: ainda não foi decidido (repositório próprio ou branch de projeto, nunca a `main`).
