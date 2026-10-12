---
nome: "Ferramentas de vídeo com IA, 3D com IA e assets (não são skills)"
data_verificada: "2026-10-11 (pesquisas); Seedance e SeedVR2 do relatório de 2026-10-12"
categoria_nossa: "video"
status: "so-referencia"
onde_entrou: "skills/sites/scroll-cinematico (já na main, PR #21: references/ia-de-video.md) e skills/video/blender-ia/SKILL.md (tabela de licenças de 3D por IA)"
fontes_locais: ["research_notes/Caça de skills 2026-10/video-ia/pesquisa-A.md", "research_notes/Caça de skills 2026-10/video-ia/TRANSCRICAO.md", "research_notes/Caça de skills 2026-10/video-ia/RESUMO.md", "research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md", "research_notes/Caça de skills 2026-10/blender-ia/pesquisa-A.md", "reports/Blender premium, sites com IA, scroll e vídeo com IA (2026-10-12).md"]
---
# Ferramentas de vídeo com IA, 3D com IA e assets

Estas ferramentas NÃO são skills, por isso não têm ficha própria. Cada uma tem uma seção curta: licença, uso comercial no plano grátis, requisitos de placa de vídeo (GPU) e nota.

**Como ler**
- A nota (0 a 10) vem das pesquisas e vale para o NOSSO caso: entrega comercial em site de cliente, de graça. Nota baixa não quer dizer que o modelo é ruim.
- "Termos mudam todo mês." Vários números vêm de blogs que se contradizem. Conferir na tela do serviço no dia do uso. Isto não é parecer jurídico.
- **Container da nuvem:** 4 CPUs, 15 GB de RAM, sem GPU. Não gera vídeo por IA (os modelos nem cabem). Aqui só fazemos quadros no Blender e a pós-produção no ffmpeg.
- **GPU do Daniel:** o relatório de 12/10/2026 diz que a placa dele é NVIDIA de cerca de 4 GB, pequena demais para os modelos abaixo (eles pedem de 8 a 12 GB ou mais). O caminho é Kaggle, Colab ou Hugging Face. (No RESUMO de scroll de 11/10 a pergunta sobre a placa ainda estava pendente; o relatório de 12/10 já traz a resposta.)
- Regra de ouro: serviço online grátis serve para RASCUNHO e para mostrar a ideia ao cliente; não entregar vídeo de plano grátis (marca d'água, uso comercial vetado ou não esclarecido). Nunca remover marca d'água: viola os termos e não tira o SynthID invisível do Google.

---

## Parte 1. Modelos abertos (rodam no computador, no Kaggle ou no Colab)

### Wan 2.2 (Alibaba, jul/2025)
- **Licença:** Apache 2.0 (conferida no Hugging Face). A mais limpa: sem teto de faturamento nem de território.
- **Uso comercial no grátis:** sim, livre. Sem marca d'água.
- **GPU:** versão 5B cerca de 8 GB (GGUF com offload). Versão 14B: 6 a 8 GB em 480p com GGUF Q3/Q4; 12 GB confortável com Q5/Q6; FP16 pede 50 GB ou mais.
- **Saída:** 720p, cerca de 5 s; 24 fps (5B) e 16 fps (14B). Tem quadro inicial e final (para loop e transição) e muito suporte no ComfyUI. Os Wan 2.5 a 3.0 são fechados (só API).
- **Nota:** 8. Top 2 da pesquisa; roda de graça no Kaggle ou Colab (T4).

### LTX-2.3 (mar/2026) e LTX-2.5 (jul/2026, 22B), da Lightricks
- **Licença:** LTX-2.x Community License. Grátis para quem fatura menos de US$ 10 milhões por ano (empresa mais afiliadas); acima disso, licença paga. O 2.5 pede aceite no Hugging Face (aprovação automática).
- **Uso comercial no grátis:** sim, abaixo do teto. Se o cliente for grande e rodar o modelo ele mesmo, precisa de licença paga; quando o Daniel roda e entrega só o vídeo, quem usa é o Daniel. Anotar a licença mesmo assim.
- **GPU:** 2.3 cerca de 10 GB (Q4 GGUF, 960×544) ou 16 GB (FP8 em 720p). 2.5: transformer 22B mais codificador Gemma 12B, então 24 a 32 GB, ou menos com `--quantization fp8-cast --offload cpu` (mais lento). Pesos do 2.5: cerca de 44 GB mais 24 GB do codificador.
- **Saída:** até 4K, 24 a 50 fps, 5 a 10 s, com áudio. Recursos úteis para scroll: KeyframeInterpolation (quadro inicial e final), IC-LoRA Alpha-Gen (gera matte alpha, vídeo com fundo transparente), LoRA Slow-Motion-Control, Restore, SDR para HDR e versão destilada rápida.
- **Nota:** 9. Top 1 da pesquisa. Sem GPU: Space oficial `Lightricks/LTX-2-3` com conta grátis no Hugging Face (cerca de 1 clipe por dia).

### HunyuanVideo 1.5 (Tencent, nov/2025, 8,3B)
- **Licença:** Tencent Community. Comercial ok, MAS não vale na União Europeia, Reino Unido e Coreia do Sul, inclusive para exibir o resultado lá (cláusula 5c).
- **Uso comercial no grátis:** sim, fora desses territórios. Site público aberto a visitantes europeus = risco: evitar em site de cliente.
- **GPU:** cerca de 14 GB com offload; 24 GB no padrão.
- **Saída:** 720p, 5 a 10 s; bom em rostos e cenas com várias pessoas.
- **Nota:** 5.

### CogVideoX (2B, 5B e 1.5-5B)
- **Licença:** 2B é Apache 2.0. 5B: comercial só com cadastro na Zhipu e até 1 milhão de visitas por mês.
- **Uso comercial no grátis:** 2B sim; 5B com cadastro.
- **GPU:** 8 a 12 GB. **Saída:** 720×480, 6 s, 8 fps (o 1.5 vai a 1360×768). Imagem para vídeo razoável, mas ultrapassado.
- **Nota:** 3.

### Mochi 1 (Genmo)
- **Licença:** Apache 2.0. **Comercial:** sim.
- **GPU:** cerca de 20 GB (ComfyUI) ou 24 GB. **Saída:** 480p, cerca de 5 s, 30 fps. Segue bem o prompt, mas ultrapassado.
- **Nota:** 3.

### Stable Video Diffusion XT (Stability AI)
- **Licença:** Stability Community: comercial só com cadastro e receita abaixo de US$ 1 milhão por ano.
- **GPU:** menos de 10 GB. **Saída:** 1024×576, 14 a 25 quadros (2 a 4 s). Só imagem para vídeo, movimento fraco, ultrapassado.
- **Nota:** 2.

### Step-Video-T2V (StepFun, 30B)
- **Licença:** MIT. **GPU:** 80 GB (servidor). Grande demais para uso local.
- **Nota:** 1.

### SeedVR2 (ByteDance)
- **Licença:** Apache 2.0, segundo o relatório de 12/10/2026 (verificação feita pelo agente principal, fora das pesquisas A e B).
- **Uso:** melhora (restaura/amplia) um vídeo que já existe; NÃO gera vídeo novo. Pode servir de acabamento para clipes de Wan ou LTX.
- **GPU:** não verificado nos arquivos locais.
- **Nota:** sem nota.

---

## Parte 2. Serviços online (plano grátis)

Todos: rascunho e aprovação com o cliente; não entregar.

### Google Flow (Veo 3.1 Lite/Fast)
- **Licença:** serviço proprietário do Google.
- **Grátis:** 50 créditos por dia, sem acumular (oficial). Lite custa 10, Fast 20 e Quality 100 (não cabe num dia). Pode bloquear vídeo em horário de pico. Clipes de 4, 6 ou 8 s; upscale para 1080p só para assinantes.
- **Marca d'água:** sim (selo visível 'Veo' fora do Ultra) mais SynthID invisível.
- **Uso comercial no grátis:** NÃO esclarecido (há pergunta sem resposta oficial no fórum do Google). Tratar como não.
- **GPU:** nenhuma (nuvem). **Nota:** 7, só para rascunho. Melhor qualidade grátis (física, luz, câmera lenta, áudio).

### Veo 3.1 Lite pela API Gemini (pago, quase grátis)
- **Grátis:** não (a página oficial de preços, de 09/10/2026, mostra 'Free Tier: Not available' para Veo 3.1 e para o Gemini Omni). A chave do Google que já existe NÃO dá Veo de graça.
- **Preço:** US$ 0,05 por segundo em 720p (cerca de US$ 0,40 por clipe de 8 s) e US$ 0,08 por segundo em 1080p (8 s = US$ 0,64). Exige ligar o faturamento.
- **Uso comercial:** ok pelos termos do Google Cloud/API. Marca invisível SynthID; marca visível não documentada.
- **Teste:** NÃO testado, para não gerar cobrança. A chave não foi lida nem impressa.
- **Nota:** 8, se o Daniel aprovar o gasto.

### Google Cloud, teste de US$ 300 por 90 dias (Vertex AI)
- Exige conta nova e cartão. Blogs dizem que serve para Veo, sem confirmação oficial para o 3.1. Uso comercial ok (Cloud). Só se o Daniel quiser cadastrar.

### Gemini app / Gemini Omni
- O app grátis não gera vídeo (agora é o Gemini Omni, pago). A promoção de 10 vídeos acabou em 04/08/2026. **Nota:** 0.

### Kling
- **Grátis:** cerca de 66 créditos por dia (há quem diga que é por mês), uns 2 vídeos em 720p de 5 s, só modelos antigos.
- **Marca d'água:** sim. **Uso comercial no grátis:** NÃO. **GPU:** nenhuma.
- **Nota:** 5. Muito boa em produto e movimento; quadro inicial e final em alguns modelos.

### Dreamina / CapCut (Seedance)
- **Grátis:** créditos diários (um artigo da Dreamina cita 120 por dia), sem número fixo de vídeos. Marca d'água no grátis. Comercial: não verificado.
- **Seedance (ByteDance) NÃO é open source:** a conta oficial da ByteDance no Hugging Face não tem modelo Seedance, e os guias dizem que ele só funciona pela nuvem. Uso comercial e plano grátis não ficaram claros (relatório de 12/10/2026).
- **Nota:** 4 (Seedance 2.x é topo de linha, mas o grátis não serve para entrega).

### Runway
- **Grátis:** 125 créditos uma vez só (oficial), clipes de cerca de 5 s. Marca d'água; o plano Standard (US$ 12 a 15 por mês) remove.
- **Uso comercial no grátis:** sim, segundo um comparativo; a Runway diz que o resultado é do usuário. Confirmar nos termos.
- **Nota:** 4. Boa (Gen-4). É o único serviço que parece permitir comercial no grátis, mas são só 125 créditos.

### Hailuo / MiniMax
- **Grátis:** desde 2025 é só um pacote de teste que expira em 3 dias (a cota diária é política antiga). Clipes de cerca de 6 s em 720p. Marca d'água sim. Comercial: NÃO.
- **Nota:** 3. O Hailuo 02 tem quadro inicial e final.

### Luma Dream Machine (Ray3.x)
- **Grátis:** a página de preços não tem plano grátis; contas novas podem ganhar créditos de teste. Marca d'água. Comercial a partir do Plus (US$ 25 a 30 por mês).
- **Nota:** 3. Muito boa; tem loop e keyframes.

### PixVerse
- **Grátis:** cerca de 90 créditos no cadastro e depois cerca de 60 por dia (fonte única, revendedora). 360p a 720p. Marca d'água: fontes divergem. Comercial: NÃO.
- **Nota:** 3. Tem 'Transition' (quadro inicial e final).

### Vidu
- **Grátis:** cerca de 40 créditos por mês, ou ilimitado no 'Off-Peak' (fontes divergem). 720p, marca d'água no canto inferior direito. Comercial: NÃO.
- **Nota:** 3. Quadro inicial e final.

### Pika
- **Grátis:** 80 créditos por mês (Krea) ou 0 (outra fonte). 480p, marca d'água. Comercial a partir do Creator (US$ 35 por mês).
- **Nota:** 2. Tem Pikaframes.

### Higgsfield (conector deste ambiente)
- **Grátis:** de 0 a 10 créditos por dia (fontes divergem). Agrega Kling, Veo, Seedance e Wan.
- **Cuidado:** o conector gasta créditos PAGOS da conta do Daniel a cada geração. Só usar com a aprovação dele. Direito comercial segue o plano Higgsfield.
- **Nota:** 2.

### Grok Imagine (xAI)
- Vídeo quase só pago (SuperGrok, cerca de US$ 30 por mês). A xAI diz que o resultado é do usuário, mas no plano pago. **Nota:** 2.

### Meta AI / Vibes
- Grátis durante o lançamento, com limites não publicados. Marca invisível e rótulo 'IA'. Uso comercial não esclarecido; pouco controle. **Nota:** 2.

### Sora (OpenAI)
- App de consumo encerrado em 26/04/2026. **Nota:** 0.

---

## Parte 3. Onde rodar de graça

### Hugging Face Spaces (ZeroGPU)
- **Cota:** 2 min por dia sem login; 3,5 min por dia com conta grátis; PRO 25 min. Cada chamada reserva a duração declarada. O LTX-2.3 pede 150 s, então sem login nunca cabe.
- **Spaces oficiais:** `Lightricks/LTX-2-3`, `multimodalart/wan-2-2-first-last-frame`, `zerogpu-aoti/wan2-2-fp8da-aoti-faster`: cerca de 1 clipe por dia.
- **Teste nosso (11/10):** recusado por cota (o IP compartilhado do container tinha 55 s). Nenhum vídeo gerado, nenhum custo. Falta uma conta grátis do Daniel.

### Kaggle
- Cerca de 30 h por semana de GPU (T4 ×2 ou P100, 16 GB); sessão de até 12 h; exige telefone verificado. Roda Wan 2.2 (5B e 14B GGUF) e LTX-2.3 GGUF por script Python (diffusers). Lento numa T4, mas comercial e sem marca d'água.

### Google Colab (grátis)
- T4 de 16 GB; cota não publicada (cerca de 15 a 30 h por semana); sessão de até 12 h. O grátis restringe interfaces web remotas (túnel de ComfyUI): usar notebook/diffusers.

### ComfyUI
- Interface local onde estão os workflows prontos de Wan 2.2 e LTX. Licença não verificada nos arquivos locais.

### Faixas de placa de vídeo (NVIDIA; Mac e AMD funcionam mal ou devagar)
- 8 GB: Wan 2.2 5B GGUF, só para testar. 12 GB: Wan 14B Q5/Q6 e LTX-2.3 Q4 (mínimo recomendado). 16 GB: LTX-2.3 FP8 em 720p e Hunyuan 1.5 com offload. 24 a 32 GB: LTX-2.5 FP8 e 1080p ou mais (ideal). PC com 32 GB de RAM ou mais e 60 a 120 GB de disco.

---

## Parte 4. 3D com IA e assets para o Blender (não são skills)

Quase todos geram um arquivo GLB para importar no Blender. O celular-herói continua PROCEDURAL (feito por script); IA de 3D só para objetos de cena (moedas, halteres, ícones). Evitar aparência de iPhone ou de qualquer produto Apple.

### Poly Haven
- **Licença:** CC0. **Comercial:** sim, sem crédito. HDRIs, texturas e modelos. 96 HDRIs na categoria 'studio'; usado no teste: `studio_small_09` (1k, 1,6 MB). **GPU:** nenhuma. **Nota:** padrão para HDRI de estúdio.

### Physically Based (API de materiais)
- **Licença:** CC0 (conferida no LICENSE do repositório). Valores medidos de cor base e IOR (alumínio, aço inox, titânio, cromo; vidro 1,52). **Nota:** sem nota; fonte de números para materiais.

### Poly Pizza
- Cerca de 69% dos modelos são CC-BY (crédito obrigatório). Bloqueia IPs de nuvem. Comercial: em parte.

### Sketchfab
- **Licença:** varia por modelo. Standard (loja): qualquer uso. CC0 e CC BY servem (CC BY com crédito). **Editorial: proibido em uso comercial ou promocional.** CC BY-NC: proibido em anúncio.

### Meshy
- **Grátis:** CC BY 4.0, uso comercial permitido COM crédito à Meshy. Pro ou superior: privado, comercial, sem crédito. **GPU:** nenhuma (nuvem). **Nota:** sem nota (skill `meshy-3d-generation` tem ficha própria).

### Tripo
- Grátis = CC BY 4.0 e público; pago = privado e comercial. Fontes CONTRADITÓRIAS sobre o grátis (o blog deu 403 para o agente). Para anúncio, usar plano pago.

### Hyper3D Rodin
- Plano Creator (US$ 30 por mês): 'Unlimited export and any use'. O grátis não diz nada sobre uso comercial; uma fonte de terceiros fala em marca d'água. Usar Creator ou superior.

### Hunyuan3D 2.1 (Tencent)
- **Licença:** Tencent Hunyuan 3D 2.1 Community License. Não vale na UE, Reino Unido e Coreia do Sul; acima de 1 milhão de usuários ativos por mês pede licença; a Tencent não reivindica direitos sobre os resultados; é proibido usar os resultados para melhorar outro modelo de IA; incluir a licença ao redistribuir os pesos. O Brasil está coberto. O 3.0 não tem pesos abertos confirmados. **GPU:** não verificado.

### TRELLIS.2-4B (Microsoft)
- **Licença:** MIT. Imagem para 3D com PBR. **GPU:** Linux com NVIDIA de 24 GB ou mais (fonte de terceiros, ComfyUI Wiki), então NÃO roda no container nem na placa do Daniel. Pode deixar furos na malha.

### Stable Fast 3D / SPAR3D (Stability AI)
- **Licença:** Stability Community License: comercial se a receita anual for menor que US$ 1 milhão; acima disso, licença Enterprise. **GPU:** não verificado.

### SAM 3D Objects (Meta)
- **Licença:** SAM License (19/11/2025). Comercial com restrições (militar, armas etc.); repassar a licença ao redistribuir. **GPU:** não verificado.

### UniRig
- Código MIT; checkpoints e dados com termos próprios. Rig automático de personagem; GPU de 8 GB ou mais. Pouco útil para promo de app.

### Skybox AI (Blockade Labs)
- Grátis = CC BY mais marca d'água (dados de 2023); pago = comercial. O 'HDR' exportado não era de 32 bits. Para estúdio de produto, preferir HDRI CC0 da Poly Haven.

---

## Fontes
- `research_notes/Caça de skills 2026-10/video-ia/pesquisa-A.md` (modelos abertos, serviços, onde rodar, alertas de licença, teste no Hugging Face)
- `research_notes/Caça de skills 2026-10/video-ia/TRANSCRICAO.md` e `RESUMO.md`
- `research_notes/Caça de skills 2026-10/blender-ia/pesquisa-B.md` (seção 7, licenças de 3D por IA e assets) e `pesquisa-A.md`
- `reports/Blender premium, sites com IA, scroll e vídeo com IA (2026-10-12).md` (Seedance, SeedVR2, placa do Daniel)
- Links oficiais citados pelas pesquisas: https://ai.google.dev/gemini-api/docs/pricing, https://support.google.com/flow/answer/16526234, https://runway.com/pricing, https://lumalabs.ai/pricing, https://github.com/Lightricks/LTX-2/blob/main/LICENSE-2_x, https://huggingface.co/docs/hub/spaces-zerogpu
