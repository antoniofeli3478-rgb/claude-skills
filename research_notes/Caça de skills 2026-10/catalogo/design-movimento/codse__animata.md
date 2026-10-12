---
nome: "Animata (status-button, duolingo, swipe-button, shining-button)"
repositorio: "codse/animata (https://github.com/codse/animata)"
caminho_no_repo: "não verificado"
autor: "Animata (codse)"
instalacoes: "não verificado"
commit_ou_versao: "36674e4 (14/09/2026)"
data_verificada: "2026-10-11 (data da pesquisa)"
categoria_nossa: "design"
nota: 7
status: "ideias-aproveitadas"
onde_entrou: "skills/design/button-animation/SKILL.md (§4 receitas 12 botão 3D e 13 troca de texto no hover; ideia do botão de estados em assets/status-button.tsx)"
fontes_locais: ["research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.4–2.5, §2.14–2.15)", "research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)", "skills/design/button-animation/ORIGEM.md", "skills/design/button-animation/LICENCAS-TERCEIROS.txt"]
---
# Animata (status-button, duolingo, swipe-button, shining-button)

## O que é
Coleção de componentes de interface com versões em Tailwind. A pesquisa aproveitou o botão de estados (carregando e sucesso), o botão 3D estilo Duolingo, a troca de texto no hover e o brilho passando só no hover.

## O que aproveitamos
- Botão 3D "afunda": `border-b-4` mais escuro e, no `active:`, `border-b-0 border-t-4` (afunda 4 px); `touch-manipulation` e `h-11` (44 px). Bom para apps de hábito como o Rotina e para vídeo.
- Troca de texto deslizando: dois spans em `overflow-hidden`; no hover um sobe (`-translate-y-full`) e o outro entra, em 300 ms.
- Brilho só no hover: uma faixa `bg-white/10 rotate-[30deg]` cruza o botão em 700 ms, sem loop.
- Ideia do `status-button` (loading, sucesso), reescrita por nós.

## O que NÃO aproveitamos e por quê
- Nenhum texto copiado: tudo reescrito.

## Riscos e cuidados
- Código de terceiros; nada executado. Licença MIT.

## Transcrição: o que aprendemos (nas nossas palavras)
- Um botão "afunda" mudando a espessura da borda de baixo para a de cima: barato, sem JavaScript.
- Troca de texto: uma janela recortada com dois rótulos que sobem juntos.
- O brilho só no hover é melhor para app do que um loop contínuo.

## Fontes
- https://github.com/codse/animata
- Arquivos locais (a partir de `/home/user/claude-skills-main/`): `research_notes/Caça de skills 2026-10/button-animation/pesquisa-B.md (§2.4–2.5, §2.14–2.15)`; `research_notes/Caça de skills 2026-10/button-animation/TRANSCRICAO.md (§2)`; `skills/design/button-animation/ORIGEM.md`; `skills/design/button-animation/LICENCAS-TERCEIROS.txt`
