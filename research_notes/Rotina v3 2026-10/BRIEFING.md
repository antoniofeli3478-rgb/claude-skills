# Briefing: vídeo v3 do app Rotina (12/10/2026)

Pedido do Daniel (editor de motion profissional, brasileiro): um vídeo novo do Rotina. Um agente faz o roteiro com a skill de copy e depois passa pelo "copy humanizado"; em seguida vêm a voz e as animações. Usar as skills de design e de animação e **todas** as skills do repositório que servirem (ler todas e ver como usar). No fim, listar tudo o que foi usado para criar mais skills e combos.

## O app
- **Rotina**: um app só para três áreas da vida: **Hábitos**, **Treinos** (Treino, Planejar, Progresso, Cardio) e **Financeiro** (Visão, Transações, Cartões, Rendas, Investimentos, Relatórios), mais Ajustes.
- O que cada tela faz de verdade (não invente função): veja o código das telas em `/home/user/claude-skills/rotina-video/src/explainer/screens/` (Spreadsheet, HabitsList, HabitsStats, WeekPlan, Styles, LogSets, FinOverview, Transactions, Expenses) e `/home/user/claude-skills/rotina-video/src/screens/`. O roteiro anterior (v2) está em `/home/user/claude-skills/rotina-video/audio/v2/falas-v2.txt` (mostra as funções confirmadas: marcar hábitos e ver sequência crescer; gráficos semana a semana; montar treino de cada dia e plano da semana; estilos força/hipertrofia/resistência/deload; registrar carga, repetições e séries com gráfico de evolução; ver para onde vai cada real; entradas e saídas de bancos e cartões; despesa fixa x pontual; cronograma do mês por categoria).
- Visual da marca: fundo `#080808`, vermelho `#CC0000` / `#FF5A5A`, fontes Barlow e Barlow Condensed.

## Formato
- Reels/Stories **1080×1920**, **30 s** (900 quadros a 30 fps).
- Voz **Gemini TTS, voz Leda** (pt-BR), em velocidade **natural ou quase** (no máximo 1,25x). A v2 precisou de 1,6x porque o roteiro tinha 139 palavras. Regra medida: a Leda fala ~2,67 palavras por segundo em 1,0x. **Meta: 80 a 95 palavras** no roteiro inteiro.
- Chamada final: "Baixe o Rotina agora. Link na bio."
- Público: quem tenta organizar a vida (hábitos, academia, dinheiro) em vários apps ou planilhas; 20–40 anos; Brasil.

## Regras do editor (obrigatórias em todo quadro)
1. Nada de texto sobre texto, nem logo sobre texto.
2. Nenhum recorte (máscara) cortando a animação no meio da tela.
3. O elemento principal (o celular) fica por cima do texto; o texto fica entre o fundo e o celular.
4. O texto entra de fora do quadro ou de trás do elemento principal, nunca surge do nada.

## Limites deste trabalho
- **Todos os recursos liberados pelo Daniel (12/10/2026), inclusive Blender, 3D, animação e suavização.** **Todo o 3D passa pelo Blender** (`blender-ia`, base `premium.py`): o celular 3D de TODAS as cenas é renderizado no Blender com fundo transparente e entra como camada no Remotion, onde ficam fundo, textos, cartões e efeitos. Custo medido: ~24 s por quadro em 1080×1920 no plano-herói (menos quando o celular ocupa menos da tela); rascunho a 25% ~1,5 s por quadro.
- **Algo novo:** reaproveitar POUCO da v2 (o Daniel quer um vídeo novo). Pode reaproveitar só a infraestrutura (voz.json como fonte dos tempos, camadas separadas); visual, cenas, transições, textos e coreografia do celular são novos.
- **Regra nova do Daniel para o celular:** ele fica **sempre reto ou inclinado para cima** (tela de frente para a câmera ou levemente voltada para cima, com o topo do aparelho se afastando da câmera). **Nunca inclinado para baixo** (tela voltada para o chão, topo vindo na direção da câmera), como aconteceu na v2.
- **Nada pago** (Higgsfield, Lyria pago, ElevenLabs) sem o "sim" do Daniel.
- **Nunca** salvar chave de API em arquivo do repositório. A chave do Gemini fica em `/root/.config/rotina/gemini.key`.
- O repositório `/home/user/claude-skills-main` (branch main, skills) é **somente leitura** para os agentes. O projeto está em `/home/user/claude-skills/rotina-video` (branch rotina-video).
