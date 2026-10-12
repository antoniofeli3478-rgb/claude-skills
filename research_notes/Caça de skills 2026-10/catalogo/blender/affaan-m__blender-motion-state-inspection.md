---
nome: "blender-motion-state-inspection (ECC)"
repositorio: "affaan-m/ECC"
caminho_no_repo: "blender-motion-state-inspection"
autor: "affaan-m"
instalacoes: "276,8 mil estrelas no repositório; 5,9 mil instalações (a pesquisa premium diz 6 mil)"
commit_ou_versao: "último push 11/10/2026"
data_verificada: "2026-10-11"
categoria_nossa: "video"
nota: 2
status: "ideias-aproveitadas"
onde_entrou: "(ideia de método) verifica_curvas.py em skills/video/blender-ia (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-premium/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-premium/TRANSCRICAO.md"]
---
# blender-motion-state-inspection (ECC)

## O que é
Skill feita para personagens e rigs (poses, retarget). Está fora do nosso escopo, mas trouxe uma ideia de método: extrair os fatos do Blender quadro a quadro antes de julgar pela imagem.

## O que aproveitamos
- A ideia 'extraia os fatos quadro a quadro antes de julgar pelo print' virou o CSV de curvas e o verificador `verifica_curvas.py` da atualização premium da `blender-ia` (já na main).

## O que NÃO aproveitamos e por quê
- Rigs, retarget e poses de personagem não servem para promo de app.
- A pesquisa A manda não trazer ('não são o nosso caso').

## Riscos e cuidados
- Repositório enorme (276,8 mil estrelas): nenhuma parte foi instalada; só esta skill foi lida.

## Transcrição: o que aprendemos (nas nossas palavras)
- Método: medir (posições, velocidades) em vez de olhar só o print.
- No nosso caso isso virou uma tabela por quadro (valor alvo contra valor do Blender, velocidade máxima, quadros da varredura do reflexo) conferida em 2 s, antes do render demorado.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-premium/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-premium/TRANSCRICAO.md`
- https://github.com/affaan-m/ECC
