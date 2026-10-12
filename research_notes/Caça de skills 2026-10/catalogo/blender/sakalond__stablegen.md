---
nome: "StableGen (texturização por IA via ComfyUI)"
repositorio: "sakalond/StableGen (pesquisa A) ou Zaws77/StableGen (pesquisa B): repositório divergente nas duas pesquisas"
caminho_no_repo: "não verificado"
autor: "sakalond (A) / Zaws77 (B)"
instalacoes: "942 estrelas"
commit_ou_versao: "último push 29/09/2026 (pesquisa A); 2025 (pesquisa B)"
data_verificada: "2026-10-11"
categoria_nossa: "video"
nota: 1
status: "descartada"
onde_entrou: "—"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md", "research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md"]
---
# StableGen (texturização por IA via ComfyUI)

## O que é
Add-on que texturiza modelos 3D por difusão (SDXL/FLUX.1-dev, ou TRELLIS 2, conforme a pesquisa) usando um servidor ComfyUI. Precisa de GPU NVIDIA.

## O que aproveitamos
- Nada.

## O que NÃO aproveitamos e por quê
- Precisa de GPU NVIDIA e de um servidor ComfyUI; o container não tem.
- Textura por IA não é gargalo do nosso caso.
- Discrepância registrada: a pesquisa A aponta sakalond/StableGen e a B aponta Zaws77/StableGen; confirmar antes de qualquer uso.

## Riscos e cuidados
- FLUX.1-dev e SDXL têm licenças próprias, a conferir.
- Servidor local ComfyUI: abre porta e exige instalação pesada.

## Transcrição: o que aprendemos (nas nossas palavras)
- Texturização por difusão projetada sobre o modelo; acompanha o fluxo D da pesquisa B.
- Resposta curta: sem GPU, fora do escopo.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md`
- `research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md`
- https://github.com/sakalond/StableGen
- https://github.com/Zaws77/StableGen
