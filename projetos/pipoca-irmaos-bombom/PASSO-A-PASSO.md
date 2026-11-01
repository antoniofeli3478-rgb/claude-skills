# Projeto Pipoca Irmãos Bombom: vídeo para Instagram (passo a passo)

Carrinho de pipoca "Irmãos Bombom" (animamos festas, telefone 16 99741-5599). Pedido de 01/11/2026: animação de entrada, música animada da internet, cartão final "entre em contato" com os dados, tudo na paleta do carrinho, sem legenda.

## Onde está cada coisa

- **Código (Remotion 4.0.534):** `/home/user/pipoca-video` no container (temporário). Ainda não foi para o GitHub: falta o usuário aprovar uma branch própria (sugestão: `pipoca-video`).
- **Material do usuário:** vídeo `20261031_090809.mp4` (18,8 s, 4K/1080p vertical, sem fala) baixado do Google Drive; 2 fotos do carrinho enviadas no chat.
- **Saída:** `out/pipoca-irmaos-bombom-v1.mp4` (22 s, 1080×1920, 30 fps, −14 LUFS).

## Habilidades usadas

| Para quê | Caminho |
|---|---|
| Vídeo em Remotion, regras de composição do editor, zona segura do Reels | `skills/video/remotion-video/SKILL.md` |
| Música (escolha, grade de batidas), efeitos e mixagem | `skills/audio/trilha-e-mixagem/SKILL.md` (script `scripts/mixar_com_efeitos.py`) |
| Repositórios de edição baixados (referência) | `research_notes/Edição de vídeo 2026-11/repositorios-baixados.md` |

## O que foi feito (v1)

1. **Baixar o vídeo do Drive** (link "qualquer pessoa com o link"):
   `curl -L -o video.mp4 "https://drive.usercontent.google.com/download?id=<ID>&export=download&confirm=t"`
   O app do Claude não aceita anexo acima de 30 MB; o Drive resolve.
2. **Analisar:** folha de quadros com `ffmpeg ... tile=6x2`, faster-whisper (sem fala, só barulho da rua). O vídeo tem `rotation=-90`: o ffmpeg já endireita.
3. **Preparar o clipe:** `fps=30, scale=1080:1920, eq=saturation=1.18:contrast=1.06, colorbalance` (mais quente), `-g 10` para busca rápida no Remotion → `public/clip.mp4`.
4. **Música:** Mixkit #805 "Mi Gente" (categoria festas, 108 BPM, licença Mixkit grátis, sem crédito). Trecho a partir de 17,113 s, para a virada de seção (20,5 s da faixa) cair no quadro 101, o corte da entrada para o vídeo. Batidas com librosa → lista `B` em `src/Pipoca.tsx`.
5. **Estrutura (cortes nas batidas):**
   - 0–101 entrada: listras vermelho/branco caindo de cima, placa dourada "IRMÃOS BOMBOM" pendurada (balança), letras "PIPOCA" subindo de baixo no estilo do logo do carrinho (fonte Luckiest Guy, contorno vermelho, sombra amarela), "ANIMAMOS FESTAS!" entrando pela direita, pipocas pulando.
   - Transição: **onda de pipoca** subindo de baixo e cobrindo a tela no quadro 101 (esconde o corte).
   - 101–505 vídeo: close da pipoca, volta no carrinho (1,57x), "Pipoca grande", vitrine, QR com selo "ACEITAMOS PIX", 2 fotos com Ken Burns, plano aberto. Cada corte tem um "punch" de 1,07→1,00.
   - 505–660 contato: painel listrado entra pela direita, placa desce, cartão creme sobe, "LEVE A PIPOCA / PRA SUA FESTA!", "ORÇAMENTOS E ENCOMENDAS", botão vermelho com o telefone pulsando na batida, "LIGUE OU CHAME NO WHATSAPP".
6. **Efeitos sonoros** (36, lista em `efeitos.json`): pops nas letras e nas pipocas, whoosh na onda (pico no corte), swishes nas entradas, slam + chime no telefone. Mixados com `mixar_com_efeitos.py` usando um WAV de silêncio como `--voz` e `--musica-db 0 --efeitos-db -4`.
7. **Render:** `npx remotion render Pipoca out/... --crf 18 --concurrency 3` (2 min).
8. **Revisão:** folhas a cada 6 quadros. Corrigido: letra "P" passando sobre a placa (mola 260/15 → 260/25) e botão do telefone passando sobre o texto (260/20 → 260/30).

## Aprendizados

- Mola com pouco amortecimento faz o elemento passar do ponto e cruzar o texto vizinho (regra 1). Para elementos que pousam perto de outro texto, use ζ ≥ 0,75.
- Onda de objetos cobrindo a tela inteira é uma transição que respeita a regra 2 (não é máscara) e combina com o tema.

## Próximo passo

- Esperar o retorno do usuário sobre a v1. Confirmar se o número é WhatsApp, se tem @ do Instagram e se quer preços/sabores no vídeo.
- Pedir aprovação para subir o código numa branch `pipoca-video`.
