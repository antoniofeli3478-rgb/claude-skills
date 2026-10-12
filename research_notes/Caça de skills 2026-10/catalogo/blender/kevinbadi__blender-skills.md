---
nome: "kevinbadi/blender-skills (planos de câmera para produto)"
repositorio: "kevinbadi/blender-skills"
caminho_no_repo: "turntable, slow-zoom, dolly-rotate, crane-shot, dynamic-full-loop, perfect-loop, product-polish, polyhaven-studio, polyhaven-material-swap, image-to-3d (Meshy), threejs-export"
autor: "kevinbadi"
instalacoes: "148 estrelas"
commit_ou_versao: "último push 04/04/2026"
data_verificada: "2026-10-11"
categoria_nossa: "video"
nota: "7 como ideia; 0 para copiar"
status: "ideias-aproveitadas"
onde_entrou: "skills/video/blender-ia/SKILL.md (só o vocabulário de planos, reescrito do zero)"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-premium/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md", "skills/video/blender-ia/ORIGEM.md"]
---
# kevinbadi/blender-skills (planos de câmera para produto)

## O que é
Conjunto de skills de Blender para vídeo de produto: turntable, zoom lento, dolly com giro, crane, loop perfeito e acabamento. É o vocabulário de planos que queremos, mas não tem licença, então não se copia nada.

## O que aproveitamos
- Só o vocabulário de planos: turntable, push-in, dolly com giro, crane, speed-ramp (chaves próximas = lento, distantes = whip) e loop perfeito. Os nossos planos foram escritos do zero.
- Câmera com `TRACK_TO` num Empty e saída ProRes 4444 com alfa: a receita da pesquisa B usa o mesmo desenho.

## O que NÃO aproveitamos e por quê
- Nada de código ou texto foi copiado: sem licença significa todos os direitos reservados.
- Supõe GPU: Cycles em GPU com 64 amostras. O nosso container é só CPU.
- O `slow-zoom` usa 24 fps e o Bezier padrão do Blender; a nossa skill usa 30 fps e curvas próprias.
- O `slow-zoom` varia a lente de 35 para 65 mm; a pesquisa premium achou exagerado para celular.

## Riscos e cuidados
- Sem licença, copiar ou adaptar não é permitido por padrão. Não instalar, não copiar, não adaptar; só ler como ideia.
- Foi lida só como dado; nenhum script rodou.

## Transcrição: o que aprendemos (nas nossas palavras)
- `slow-zoom`: push-in de 5 s, lente de 35 para 65 mm e distância de 4,0 para 1,8.
- `dynamic-full-loop` (lido) e `perfect-loop`: loops sem emenda. O speed-ramp (chaves próximas = lento, distantes = whip) faz parte do vocabulário da skill.
- O `turntable` usa câmera `TRACK_TO` num Empty e exporta ProRes 4444 com alfa.
- Lição de vocabulário: nomear os planos (revelação, push-in, órbita, crane, loop) ajuda a pedir e a revisar; a duração e a curva de cada um é medida por nós.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-premium/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md`
- `skills/video/blender-ia/ORIGEM.md`
- https://github.com/kevinbadi/blender-skills
