# Rotina v3: roteiro final

- **Ângulo:** "Segunda eu começo". A semana é o fio do vídeo: segunda → quarta → o dia a dia no app → "nem precisa esperar a segunda" → "agora".
- **Texto para a voz:** `falas-v3.txt`.
- **Como o texto foi feito:** `1-copy.md` e `2-humanizado.md`.

## Números

| | |
|---|---|
| Falas | 13 |
| **Total de palavras** | **91** (meta 80–95; a CTA tem 7) |
| **Duração estimada a 1,0x** | 91 ÷ 2,67 = **34,1 s** |
| **Velocidade para caber em 30 s** | 34,1 ÷ 30 = **1,14x** (dentro de 1,0–1,25x) |
| Para a voz terminar em 29 s (folga para o último quadro, como na v2) | 1,18x |
| Gancho (falas 1–2, 7 palavras) | termina em ~2,3 s a 1,14x |

**Aviso sobre a estimativa:**
- A regra de 2,67 palavras/s foi medida na v2: 13 falas e 139 palavras, com as pausas dentro.
- A v3 tem as mesmas 13 falas e menos palavras, então as pausas pesam mais. A voz em 1,0x pode sair um pouco acima de 34 s.
- Apertar as pausas (o `compose_vo.py` da v2, 0,15–0,26 s entre falas) compensa isso.
- Confirme com `apertar_pausas.py --estimar` (`voz-pt-br`, seção 3) logo depois de gerar a voz. O `voz.json` manda nos tempos.

## Regras visuais deste roteiro

1. **Camadas, de trás para a frente:**
   1. fundo `#080808` com brilho vermelho suave (Remotion);
   2. textos e cartões (Remotion);
   3. celular 3D do **Blender**, PNG transparente, sempre por cima (regra 3).
2. **Celular sempre reto ou inclinado para cima:** tela de frente para a câmera, ou com o topo se afastando dela, entre 0 e ~15°.
   - Pode virar de lado no eixo vertical (até ~20°) e deslizar.
   - **Nunca** inclinar para a frente, nunca giro de 360° nem capotada (o que a v2 fazia).
3. **Troca de seção:** um toque na barra de abas do app. O conteúdo desliza **dentro** da tela do celular, e a borda da tela é uma borda real, então a regra 2 é cumprida. Enquanto isso, o celular só desliza para o outro terço do quadro.
4. **Texto:**
   - Barlow Condensed, branco, com o vermelho `#CC0000`/`#FF5A5A` como destaque.
   - Entra pela borda do quadro ou sai de trás do celular (regra 4).
   - Para numa área livre, fora da silhueta do celular.
   - A palavra anterior **sai antes** de a próxima entrar, ou fica em outra área (regra 1).
   - Nenhuma revelação por máscara (regra 2).
5. **Tudo novo em relação à v2:**
   - fundo escuro da marca, em vez do "papel e tinta";
   - sem planilha, sem o "?" que abre o vermelho, sem giros, sem o celular voando para dentro do cartão final.

## Roteiro com tela e palavra-chave

Os tempos de início são aproximados, a 1,14x, distribuídos por palavra. Os tempos reais virão do `voz.json`.

| # | Fala | Palavras | Início ≈ | O que aparece na tela (tela do app, ação, celular) | Palavra-chave na tela |
|---|---|---|---|---|---|
| 1 | Segunda eu começo. | 3 | 0,0 s (q0) | **Celular entra pela borda de baixo**, reto, subindo devagar até ficar levemente inclinado para cima (~8°). Tela **apagada**, vidro preto com um reflexo passando. Nenhum app ainda. | **SEGUNDA**: entra pela borda esquerda e para no terço de cima, ao lado do celular. |
| 2 | Dieta, academia, guardar dinheiro. | 4 | 1,0 s (q30) | Três selos-ícone saem **de trás do celular**, um em cada palavra: prato (dieta), halter (academia) e cofrinho (dinheiro). Cada um para numa área livre diferente. Celular reto, quase parado (deriva leve). | **DINHEIRO**: entra pela borda direita junto com o cofrinho. SEGUNDA sai antes pela esquerda. |
| 3 | Aí cada coisa fica num app diferente. | 7 | 2,3 s (q69) | Os três selos viram três "apps" genéricos: quadrados arredondados de cores diferentes, sem marca. Eles se afastam uns dos outros e do celular, cada um para um canto. O celular vira ~10° para um lado e depois para o outro, como quem procura. Tela ainda apagada. | **DIFERENTE**: entra pela borda esquerda. DINHEIRO sai antes pela direita. |
| 4 | Na quarta, você já perdeu a conta. | 7 | 4,6 s (q139) | Os três apps escorregam para fora do quadro, cada um pela borda mais próxima. O celular fica sozinho, reto, tela apagada. | **QUARTA**: entra pela borda direita, espelhando a SEGUNDA da fala 1. DIFERENTE sai antes pela esquerda. |
| 5 | O Rotina junta as três no mesmo lugar. | 8 | 6,9 s (q208) | Os três apps voltam das bordas e passam **por trás** do celular. Cada vez que um some, acende uma aba da barra do app: Hábitos, Treinos, Financeiro. A tela liga em **Hábitos de hoje** (`HabitsList`, ainda sem marcações). Aproximação lenta, celular inclinado para cima (~10°), a pose de herói. | **ROTINA**: sai de trás do celular, pelo lado. QUARTA sai antes pela direita. A logo não aparece agora, então não há logo sobre texto. |
| 6 | Cada dia cumprido soma um no foguinho. | 7 | 9,6 s (q287) | **HabitsList:** toques nas caixas, com "Dieta" primeiro (retoma a fala 2), depois "Beber 3L de água", "Ler 10 páginas" e "Treinar". O anel vai de 0/5 a 4/5. Em "foguinho", a chama da **Sequência** pula de 11 para 12 "dias seguidos" (como em `Habits.tsx`). O cabeçalho mostra "Quarta · 8 de outubro", outro retorno à quarta. Celular inclinado para cima (~10°), a câmera chega perto da chama. | **+1**: sai de trás do celular, do lado da chama. |
| 7 | E dá pra ver em qual dia você furou. | 9 | 11,9 s (q356) | Toque na pílula **Métricas** (`HabitsStats`, "Sua evolução"). As barras de "Esta semana" sobem dia a dia. Os dados são de exemplo: deixar a barra de **quarta** como a mais baixa. Em "furou", essa barra ganha um contorno vermelho. Depois a linha das 12 semanas se desenha. Celular reto. | **FUROU**: entra pela borda esquerda. O +1 sai antes, voltando para trás do celular. |
| 8 | Você planeja a semana: segunda peito, terça costas. | 8 | 14,8 s (q445) | Toque na aba **Treinos**; o conteúdo desliza dentro da tela até **Planejar** (`WeekPlan`). O celular desliza para o terço esquerdo e vira ~15° para a direita, sem inclinar para a frente. O chip "Seg · Peito" acende em "segunda peito" e o "Ter · Costas" em "terça costas". Qua, Qui e Sex entram em cascata, e há um toque em "+ Adicionar exercício". | **SEMANA**: entra pela borda direita, na área livre. FUROU sai antes. |
| 9 | Anotou peso e repetição, cai direto no gráfico. | 8 | 17,5 s (q524) | **LogSets** ("Supino Reto"): "82,5 kg" digitado em "peso" e "10" em "repetição". Toque em **Salvar séries** em "cai". O gráfico "Evolução · 1RM estimado" se desenha em "gráfico". O celular inclina um pouco mais para cima (~15°) enquanto o cartão do gráfico sobe. | **PESO**: sai de trás do celular, para a direita. SEMANA sai antes pela direita. |
| 10 | Entrou salário, saiu PIX, o saldo do mês acompanha. | 9 | 20,1 s (q603) | Toque na aba **Financeiro**; o conteúdo desliza dentro da tela até **Visão** (`FinOverview`). O celular desliza para o terço direito e vira ~15° para a esquerda. Em "entrou salário", o cartão **Entradas** sobe (verde). Em "saiu PIX", **Saídas** sobe (vermelho). Em "acompanha", o **Saldo do mês** gira até o novo valor. | **SALDO**: entra pela borda esquerda. PESO sai antes. |
| 11 | Conta fixa de um lado, gasto pontual do outro. | 9 | 23,1 s (q692) | **Expenses** ("Agendar pagamento"). Em "conta fixa", o seletor está em "Fixa · todo mês". Em "gasto pontual", um toque leva o seletor para "Pontual · uma vez". As linhas do "Cronograma de outubro" entram com os selos Fixa/Pontual, e a rosca "Por categoria" cresce no fim. Celular volta ao centro, reto, aproximação lenta. | **FIXA \| PONTUAL**: "FIXA" entra pela borda esquerda em "fixa"; "PONTUAL" entra pela direita em "pontual". Cada uma fica do seu lado do celular, com mola sem passar do ponto, para as duas nunca se cruzarem. SALDO sai antes. |
| 12 | Nem precisa esperar a segunda. | 5 | 26,0 s (q781) | Toque na aba **Hábitos**; a tela volta para "Hábitos de hoje" com a sequência acesa. Celular reto, no centro. | **SEGUNDA**: entra pela borda esquerda, no mesmo lugar da fala 1. Um **traço vermelho se desenha** sobre ela, da esquerda para a direita, no estilo do hábito concluído no app (traço desenhado, não máscara). FIXA e PONTUAL saem antes pelas bordas. |
| 13 | Baixe o Rotina agora. Link na bio. | 7 | 27,7 s (q831) | O celular sobe para a pose de herói, inclinado para cima (~12°), e a tela troca para a abertura do Rotina, com logo e ícone. **"LINK NA BIO"** sobe pela borda de baixo, sob o celular. Nos últimos quadros (até o q900), uma deriva leve, para nada ficar parado. | **AGORA**: entra pela borda direita, em vermelho, ao lado do celular, sem tocar na logo da tela. A SEGUNDA riscada sai antes pela esquerda. |

## Notas para quem anima

- **O 3D no Blender custa ~24 s por quadro** no plano de herói, quando o celular ocupa muito do quadro. Isso acontece nas falas 5, 6 e 13 (aproximações).
  - Nas falas 8–11, o celular fica num terço do quadro: menor e mais barato.
  - Use o rascunho a 25% (~1,5 s por quadro) para conferir as poses.
- **Dados das telas:**
  - Os números nas telas (R$, kg, 12 dias, 86%) são **exemplos da interface**; nenhum deles é dito na voz.
  - Na fala 7, mude o dado de exemplo da semana para a quarta ficar mais baixa. Hoje, as mais baixas são o domingo (40) e a quinta (60).
- **`[NEED]` LogSets:**
  - Depois de "Salvar séries", a tela mostra "Próximo treino: 85 kg" (sugestão de aumento de carga) e "Novo recorde: 110 kg".
  - Essa sugestão **não está** na lista de funções confirmadas do briefing. Esconda o botão ou confirme com o Daniel antes de mostrar.
- **`[NEED]` marcas reais nas telas:**
  - `Transactions` e `Expenses` mostram "Nubank", "Inter", "Fatura Nubank" e "iFood".
  - Num anúncio pago, considere nomes genéricos. A fala da v3 não cita nenhuma marca.
- **`[NEED]` financeiro:** confirme se os lançamentos são manuais. A fala foi escrita para valer nos dois casos ("o saldo acompanha"). Se o Daniel achar que "Entrou salário, saiu PIX" sugere conexão com o banco, a troca com o mesmo número de palavras é "Lançou salário, pagou PIX, o saldo do mês acompanha."
- **Voz:**
  - Mande só o texto do `falas-v3.txt`, num pedido só (`--juntar`). A Leda lê em voz alta qualquer instrução de estilo.
  - Confira "PIX" e "foguinho" na transcrição.
- **Conferência das 4 regras do editor:**

  | Regra | Como foi cumprida |
  |---|---|
  | 1. Nada de texto sobre texto | Cada palavra-chave sai antes da próxima entrar. FIXA e PONTUAL ficam em lados opostos. A logo só aparece na fala 13, longe do AGORA. |
  | 2. Nenhum recorte no meio da tela | Só a borda da tela do celular recorta, quando o conteúdo desliza entre abas. O traço sobre SEGUNDA é desenhado, não é máscara. |
  | 3. Celular por cima do texto | Todo texto e selo fica na camada do meio. Os apps da fala 5 passam **por trás** do celular. |
  | 4. Texto entra da borda ou de trás do celular | Toda palavra tem a origem indicada na tabela. |
