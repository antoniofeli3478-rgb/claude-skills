---
nome: "Material Design 3 (tokens de movimento e de estado)"
repositorio: "material-components/material-web (tokens/versions/latest/sass/_md-sys-motion.scss) e androidx/androidx (compose/material3/.../tokens)"
caminho_no_repo: "_md-sys-motion.scss (material-web); StateTokens.kt, StandardMotionTokens.kt, ExpressiveMotionTokens.kt (AndroidX)"
autor: "Google (Material Design)"
instalacoes: "não verificado"
commit_ou_versao: "material-web 47adb65 (07/10/2026); androidx branch main (baixado em 11/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "\"7 (motion B) / 8 (botões B)\""
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/references/molas-e-curvas.md §1–2; skills/design/button-animation/SKILL.md (§2.3 Molas; §3 Estados)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.3–3.5)", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.7)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.12–2.13)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.3, §3.5)", "skills/design/motion-oficial/ORIGEM.md", "skills/design/button-animation/ORIGEM.md", "skills/design/button-animation/LICENCAS-TERCEIROS.txt", "skills/design/motion-oficial/LICENSES/material-web-Apache-2.0.txt"]
---
# Material Design 3 (tokens de movimento e de estado)

## O que é
Números oficiais do Google para movimento: curvas, durações, molas e opacidade das camadas de estado (hover, foco, pressionado). Não é uma skill, e sim tokens de design. Só os números foram usados.

## O que aproveitamos
- Curvas: `standard` (0.2, 0, 0, 1); `emphasized-decelerate` (0.05, 0.7, 0.1, 1); `emphasized-accelerate` (0.3, 0, 0.8, 0.15).
- Molas convertidas para o `damping` do Motion pela fórmula razão x 2 x raiz(rigidez) (conferida na transcrição): fast-spatial 1400/67; default-spatial 700/48; slow-spatial 300/31; fast-effects 3800/123; default-effects 1600/80; slow-effects 800/57.
- Molas "Expressive" (botões): fast 800/34 (quica); default 380/31; slow 200/23.
- Lição: molas "espaciais" podem passar um pouco do ponto (0,9); molas de "efeito" (opacidade, cor) nunca (1,0).
- Camadas de estado: hover 0,08; foco 0,10; pressionado 0,10; arrastando 0,16; desativado 0,38 (conteúdo) e 0,12 (container, da especificação M3, não do arquivo lido).
- Durações: short 50/100/150/200 ms; medium 250–400 ms; extra-long 700–1000 ms para marketing.

## O que NÃO aproveitamos e por quê
- Só valores numéricos foram usados; nenhum texto ou código foi copiado do AndroidX.
- A curva "legacy" do Material (0.4, 0, 0.2, 1), que o dylantarre recomenda, conflita com o Emil e não foi adotada.
- Nome da curva (0.2, 0, 0, 1): a pesquisa de motion a chama de `standard` e a de botões, de `emphasized`; o arquivo `_md-sys-motion.scss` é a referência.

## Riscos e cuidados
- Apache-2.0: aviso/atribuição se copiar texto (aqui só números, que são fatos). Cópia da licença guardada em `LICENSES/material-web-Apache-2.0.txt`.
- Nada executado.

## Transcrição: o que aprendemos (nas nossas palavras)
- No Material, `damping` é a razão de amortecimento com massa 1; no Motion é um coeficiente. Converta com razão x 2 x raiz(rigidez).
- Movimento no espaço (posição, tamanho) pode ter leve overshoot; mudança de cor e opacidade não deve ter.
- "Sério" = Standard (700/48 ou 1400/67); "divertido" = Expressive (800/34).
- Em vez de trocar a cor de fundo no hover, desenhe uma camada com a cor do conteúdo e opacidade fixa (0,08, 0,10…): funciona com qualquer cor de marca.

## Fontes
- https://github.com/material-components/material-web/blob/main/tokens/versions/latest/sass/_md-sys-motion.scss
- https://github.com/androidx/androidx
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.3–3.5)`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.7)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.12–2.13)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.3, §3.5)`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/button-animation/LICENCAS-TERCEIROS.txt`; `skills/design/motion-oficial/LICENSES/material-web-Apache-2.0.txt`
