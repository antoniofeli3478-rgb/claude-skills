---
nome: "image-to-code"
repositorio: "leonxlnx/taste-skill"
caminho_no_repo: "image-to-code-skill"
autor: "Leonxlnx"
licenca: "MIT (mesmo LICENSE da Taste Skill, Copyright (c) 2026 Leonxlnx)"
instalacoes: "359,4 mil instalações (skills.sh, 12/10/2026); 94,8 mil estrelas no repositório"
commit_ou_versao: "d237dd3 (11/10/2026)"
data_verificada: "2026-10-12"
categoria_nossa: "design"
nota: "sem nota"
status: "instalada-intacta"
onde_entrou: "skills/design/image-to-code/ (cópia intacta, opcional: exige gerador de imagem e gasta créditos) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md"]
---
# image-to-code (taste-skill)

## O que é
Item 4 da lista do Daniel. A IA gera uma imagem do visual de cada seção do site, analisa a imagem e só depois escreve o código igual a ela. O arquivo tem 36 KB (uns 9 mil tokens). Identificação feita pelo catálogo: é a primeira resposta para "image to code" e é do mesmo autor da Taste Skill. Confiança alta; se o vídeo falava de outra ferramenta com esse nome (por exemplo converter imagem do Figma), o Daniel precisa avisar.

## O que aproveitamos
- Ainda nada entrou na `main`. A proposta é incluir a skill intacta, com `ORIGEM.md` e `APRENDIZADOS.md` nossos, no passo 3 (visual) do `combo-site-premium`.
- Ideia útil: gerar uma imagem por seção para validar o visual antes do código.

## O que NÃO aproveitamos e por quê
- A skill quer a imagem como "fonte da verdade". Aqui a fonte da verdade é o `DESIGN.md`. Regra anotada: gerar as imagens depois do `DESIGN.md` aprovado e mostrar ao Daniel antes do código.
- Texto dentro de imagem gerada costuma sair errado: o texto vem sempre do passo de copy.

## Riscos e cuidados
- Precisa de gerador de imagem (foi escrita para o Codex). No nosso ambiente seria Gemini ou Higgsfield: gasta crédito e envia o pedido a um servidor. Precisa do "sim" do Daniel, da escolha do gerador e de um teto de imagens (a skill manda gerar uma imagem por seção).
- Nada executa sozinho; sem chave própria. A skill manda ser "não preguiçosa", o que aumenta o gasto.

## Transcrição: o que aprendemos (nas nossas palavras)
- O fluxo é imagem primeiro, código depois: uma imagem de referência por seção, análise, código fiel.
- Funciona como "prova de visual" antes de gastar tempo em código.
- Vale no nosso fluxo só depois do `DESIGN.md` e com orçamento de imagens definido.
- Não é parte do Taste Skill principal; é uma pasta à parte no mesmo repositório.

## Fontes
- https://github.com/leonxlnx/taste-skill (pasta `image-to-code-skill`)
- https://skills.sh/leonxlnx/taste-skill/image-to-code
- research_notes/Caça de skills 2026-10/sites/RELATORIO-lista-download.md (item 4)
