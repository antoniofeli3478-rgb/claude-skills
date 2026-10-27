# Projeto Finanças (antes "Gerente Financeiro") (passo a passo)

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

## 27/10/2026 (2): dois apps separados e ligados

- Pedido do Daniel: **dois apps separados**. O **Plaquinhas NFC** volta a ser o app das placas, com a mesma tela e os mesmos dados (`docs/index.html` é idêntico ao da versão 1.3; só o cache offline do `docs/sw.js` mudou para v6, para limpar uma página salva por engano). O **Finanças** (novo nome) ganhou a aba **Meus negócios**, com o card do Plaquinhas NFC e o botão para abrir o app das placas.
- **Ligação:** mesmo endereço (`antoniofeli3478-rgb.github.io`) = o Finanças lê o localStorage `placas-nfc:v1` do app das placas. Só leitura. Conversor em `financeiro/src/integracoes/nfc.ts`; 5 testes novos (63 no total). O módulo de vendas/produtos/compras de dentro do Finanças saiu: isso agora é do app das placas.
- **Endereço provisório:** o Finanças continua em `/claude-skills/financeiro/` até o Daniel criar o repositório `financas` (a integração do GitHub não tem permissão para criar repositórios). Depois disso: código e build vão para `antoniofeli3478-rgb/financas` (Pages: branch main, pasta /docs), o endereço vira `https://antoniofeli3478-rgb.github.io/financas/`, e `/claude-skills/financeiro/` vira só um redirecionamento. Os dados do Finanças continuam (mesmo endereço base = mesmo banco no navegador).
- Por que separar de endereço: o escopo do app das placas é `/claude-skills/`, que engloba `/claude-skills/financeiro/`. No Android, um app instalado dentro do escopo do outro se mistura.

## Próximo passo

- Daniel criar o repositório `financas` e ligar o Pages; Claude move o app e redireciona o endereço antigo.
- Daniel instalar no celular e rodar o assistente com os números reais.
- Possíveis versões futuras: notificações com o app fechado (exigiria servidor de push), importação de extrato OFX/CSV do Inter, sincronizar entre celular e computador.
