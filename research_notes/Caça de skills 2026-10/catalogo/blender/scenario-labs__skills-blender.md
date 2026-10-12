---
nome: "scenario-labs/skills (blender-expert, animation, rigging, lighting-rendering, hard-surface)"
repositorio: "scenario-labs/skills"
caminho_no_repo: "scenario-blender-expert, scenario-blender-animation, scenario-blender-rigging, scenario-blender-lighting-rendering, scenario-blender-hard-surface"
autor: "scenario-labs"
instalacoes: "964 estrelas; 170 a 217 instalações"
commit_ou_versao: "último push 10/10/2026"
data_verificada: "2026-10-11"
categoria_nossa: "video"
nota: 2
status: "ideias-aproveitadas"
onde_entrou: "skills/video/blender-ia (atualização Blender premium: lentes, luz, curvas) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-premium/pesquisa-B.md", "research_notes/Caça de skills 2026-10/blender-premium/TRANSCRICAO.md", "research_notes/Caça de skills 2026-10/blender-premium/RESUMO.md"]
---
# scenario-labs/skills (blender-expert, animation, rigging, lighting-rendering, hard-surface)

## O que é
Pacote de skills de Blender ligado à plataforma paga Scenario. As de animação e rigging não servem (nota 2). Mas as de luz/render e de hard surface foram as mais úteis da rodada premium, testadas no Blender 5.2.

## O que aproveitamos
- View transform: Khronos PBR Neutral como padrão para produto com tela de app. A skill mediu o erro de cor de marca em passos de 8 bits: Khronos 3,8; AgX 27 a 42; Standard 33.
- Exposição ajustada no view transform (`exposure`), nunca escalando as lâmpadas.
- Compositor do Blender 5.x: é um grupo de nós (`scene.compositing_node_group`) e os ajustes do Glare são entradas (sockets).
- Hard surface: dois bevels (desenho de 8 a 12 segmentos e microbevel de 30 graus com Harden Normals), Weighted Normal por último, e toda parede ou ranhura com mais de 2 vezes a largura do bevel.
- Armadilha do bmesh: `bmesh.ops.bevel` vem com `segments=0, profile=0`; passar sempre os dois.

## O que NÃO aproveitamos e por quê
- As skills de animação e rigging da Scenario dependem da plataforma paga Scenario: não servem.
- Nada foi copiado: só números e nomes de parâmetros, que são fatos; o código do teste é nosso (GPL, como a skill).
- Tudo isso só vale depois que o Daniel aprovar a atualização premium.

## Riscos e cuidados
- Parte da skill usa a plataforma paga Scenario (conta e plano); não ligar.
- Foi lida como dado; nada executado.

## Transcrição: o que aprendemos (nas nossas palavras)
- Cor: a maior mudança de cor do teste premium veio do view transform. O Khronos PBR Neutral comprime os brilhos sem lavar a cor da UI.
- Brilho geral: usar exposição, que mantém a proporção entre as luzes.
- Compositor 5.x: o nó de mistura antigo (`CompositorNodeMixRGB`) não existe mais; usar `ShaderNodeMix`.
- Modelagem de aro e vidro: bevel de desenho mais microbevel, e normais ponderadas por último, para brilho limpo na quina.
- Lição: a regra de largura (parede maior que 2 vezes o bevel) evita o bevel estragar a geometria.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-premium/pesquisa-B.md`
- `research_notes/Caça de skills 2026-10/blender-premium/TRANSCRICAO.md`
- `research_notes/Caça de skills 2026-10/blender-premium/RESUMO.md`
- https://github.com/scenario-labs/skills
