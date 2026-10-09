# Aprendizados: combo-video-explicativo

Adendos datados. O mais novo fica embaixo.

## 09/10/2026: roteiro com copy

- **Para escrever ou melhorar o roteiro** (passo 2 do combo), use o combo `skills/combo-copy-humanizada/SKILL.md` (copywriting e depois humanizer). Ele já traz a seção de roteiro de narração: escrever para o ouvido, uma fala por momento e fechar o texto **antes** de gerar a voz.
- Isto é um aviso. O `SKILL.md` deste combo só muda com a aprovação do usuário, e a inclusão desse passo fica como proposta.

## 09/10/2026: promo v2 do Rotina (30 s, visual novo, celular 3D)

Ordem que funcionou, do zero ao vídeo final, numa sessão (passo a passo completo em `projetos/rotina/PASSO-A-PASSO.md`):
1. **Roteiro** com `combo-copy-humanizada`, aprovado pelo usuário.
2. **Voz** num pedido só, acelerada com `atempo`. Aperte as pausas e alinhe com WhisperX. O `voz.json` vira a fonte de todos os tempos.
3. **Grade da música:** escolha o BPM e a fase para que a virada caia numa palavra-chave. Defina as seções pelos tempos da voz.
4. **Rig do celular**, depois as telas do app (JPEG), depois o celular 3D (PNG) e por fim as camadas 2D.
5. **Efeitos** pela mesma lista de palavras, depois a mixagem e a medição da voz contra o fundo.
6. **Rascunho** em meia resolução e revisão em folhas de quadros. Depois o render final e a transcrição do áudio final.

**Avisos:**
- **Duração × velocidade:** quando o pedido traz as duas, calcule antes a duração da fala para ver se cabem juntas. Se não couberem, diga ao usuário qual foi a escolha.
- **Ajudantes em paralelo** (subagentes) gastam a mesma cota da sessão. Um ajudante parou no meio por limite de uso, e o trabalho dele se perdeu. Peça que salvem arquivos parciais, ou faça a tarefa no agente principal.
- **Tempo de render** (4 CPUs): telas ~1,5 min, celular 3D ~11 min, rascunho 72 s e final 3 min. A sessão inteira leva umas 3–4 horas de trabalho.
