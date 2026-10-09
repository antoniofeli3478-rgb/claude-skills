# claude-skills: a base de conhecimento do Daniel para o Claude

Este repositório existe para o Claude usar e ir melhorando. Ele guarda as habilidades (skills) do Claude, as anotações e os relatórios de pesquisa, e um passo a passo de cada projeto.

## Antes de qualquer tarefa (economia de créditos)

1. Leia **só** o `HABILIDADES.txt`: é o índice das habilidades, com uma linha por item e o caminho de cada uma.
2. Escolha a habilidade e abra apenas o arquivo indicado. Não percorra o repositório para descobrir o que existe.
3. Se a tarefa continuar um projeto já existente, leia o `projetos/<nome>/PASSO-A-PASSO.md` dele, que diz o que foi feito e quais habilidades usar.
4. Ao criar, mudar ou remover uma habilidade ou um projeto, atualize o `HABILIDADES.txt` no mesmo commit.

## Como organizar

- **Habilidades** ficam em `skills/<nome>/`, uma pasta por habilidade, na branch `main`.
- **Projetos** têm uma pasta em `projetos/<nome>/` com **apenas** o `PASSO-A-PASSO.md`. Ele registra:
  - o que foi feito e como;
  - os comandos usados;
  - os aprendizados e o próximo passo;
  - os **caminhos** das habilidades usadas.
  - Nunca copie skills para dentro de projetos.
- **Código de projetos** (fontes, áudios, vídeos) fica fora da `main`, numa branch do projeto ou num repositório próprio. O `PASSO-A-PASSO.md` diz onde.

## Combinado com o usuário

- **Idioma:** fale com o usuário em português do Brasil. Ele está aprendendo inglês aos poucos, então:
  - **Em português, sempre:** o essencial, para ele não precisar traduzir. Isso inclui conversas, resumos, perguntas e decisões, relatórios, propostas de skill para aprovação, instruções de uso e o PASSO-A-PASSO.
  - **Pode ficar em inglês:** material técnico e de apoio, como código, comentários de código, logs e anotações brutas de pesquisa.
- **Skills precisam de aprovação.** Ao encontrar uma versão melhor de uma skill, ou ao querer mudar uma, mostre primeiro a skill atualizada e pergunte antes de salvá-la no repositório.
  - Escreva a proposta no scratchpad, envie com SendUserFile e resuma o que mudou.
- **Conhecimento pode ser atualizado livremente.** Anotações de pesquisa, relatórios, passo a passo de projetos e documentação podem ser criados e atualizados sem perguntar. Ficam em `research_notes/`, `reports/`, `projetos/` e nos READMEs.
- **Salve sempre:** faça commit e push do trabalho aos poucos, porque o container da nuvem é temporário.
- **Chaves de API** (ex.: Google AI Studio) **nunca** vão para o repositório. Guarde no container em `/root/.config/rotina/` e peça de novo ao usuário quando o container for novo.

## Estrutura

| Caminho | O que é |
|---|---|
| `HABILIDADES.txt` | Índice rápido de tudo: habilidades, conhecimento, projetos e conectores |
| `skills/` | Uma pasta por habilidade: `remotion-video`, `motion-video` (kit com motor), `button-animation`, `thumbnail-carousel`, `ui-ux-pro-max` |
| `projetos/<nome>/PASSO-A-PASSO.md` | Passo a passo de cada projeto, com os caminhos das habilidades usadas |
| `research_notes/<tema>/` | Anotações brutas da pesquisa (a maioria em inglês) e amostras de voz |
| `reports/` | Relatórios finais de pesquisa, em português |

## Notas do ambiente (container da nuvem, sem GPU)

- **Remotion:** versão 4.0.534. Mantenha todos os pacotes `@remotion/*` na mesma versão exata.
  - Renderize com `--browser-executable=/opt/pw-browsers/chromium_headless_shell-1194/chrome-linux/headless_shell`.
  - Para 3D, acrescente `--gl=swangle`.
- **Kit motion-video:** veja `skills/motion-video/README.md`. Ele precisa de um script que roda o Chrome com `--no-sandbox`, porque o container roda como root.
- **Proxy:** o HTTPS de saída passa por um proxy que intercepta TLS, então ferramentas em Python podem precisar do certificado em `/root/.ccr/ca-bundle.crt`.
- **Desempenho:** a máquina tem só 4 CPUs. Evite rodar vários renders ao mesmo tempo, porque eles disputam a CPU e podem travar.
