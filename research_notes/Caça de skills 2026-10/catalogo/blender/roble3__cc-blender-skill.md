---
nome: "cc-blender-skill (30 skills de Blender)"
repositorio: "RobLe3/cc-blender-skill (a pesquisa de animação premium grafou 'roble3')"
caminho_no_repo: "pastas por domínio: blender-animation, animation-quality-gate, blender-materials, blender-lighting, blender-rendering, knowledge/08-cameras-composition, knowledge/09-animation"
autor: "RobLe3"
instalacoes: "88 estrelas; skills.sh: modeling 1,5 mil, materials 1,1 mil, rendering 929, animation 897, lighting 866"
commit_ou_versao: "último push 01/05/2026"
data_verificada: "2026-10-11"
categoria_nossa: "video"
nota: 7
status: "ideias-aproveitadas"
onde_entrou: "skills/video/blender-ia/SKILL.md (ideias de receitas, gates de qualidade, lentes e razão de luz; nada copiado) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md", "research_notes/Caça de skills 2026-10/blender-premium/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-premium/pesquisa-B.md", "research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md", "skills/video/blender-ia/ORIGEM.md"]
---
# cc-blender-skill (30 skills de Blender)

## O que é
Pacote de 30 skills de Blender com receitas `bpy` por assunto: texto para Blender, modelagem, materiais, luz, câmeras, render, animação, exportação e 'gates' de qualidade. É a mais próxima do nosso caso entre as de licença livre (MIT), mas é genérica e foi escrita para o MCP.

## O que aproveitamos
- Ideias de receitas de animação e render (Bezier, giro, BACK/ELASTIC, drivers, NLA, loop com extrapolação cíclica; perfis Cycles/Eevee; nome do motor por versão; sequência PNG resistente a falhas), reescritas por nós, com atribuição na ORIGEM.md.
- Gate de qualidade: folha de contato da animação antes de aceitar. Virou a nossa folha de quadros (`folha.sh`) e o 'quadro de aprovação'.
- Para a atualização premium (já na main): tabela de lentes (50 mm neutra, 85 mm íntima, 100 a 135 mm herói de produto com foco raso), 'push-in por movimento físico (dolly, não zoom)', 'Metallic é chave, não controle deslizante (0 ou 1)', classe de luz de produto com razão key:fill:rim de 5:1:1,5 a 5000 K e `transmission_bounces` de 16 ou mais para vidro grosso.

## O que NÃO aproveitamos e por quê
- Bug na receita 4 de animação: põe `BOUNCE` na ÚLTIMA chave, onde não tem efeito, porque a interpolação vale para o trecho depois da chave. A nossa skill já registrava o mesmo erro.
- A regra 'nunca use Standard' e as 256 amostras pressupõem GPU; o nosso container só tem CPU e usamos 8 a 16 amostras com OIDN.
- Foi feita para rodar via `execute_blender_code` do MCP; nós rodamos `blender -b -P` sem janela (o código é `bpy` puro e serve nos dois).
- Não entra como dependência: skills de terceiros não são instaladas direto.

## Riscos e cuidados
- Lida como dado; nada foi instalado nem executado.
- MIT permite adaptar com atribuição; manter a atribuição no ORIGEM.md.
- Antes de trazer qualquer skill dela: `npx -y skills@1.7.2 add RobLe3/cc-blender-skill --list`, mostrar ao Daniel e pedir aprovação.

## Transcrição: o que aprendemos (nas nossas palavras)
- Receitas organizadas por domínio, com uma tabela honesta do que funciona e do que não funciona.
- Animação: curvas Bezier, giro, overshoot com BACK/ELASTIC, drivers, NLA e loop por extrapolação cíclica; um helper de compatibilidade 4.x/5.x que percorre layers, strips e channelbags.
- Render: perfis para Cycles e Eevee, o nome do motor muda por versão (`BLENDER_EEVEE` no 5.x), color management e sequência PNG.
- Qualidade: 'gates' que exigem folha de contato antes de aceitar a animação, como a nossa regra.
- Câmera: push-in por movimento físico e tabela de lentes (50 mm neutra, 85 mm íntima, 100 a 135 mm herói de produto).
- Lição: serve de caderno de receitas; cada número de GPU precisa ser refeito para CPU.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md`
- `research_notes/Caça de skills 2026-10/blender-premium/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-premium/pesquisa-B.md`
- `research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md`
- `skills/video/blender-ia/ORIGEM.md`
- https://github.com/RobLe3/cc-blender-skill
