# Aprendizados: remotion-video

Adendos datados. O mais novo fica embaixo. A skill (`SKILL.md`) já incorpora a pesquisa até 09/10/2026, com aprovação do usuário. Novidades entram aqui primeiro.

## 09/10/2026: atualização para 4.0.534 e rotina de testes

- **Atualização da 4.0.250 para a 4.0.534** no projeto Rotina: foi preciso só trocar as versões de `remotion`, `@remotion/cli`, `@remotion/motion-blur` e `@remotion/transitions`, todas exatas e iguais.
  - Os tipos passaram sem mudança no código.
  - Os quadros de teste saíram idênticos aos de antes.
- **Stills rápidos:** cada `remotion still` leva cerca de 5–6 s, quase tudo para iniciar o navegador. Para revisar vários quadros, gere o bundle uma vez (`npx remotion bundle src/index.ts --out-dir=out/bundle`) e renderize os stills a partir dele, com `--scale=0.5`.
- **Tempo do render completo:** o explicativo de 60 s (1.800 quadros, CameraMotionBlur com 8 amostras, 4 CPUs) levou cerca de 15 min.
- **Renders em paralelo** (por exemplo, o vídeo e testes de 3D ao mesmo tempo) dividem os 4 CPUs e já travaram um render. Rode um por vez.

## 09/10/2026: promo v2 do Rotina (30 s) com celular 3D de verdade em produção

- **Pipeline em camadas** (código na branch `rotina-video`, pasta `src/v2`):
  - **Telas do app:** uma composição 1080×2340 renderizada como sequência JPEG (`--sequence --image-format=jpeg`). Foram 669 quadros em cerca de 1,5 min.
  - **Celular 3D:** uma composição só com o celular, fundo transparente, como sequência PNG (`--sequence --image-format=png --gl=swangle --concurrency=1`).
    - Mediu cerca de 1 s por quadro: 665 quadros em ~11 min, 172 MB.
    - Os arquivos saem como `element-182.png` (3 dígitos para 900 quadros).
  - **Vídeo final 2D:** composição que põe o PNG do quadro com `<Img>` entre as camadas 2D.
    - Rascunho em `--scale=0.5`: 72 s. Final em 1080×1920 com áudio: 182 s (4 CPUs).
- **Um "rig" compartilhado** (`rig.ts`): uma função dá posição e rotação do celular por quadro.
  - O 3D usa essa função; o 2D projeta pontos da tela com a mesma câmera do three.js (`Vector3.project`).
  - Assim cartões, sombras e brilho saem do ponto exato da tela do celular.
- **Troca de tela durante giros:** troque o conteúdo do celular no quadro em que a traseira está virada para a câmera (calcule com a normal da tela). Num giro rápido de 360°, isso dura só 2–3 quadros.
- **Motion blur barato:** um blur direcional em SVG (`feGaussianBlur stdDeviation="X 0"`) na camada do celular, proporcional à velocidade de rotação, no lugar do `CameraMotionBlur`. Não custa render extra.
- **Zoom para dentro da tela:** leve o celular até a câmera (z ~2,75 com câmera em 4,5 e fov 30) até a tela cobrir o quadro. A tela mostra o splash vermelho, e no quadro seguinte entra o cartão final 2D com o mesmo desenho. A emenda fica invisível.
- **Fundo com a mesma cor do conteúdo:** íris (círculo em `clipPath`) a partir de um elemento (o ponto do "?"), wipes diagonais acompanhando a direção do giro.
- **Cuidados:**
  - O `public/` é copiado em cada bundle. Pastas grandes de quadros deixam o bundle lento; deixe essas pastas fora do Git e gere de novo pelo código.
  - Um `pgrep -f "remotion render"` encontra o próprio comando do shell. Confira a contagem de arquivos em vez disso.
