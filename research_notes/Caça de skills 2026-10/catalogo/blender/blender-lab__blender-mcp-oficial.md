---
nome: "Blender Lab MCP (MCP oficial do Blender)"
repositorio: "projects.blender.org/lab/blender_mcp (página: blender.org/lab/mcp-server)"
caminho_no_repo: "não verificado"
autor: "Blender Lab (Blender Authors)"
instalacoes: "sem números; add-on 1.0.3"
commit_ou_versao: "add-on v1.0.3 (11/09/2026); v1.0.0 em 27/04/2026; exige Blender 5.1+"
data_verificada: "2026-10-11"
categoria_nossa: "mcp"
nota: 4
status: "ideias-aproveitadas"
onde_entrou: "skills/video/blender-ia/SKILL.md (seção 'No computador do Daniel (com placa de vídeo e MCP)')"
fontes_locais: ["research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md", "research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md", "skills/video/blender-ia/ORIGEM.md", "skills/video/blender-ia/SKILL.md"]
---
# Blender Lab MCP (MCP oficial do Blender)

## O que é
O conector oficial do Blender para o Claude (disponível desde 28/04/2026). Serve para entender, consultar e depurar cenas e a API do Blender, e não para criar modelos do zero. Precisa do Blender 5.1 ou mais novo, aberto.

## O que aproveitamos
- Descrição na tabela de MCPs do SKILL.md: bom para analisar e depurar um .blend, não gera 3D, a página pede rodar numa VM.
- Lição do teste da MindStudio (conector oficial, plano Max 5x): melhor para geometria e trabalho em lote; salvar o .blend e abrir sessão nova.

## O que NÃO aproveitamos e por quê
- Nota 4 para o nosso caso: voltado a consultar, não a produzir vídeo de produto.
- A página documenta llama.cpp; o Claude Code não aparece nela.
- A licença do repositório não foi confirmada pelo agente B (projects.blender.org deu 403); a GPL-3.0 vem dos cabeçalhos lidos pelo agente A.
- Copiar código GPL para a nossa skill obrigaria a skill a ser GPL.

## Riscos e cuidados
- A própria página avisa que o add-on executa código gerado pela IA 'without any guards' e recomenda uma máquina virtual.
- Usa a porta 9876, a mesma do MCP principal: não rodar os dois juntos.
- Não foi testado ao vivo (o container não tem interface gráfica).

## Transcrição: o que aprendemos (nas nossas palavras)
- 26 ferramentas, seis delas `_for_cli` (rodam o Blender em segundo plano); sem geração 3D (fonte de terceiros, StraySpark, que vende MCP concorrente: ler com cautela).
- Anthropic divulgou o conector em 28/04/2026 ('Claude for Creative Work') como interface em linguagem natural para a API Python do Blender.
- Teste da MindStudio (01/05/2026): 2 h e 60% dos tokens da sessão num donut, com objetos atravessando objetos, escala e câmera ruins e tom magenta.
- Lição: contexto longo degrada; salvar o .blend e começar sessão nova.

## Fontes
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md`
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md`
- `research_notes/Caça de skills 2026-10/blender-ia/TRANSCRICAO.md`
- `skills/video/blender-ia/ORIGEM.md`
- `skills/video/blender-ia/SKILL.md`
- https://www.blender.org/lab/mcp-server/
- https://projects.blender.org/lab/blender_mcp
