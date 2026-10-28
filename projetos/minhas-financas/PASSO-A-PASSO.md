# Minhas Finanças: passo a passo

App de finanças pessoais que roda no computador do Daniel: abre com dois cliques no `index.html`, sem instalar nada e sem internet.

## Onde está o código

- Repositório próprio: https://github.com/antoniofeli3478-rgb/minha-financa (branch `main`, público). Criado pelo Daniel em 28/10/2026, porque o app do GitHub do Claude não tem permissão para criar repositórios (erro 403); o Claude só enviou o código.
- O Daniel também tem o repositório `Minhasfinancas`, de 27/10/2026, que **não foi mexido**. Ele pode ser o app parecido que fez antes.

## O que foi feito (27/10/2026)

1. **Escolha da tecnologia:** servidor em Python usando só a biblioteca padrão (`financas.py`) + interface em HTML/CSS/JS puro (`app/`), sem build. O Chart.js 4.4.7 foi baixado para `app/vendor/` (funciona offline).
   - Motivo: o Daniel não programa, então abrir tem que ser "dois cliques" (`Abrir no Windows.bat` / `abrir-mac-linux.sh`).
2. **Dados:** `dados/financas.json` (gravação atômica) + uma cópia por dia em `dados/backups/` (últimos 30). Se abrir o `index.html` direto, usa o localStorage do navegador e mostra um aviso.
3. **Funções:** painel (cartões, rosca por categoria, barras de 6 meses, saldo das contas, orçamento), lançamentos (despesa, receita, transferência, repetir todo mês, parcelado, pendente), orçamento por categoria, metas, contas e categorias editáveis, tema claro/escuro, backup JSON, exportar/importar CSV, dados de exemplo, atalhos de teclado.
4. **Teste:** Playwright (Chromium do container) no computador (1280 px) e no celular (390 px): sem erros no console, sem rolagem lateral, parcelas somando o valor certo (1000,00 em 3x = 333,34 + 333,33 + 333,33).

## Mudança (27/10/2026, pedido do Daniel)

O Daniel não quis Python ("é só um aplicativo pra rodar local"). Removido o `financas.py` e os atalhos `.bat`/`.sh`; a interface foi para a raiz (`index.html`, `style.css`, `app.js`, `vendor/`). Os dados ficam no localStorage do navegador, com lembrete de backup a cada 15 dias (`config.ultimoBackup`). Testado abrindo por `file://`: salva, recarrega e baixa o backup sem erros.

**Aprendizado:** para app pessoal "local" do Daniel, o padrão é **HTML puro que abre com dois cliques**, sem servidor nem instalação.

## App de celular (28/10/2026, pedido do Daniel: "baixar no celular e ir pra tela inicial")

Virou **PWA** (site instalável), hospedado grátis no **GitHub Pages**: https://antoniofeli3478-rgb.github.io/minha-financa/
- `manifest.webmanifest` + ícones em `icons/` (gerados pelo Playwright a partir de um SVG: normal, maskable e apple-touch-icon) + `sw.js` (offline, rede primeiro e cache como reserva; **aumentar `VERSION` a cada mudança**) + `.nojekyll`.
- No celular (até 640 px): barra de abas embaixo com ícones, botão "+" flutuante com menu (despesa, receita, transferência), lista de lançamentos empilhada no lugar da tabela, formulário abrindo de baixo, campos com 16 px (o iPhone não dá zoom), safe-area do iPhone.
- O backup no celular abre o menu de compartilhar (Drive, WhatsApp); `navigator.storage.persist()` pede para o sistema não apagar os dados.
- Testado com Playwright emulando um Pixel 7: instalação do service worker, uso offline, sem erros e sem rolagem lateral.
- O GitHub Pages foi ligado pelo Daniel (o Claude não tem ferramenta para isso). Ele escolheu a pasta **/docs**, e o build falhou (`No such file or directory ... /docs`). Solução: o app foi movido para `docs/`. **O app agora fica em `docs/`** (no computador: `docs/index.html`). No ar e testado no endereço público em 28/10/2026, inclusive sem internet.

**Aprendizados:**
- "Baixar no celular" para o Daniel = PWA no GitHub Pages; não é preciso APK nem loja.
- Para ver por que o Pages falhou: `actions_list` (run "pages build and deployment") e depois `get_job_logs`. Ao pedir para ligar o Pages, diga a pasta exata e confira o build logo depois: a pasta escolhida no Pages precisa ser a pasta onde está o `index.html`.
- `pkill -f` matou o shell **de novo**: usar `kill $(pgrep -f "padrao[x]")` (o colchete impede que o padrão case com o próprio comando).

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

- Ideias se o Daniel quiser: cartão de crédito com fatura e fechamento, relatório anual, senha para abrir, versão para celular (PWA).
