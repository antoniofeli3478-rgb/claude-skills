# Scroll-vídeo sem travar: material parcial (12/10/2026)

**Situação: CANCELADO. O Daniel mandou parar tudo e depois cancelou o trabalho em 12/10/2026. O material foi salvo como estava (≈ 40% pronto) e não será continuado, a não ser que ele peça de novo.** Nada aqui altera a skill `skills/sites/scroll-cinematico/` (ela continua como entrou no PR #21).

O pedido do Daniel: "foque em ter uma boa utilização do scroll vídeo nos sites, de forma que ele não fique travado". Foram três agentes: pesquisa (A), teste de velocidade no navegador (B) e proposta de atualização da skill (C).

## Quanto ficou pronto (estimativa nossa)

Método: cada etapa vale 1 (pronta), 0,5 (parcial) ou 0 (não feita). Para o teste B, as execuções contam pelo número real (2 de 373). A nota geral pesa A 30%, B 40% e C 30%.

| Parte | Pronto | Detalhe |
|---|---|---|
| **A. Pesquisa** | **63%** (5 de 8) | Pronto: causa e correção do travamento do vídeo; comparação de codecs; como medir. Parcial: janela deslizante de quadros, WebCodecs, suavização, qualidade adaptativa. Não feito: o texto final `pesquisa-A.md`. |
| **B. Teste de velocidade** | **40%** | Montagem do teste 100% (conteúdo, servidor, página, rodador). Execuções 0,5% (**2 de 373**). Texto final 0%. |
| **C. Proposta da skill** | **17%** (1 de 6 etapas) | Só copiou a skill atual. Não escreveu: a seção "Scroll sem travar", `travadas.md`, a página e o componente vencedores, os scripts novos, a transcrição e o resumo. |
| **Total** | **≈ 40%** | 0,3 × 63 + 0,4 × 40 + 0,3 × 17. |

## O que tem nesta pasta

- `A-achados-parciais.md`: o que a pesquisa leu e mediu, com a fonte de cada fato.
- `B-teste-parcial.md`: o equipamento de teste e as 2 únicas medições.
- `codigo-A/`: rascunhos de código (janela deslizante, suavização, qualidade adaptativa, medidor) e as receitas de ffmpeg testadas.
- `codigo-B/`: gerador de conteúdo, servidor, página de teste e rodador da matriz (373 execuções), mais o resultado das 2 execuções.

## Se o Daniel pedir de novo (o trabalho está cancelado)

1. Rodar `codigo-B/gerar-conteudo.sh` (uns 6 minutos) e `node rodar.js --perfil=principal` e os outros perfis (uns 60 minutos no total).
2. Terminar os testes de A: janela deslizante com rolagem real e rede lenta; WebCodecs.
3. Escrever `pesquisa-A.md`, `pesquisa-B.md` e a proposta (seção "Scroll sem travar" no `SKILL.md`, que precisa da aprovação do Daniel).
4. Testar num celular real (Android por USB; iPhone pelo Safari do Mac).

Os arquivos grandes de teste (cerca de 600 MB de vídeo e quadros) ficaram só na pasta temporária da sessão e se perdem; o `gerar-conteudo.sh` os recria.
