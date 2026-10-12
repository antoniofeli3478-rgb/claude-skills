# Rotina v3: etapa 2, humanizado (skill `humanizer`)

Combo seguido: `skills/combos/combo-copy-humanizada/SKILL.md`, passos 4 e 5.

## Parâmetros

- **Chamada equivalente:** `/humanizer "<roteiro v1>" --mode rewrite --voice casual --purpose marketing --score --iterate 2`
  - `--voice casual`: a tabela do combo indica `casual` para "anúncio ou Reels com energia". A Leda é a voz "animada" da `voz-pt-br`, a de anúncio.
  - `--purpose marketing`: frases curtas, benefício concreto e **uma só** CTA no fim.
  - Antes do `rewrite`, rodei um `detect` na v1 (abaixo). O combo pede isso para texto pronto; aqui serviu para mexer só no que de fato soava como IA.
  - O `--openings 3` (disputa de aberturas) já foi feito na etapa 1 com os ganchos A, B e C, e o A venceu de novo pelos mesmos motivos.
- **Leitura da voz (passo 3 da skill):** "Leio isto como: narração de anúncio Reels de 30 s para brasileiros de 20 a 40 anos, registro casual, falado."
- **`humanizer-context.md`:** não existe no projeto. Seguiu-se sem ele.
- **Adaptação ao português e à narração:**
  - A skill é escrita para inglês. Usei a lista pt-BR do combo ("Português do Brasil: o que procurar").
  - A regra de ritmo variado da skill (frases de 3 a 40 palavras) **não vale** para narração de Reels: o `combo-video-explicativo` pede falas curtas, e o briefing limita a 95 palavras. A variedade veio da **forma** das frases, não do tamanho (ver o placar).

## Detect na v1 (relatório de padrões)

| Padrão | Trecho | Por que pega | Correção |
|---|---|---|---|
| **P10 Trio por reflexo** (`ai-tells` #7) | fala 2 "Dieta, academia e guardar dinheiro"; fala 4 "hábitos, treino e financeiro"; fala 7 "carga, repetições e séries" | Três trios em 7 falas, dois deles em sequência | Manter só o trio do gancho (são de fato três áreas). Na fala 4, retomar com "as três". Na fala 7, ficar com dois itens. |
| **P30 Ritmo uniforme** (`ai-tells` #9) | falas 4–9: 10, 11, 11, 10, 12 e 11 palavras | Seis falas seguidas do mesmo tamanho e com o mesmo desenho | Variar a forma (ver abaixo) e dividir a fala 3 em duas. |
| **Estrutura paralela** (`ai-tells`, "Parallel bullet openers"; alma #8 da skill) | "Marque… e veja", "Monte…", "Registre… e veja", "veja…", "Separe… e veja" | O molde "[imperativo] + e veja" aparece cinco vezes ("veja" quatro vezes). Soa como lista de funções. | Trocar o molde a cada fala: condicional sem "se", "você" + presente, fragmento, acontecimento ("Entrou…, saiu…"). |
| **P18 Registro formal** | "reúne", "despesa fixa de pontual", "Registre" | Português escrito, de manual, num anúncio falado | "junta", "conta fixa" / "gasto pontual", "anotou". Palavras do público (`copywriting`, *Customer language*). |
| **Honestidade** (regra "No fabrication" da skill e "Honest over sensational" da copy) | "veja cada exercício evoluir"; "dos seus bancos e cartões" | Promete evolução; sugere conexão automática com banco | Descrever o mecanismo real: o que você anota vai para o gráfico; o saldo acompanha o que entra e sai. |

**Placar v1: 21/100.** Conta: 4 padrões × 4 = 16, mais o termo de ritmo, 5,2. Fica em "Mostly human", no limite.

## Reescrita 1 (93 palavras)

```
Segunda eu começo.
Dieta, academia, guardar dinheiro.
Aí cada coisa fica num app diferente.
Na quarta, você já perdeu a conta.
O Rotina junta tudo isso num app só.
Marcou os hábitos do dia, a sequência cresce.
E cada semana vira um gráfico.
No treino, você monta a semana dia por dia.
Anota carga e repetições, e o gráfico mostra a evolução.
No financeiro, você vê cada real que entra e sai.
Conta fixa de um lado, gasto pontual do outro.
Nem precisa esperar a segunda.
Baixe o Rotina agora. Link na bio.
```

## Autocrítica: "o que ainda faz isto soar como IA?" (passo 5 da skill)

1. **P38 Blocos intercambiáveis.**
   - "No treino, você monta…" e "No financeiro, você vê…" usam o mesmo molde de abertura de seção.
   - As falas 6–11 poderiam trocar de lugar sem quebrar nada. O meio era uma lista de funções, desligada do gancho da semana.
2. **Perto demais da v2.** O briefing atualizado (12/10) pede **frases bem diferentes da v2**. Linha a linha:

   | Reescrita 1 | Linha parecida na v2 |
   |---|---|
   | "Marcou os hábitos do dia, a sequência cresce." | "Marque ou desmarque seus hábitos do dia e veja a sua sequência crescer." |
   | "No treino, você monta a semana dia por dia." | "No treino, você monta cada dia e o plano da semana inteira." |
   | "Anota carga e repetições, e o gráfico mostra a evolução." | "Registre carga, repetições e séries, e veja no gráfico…" |
   | "No financeiro, você vê cada real que entra e sai." | "No financeiro, você vê para onde cada real vai." |
   | "…num app só." | "Hábitos, treino e dinheiro. Um app só." |

3. **Ritmo:** cinco falas seguidas de 6 a 9 palavras. Isso é limite do formato, mas pede ainda mais variedade de forma.

**Correção (reescrita 2):** amarrar cada função ao fio da semana e usar a linguagem de quem vive aquilo:
- **Hábitos:** "foguinho", a chama da sequência que aparece na tela.
- **Gráfico da semana:** "furou", com retorno à quarta do gancho.
- **Treino:** "segunda peito, terça costas", a mesma segunda do gancho e a piada de academia.
- **Financeiro:** "Entrou salário, saiu PIX".

Com isso, as falas dependem do gancho e não dá mais para embaralhá-las (P38 resolvido).

## Versão final (reescrita 2, 91 palavras)

```
Segunda eu começo.
Dieta, academia, guardar dinheiro.
Aí cada coisa fica num app diferente.
Na quarta, você já perdeu a conta.
O Rotina junta as três no mesmo lugar.
Cada dia cumprido soma um no foguinho.
E dá pra ver em qual dia você furou.
Você planeja a semana: segunda peito, terça costas.
Anotou peso e repetição, cai direto no gráfico.
Entrou salário, saiu PIX, o saldo do mês acompanha.
Conta fixa de um lado, gasto pontual do outro.
Nem precisa esperar a segunda.
Baixe o Rotina agora. Link na bio.
```

Convergiu em 2 passadas.

## O que mudou e por quê (antes → depois)

| Antes (v1) | Depois (final) | Por quê |
|---|---|---|
| "Dieta, academia **e** guardar dinheiro." | "Dieta, academia, guardar dinheiro." | Sem o "e", a lista fica falada e corrida, mais rápida no gancho. |
| "Só que cada coisa fica num app diferente, e na quarta você já se perdeu." (15) | "Aí cada coisa fica num app diferente." + "Na quarta, você já perdeu a conta." | Duas falas curtas (para a voz e para a tela). "Aí" é conector falado. "Perdeu a conta" é expressão comum e ecoa a "conta fixa" da fala 11. Considerei "Na quarta, já era.", que é mais engraçado, mas **sugere que o app faz você cumprir a promessa**, coisa que ele não garante. "Perder a conta" é a dor que o app resolve de fato: tudo anotado num lugar. |
| "O Rotina reúne hábitos, treino e financeiro num app só." | "O Rotina junta as três no mesmo lugar." | Tira o 2º trio (P10), o registro formal "reúne" (P18) e o "app só" da v2. "As três" retoma o gancho. |
| "Marque os hábitos do dia e veja a sua sequência crescer." | "Cada dia cumprido soma um no foguinho." | Era quase a frase da v2 e repetia o molde "e veja". "Foguinho" é como o público chama a sequência (a chama com "dias seguidos" da tela). "Soma um" descreve o mecanismo exato, de 11 para 12. |
| (não tinha) | "E dá pra ver em qual dia você furou." | Os gráficos por semana (`HabitsStats`, "Esta semana") entram com uma opinião ("furou") e voltam à quarta. |
| "Monte o treino de cada dia e o plano da semana." | "Você planeja a semana: segunda peito, terça costas." | "Planeja" é o nome da aba ("Planejar"), e "segunda peito, terça costas" é o que aparece na tela (`WeekPlan`). A segunda volta pela segunda vez. Os dois-pontos abrem uma lista, um uso permitido. |
| "Registre carga, repetições e séries, e veja cada exercício evoluir." | "Anotou peso e repetição, cai direto no gráfico." | Tira o 3º trio, o molde "e veja" e a promessa de evolução. Diz o mecanismo verdadeiro: salvou as séries, o ponto vai para o gráfico de evolução. "Peso" é a palavra do público; "carga" é a do app. |
| "No financeiro, veja cada entrada e saída dos seus bancos e cartões." | "Entrou salário, saiu PIX, o saldo do mês acompanha." | Ficou concreto e brasileiro, com "salário" e "PIX". "Acompanha" diz o mecanismo (o saldo segue o que entra e sai) **sem** sugerir que o app puxa extrato do banco. |
| "Separe despesa fixa de pontual e veja o mês por categoria." | "Conta fixa de um lado, gasto pontual do outro." | São as palavras do público. "De um lado / do outro" é literalmente o seletor de dois lados da tela `Expenses`. A parte "por categoria" fica só na imagem. |
| "Não espere a segunda." | "Nem precisa esperar a segunda." | "Não espere" + "Baixe" eram duas ordens seguidas. "Nem precisa" libera em vez de mandar, e a CTA vem logo depois com "agora". |
| CTA | igual | Fixa pelo briefing. Única, clara e no fim (passo 5 do combo). |

## Reconferência depois de humanizar (passo 5 do combo)

- **Os fatos continuam iguais?** Sim. Toda fala aponta para uma tela real:

  | Fala | Tela |
  |---|---|
  | Foguinho | `HabitsList` / `Habits` (sequência 11 → 12) |
  | Dia que furou | `HabitsStats` ("Esta semana") |
  | Segunda peito, terça costas | `WeekPlan` (Seg Peito, Ter Costas) |
  | Peso e repetição no gráfico | `LogSets` (Carga, Reps, Salvar séries, Evolução) |
  | Salário, PIX, saldo | `Transactions` (Salário via PIX, Aluguel via PIX) e `FinOverview` ("Saldo do mês") |
  | Fixa × pontual | `Expenses` (seletor "Fixa · todo mês" / "Pontual · uma vez") |

  Não há números, nomes, depoimentos nem promessas inventados.
- **Ficou claro?**
  - "Foguinho" é gíria, mas a chama aparece na tela e a palavra-chave ajuda quem assiste sem som.
  - Se o Daniel achar que não é claro para 35–40 anos, a troca segura é "Cada dia cumprido soma um na sequência." O número de palavras é o mesmo.
- **A CTA continua única, clara e no fim?** Sim. "Nem precisa esperar a segunda" não é uma segunda CTA: dá a razão do "agora".
- **Trocou um tique por outro?**
  - "Gráfico" aparece uma vez e "segunda" três vezes (1, 8, 12), de propósito, como retorno.
  - A skill manda repetir o termo em vez de alternar sinônimos (P11/P31).
  - Não sobrou o molde "e veja"; cada fala tem uma forma diferente.

## Conferência final (passo 5 da skill)

1. **Leitura em voz alta, mental:**
   - O gancho entra seco, em ~1 s.
   - "Aí cada coisa…" e "Na quarta…" soam como alguém contando um caso.
   - "Entrou salário, saiu PIX" tem ritmo de fala (verbo + coisa, verbo + coisa).
   - "Saldo" e "salário" fazem uma aliteração leve.
   - O fim fecha em "segunda → agora".
   - Nenhuma frase trava.
2. **Abertura:** é gancho, não visão geral.
3. **Final:** a CTA, sem fecho genérico ("o futuro…").
4. **Lista proibida:** nenhuma palavra ("revolucione", "potencialize", "jornada", "transforme"…).
5. **Travessões:** zero. **Exclamações:** zero. **Perguntas:** zero.
6. **Tamanho das falas:** 3, 4, 7, 7, 8, 7, 9, 8, 8, 9, 9, 5, 7.
   - Pelo critério da skill para prosa, ainda há trechos com 3 ou mais falas de tamanho parecido. O formato exige isso (13 momentos em 30 s).
   - A variedade está na forma:

     | Fala | Forma |
     |---|---|
     | 1 | 1ª pessoa |
     | 2 | lista |
     | 3 | narração |
     | 5 | afirmação |
     | 6 | sujeito "cada dia" |
     | 7 | "dá pra" |
     | 8 | "você" + lista |
     | 9 | condicional sem "se" |
     | 10 | dois acontecimentos + resultado |
     | 11 | fragmento |
     | 12 | ausência |
     | 13 | ordem |

7. **Quem escreveu isto?** Dá para imaginar alguém que treina, paga PIX e já disse "segunda eu começo".
8. **Cuidados de pronúncia para a etapa da voz** (`voz-pt-br`):
   - "PIX" e "foguinho" devem sair bem na Leda; confira na transcrição.
   - "app" já saiu bem na v2.
   - "bio" pode ser transcrito como "Bill" (está no APRENDIZADOS, é só a transcrição).
   - Não há mais "deload", "Excel" nem "1RM", que eram os riscos da v2.

## Placar (`--score`)

| Versão | Padrões | Termo de ritmo | Vocabulário | **Placar** | Faixa |
|---|---|---|---|---|---|
| v1 (copy) | 4 (P10, P30, paralela, P18) = 16 | 5,2 | 0 | **21** | Mostly human |
| Reescrita 1 | 3 (P38, abertura "No X, você…", P30 leve) = 12 | 10,3 | 0 | **22** | Mostly human |
| **Final** | 1 (P30 leve, por causa do formato) = 4 | 11,9 | 0 | **16** | Pristine |

- **Fórmula da skill:** `4 × padrões + 25 × (1 − ritmo_normalizado) + 15 × proporção_de_vocabulário`.
- A skill **não define** como normalizar o ritmo. Usei `CoV / 0,5`, limitado a 1, onde CoV é o desvio-padrão dividido pela média de palavras por fala:
  - v1: CoV 0,40;
  - reescrita 1: 0,29;
  - final: 0,26.
- **Limite honesto:**
  - O termo de ritmo **piora** quando as falas ficam curtas, que é justamente o que o vídeo pede. Por isso o número de padrões (4 → 3 → 1) diz mais que a nota.
  - A própria skill avisa que a nota dada pelo modelo à própria saída infla o resultado.
  - O teste de verdade é o Daniel ouvir a Leda falando.

## Aprendizados para salvar (proposta, passo 7 do combo)

O repositório main é só leitura para os agentes, então nada foi gravado. Proposta para o agente principal ou para o Daniel aprovar:

- **combo-copy-humanizada / humanizer:**
  - Em narração de Reels, a regra de ritmo variado (frases de 3 a 40 palavras) e o termo de ritmo do placar não servem.
  - Varie a **forma** da frase e conte os padrões; não confie na nota.
- **combo-copy-humanizada:** quando for um vídeo novo do mesmo produto, faça uma checagem **"distância da versão anterior"**, linha a linha. Na reescrita 1, cinco frases repetiam a v2 sem ninguém perceber.
- **Lista pt-BR do combo:**
  - Um encadeamento de imperativos ("Marque… Monte… Registre… Separe…" + "e veja") é o equivalente pt-BR de "parallel bullet openers".
  - Em anúncio falado, "você" + presente ou o condicional sem "se" ("Anotou…, cai…") soam mais humanos.
- **copywriting (honestidade):** cuidado com verbos que prometem funções não confirmadas:
  - "aparece" e "puxa do banco" sugerem importação automática;
  - "o app lembra" sugere lembretes;
  - "veja evoluir" promete resultado.
