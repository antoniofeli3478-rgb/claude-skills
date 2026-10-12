---
nome: "Motion Lexicon (skill motion-lexicon)"
repositorio: "Ryan-yang125/motion-lexicon (https://github.com/Ryan-yang125/motion-lexicon)"
caminho_no_repo: "não verificado"
autor: "Ryan-yang125 e colaboradores"
instalacoes: "não verificado"
commit_ou_versao: "88f979a (19/08/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: "\"8 (motion B) / 7 (botões B)\""
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/references/coreografia.md §2–3 e references/revisao.md §6; skills/design/button-animation/SKILL.md (tempos 240/150/150 ms, geometria estável, tremor de erro)"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.4, §3.8)", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.6 e §2 (correção de licença))", "research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.2, §2.4, §2.11)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2, §3.2)", "skills/design/motion-oficial/ORIGEM.md", "skills/design/button-animation/ORIGEM.md", "skills/design/button-animation/LICENCAS-TERCEIROS.txt", "skills/design/motion-oficial/LICENSES/motion-lexicon-CONTENT-CC-BY-4.0.txt"]
---
# Motion Lexicon (skill motion-lexicon)

## O que é
Vocabulário e método para coreografar movimento: quem é o ator principal, quais são as "batidas" (beats) de um momento, tempos de entrada e saída, e uma rubrica de revisão. Serve para UI e também para roteiro de vídeo promocional.

## O que aproveitamos
- Um ator principal e no máximo dois de apoio por beat; papéis: trigger, hero, status, record, environment.
- Um momento normal tem 3 a 5 beats perceptíveis; apoio só entra depois que o principal assenta; "atraso só quando revela dependência, sequência ou processo real".
- Gramática de beat: `at | actor | purpose | primitive | from→to | durationMs | easing`.
- Padrões: Confirmar e seguir; Selecionar e inspecionar; Mudar no lugar; Recuperar com controle.
- Tempos: chegada normal 200–280 ms; saída 110–180 ms (mais curta que a entrada); stagger 30–70 ms; tokens `--motion-arrive: 240ms`, `--motion-leave: 150ms`, `--motion-feedback: 150ms`.
- Rubrica de revisão (Critical / Important / Polish) e regra de corrida assíncrona (cada intenção ganha uma versão; resposta velha não dispara animação).
- Geometria estável do controle que iniciou a ação; tremor de erro com alternativa sem movimento.

## O que NÃO aproveitamos e por quê
- Nenhum texto literal copiado: tradução, resumo e exemplos são nossos.
- Exige atribuição: "Based on Motion Lexicon content, licensed under CC BY 4.0 (adaptado)" (registrada em `LICENCAS-TERCEIROS.txt` e no `ORIGEM.md`).

## Riscos e cuidados
- Licença CC BY 4.0 no texto: citar a fonte, a licença e dizer o que mudou. Código é MIT.
- Só texto; nada executado.

## Transcrição: o que aprendemos (nas nossas palavras)
- Pense em cada momento da interface como uma cena curta: quem é o personagem principal e o que cada apoio faz.
- Se tudo se mexe ao mesmo tempo, nada é o foco; deixe o principal assentar antes dos apoios.
- Saída mais rápida que entrada: o usuário já decidiu e quer seguir.
- Se uma resposta do servidor chega atrasada, ela não pode disparar a animação de uma intenção antiga.
- Dá para escrever o plano de um vídeo com a mesma tabela de beats.

## Fontes
- https://github.com/Ryan-yang125/motion-lexicon
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-B.md (§3.4, §3.8)`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.6 e §2 (correção de licença))`; `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.2, §2.4, §2.11)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2, §3.2)`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/button-animation/ORIGEM.md`; `skills/design/button-animation/LICENCAS-TERCEIROS.txt`; `skills/design/motion-oficial/LICENSES/motion-lexicon-CONTENT-CC-BY-4.0.txt`
