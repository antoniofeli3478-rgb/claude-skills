---
nome: "apple-design"
repositorio: "emilkowalski/skills (https://github.com/emilkowalski/skills)"
caminho_no_repo: "não verificado (a pesquisa só cita o nome da skill)"
autor: "Emil Kowalski"
instalacoes: "201 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "e8a175d (02/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "8 (motion A) / 10 (motion B e botões, nota do grupo Emil)"
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/references/gestos.md e references/molas-e-curvas.md; skills/design/button-animation/SKILL.md (§2.3 Molas: padrão sem quique)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.2)", "research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.5, §3.7, §3.10)", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.1)", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§3.18)", "skills/design/motion-oficial/ORIGEM.md"]
---
# apple-design

## O que é
Traduz para a web as palestras da Apple sobre interfaces fluidas (WWDC, "Designing Fluid Interfaces"), com o mapeamento para o Motion. Foca em gestos, molas, velocidade e interrupção.

## O que aproveitamos
- Molas no padrão Apple (damping / response): mover 1.0 / 0.4; rotação 0.8 / 0.4; gaveta (sheet) 0.8 / 0.3. No Motion, `bounce` + `duration` fazem papel parecido.
- Mola padrão sem quique (`bounce: 0`, 0,3–0,4 s); quique só quando o gesto trouxe impulso.
- Passagem de velocidade: ao soltar, a mola continua com a velocidade do dedo (`velocity` em px/s).
- Projeção de momento (taxa de desaceleração 0,998) para escolher o ponto de parada.
- Elástico na borda (rubber-band, constante 0,55).
- Interrupção: animar sempre a partir do valor atual na tela; molas X e Y independentes.
- Responder no `pointerdown`; acessibilidade em 3 sinais (reduced motion, reduced transparency, contrast more); evitar oscilação lenta perto de 0,2 Hz.
- "Materialize, don't just fade": em vidro/blur, animar o raio do blur junto com a escala (útil para telas de app premium nos vídeos).

## O que NÃO aproveitamos e por quê
- Os números da Apple vêm em damping ratio / response; foram usados como referência, não como API do Motion.
- A pesquisa A de motion deu nota 8 (menor que a do grupo Emil, 10): o foco é gesto e física, não decisão de movimento.

## Riscos e cuidados
- Só texto e exemplos; não executa nada. Lida como dado.
- Licença MIT: manter aviso de copyright.

## Transcrição: o que aprendemos (nas nossas palavras)
- Uma interface parece viva quando o movimento parte do valor que já está na tela, herda a velocidade do usuário, projeta o impulso para frente e pode ser agarrado e revertido a qualquer instante (ideia central, nossas palavras).
- Comece com amortecimento crítico (sem quique) e só acrescente quique se o gesto tinha impulso: overshoot num menu que acabou de aparecer parece errado; num cartão arremessado, parece certo.
- Dispense por velocidade, não só por distância (limite citado: 0,11 px/ms).
- Nas bordas, use resistência elástica em vez de parada seca.
- Decomponha movimento 2D em duas molas independentes (X e Y).
- Respeite quem pede menos movimento, menos transparência e mais contraste.

## Fontes
- https://skills.sh/emilkowalski/skills/apple-design
- https://github.com/emilkowalski/skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.2)`; `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.5, §3.7, §3.10)`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.1)`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-A.md (§3.18)`; `skills/design/motion-oficial/ORIGEM.md`
