# claude-skills

Base de conhecimento e habilidades (skills) do Claude para os projetos do Daniel. O índice rápido de tudo está em **`HABILIDADES.txt`**: uma linha por item, com o caminho.

## Habilidades (`skills/`, uma pasta por habilidade)

Cada pasta tem o `SKILL.md` (a skill original) e, quando houver, o `APRENDIZADOS.md` com os adendos do que foi aprendido usando a skill. As pastas `combo-*` juntam várias skills com um passo a passo pronto.

| Habilidade | O que faz | Onde está |
|---|---|---|
| `remotion-video` | Vídeos com Remotion (React): promos e explicativos de app, legendas, 3D, voz, música e regras de motion premium | `skills/remotion-video/` |
| `motion-video` | Kit de motion design em canvas (sem Remotion), com iPhone realista, cards 3D e sons sintetizados | `skills/motion-video/` (veja o `APRENDIZADOS.md` dela) |
| `voz-pt-br` | Narração em português (Gemini, Kokoro), licença da voz e legendas sincronizadas | `skills/voz-pt-br/` |
| `trilha-e-mixagem` | Música, efeitos sonoros, ducking e −14 LUFS | `skills/trilha-e-mixagem/` |
| `combo-video-explicativo` | **Combo:** remotion-video, voz-pt-br e trilha-e-mixagem juntas, com passo a passo | `skills/combo-video-explicativo/` |
| `button-animation` | Animações de botões e de UI, mais o componente Compose | `skills/button-animation/` |
| `thumbnail-carousel` | Carrossel com miniaturas (Motion 14, antigo Framer Motion) | `skills/thumbnail-carousel/` |
| `ui-ux-pro-max` | Como instalar e usar a skill externa de design de UI/UX | `skills/ui-ux-pro-max/` |

## Projetos (`projetos/`)

Cada projeto tem só um `PASSO-A-PASSO.md`: o que foi feito, como, e os caminhos das habilidades usadas. O código fica numa branch ou num repositório do projeto.

- `projetos/rotina/PASSO-A-PASSO.md`: vídeos do app Rotina. O código está na branch `rotina-video`.

## Conhecimento

- `reports/`: relatórios de pesquisa, em português.
- `research_notes/`: anotações brutas e amostras de voz.
- `CLAUDE.md`: regras de trabalho do Claude neste repositório.

Para usar uma skill em outro projeto no Claude Code, copie a pasta dela para `~/.claude/skills/` (vale para todos os projetos) ou para `<projeto>/.claude/skills/`. Os arquivos originais (`.txt` e o link) continuam na raiz.
