---
nome: "deploy-to-vercel"
repositorio: "vercel-labs/agent-skills"
caminho_no_repo: "skills/deploy-to-vercel"
autor: "Vercel"
instalacoes: "151,9 mil (skills.sh find, 11/10/2026)"
commit_ou_versao: "063bee94c3f4df8453406c830b0a7df0f2860278 (28/08/2026); skill versão 3.0.0"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: 8
status: "instalada-intacta"
onde_entrou: "skills/sites/deploy-to-vercel/ (cópia intacta; aviso de hospedagem comercial no APRENDIZADOS.md) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-A.md (tabela e seção 3.5)", "research_notes/Caça de skills 2026-10/sites/pesquisa-B.md (item 6)", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md", "scratchpad/hunter/sites/PROPOSTA/skills/sites/deploy-to-vercel/ORIGEM.md, APRENDIZADOS.md, LICENCA-NOTA.md"]
---
# deploy-to-vercel

## O que é
Skill da Vercel para colocar o site no ar. Confere o estado do projeto, escolhe o caminho (enviar pelo git ou usar a linha de comando da Vercel) e publica **sempre em preview** primeiro. Produção só se o Daniel pedir.

## O que aproveitamos
- Entra **intacta**: `SKILL.md` e `resources/` (`deploy.sh`, `deploy-codex.sh`) mais um `Archive.zip`.
- Roteiro: 1) conferir git remote, pasta `.vercel/` e `vercel whoami`; 2) escolher o caminho; 3) publicar em preview; 4) **perguntar antes de dar push**.
- Onde entra: passo 10 "publicar" do fluxo da pesquisa A (o SEO vem depois, no passo 11), **depois** da aprovação do Daniel. Antes dele rodam `webapp-testing` e `web-quality-audit`.

## O que NÃO aproveitamos e por quê
- `Archive.zip`: cópia antiga (março/2026) com lixo `__MACOSX`. É redundante e **não deve ser usado**; os arquivos soltos são os atuais. Entra só porque a regra é copiar intacto.
- A opção "sem login" (`deploy.sh`) só com o "sim" do Daniel (ver riscos).
- Não instalada: `vercel-cli-with-tokens` (deploy com token, mesmo repositório).

## Riscos e cuidados
- **Envia dados para fora:** `resources/deploy.sh` empacota o projeto (tira `.env`, `.git` e `node_modules`) e envia para `claude-skills-deploy.vercel.com`, que devolve um link de preview e um link para "reivindicar" o site. `deploy-codex.sh` envia para `codex-deploy-skills.vercel.sh`. Só usar com o "sim" do Daniel e depois de conferir que não há chave em nenhum arquivo.
- **Plano pago:** o plano Hobby da Vercel é grátis, mas é para uso pessoal e **não comercial**. Site de cliente pede o Pro (pago). Avisar o Daniel.
- **Push:** a skill pergunta antes de `git add . && git commit && git push`, mas no nosso repositório vale a regra de nunca dar push sem aprovação.
- **Chave:** o token da Vercel nunca vai para o repositório (fica em `/root/.config/`).
- Licença só no README: ver `LICENCA-NOTA.md` na proposta.

## Transcrição: o que aprendemos (nas nossas palavras)
- Ordem segura de publicar: ver onde o projeto está ligado, escolher entre git e linha de comando, mandar para **preview**, mostrar o link, e só então pensar em produção.
- Preview é um endereço de teste: dá para o Daniel aprovar antes de o site oficial mudar.
- Antes de qualquer push, perguntar. Antes de qualquer envio "sem login", perguntar mais ainda, porque o projeto sai do container.
- O pacote vem com dois scripts de envio, um para cada programa (Claude e Codex), com endereços diferentes.
- Conta grátis da Vercel não serve para trabalho de cliente: avisar cedo para não ter surpresa de plano.

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-A.md
- research_notes/Caça de skills 2026-10/sites/pesquisa-B.md
- research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md
- https://github.com/vercel-labs/agent-skills (citado nas pesquisas; não reaberto)
