---
nome: "bpy-dev/blender-mcp (Lab MCP turbinado)"
repositorio: "bpy-dev/blender-mcp"
caminho_no_repo: "raiz (readme.md, readme_bpy_backend.rst, NOTICE.md)"
autor: "bpy-dev"
instalacoes: "98 estrelas"
commit_ou_versao: "developer preview, sem pacote publicado (último push 12/09/2026)"
data_verificada: "2026-10-11"
categoria_nossa: "mcp"
nota: 5
status: "so-referencia"
onde_entrou: "— (ORIGEM.md: reavaliar quando sair do preview)"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md", "skills/video/blender-ia/ORIGEM.md"]
---
# bpy-dev/blender-mcp (Lab MCP turbinado)

## O que é
Distribuição modificada do MCP oficial, com ferramentas `_for_cli` que abrem um .blend salvo num processo sem janela. É a única abordagem de MCP que conversa com o Blender headless. Ideia certa, mas ainda imatura.

## O que aproveitamos
- Registrado no ORIGEM.md como 'reavaliar quando sair do preview'.

## O que NÃO aproveitamos e por quê
- Está em 'developer preview', sem pacote publicado, com 98 estrelas e cerca de 1 mês de vida.
- O backend usa o executável do Blender ou um `bpy` independente (Python 3.13 mais `bpy` 5.2.1 de um build de terceiros), o que é mais uma dependência não oficial.
- GPL-3.0: copiar código obrigaria GPL.

## Riscos e cuidados
- Build de `bpy` de terceiros: origem e integridade não verificadas.
- Executa código gerado pela IA, como o MCP oficial.

## Transcrição: o que aprendemos (nas nossas palavras)
- Inclui consulta da API em tempo de execução e um benchmark (BlenderBench).
- A ideia de rodar ferramentas num subprocesso headless resolve a limitação que faz os outros MCPs dependerem de janela.
- Nota 5: ideia certa, imaturo. Reavaliar quando houver versão estável.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md`
- `skills/video/blender-ia/ORIGEM.md`
- https://github.com/bpy-dev/blender-mcp
