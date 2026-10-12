---
nome: "meshy-3d-generation (Meshy, geração de 3D por IA)"
repositorio: "meshy-dev/meshy-3d-agent (skill meshy-3d-generation)"
caminho_no_repo: "meshy-3d-generation"
autor: "meshy-dev"
instalacoes: "cerca de 2 mil instalações (skills.sh)"
commit_ou_versao: "não verificado"
data_verificada: "2026-10-11"
categoria_nossa: "video"
nota: "sem nota"
status: "so-referencia"
onde_entrou: "skills/video/blender-ia/SKILL.md (apenas a tabela de licenças de modelos 3D por IA)"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md", "skills/video/blender-ia/ORIGEM.md"]
---
# meshy-3d-generation (Meshy, geração de 3D por IA)

## O que é
Skill que gera modelos 3D (arquivo GLB) pelo serviço Meshy, para importar no Blender. A pesquisa A só a citou; as licenças e o custo ficaram para a pesquisa B.

## O que aproveitamos
- A regra de licença do Meshy entrou na tabela de licenças do SKILL.md: plano grátis = CC BY 4.0, exige crédito à Meshy; Pro ou superior = privado, comercial, sem crédito.

## O que NÃO aproveitamos e por quê
- Não usamos geração 3D por IA para o celular-herói: o celular procedural sai em cerca de 40 linhas de `bpy`, evita parecer iPhone e não tem malha densa nem furos.
- Serve só para objetos de cena (moedas, halteres, ícones 3D), e nunca foi testada.

## Riscos e cuidados
- Serviço externo com conta e créditos: envia imagem ou texto para fora. Não usar sem aprovação.
- Nunca salvar chave de API no repositório.
- Licença do plano grátis exige crédito; para anúncio, usar plano pago.

## Transcrição: o que aprendemos (nas nossas palavras)
- Fluxo C da pesquisa B: imagem ou texto para GLB (Meshy, Tripo, Rodin, TRELLIS.2, Hunyuan3D...), importar no Blender, limpar a malha (remesh/decimate, apagar faces soltas) e dar material próprio.
- Bom para objetos de cena; ruim para o celular-herói (malha triangulada, densa e sem bordas precisas).
- Neste ambiente existe `mcp__higsfield__generate_3d` (imagem para GLB); não foi testado e a licença não foi lida.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md`
- `skills/video/blender-ia/ORIGEM.md`
- https://docs.meshy.ai/en/webapp/pricing
