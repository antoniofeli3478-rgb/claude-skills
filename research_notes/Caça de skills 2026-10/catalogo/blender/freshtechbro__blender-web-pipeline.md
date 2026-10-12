---
nome: "blender-web-pipeline (claudedesignskills)"
repositorio: "freshtechbro/claudedesignskills"
caminho_no_repo: "blender-web-pipeline"
autor: "freshtechbro"
instalacoes: "1.026 estrelas; 3,1 mil instalações"
commit_ou_versao: "último push 20/11/2025"
data_verificada: "2026-10-11"
categoria_nossa: "video"
nota: 5
status: "so-referencia"
onde_entrou: "—"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-premium/pesquisa-B.md"]
---
# blender-web-pipeline (claudedesignskills)

## O que é
Skill para levar modelos do Blender para a web: export glTF/GLB, lote com `blender --background`, níveis de detalhe (LOD) e baking. Útil se um dia o site precisar de um modelo 3D leve, mas o nosso fluxo entrega imagens PNG com fundo transparente.

## O que aproveitamos
- Nada incorporado. Fica como referência para um eventual caminho Blender para glTF para site (Three.js/R3F).

## O que NÃO aproveitamos e por quê
- Não é o nosso caso: o fluxo do Rotina usa PNG RGBA do Blender como camada no Remotion.
- Antes de qualquer uso, a própria pesquisa pede conferir com `npx -y skills@1.7.2 add freshtechbro/claudedesignskills --list`.

## Riscos e cuidados
- Foi clonada e lida como dado (pesquisa A); nada executado.
- Licença MIT: pode ser adaptada com atribuição.

## Transcrição: o que aprendemos (nas nossas palavras)
- Pipeline de exportação para web: glTF/GLB, lote sem janela, LOD e baking de texturas.
- Casa com a lição de outras skills: exportar GLTF sem janela, porque pelo MCP dá timeout.
- A pesquisa B de scroll indica que, para beleza cinematográfica, quadros pré-renderizados no Blender ganham; GLB só vale se a pessoa precisa girar o produto com o dedo.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-premium/pesquisa-B.md`
- https://github.com/freshtechbro/claudedesignskills
