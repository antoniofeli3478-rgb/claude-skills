---
nome: "webapp-testing"
repositorio: "anthropics/skills"
caminho_no_repo: "skills/webapp-testing"
autor: "Anthropic"
licenca: "Apache-2.0 (LICENSE.txt dentro da pasta da skill)"
instalacoes: "174,8 mil (skills.sh find, 11/10/2026)"
commit_ou_versao: "dbd4588f9e1033efb41dad4bef2f7947c8993d44 (09/10/2026)"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: 8
status: "instalada-intacta"
onde_entrou: "skills/sites/webapp-testing/ (cópia intacta) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-A.md (tabela e seção 3.4)", "research_notes/Caça de skills 2026-10/sites/pesquisa-B.md (item 9)", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md", "scratchpad/hunter/sites/PROPOSTA/skills/sites/webapp-testing/ORIGEM.md, APRENDIZADOS.md"]
---
# webapp-testing

## O que é
Skill da Anthropic para testar um site no navegador com Playwright (em Python): sobe o servidor, tira prints, clica, preenche formulário e lê o console. Dá "olhos" ao Claude, que escreve código bonito mas não vê o resultado sozinho. É curta (481 palavras).

## O que aproveitamos
- Entra **intacta**: `SKILL.md`, `LICENSE.txt`, `scripts/with_server.py` e `examples/` (`element_discovery.py`, `console_logging.py`, `static_html_automation.py`).
- Árvore de decisão: página HTML estática ou app dinâmico.
- Padrão "reconhecer, depois agir": esperar `networkidle`, tirar print, achar os seletores e só então agir.
- `scripts/with_server.py` sobe o `npm run dev` e roda o teste.
- É o "como" do passo 6 da `ui-ux-pro-max`: prints em 375 px e 1440 px, claro e escuro.
- Onde entra: passo 8 "testar" do `combo-site-premium`.

## O que NÃO aproveitamos e por quê
- Fica de fora a `frontend-design` do mesmo repositório (já é fonte da `ui-ux-pro-max`) e a `web-artifacts-builder` (protótipos; ver ficha própria).
- A instrução da skill de "não ler o script, só rodar com `--help`" **não vale** para nós.

## Riscos e cuidados
- Precisa de `pip install playwright` e `playwright install chromium`. Em 11/10/2026 **não estavam no container** (`import playwright` falhou). Instalar só com aprovação.
- `with_server.py` roda comandos de servidor com `shell=True`. Regra nossa: **ler o script antes do primeiro uso**.
- Sem serviço pago e sem chave.
- Nos prints, conferir também as 4 regras de composição do editor nas animações.

## Transcrição: o que aprendemos (nas nossas palavras)
- Primeiro decidir se o site é HTML solto ou app que precisa de servidor. Para app, o script sobe o servidor, roda o teste e desliga.
- Receita de teste: abrir a página, esperar a rede acalmar, tirar print, listar os elementos, e só depois clicar ou preencher.
- Ler o console do navegador junto com o print mostra erros que a imagem esconde.
- Os três exemplos mostram: descobrir elementos, registrar o console e automatizar HTML estático.
- Para testes mais robustos existe a `playwright-best-practices` (ficha própria, só referência).

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-A.md
- research_notes/Caça de skills 2026-10/sites/pesquisa-B.md
- research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md
- https://github.com/anthropics/skills/tree/main/skills/webapp-testing (citado na pesquisa B; não reaberto)
