# Minhas Finanças: passo a passo

App de finanças pessoais que roda no computador do Daniel: abre com dois cliques no `index.html`, sem instalar nada e sem internet.

## Onde está o código

- Cópia de segurança: branch `ccr-6d4f3f42-u1lz3q` deste repositório, pasta `app-minhas-financas/`.
- Destino final: repositório próprio `minhas-financas` no GitHub do Daniel. Em 27/10/2026 o Claude **não conseguiu criar o repositório** (o app do GitHub não tem permissão de criar repositórios: erro 403). O Daniel cria um repositório vazio em https://github.com/new?name=minhas-financas e o adiciona à sessão; aí é só dar push do repositório local (`/home/user/minhas-financas`, 2 commits) ou copiar a pasta.

## O que foi feito (27/10/2026)

1. **Escolha da tecnologia:** servidor em Python usando só a biblioteca padrão (`financas.py`) + interface em HTML/CSS/JS puro (`app/`), sem build. O Chart.js 4.4.7 foi baixado para `app/vendor/` (funciona offline).
   - Motivo: o Daniel não programa, então abrir tem que ser "dois cliques" (`Abrir no Windows.bat` / `abrir-mac-linux.sh`).
2. **Dados:** `dados/financas.json` (gravação atômica) + uma cópia por dia em `dados/backups/` (últimos 30). Se abrir o `index.html` direto, usa o localStorage do navegador e mostra um aviso.
3. **Funções:** painel (cartões, rosca por categoria, barras de 6 meses, saldo das contas, orçamento), lançamentos (despesa, receita, transferência, repetir todo mês, parcelado, pendente), orçamento por categoria, metas, contas e categorias editáveis, tema claro/escuro, backup JSON, exportar/importar CSV, dados de exemplo, atalhos de teclado.
4. **Teste:** Playwright (Chromium do container) no computador (1280 px) e no celular (390 px): sem erros no console, sem rolagem lateral, parcelas somando o valor certo (1000,00 em 3x = 333,34 + 333,33 + 333,33).

## Mudança (27/10/2026, pedido do Daniel)

O Daniel não quis Python ("é só um aplicativo pra rodar local"). Removido o `financas.py` e os atalhos `.bat`/`.sh`; a interface foi para a raiz (`index.html`, `style.css`, `app.js`, `vendor/`). Os dados ficam no localStorage do navegador, com lembrete de backup a cada 15 dias (`config.ultimoBackup`). Testado abrindo por `file://`: salva, recarrega e baixa o backup sem erros.

**Aprendizado:** para app pessoal "local" do Daniel, o padrão é **HTML puro que abre com dois cliques**, sem servidor nem instalação.

## Comandos (versão antiga, com Python; não usar mais)

```bash
python3 financas.py                 # abre o app em http://127.0.0.1:8765
python3 financas.py --porta 8800 --sem-navegador
```

## Aprendizados

- Valores em centavos inteiros evitam erro de arredondamento em parcelas.
- `text-transform: capitalize` deixa "Outubro **De** 2026": capitalizar só a 1ª letra no JS.
- Tabelas precisam de um contêiner com `overflow-x: auto` para não estourar a largura no celular.
- `pkill -f <padrão>` dentro do mesmo comando Bash pode matar o próprio shell (o padrão aparece no comando): use o PID.
- `.bat` precisa de fim de linha CRLF (`.gitattributes` com `*.bat text eol=crlf`).

## Habilidades usadas

- Teste visual com Playwright, como em `skills/sites/webapp-testing/SKILL.md` (só o método; script próprio no scratchpad).

## Próximo passo

- Criar o repositório `minhas-financas` e publicar lá.
- Ideias se o Daniel quiser: cartão de crédito com fatura e fechamento, relatório anual, senha para abrir, versão para celular (PWA).
