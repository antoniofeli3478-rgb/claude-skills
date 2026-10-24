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
Esperando o Daniel mandar os dados da empresa:
- nome;
- catálogo e preços;
- Instagram ou exemplos de conversa;
- frete, sinal e cancelamento.

Também falta decidir até onde vai a reserva e o provedor (Zernio ou Meta).
Onde fica o código: ainda não foi decidido (repositório próprio ou branch de projeto, nunca a `main`).
