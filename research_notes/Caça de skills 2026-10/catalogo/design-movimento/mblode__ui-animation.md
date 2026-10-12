---
nome: "ui-animation"
repositorio: "mblode/agent-skills (https://github.com/mblode/agent-skills)"
caminho_no_repo: "não verificado (a skill tem 18 referências e 3 scripts Python)"
autor: "Matthew Blode"
licenca: "MIT"
instalacoes: "9,9 mil (skills.sh, 11/10/2026)"
commit_ou_versao: "872bc83 (11/10/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 9
status: "ideias-aproveitadas"
onde_entrou: "skills/design/motion-oficial/SKILL.md; references/api-motion.md §7; references/molas-e-curvas.md; references/revisao.md §5; references/coreografia.md §6"
fontes_locais: ["research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.3)", "research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.2)", "research_notes/Caça de skills 2026-10/motion-oficial/RESUMO.md", "skills/design/motion-oficial/ORIGEM.md", "skills/design/motion-oficial/LICENSES/mblode-agent-skills-MIT.txt"]
---
# ui-animation

## O que é
A skill mais completa e prática de animação de UI: junta conteúdo do Emil e da Apple com muito material próprio. Traz curvas, molas, armadilhas do Motion, desempenho, depuração por sintoma e um método para medir uma animação a partir de um vídeo.

## O que aproveitamos
- Curvas nomeadas extras: enter (0.22, 1, 0.36, 1); move (0.25, 1, 0.5, 1); expo out (0.19, 1, 0.22, 1); press (0.25, 0.46, 0.45, 0.94); on-screen (0.645, 0.045, 0.355, 1).
- Durações por componente com curva (inclui página 250–400 ms e marketing até 1000 ms); regra de escalar a duração com a distância.
- Presets de mola: Snappy 500/40, Bouncy 300/20, Gentle 200/30, Stiff 700/50.
- Mola assimétrica medida quadro a quadro: abrir 155/11 (quica), fechar 620/36 (amortecida). Regra: o quique é da abertura.
- Armadilhas do Motion: reduced motion só em CSS não afeta o Motion (usar `MotionConfig reducedMotion="user"`); `borderRadius` inline com `layout`/`layoutId`; `AnimatePresence` sem `key`; atalhos `x/y` travam com a página ocupada.
- Tarefas longas: se trava "só às vezes", comece o movimento, deixe um quadro passar, depois faça o trabalho.
- Depuração por sintoma (lento, robótico, barato, travando, pulando, disparando à toa); ciclo "câmera lenta, classificar, mudar uma variável".
- Regras curtas: regra dos pares (modal e overlay com a mesma curva e duração), confirmar no próprio botão, desligar transições na troca de tema, `touch-action: manipulation`, pausar loops fora da tela.
- Método de engenharia reversa a partir de vídeo (ffmpeg, medir abertura e fechamento separados, informar o fps na extração).

## O que NÃO aproveitamos e por quê
- Os 3 scripts Python (`extract_frames.py`, `track_motion.py`, `fit_curves.py`) NÃO entraram: não foram lidos linha a linha nem executados. Só o método passo a passo foi aproveitado.
- Conflito resolvido: "x/y é o padrão para arrasto" (Blode) contra "string transform por padrão" (Emil e oficial). Valeu a regra oficial, com `x/y/scale` nos casos compostos, gestos e MotionValues.
- "Curva primeiro, depois duração" (Blode) foi conciliado com a regra do Salaja: se parece lento, mexa na duração; se parece robótico, mexa na curva.

## Riscos e cuidados
- Scripts em Python incluídos na skill: não foram executados; só devem ser usados depois de leitura completa e aprovação do Daniel.
- Sem chave ou plano pago citados; telemetria não mencionada nas pesquisas (não verificado a fundo).
- Licença MIT: manter aviso (guardado em `LICENSES/`).

## Transcrição: o que aprendemos (nas nossas palavras)
- Quando o quique "parece barato", a causa costuma ser quique também no fechamento: deixe a abertura com mola solta e o fechamento firme.
- Reduced motion escrito só em CSS não alcança as animações do Motion, que rodam em JavaScript.
- Com `layout`, o raio da borda distorce se vier de classe; ponha `style={{ borderRadius }}` inline.
- Para saber se algo travou por trabalho pesado, adie o trabalho até `onAnimationComplete` ou `transitionend`.
- Animação ligada ao scroll usa curva `linear` e nenhuma duração.
- Para copiar um movimento de um vídeo de referência: grave a 60 fps, extraia quadros, meça, e ajuste mola ou bezier; medir abrir e fechar à parte.
- O método é "muito alinhado" a quem recria movimentos de vídeos de referência (como o Daniel); por isso o método entrou, os scripts não.

## Fontes
- https://skills.sh/mblode/agent-skills/ui-animation
- https://github.com/mblode/agent-skills
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/motion-oficial/pesquisa-A.md (§2.3)`; `research_notes/Caça de skills 2026-10/motion-oficial/TRANSCRICAO.md (§3.2)`; `research_notes/Caça de skills 2026-10/motion-oficial/RESUMO.md`; `skills/design/motion-oficial/ORIGEM.md`; `skills/design/motion-oficial/LICENSES/mblode-agent-skills-MIT.txt`
