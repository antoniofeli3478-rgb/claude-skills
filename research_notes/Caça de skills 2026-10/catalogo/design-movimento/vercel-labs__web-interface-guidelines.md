---
nome: "Web Interface Guidelines (seção Animation e controles)"
repositorio: "vercel-labs/web-interface-guidelines (https://github.com/vercel-labs/web-interface-guidelines)"
caminho_no_repo: "não verificado"
autor: "Vercel Labs"
licenca: "MIT"
instalacoes: "não verificado"
commit_ou_versao: "434b7f9 (05/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "\"7 (motion B) / 9 (botões B)\""
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/SKILL.md (passos 3, 4 e 7); skills/design/button-animation/SKILL.md (§3 Estados, §5 Acessibilidade)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.2, §3.6, §3.10)", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.8)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.4, §2.16)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.5, §3.8)", "skills/design/motion-oficial/ORIGEM.md", "skills/design/button-animation/ORIGEM.md", "skills/design/motion-oficial/LICENSES/vercel-web-interface-guidelines-MIT.txt"]
---
# Web Interface Guidelines (seção Animation e controles)

## O que é
Guia de regras curtas da Vercel para interface. Para nós valem a seção de animação (preferência de tecnologia, reduced motion, SVG) e as regras de botão (foco, alvo de toque, spinner).

## O que aproveitamos
- Preferência: CSS > Web Animations API > bibliotecas JavaScript.
- Honrar `prefers-reduced-motion`; autoplay de mais de 5 s ao lado de outro conteúdo precisa de pausar/parar/esconder (WCAG 2.2.2).
- SVG: aplicar transforms num `<g>` com `transform-box: fill-box; transform-origin: center;` (corrige bugs do Safari).
- Botão de carregando: manter o rótulo com reticências ("Salvando…"); spinner com atraso de 150–300 ms e tempo mínimo de 300–500 ms para não piscar.
- Nunca `transition: all`; alvos de toque (24 px WCAG, 44 px no celular); `touch-action: manipulation`; interações (hover, active, foco) aumentam o contraste; botão só com ícone leva `aria-label`; `translateZ(0)` para texto escalado com artefato.

## O que NÃO aproveitamos e por quê
- Texto da Vercel foi parafraseado, não colado (a pesquisa de botões marca "Não" em texto literal).

## Riscos e cuidados
- Só texto; nada executado.
- Licença MIT: manter o aviso (em `LICENSES/`).

## Transcrição: o que aprendemos (nas nossas palavras)
- Use a tecnologia mais simples: CSS primeiro, depois WAAPI, depois biblioteca.
- Um spinner que aparece e some em 100 ms parece defeito; por isso há atraso para aparecer e tempo mínimo na tela.
- Autoplay longo precisa ter controle de pausa.
- Em SVG, anime um wrapper e fixe a origem do transform.

## Fontes
- https://github.com/vercel-labs/web-interface-guidelines
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.2, §3.6, §3.10)`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.8)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.4, §2.16)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§3.5, §3.8)`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/motion-oficial/LICENSES/vercel-web-interface-guidelines-MIT.txt`
