# Projeto Gerente Financeiro (passo a passo)

App financeiro pessoal do Daniel: renda variável, envelopes, negócio NFC, cartão Inter (manual), meta do PC e reserva. Local e offline (PWA), dados só no aparelho.

## Onde está cada coisa

- **Código:** branch `claude/nfc-cards-dashboard-wh964t`, pasta `financeiro/` (React + TypeScript + Vite + Tailwind + Dexie).
- **Versão publicada:** `docs/financeiro/` (gerada por `npm run build`), no ar em https://antoniofeli3478-rgb.github.io/claude-skills/financeiro/
- **Manual completo** (rodar, build, backup, restauração, decisões técnicas): `financeiro/README.md`.

## Como foi feito (27/10/2026)

1. **Núcleo primeiro** (`financeiro/src/core/`): tudo em centavos inteiros; saldos nunca guardados, sempre recalculados dos lançamentos (`ledger.ts`); uma função calcula a situação para todas as telas (`situacao.ts`). 43 testes das 18 regras de integridade.
2. **Banco local** (`src/data/`): Dexie com versão de esquema, gravações em transação, recorrências sem duplicar (`geradoAte`), backup JSON validado com Zod e opcionalmente cifrado (AES-GCM + PBKDF2), modo demonstração num banco separado. 15 testes com `fake-indexeddb`.
3. **Telas**: Visão geral, Movimentações, Envelopes, Contas e faturas, Negócio NFC, Metas, Relatórios, Consultor, Configurações e assistente de 8 passos. Registro rápido com 11 tipos.
4. **Teste no navegador** (Playwright, celular 390 px e computador): assistente, recebimento + distribuição, despesa, cartão 3x, venda pendente, todas as telas, recarregar a página (persistência), modo demonstração. Sem erros no console.

## Comandos

```bash
cd financeiro
npm install
npm test          # 58 testes
npm run build     # gera ../docs/financeiro
npm run preview   # http://localhost:4173
```

## Aprendizados

- **CSS fora de camada vence o Tailwind 4.** Uma regra base solta (`button { color: inherit }`) apagou a cor do texto dos botões dourados. Regras base vão dentro de `@layer base`.
- **`<label>` em volta de vários botões:** tocar no rótulo aperta o primeiro botão. Campos com grupos de botões usam `<Campo grupo>` (vira `div role="group"`).
- **Dexie devolve na ordem da chave**, não na ordem da tela: ordenar contas e envelopes por `ordem` ao carregar (o assistente chegou a gravar o saldo na conta errada).
- **Dois PWAs na mesma pasta do Pages:** o service worker do app NFC (escopo `/claude-skills/`) cobria também `/financeiro/`. Ele agora ignora essa pasta (`docs/sw.js`, cache v5).
- `Omit<União, ...>` do TypeScript não distribui: usar o tipo `NovoMov<T>` de `core/types.ts`.
- Playwright global no container: usar `require("playwright")` em `.cjs`.

## Habilidades usadas

| Para quê | Habilidade | Caminho |
|---|---|---|
| Plano visual, anti-"cara de IA" | frontend-design | `skills/design/frontend-design/SKILL.md` |
| Referência de interface premium | ui-ux-pro-max | `skills/design/ui-ux-pro-max/SKILL.md` |

## Próximo passo

- Daniel instalar no celular e rodar o assistente com os números reais.
- Possíveis versões futuras: notificações com o app fechado (exigiria servidor de push), importação de extrato OFX/CSV do Inter, sincronizar entre celular e computador.
