---
nome: "blenderwright (antigo blend-ai)"
repositorio: "HoldMyBeer-gg/blenderwright"
caminho_no_repo: "raiz do repositório (add-on como extensão, zip nos Releases)"
autor: "HoldMyBeer-gg"
licenca: "MIT"
instalacoes: "168 estrelas; PyPI blenderwright 2.2.1"
commit_ou_versao: "2.2.1 (último push 08/10/2026)"
data_verificada: "2026-10-11"
categoria_nossa: "mcp"
nota: 6
status: "ideias-aproveitadas"
onde_entrou: "skills/video/blender-ia/SKILL.md (seção 'No computador do Daniel (com placa de vídeo e MCP)')"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md", "skills/video/blender-ia/ORIGEM.md", "skills/video/blender-ia/SKILL.md"]
---
# blenderwright (antigo blend-ai)

## O que é
Outro MCP de Blender, com 191 ferramentas e zero telemetria. É a alternativa mais segura ao MCP principal. Também só funciona com o Blender aberto, no computador do Daniel.

## O que aproveitamos
- Entrou como alternativa sem telemetria na tabela de MCPs do SKILL.md da `blender-ia`.
- Registro de que o Python livre vem desligado e só se liga por sessão ('Allow raw Python' no painel N), na mesma linha da tabela.

## O que NÃO aproveitamos e por quê
- Nada foi instalado nem executado; só o README foi lido.
- Precisa de Blender 4.2+ e do add-on instalado à mão (zip dos GitHub Releases em Edit > Preferences > Get Extensions > Install from Disk).
- O README admite que capturas de viewport podem não funcionar sem janela; a captura usa `bpy.ops.render.opengl`, que funcionou sob Xvfb no teste.

## Riscos e cuidados
- Add-on de terceiros com porta local (a pesquisa diz que os sockets dos MCPs de Blender, em geral, não têm autenticação).
- Usa a mesma porta 9876 dos outros MCPs: um de cada vez.
- Instalar só depois da aprovação do Daniel, pela receita de skills de terceiros.

## Transcrição: o que aprendemos (nas nossas palavras)
- 191 ferramentas em 28 módulos: modelagem, materiais procedurais, luz, câmera, animação, `render_animation`, física, geometry nodes e outros.
- 12 'prompts de especialista' (estúdio, PBR, autocrítica) e análise de malha.
- Instalação: `claude mcp add blenderwright -- uvx blenderwright`; no Blender, painel N, 'Start Server'.
- Exemplo do README: um foguete animado de 60 quadros em Cycles, feito com o Claude Code.
- Lição: com o Python livre desligado por padrão, ele é mais seguro que os MCPs que já nascem com Python livre.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md`
- `skills/video/blender-ia/ORIGEM.md`
- `skills/video/blender-ia/SKILL.md`
- https://github.com/HoldMyBeer-gg/blenderwright
