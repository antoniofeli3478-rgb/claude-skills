# Rotina v3: etapa 1, copy (skill `copywriting`)

Combo seguido: `skills/combos/combo-copy-humanizada/SKILL.md`, passos 1 a 3. Este arquivo é a saída do passo 2 (escrever com a `copywriting`) e do passo 3 (autoconferência). A humanização está em `2-humanizado.md`.

## 1. Briefing (passo 1 do combo)

- Não existe `.agents/product-marketing.md` nem `.claude/product-marketing.md` nos dois repositórios. O contexto veio do `BRIEFING.md`, do código das telas e do roteiro da v2.
- **Página/peça:** anúncio Reels/Stories de 30 s, 1080×1920, narrado pela Leda (Gemini TTS).
- **A única ação:** baixar o app. A CTA é fixa: "Baixe o Rotina agora. Link na bio."
- **Tráfego:** frio, de quem está rolando o feed, muitas vezes **sem som**. Por isso as palavras-chave na tela precisam contar a história sozinhas.
- **Provas reais disponíveis:** nenhuma (sem número de usuários, nota na loja ou depoimento). Por isso o roteiro não tem prova social; ver os `[NEED]` no fim.

### Público
Brasileiros de 20 a 40 anos que tentam organizar hábitos, academia e dinheiro, cada um num app ou numa planilha.
- **Como eles falam do problema:** "segunda eu começo", "academia", "quanto você pegou", "conta fixa", "gasto", "PIX", "salário caiu".
- **Objeções prováveis:** "mais um app?", "vou ter que anotar tudo?" e "não sei se vou manter".
  - A primeira é respondida pela promessa: o Rotina **tira** apps, porque junta os três num só.
  - As outras duas são respondidas pelo que aparece na tela: toques rápidos, sem prometer facilidade com palavras. "Fácil" e "em poucos toques" estão na lista proibida da `copywriting`.

### Dor
A promessa de segunda-feira (dieta, academia, guardar dinheiro) se espalha em apps diferentes, e no meio da semana a pessoa já não sabe onde anotou o quê.

### Promessa
Hábitos, treino e dinheiro no mesmo app, cada um com o seu registro:
- **Hábitos:** marcar o dia e ver a sequência e a semana.
- **Treino:** planejar a semana e anotar peso e repetição, que vão para o gráfico.
- **Financeiro:** ver entradas, saídas e o saldo do mês, e separar a conta fixa do gasto pontual.

- **Teste "Now you can" (`copy-frameworks.md`):** "Agora você pode juntar hábitos, treino e dinheiro no mesmo app e ver cada um evoluir." O resultado é **convincente e verdadeiro**: todas as funções existem nas telas `HabitsList`, `HabitsStats`, `WeekPlan`, `LogSets`, `FinOverview`, `Transactions` e `Expenses`.

### Ângulo
**"Segunda eu começo."** É a frase que todo brasileiro já disse. O vídeo usa a semana como fio:
1. a promessa da segunda;
2. a quarta, quando a pessoa já se perdeu;
3. o Rotina segurando os três registros;
4. o fecho "nem precisa esperar a segunda", que a CTA resolve com "agora".

Isso é diferente da v2, cujo ângulo era a planilha de Excel ("Ainda controla a sua rotina numa planilha…" e "Está tudo sob controle?").

## 2. Três ganchos (diferentes do da v2)

| | Gancho | Ângulo | Tempo a 1,0x (2,67 p/s) |
|---|---|---|---|
| **A** | "Segunda eu começo. Dieta, academia, guardar dinheiro." | Procrastinação da segunda-feira; a promessa nas três áreas | 7 palavras, 2,6 s |
| **B** | "Um app pro hábito, outro pra academia, outro pro banco." | Fragmentação: contar os apps | 10 palavras, 3,7 s |
| **C** | "Quanto você pegou no supino semana passada? E quanto saiu da conta ontem?" | Memória: perguntas que a pessoa não sabe responder | 13 palavras, 4,9 s |

### Escolha: gancho A

1. **Cabe nos 3 primeiros segundos** (`combo-video-explicativo`, passo 2).
   - As 3 primeiras palavras levam ~1 s e já dizem tudo.
   - Em 2,6 s (2,3 s a 1,14x), o público já ouviu as três áreas do app.
   - B passa de 3 s; C, mais ainda.
2. **Identificação imediata.** É o primeiro passo do *Human Action Model* (`copy-frameworks.md`, "Current discomfort": a dor dita com as palavras do próprio público). Ninguém precisa decifrar nada, o que segue "Clarity over cleverness".
3. **É voz de gente.** É uma expressão do grupo e da época, que o `humanizer` lista como sinal de escrita humana ("era-bound or in-group voice"). Também passa no *swap test*: a frase não funcionaria igual no anúncio de um app só de finanças.
4. **Cria um fecho natural.** A segunda-feira volta no fim ("Nem precisa esperar a segunda.") e a CTA responde com "agora". O gancho e a CTA viram uma ideia só.
5. **Não promete nada.** É a fala do público, não uma afirmação sobre o app.

### Por que não B nem C

- **B** é claro, mas:
  - passa de 3 s;
  - tem a cadência "um…, outro…, outro…", o trio por reflexo do `ai-tells.md` (#7);
  - fica perto do fecho da v2 ("Um app só"), então a v3 soaria como a v2 reescrita.
- **C** é forte para quem treina, mas:
  - deixa de fora quem só quer hábitos ou finanças, a *Perception Gap* do `copy-frameworks.md`;
  - duas perguntas seguidas soam como prova;
  - leva a dizer "o Rotina lembra", que sugere **lembretes**, uma função que não está confirmada nas telas.

## 3. Roteiro v1 (saída da `copywriting`, antes de humanizar)

| # | Fala | Palavras |
|---|---|---|
| 1 | Segunda eu começo. | 3 |
| 2 | Dieta, academia e guardar dinheiro. | 5 |
| 3 | Só que cada coisa fica num app diferente, e na quarta você já se perdeu. | 15 |
| 4 | O Rotina reúne hábitos, treino e financeiro num app só. | 10 |
| 5 | Marque os hábitos do dia e veja a sua sequência crescer. | 11 |
| 6 | Monte o treino de cada dia e o plano da semana. | 11 |
| 7 | Registre carga, repetições e séries, e veja cada exercício evoluir. | 10 |
| 8 | No financeiro, veja cada entrada e saída dos seus bancos e cartões. | 12 |
| 9 | Separe despesa fixa de pontual e veja o mês por categoria. | 11 |
| 10 | Não espere a segunda. | 4 |
| 11 | Baixe o Rotina agora. Link na bio. | 7 |
| | **Total** | **99** (4 acima da meta; o corte fica para a etapa 2) |

A 1,0x, a v1 dura 37,1 s. Para caber em 30 s, precisaria de 1,24x, no limite do briefing.

### Anotações (por que cada parte está ali)

- **Falas 1–3 (desconforto):** a dor com as palavras do público. "Na quarta" dá o tempo da história sem inventar dado.
- **Fala 4 (visão):** passa no "Now you can": "Agora você pode reunir hábitos, treino e financeiro num app só."
- **Falas 5–9 (como funciona):**
  - uma ideia por fala, uma função real por fala (*One idea per section*);
  - verbos de ação concretos (*Specificity over vagueness*);
  - benefício ligado à função: sequência, evolução, mês por categoria.
- **Fala 10 (caminho):** uma ausência dita uma vez só, ao lado da CTA. O `ai-tells.md` permite isso ("State a real absence once, near the CTA").
- **Fala 11:** a CTA fixa do briefing.
- **Funções usadas:** só as confirmadas no briefing e no código das telas.
  - `HabitsList`: marcar hábitos, sequência de "dias seguidos".
  - `WeekPlan`: plano da semana, treino de cada dia.
  - `LogSets`: carga, repetições e séries; gráfico de evolução.
  - `Transactions`: entradas e saídas, bancos.
  - `Expenses`: fixa × pontual, cronograma e "Por categoria".

### Alternativas de CTA (formato de saída da `copywriting`)

A CTA é **fixa pelo briefing** e foi mantida. Só para registro:
- **A:** "Baixe o Rotina agora. Link na bio." (fixa; verbo de ação + o que fazer + onde).
- **B:** "Começa hoje: baixe o Rotina. Link na bio." (fecha o gancho da segunda, mas tira o "agora", que já faz esse papel).
- **C:** "Baixe o Rotina e comece hoje. Link na bio." (uma palavra a mais e menos direta).

## 4. Autoconferência da `copywriting` (passo 3 do combo; `ai-tells.md`, "The Self-Check")

| Checagem | Resultado na v1 |
|---|---|
| Proibidos: "não"/"sem", "?", ":", travessão | Só uma negação: "Não espere a segunda", ausência única junto da CTA, permitida. Sem perguntas, dois-pontos, travessão ou exclamação. |
| Contraste "Não é X, é Y" / lista de negação / pergunta respondida | Nenhum. |
| Frases com mais de 20 palavras | Nenhuma. A maior é a fala 3, com 15 palavras e duas orações. Para a voz, é longa; marcada para a etapa 2. |
| Limites (fragmentos, trios, vocabulário) | **Atenção:** três trios em pouco espaço (fala 2, fala 4, fala 7). Vocabulário proibido: nenhum ("revolucione", "transforme", "potencialize" etc. não aparecem). |
| Ler em voz alta | Do meio para o fim soa como manual: "Marque… Monte… Registre… Separe…", com "e veja" quatro vezes. Marcado para a etapa 2. |
| *Swap test* | A fala 4 sobrevive, porque a combinação das três áreas é o diferencial. As falas 5–9 estão genéricas: qualquer app de hábito, treino ou finanças diria igual. |
| Honestidade | **Atenção:** "veja cada exercício evoluir" promete evolução, mas o gráfico só mostra o que aconteceu. "dos seus bancos e cartões" pode sugerir que o app conecta no banco sozinho, o que não está confirmado. Marcado para a etapa 2. |

## 5. `[NEED]` (não inventei nada no lugar)

- `[NEED: prova social real]`: número de usuários, nota na loja ou depoimento. Não usado.
- `[NEED: confirmar se os lançamentos financeiros são manuais ou importados do banco]`: o roteiro final evita prometer importação automática.
- `[NEED: confirmar se a sugestão "Próximo treino: 85 kg" da tela LogSets existe no app real]`: não entra na fala; ver a nota da tela no `ROTEIRO-FINAL.md`.

## Partes da `copywriting` usadas

- **`SKILL.md`:**
  - *Before Writing* (público, dor, oferta, tráfego);
  - *Clarity over cleverness*, *Specificity*, *Customer language*, *One idea per section*;
  - *No AI Tells*, *Use Rhetorical Questions* (para descartar o gancho C);
  - *CTA Copy Guidelines*;
  - *Output Format* (anotações e alternativas).
- **`references/copy-frameworks.md`:**
  - fórmula *Problem-Focused* (o gancho como dor);
  - teste "Now you can";
  - *Human Action Model*;
  - *Perception Gap*;
  - *Problem Section* ("Create recognition").
- **`references/ai-tells.md`:** padrões 1–9, *Marketing Phrases* ("all-in-one", "effortless", "in just a few clicks"), *Rewrite Rules* e *The Self-Check*.
- **`references/natural-transitions.md`:** só para evitar os conectores de IA ("Além disso", "Vale ressaltar"). O roteiro quase não usa conectores; o "Aí" falado faz esse papel.
- **`APRENDIZADOS.md`:** a regra de honestidade (`[NEED]` em vez de inventar) e a lista pt-BR do combo.
