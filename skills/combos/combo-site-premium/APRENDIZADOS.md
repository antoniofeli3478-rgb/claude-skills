# Aprendizados: combo-site-premium

Adendos datados. O mais novo fica embaixo.
O combo incorpora os aprendizados até 11/10/2026 (entrada na base).

## 11/10/2026: entrada na base

- **Para que serve:** o passo a passo de um site do zero, ligando as skills novas de `skills/sites/` às de design (`ui-ux-pro-max` → `estilos-premium` → código → `motion-oficial`/`button-animation` → qualidade → publicação).
- **Quando usar:** "faz um site", "landing do meu app", "site para cliente", "meu portfólio".
- **De onde veio:** das pesquisas A (catálogo skills.sh, 19 buscas, 10 repositórios lidos) e B (web e base de referência de sites com licença), ambas de 11/10/2026.
- **O que tem de mais útil:** a ordem (texto e `DESIGN.md` aprovados antes do código), os 5 pontos de aprovação e o loop "construir por seção → print → corrigir".
- **Ainda não testado num site real.** Na primeira vez, anote aqui: tempo de cada passo, o que travou no container (Playwright, Chrome, rede) e o que precisou mudar na ordem.

## 12/10/2026: peças da lista do Daniel (vídeo)

- **Entraram (todas opcionais ou de apoio):** `frontend-design` e `image-to-code` no passo 3; `taste-skill` no passo 6; `playwright-cli` no passo 9; `web-design-guidelines` e o Chrome DevTools MCP (manual em `mcps-para-sites`) no passo 10; o shadcn MCP no passo 5.
- **Escolha entre as que se parecem:** para prints e cliques, `playwright-cli` primeiro (comandos curtos, gasta menos); `webapp-testing` quando precisar subir o servidor junto ou escrever teste com várias etapas. Para medir desempenho, Chrome DevTools MCP (só no computador do Daniel).
- **Conflitos conhecidos:** a `taste-skill` tem padrões com recorte e texto sobre imagem em movimento (quebram as 4 regras do editor); a `image-to-code` manda a imagem ser a "fonte da verdade", mas aqui é o `DESIGN.md`. Em ambos, vale o nosso.
- **Custo de leitura:** `taste-skill` ~22 mil tokens (leia por seção); `image-to-code` ~9 mil; `frontend-design` ~2,5 mil.
- **Nada disso foi usado num site real ainda.**

## 12/10/2026: hospedagem de site de cliente (decisão aplicada)

- **Mudança no passo 11 (publicar):** a pergunta "é uso comercial?" já estava no briefing (passo 0); agora o passo 11 manda **não usar o plano Hobby da Vercel para cliente pagante**, nem nas prévias, e oferecer Cloudflare Pages (estático, grátis) ou Vercel Pro (com servidor). Motivo e fontes: `research_notes/Caça de skills 2026-10/hospedagem/pesquisa.md` e o adendo de 12/10/2026 em `skills/sites/deploy-to-vercel/APRENDIZADOS.md`.
- **Por que a mudança ficou aqui e não na `deploy-to-vercel`:** a `deploy-to-vercel` é uma cópia intacta de terceiros (não se edita). Ela continua só publicando; quem decide **onde** publicar é este combo.
- **Decisão:** o Daniel pediu para terminar as pendências (12/10/2026) e a recomendação dada foi seguida. Se ele preferir o fluxo antigo, é só desfazer este bullet.
- **Ainda não testado:** nenhum site real foi publicado por este fluxo.
