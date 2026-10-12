# MODELO de ficha de skill (um arquivo .md por skill pesquisada)

Nome do arquivo: `<autor-ou-repo>__<nome-da-skill>.md` (minúsculas, sem espaços, só letras, números e hífen), na pasta da sua área.

```
---
nome: "nome da skill"
repositorio: "dono/repo (ou URL; 'não verificado' se faltar)"
caminho_no_repo: "pasta da skill dentro do repositório, se souber"
autor: "..."
instalacoes: "número do skills.sh quando houver, senão estrelas, senão 'não verificado'"
commit_ou_versao: "..."
data_verificada: "AAAA-MM-DD (a data que consta na pesquisa)"
categoria_nossa: "design | video | audio | texto | sites | ferramentas-de-skills | mcp"
nota: 0-10 (a nota dada na pesquisa; 'sem nota' se não houver)
status: "instalada-intacta | ideias-aproveitadas | proposta-aguardando-aprovacao | so-referencia | descartada"
onde_entrou: "caminho da NOSSA skill onde ela ou as ideias dela entraram, ou '—'"
fontes_locais: ["arquivos de pesquisa/transcrição de onde tirei isto"]
---
# <nome>

## O que é
2–4 linhas, em português simples (o Daniel não é programador).

## O que aproveitamos
Itens curtos: regra, número, receita, checklist, e onde entrou na nossa skill.

## O que NÃO aproveitamos e por quê
Conflitos, riscos, redundância.

## Riscos e cuidados
Executa algo sozinha? Telemetria? Chave/conta/plano pago? Envia dados para fora?

## Transcrição: o que aprendemos (nas nossas palavras)
Síntese própria do que esta skill ensina, em 5–15 linhas ou itens. Citação literal só se for curta (até 20 palavras), entre aspas, com a origem. Não copie trechos longos.

## Fontes
- links e arquivos locais usados
```
Regras de preenchimento:
- Use SÓ o que está nos arquivos locais indicados (pesquisas, transcrições, ORIGEM.md, relatórios). Não faça pesquisa nova na web. Se um campo não aparece, escreva "não verificado": NUNCA invente números ou datas.
- Não copie trechos longos de skills de terceiros nem de páginas do Refero (os termos do Refero proíbem). A "Transcrição" é síntese nossa.
- Português do Brasil, frases curtas. Código ou nomes técnicos podem ficar em inglês.
- Nunca escreva chaves ou senhas.
- Cada área termina com `_lista.tsv` (separado por TAB, com cabeçalho): arquivo, nome, repositorio, status, nota, onde_entrou, categoria_nossa. Uma linha por ficha.
- Inclua também, em cada área, um `LEIA-ME.md` de 10–20 linhas: o que há na pasta, quantas fichas, como ler.
