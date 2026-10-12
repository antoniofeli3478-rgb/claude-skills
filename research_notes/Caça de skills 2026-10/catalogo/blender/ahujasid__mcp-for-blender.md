---
nome: "MCP for Blender (antigo blender-mcp)"
repositorio: "ahujasid/mcp-for-blender (antigo ahujasid/blender-mcp)"
caminho_no_repo: "raiz do repositório (add-on addon.py e servidor src/blender_mcp/server.py)"
autor: "ahujasid"
instalacoes: "30.454 estrelas, 2.758 forks; PyPI mcp-for-blender 2.1.9"
commit_ou_versao: "v2.1.9 (último push 06/10/2026); renomeado em 16/09/2026"
data_verificada: "2026-10-11"
categoria_nossa: "mcp"
nota: 7
status: "ideias-aproveitadas"
onde_entrou: "skills/video/blender-ia/SKILL.md (seção 'No computador do Daniel (com placa de vídeo e MCP)')"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md", "research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md", "skills/video/blender-ia/ORIGEM.md", "skills/video/blender-ia/SKILL.md"]
---
# MCP for Blender (antigo blender-mcp)

## O que é
Conector (MCP) que deixa o Claude mexer num Blender aberto, olhar a cena ao vivo e rodar código Python nele. É o MCP de Blender mais usado. Serve para o computador do Daniel, com a janela do Blender aberta. Nota 7 no PC do Daniel e 4 no container da nuvem.

## O que aproveitamos
- Descrição do MCP e da arquitetura (add-on com socket TCP na porta 9876 mais servidor MCP via `uvx`) na seção do MCP do SKILL.md da `blender-ia`.
- Comando de instalação seguro: `claude mcp add blender -e DISABLE_TELEMETRY=true -e BLENDER_MCP_SAFE_MODE=1 -- uvx mcp-for-blender`, depois `uvx mcp-for-blender install-addon`.
- O fato de ele recusar `blender -b` (addon.py, linha 1266). Por isso o render sem janela usa scripts `bpy` nossos.
- Lições de uso de terceiros: dar assets prontos, pedir mudanças pontuais, salvar o .blend a cada etapa e abrir conversa nova.
- Fluxo híbrido: montar e aprovar a cena no MCP, salvar o .blend e renderizar em lote com `blender -b cena.blend -P render.py`.

## O que NÃO aproveitamos e por quê
- Não instalamos nem executamos o add-on: são 6.441 linhas de terceiros, não auditadas.
- Não usamos no container: o MCP precisa do Blender com interface (só roda com Xvfb e Mesa lento) e não traz vantagem sobre o script headless.
- Não usamos o instalador `curl ... | sh`: ele mexe na configuração de todos os clientes.
- Não usamos o `generate_3d` (Tripo, Hunyuan3D, Rodin): o celular-herói é procedural, e cada serviço tem licença própria.

## Riscos e cuidados
- Telemetria anônima LIGADA por padrão (ID de instalação, ferramenta, sucesso, tempo, sistema). Prompts, código e capturas só com opt-in. Os termos dizem que os dados podem treinar modelos de IA. Desligar com `DISABLE_TELEMETRY=true`.
- O socket da porta 9876 não tem senha e `execute_blender_code` roda qualquer Python. `BLENDER_MCP_SAFE_MODE=1` bloqueia arquivos, subprocessos e rede no código executado.
- Usa a mesma porta do MCP oficial e do blenderwright: rodar um por vez.
- Chaves de Tripo, Hunyuan3D e Rodin: nunca salvar no repositório. Há um plano Premium pago que dispensa as chaves.
- O Poly Pizza bloqueia IPs de nuvem, e cerca de 69% dos modelos dele são CC-BY (exigem crédito).
- Exportar GLTF pelo MCP dá timeout (relato de outra skill): exportar sem janela.

## Transcrição: o que aprendemos (nas nossas palavras)
- Ferramentas: `execute_blender_code` (Python livre), `get_scene_info`, `look` (viewport, câmera, várias vistas, tira de animação, wireframe), `search_assets` e `import_asset` (Poly Haven, Sketchfab, Poly Pizza) e `generate_3d`.
- A captura da viewport usa render OpenGL/offscreen (`GPUOffScreen.draw_view3d`), com `screenshot_area` só de reserva.
- Teste nosso: a mesma arquitetura (servidor TCP numa thread mais fila drenada por `bpy.app.timers`) funciona sob `xvfb-run`: objeto criado em 1,4 s, render OpenGL da viewport em cerca de 1 s. O `screen.screenshot` sai preto. O código deles não foi rodado.
- Relatos reais: bom para analisar cenas, editar em lote e escrever scripts; fraco em criação livre. Teste da MindStudio (01/05/2026): 2 h e 60% dos tokens da sessão num donut cheio de erros.
- Lição geral: o MCP é opção para o PC do Daniel; no container o caminho é `blender -b -P script.py`.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md`
- `research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md`
- `skills/video/blender-ia/ORIGEM.md`
- `skills/video/blender-ia/SKILL.md`
- https://github.com/ahujasid/mcp-for-blender
