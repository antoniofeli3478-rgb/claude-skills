---
name: combo-video-explicativo
description: Combo para fazer um vídeo explicativo ou promo de app (Reels 1080×1920, 30–60 s) com narração, música, legendas sincronizadas e motion premium. Junta as skills remotion-video, voz-pt-br e trilha-e-mixagem, com motion-video e ui-ux-pro-max como opcionais. Use quando o pedido for "vídeo do meu app", "anúncio animado com voz", "explicativo com narração".
---

# Combo: vídeo explicativo de app (voz + música + motion)

Este combo **não repete** o conteúdo das skills: ele diz qual usar em cada passo e o que já foi aprendido ao juntá-las. Abra cada skill só quando chegar ao passo dela.

## Skills ligadas

| Papel | Skill | Caminho |
|---|---|---|
| Animação e render (principal) | remotion-video | `skills/video/remotion-video/SKILL.md` (+ `APRENDIZADOS.md`) |
| Narração e legendas | voz-pt-br | `skills/audio/voz-pt-br/SKILL.md` (+ `APRENDIZADOS.md`) |
| Música, efeitos sonoros e mixagem | trilha-e-mixagem | `skills/audio/trilha-e-mixagem/SKILL.md` (+ `APRENDIZADOS.md`) |
| Alternativa de estilo premium em canvas | motion-video | `skills/video/motion-video/.claude/skills/motion-video/SKILL.md` |
| Paleta, fonte e estilo | ui-ux-pro-max | `skills/design/ui-ux-pro-max/SKILL.md` |
| Animações de botões e telas | button-animation | `skills/design/button-animation/SKILL.md` |

Exemplo real feito com este combo: `projetos/rotina/PASSO-A-PASSO.md`.

## Passo a passo (com os pontos de aprovação do usuário)

1. **Briefing numa mensagem só:**
   - o que o app faz e as telas reais (prints ou o código das telas);
   - as cores e a fonte da marca;
   - a duração e o formato;
   - um vídeo de referência, se houver;
   - a voz preferida.
2. **Roteiro em falas curtas**, uma por momento, com o gancho nos primeiros 3 s. Para 60 s, cerca de 12–13 falas. → **Aprovação do usuário.**
3. **Voz** (`voz-pt-br`):
   - Gere as falas, corte os silêncios e meça as durações.
   - No Gemini, gere o roteiro inteiro num pedido só (o limite é de 10 por dia).
   - Não use edge-tts no vídeo final de anúncio.
4. **Linha do tempo pelas durações reais da voz.** As seções caem nos compassos da música: em 120 BPM, compassos de 2 s e batidas de 15 quadros.
5. **Música e efeitos sonoros** (`trilha-e-mixagem`):
   - Escolha a música pela licença.
   - Use um riser antes da logo e impactos nas trocas de seção.
   - Mixe com ducking e deixe em −14 LUFS.
6. **Telas e animação** (`remotion-video`):
   - Recrie as telas reais em React, com as cores e a fonte do app.
   - Aplique as regras premium: molas `{stiffness: 380, damping: 31}`, nada parado, eventos sobrepostos de 4–12 quadros e transições que nascem de um elemento da cena.
   - Use zoom no que está sendo narrado, um toque com ondulação em cada ação e selos que saltam em 3D.
   - **3D de verdade** (planos-herói curtos): `@remotion/three` com `--gl=swangle`, telas como sequência JPEG e a correção do quadro 0.
7. **Legendas:** alinhe as palavras com `alinhar_legendas.py` (WhisperX). Use palavras-chave e não a transcrição inteira, respeitando a zona segura do Reels.
8. **Storyboard em imagens:** renderize quadros-chave (`remotion still`) e junte-os numa imagem. → **Aprovação do usuário.**
9. **Render final** com `CameraMotionBlur` só onde há movimento rápido. Confira o volume (ebur128) e uns 6–8 quadros antes de entregar.
10. **Salve o aprendizado:**
    - atualize o `PASSO-A-PASSO.md` do projeto;
    - acrescente adendos nos `APRENDIZADOS.md` das skills usadas;
    - registre aqui o que mudou neste combo.

## Avisos (aprendidos ao juntar as skills)

- **O tempo da voz manda no vídeo.** Gere a voz antes de animar, porque refazer a voz depois desalinha tudo.
- **A licença da voz e da música** decide se o vídeo pode virar anúncio pago. Confirme antes de produzir.
- **Não rode vários renders ao mesmo tempo:** a máquina tem 4 CPUs, e renders em paralelo travam ou ficam lentos.
- **Gaste menos créditos:**
  - aprove roteiro e storyboard antes do render completo;
  - prefira stills em vez do vídeo inteiro;
  - edite só a cena que mudou.
- **O Claude não ouve áudio.** Confira a voz com a transcrição automática e a música com espectrograma e LUFS. Peça ao usuário para ouvir a versão final.

## Histórico do combo

- **08–09/10/2026:** criado a partir do vídeo explicativo do Rotina (60 s, voz edge-tts, trilha numpy) e da pesquisa de motion, 3D, voz e som. A próxima versão deve trocar a voz (Gemini ou Kokoro) e a música (licenciada ou Lyria).
