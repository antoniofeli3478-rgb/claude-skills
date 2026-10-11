# claude-skills

Base de conhecimento e habilidades (skills) do Claude para os projetos do Daniel. O índice rápido de tudo está em **`guia/HABILIDADES.txt`**: uma linha por item, com o caminho.

**Antes de usar, leia o [`guia/LEIA-ANTES-DE-USAR.md`](guia/LEIA-ANTES-DE-USAR.md)**, o manual de utilização do repositório.

## Habilidades (`skills/<categoria>/<nome>/`: video, audio, design, texto, ferramentas-de-skills e combos)

Cada pasta tem o `SKILL.md` (a skill original) e, quando houver, o `APRENDIZADOS.md` com os adendos do que foi aprendido usando a skill. As pastas `combo-*` juntam várias skills com um passo a passo pronto.

| Habilidade | O que faz | Onde está |
|---|---|---|
| `remotion-video` | Vídeos com Remotion (React): promos e explicativos de app, legendas, 3D, voz, música e regras de motion premium | `skills/video/remotion-video/` |
| `motion-video` | Kit de motion design em canvas (sem Remotion), com iPhone realista, cards 3D e sons sintetizados | `skills/video/motion-video/` (veja o `APRENDIZADOS.md` dela) |
| `copywriting` | Escrever texto que convence e converte, com a lista de frases de IA a evitar (skill de terceiros, MIT) | `skills/texto/copywriting/` |
| `humanizer` | Tirar o jeito de IA de um texto, com detecção, nota e 5 vozes (skill de terceiros, MIT) | `skills/texto/humanizer/` |
| `voz-pt-br` | Narração em português (Gemini, Kokoro), licença da voz e legendas sincronizadas | `skills/audio/voz-pt-br/` |
| `trilha-e-mixagem` | Música, efeitos sonoros, ducking e −14 LUFS | `skills/audio/trilha-e-mixagem/` |
| `combo-copy-humanizada` | **Combo:** copywriting e depois humanizer, em português do Brasil | `skills/combos/combo-copy-humanizada/` |
| `combo-video-explicativo` | **Combo:** remotion-video, voz-pt-br e trilha-e-mixagem juntas, com passo a passo | `skills/combos/combo-video-explicativo/` |
| `button-animation` | Botões e micro-interações premium (todos os estados, receitas, componentes prontos) | `skills/design/button-animation/` |
| `thumbnail-carousel` | Carrossel com miniaturas (Motion 14, antigo Framer Motion) | `skills/design/thumbnail-carousel/` |
| `find-skills` | Caçador de skills: procura skills prontas no catálogo skills.sh (skill de terceiros, MIT) | `skills/ferramentas-de-skills/find-skills/` |
| `ui-ux-pro-max` | Design de telas e sites premium/minimalistas com DESIGN.md | `skills/design/ui-ux-pro-max/` |
| `estilos-premium` | Biblioteca de estilos de marcas premium (fichas DESIGN.md MIT) | `skills/design/estilos-premium/` |
| `motion-oficial` | Animação de interface: quando e como animar, curvas, molas, acessibilidade | `skills/design/motion-oficial/` |
| `blender-ia` | 3D realista no Blender escrito pelo Claude, com fundo transparente para os vídeos | `skills/video/blender-ia/` |

## Projetos (`projetos/`)

Cada projeto tem só um `PASSO-A-PASSO.md`: o que foi feito, como, e os caminhos das habilidades usadas. O código fica numa branch ou num repositório do projeto.

- `projetos/rotina/PASSO-A-PASSO.md`: vídeos do app Rotina. O código está na branch `rotina-video`.

## Conhecimento

- `reports/`: relatórios de pesquisa, em português.
- `research_notes/`: anotações brutas e amostras de voz.
- `CLAUDE.md`: regras de trabalho do Claude neste repositório.

Para usar uma skill em outro projeto no Claude Code, copie a pasta dela para `~/.claude/skills/` (vale para todos os projetos) ou para `<projeto>/.claude/skills/`. Os arquivos originais que deram origem a algumas skills (`.txt` e o link) ficam na pasta `original/` de cada skill. Na raiz ficam só `CLAUDE.md` e `README.md`; o índice e o manual estão em `guia/`.
