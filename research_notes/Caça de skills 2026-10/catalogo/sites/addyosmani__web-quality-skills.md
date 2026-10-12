---
nome: "web-quality-skills (pacote de 6: web-quality-audit, performance, core-web-vitals, accessibility, seo, best-practices)"
repositorio: "addyosmani/web-quality-skills"
caminho_no_repo: "skills/ (6 pastas: web-quality-audit, performance, core-web-vitals, accessibility, seo, best-practices)"
autor: "Addy Osmani (Google Chrome)"
instalacoes: "por skill, na ordem em que a pesquisa lista: web-quality-audit 30,3 mil; performance 39 mil; core-web-vitals sem número; accessibility 60,3 mil; seo 49,7 mil; best-practices sem número (skills.sh find, 11/10/2026)"
commit_ou_versao: "afa8da942115f2961fdbfa80807ea0b232ff6c00 (24/08/2026); pacote versão 2.0"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: 9
status: "instalada-intacta"
onde_entrou: "skills/sites/web-quality/<skill>/ (6 pastas lado a lado, cópia intacta) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-A.md (tabela e seção 3.3)", "research_notes/Caça de skills 2026-10/sites/pesquisa-B.md (item 11)", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md", "scratchpad/hunter/sites/PROPOSTA/skills/sites/web-quality/ORIGEM.md, APRENDIZADOS.md"]
---
# web-quality-skills (pacote de 6)

## O que é
Um pacote de 6 skills que revisam a qualidade técnica de um site, no estilo do Lighthouse: velocidade, acessibilidade, SEO no código e segurança. O método é "baseado em evidência": **medir primeiro**, separar o que foi medido do que é suposição, corrigir e **medir de novo**. As 6 se citam entre si com caminhos `../`, por isso entram **juntas**.

## O que aproveitamos
- Entra **intacto**, com as 6 pastas lado a lado e um `LICENSE`, `ORIGEM.md` e `APRENDIZADOS.md` do pacote.
- Passo 9 "qualidade" do `combo-site-premium`, com o site rodando no navegador, antes de publicar.
- Ao chamar, dizer "do pacote web-quality", porque os nomes `seo`, `performance`, `accessibility` e `best-practices` são genéricos e podem confundir.
- Metas de Core Web Vitals: LCP menor que 2,5 s, INP menor que 200 ms, CLS menor que 0,1.

## O que NÃO aproveitamos e por quê
- Ficaram de fora os arquivos da raiz do repositório feitos para outros programas: `AGENTS.md`, `CLAUDE.md`, `docs/`, `codex/` e `gemini-extension.json`.
- Contraste e acessibilidade **de design** continuam no checklist da `ui-ux-pro-max`; aqui é a auditoria técnica.
- Desempenho de animação continua na `motion-oficial`. Desempenho de código Next/React conversa com a `vercel-react-best-practices` (se complementam).
- A skill `seo` daqui é **implementação no código**. Estratégia e diagnóstico depois que o site está no ar é a `seo-audit` (ficha própria).

## Riscos e cuidados
- Prefere o Chrome DevTools MCP ou a ferramenta Lighthouse (grátis; precisam do Chrome). O MCP só entra com aprovação.
- Sem eles, usa `web-quality-audit/scripts/analyze.sh`. O cabeçalho se declara **somente leitura** e sem rede. Regra nossa: ler o script antes da primeira vez.
- Sem serviço pago. A API do CrUX só é usada se já houver chave. Nunca guardar chave no repositório.
- Os 6 cabeçalhos foram lidos sem erro de YAML.

## Transcrição: o que aprendemos (nas nossas palavras)
- Método: medir (trace, Lighthouse), anotar o que é fato e o que é palpite, corrigir, medir outra vez. Sem medida, não se afirma que melhorou.
- Cada subseção abaixo é uma das 6 skills.

### web-quality-audit (instalações: 30,3 mil)
Porta de entrada do pacote. Roda a auditoria geral, chama as outras cinco e traz o `scripts/analyze.sh` para o caso de não haver Lighthouse nem DevTools MCP.

### performance (instalações: 39 mil)
Orçamento de desempenho, imagens e fontes. Tem referências sobre medição (`MEASUREMENT.md`) e medição de usuários reais (`RUM.md`).

### core-web-vitals (instalações: sem número)
As três medidas do Google: LCP (quando o conteúdo principal aparece), INP (resposta ao clique) e CLS (quanto a página "pula"). Uma referência para cada, com correções para Next e React.

### accessibility (instalações: 60,3 mil)
WCAG 2.2, incluindo as regras novas de tamanho do alvo de toque e de foco. Referências `WCAG.md` e `A11Y-PATTERNS.md`.

### seo (instalações: 49,7 mil)
SEO técnico no código: meta tags, sitemap, dados estruturados JSON-LD (`STRUCTURED-DATA.md`) e "navegação por agentes de IA".

### best-practices (instalações: sem número)
Segurança (`SECURITY.md`) e compatibilidade do site.

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-A.md
- research_notes/Caça de skills 2026-10/sites/pesquisa-B.md
- research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md
- https://github.com/addyosmani/web-quality-skills (citado nas pesquisas; não reaberto)
