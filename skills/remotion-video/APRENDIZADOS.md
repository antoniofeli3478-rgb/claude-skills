# Aprendizados: remotion-video

Adendos datados. O mais novo fica embaixo. A skill (`SKILL.md`) já incorpora a pesquisa até 09/10/2026, com aprovação do usuário. Novidades entram aqui primeiro.

## 09/10/2026: atualização para 4.0.534 e rotina de testes

- **Atualização da 4.0.250 para a 4.0.534** no projeto Rotina: foi preciso só trocar as versões de `remotion`, `@remotion/cli`, `@remotion/motion-blur` e `@remotion/transitions`, todas exatas e iguais.
  - Os tipos passaram sem mudança no código.
  - Os quadros de teste saíram idênticos aos de antes.
- **Stills rápidos:** cada `remotion still` leva cerca de 5–6 s, quase tudo para iniciar o navegador. Para revisar vários quadros, gere o bundle uma vez (`npx remotion bundle src/index.ts --out-dir=out/bundle`) e renderize os stills a partir dele, com `--scale=0.5`.
- **Tempo do render completo:** o explicativo de 60 s (1.800 quadros, CameraMotionBlur com 8 amostras, 4 CPUs) levou cerca de 15 min.
- **Renders em paralelo** (por exemplo, o vídeo e testes de 3D ao mesmo tempo) dividem os 4 CPUs e já travaram um render. Rode um por vez.
