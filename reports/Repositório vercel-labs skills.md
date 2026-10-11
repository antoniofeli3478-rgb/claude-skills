# Relatório: repositório `vercel-labs/skills` (o "caçador de skills")

**Repositório:** https://github.com/vercel-labs/skills
**Versão analisada:** pacote `skills` 1.7.2, commit `13e4063` de 09/10/2026.
**Licença:** MIT.
**Decisão do Daniel (11/10/2026):** instalar a `find-skills` na categoria nova `skills/ferramentas-de-skills/` e corrigir as 2 skills que o programa não achava. Feito no mesmo dia.
**Analisado em:** 11/10/2026. Baixei o repositório e testei a ferramenta aqui no container.

---

## 1. O que é, em uma frase

É o **"gerenciador de pacotes" de skills**: um programa de linha de comando (`npx skills`) que **procura, instala, atualiza e remove skills** de qualquer repositório do GitHub, para o Claude Code e mais 78 assistentes de IA (Cursor, Codex, Gemini CLI, Copilot etc.).

O repositório **não é uma coleção de skills**. Ele tem **uma skill só**, a `find-skills`, e o resto é o código do programa.

Ele trabalha junto com o site **https://skills.sh**, um catálogo público com o ranking de skills por número de instalações.

---

## 2. O que tem dentro

| Item | O que é | Serve para nós? |
|---|---|---|
| **`skills/find-skills/SKILL.md`** | A skill "caçadora": ensina a IA a procurar uma skill pronta antes de fazer do zero, a conferir se ela é confiável e a mostrar as opções ao usuário. **É a skill mais instalada do catálogo (3,8 milhões).** | **Sim**, é o que você chamou de caçador de skills. |
| **Programa `npx skills`** (pasta `src/`) | Os comandos `find`, `add`, `use`, `list`, `update`, `remove` e `init`. | **Sim**, como ferramenta (não precisa ficar no repositório). |
| `.factory/workflows/…` (triage, pr-labeling) | Skills internas que a Vercel usa para revisar os PRs do próprio projeto. | Não. |
| `AGENTS.md`, testes, scripts | Instruções para quem desenvolve o programa. | Não. |

**Não há uma skill "skill creator" neste repositório.** O comando `npx skills init` só cria um modelo vazio de `SKILL.md`. A skill de criar skills mais usada é a `anthropics/skills@skill-creator` (402 mil instalações), que **já temos disponível** pela plataforma como `anthropic-skills:skill-creator`.

---

## 3. A skill `find-skills`, explicada

Ela entra em ação quando o pedido parece algo que já pode existir pronto ("tem alguma skill para X?", "como faço X?"). O passo a passo dela:

1. Entender o pedido: a área (React, vídeo, design...) e a tarefa.
2. Olhar primeiro o ranking do skills.sh.
3. Procurar com `npx skills find <palavras>`.
4. **Conferir a qualidade antes de recomendar:**
   - de preferência mais de 1.000 instalações, e desconfiar de menos de 100;
   - autor conhecido (vercel-labs, anthropics, microsoft...);
   - repositório com mais de 100 estrelas no GitHub.
5. Mostrar ao usuário o nome, o que faz, as instalações, o comando e o link.
6. Instalar só se o usuário quiser.
7. Se nada servir, fazer a tarefa direto e sugerir criar uma skill própria.

**Combina com as nossas regras:** ela já manda mostrar ao usuário antes de instalar. O ponto em que difere de nós é o lugar da instalação: ela instala direto no assistente, e nós guardamos tudo no repositório.

---

## 4. Os comandos do programa (testados aqui)

| Comando | O que faz | Testei? |
|---|---|---|
| `npx skills find <palavra>` | Procura no catálogo e mostra o número de instalações e o link | Sim, funciona no container |
| `npx skills add <dono/repo> --list` | Lista as skills de um repositório sem instalar | Sim, inclusive no **nosso** |
| `npx skills add <dono/repo> --skill <nome>` | Instala uma skill | Não (só com aprovação) |
| `npx skills use <dono/repo@skill>` | Usa uma skill sem instalar | Não |
| `npx skills update` / `remove` / `list` | Atualizar, remover, listar instaladas | Não |
| `npx skills init <nome>` | Cria um `SKILL.md` modelo | Não |

**Exemplos reais de busca** (instalações em 11/10/2026):

- **"remotion":** `remotion-dev/skills@remotion-best-practices` (599 mil, **a skill oficial do Remotion**), `google-labs-code/stitch-skills@remotion` (40 mil).
- **"copywriting":** `coreyhaines31/marketingskills@copywriting` (221 mil). É **a mesma que já temos**, e o mesmo autor tem `copy-editing`, `ad-creative`, `marketing-psychology` e `content-strategy`.
- **"video"/"motion":** a família `heygen-com/hyperframes` (700 mil+), `leonxlnx/taste-skill@high-end-visual-design` (428 mil).
- **"skill creator":** `vercel-labs/skills@find-skills` (3,8 milhões) e `anthropics/skills@skill-creator` (402 mil).

---

## 5. Descoberta importante: testei o **nosso** repositório com o programa

Rodei `npx skills add` apontando para o nosso `claude-skills`. Ele encontrou **10 das 12 skills**. Duas não aparecem:

1. **`remotion-video`: erro no cabeçalho do `SKILL.md`.** A descrição tem dois-pontos no meio do texto ("…(React renderizado quadro a quadro): promos…"), o que quebra o formato YAML.
   - O Claude Code pode estar lendo assim mesmo, mas outros programas recusam.
   - **Correção:** pôr a descrição entre aspas. Isso muda o `SKILL.md`, então preciso da sua aprovação. Posso incluir na proposta das 4 skills que está esperando.
2. **`motion-video`: escondida.** O `SKILL.md` dela fica dentro de uma pasta oculta (`.claude/skills/motion-video/`), como veio no kit original, e o programa não procura lá.
   - Ela continua funcionando pelo nosso índice. Se quisermos que apareça, dá para pôr um `SKILL.md` curto na pasta da skill apontando para o original.

Com essas duas correções, **qualquer pessoa ou IA poderia instalar as nossas skills com um comando**:

```bash
npx skills add danielvieirasilva0257-droid/claude-skills --list
npx skills add danielvieirasilva0257-droid/claude-skills --skill voz-pt-br
```

O programa encontrou as skills mesmo dentro das pastas de categoria (`skills/audio/...`), então **a organização nova é compatível**.

---

## 6. Recomendação: como trazer para o nosso repositório

**Proposta (precisa da sua aprovação):**

1. **Instalar a skill `find-skills`** em `skills/ferramentas/find-skills/`.
   - É uma **categoria nova**: "ferramentas", para skills que ajudam a trabalhar com outras skills. Precisa da sua aprovação.
   - Ela entra sem alteração e com o `LICENSE` (MIT), um `ORIGEM.md` (de onde veio e a versão) e um `APRENDIZADOS.md` com as nossas regras:
     - procurar primeiro no nosso índice, e só depois no catálogo;
     - nunca instalar direto no assistente: mostrar ao Daniel, e depois de aprovado salvar no repositório pela receita "instalar skill de terceiros" do manual.
2. **Registrar o programa `npx skills`** no `LEIA-ANTES-DE-USAR.md` e no índice, como a forma oficial de **procurar** skills novas. Ele não precisa ser instalado: roda com `npx`.
3. **Corrigir as duas skills** que o programa não enxerga (seção 5), junto com a proposta pendente.
4. **Candidatas** para olhar depois. Cada uma seria apresentada antes, como sempre:
   - `remotion-dev/skills@remotion-best-practices` (oficial do Remotion; o nosso `remotion-video` já cita o `npx remotion skills add`);
   - `coreyhaines31/marketingskills@ad-creative` e `@copy-editing` (mesmo autor da `copywriting` que já usamos).

**Cuidados:**

- **O programa instala direto** nas pastas dos assistentes (`.claude/skills/`) e pode usar a opção `-y`, que não pergunta nada. Pela nossa regra, **nada é instalado sem você ver antes**, e o lugar oficial das skills continua sendo o repositório.
- **Número de instalações não é garantia.** Skill de autor desconhecido é lida inteira antes, porque uma skill é um texto que a IA obedece.

---

## 7. Resumo para decidir

- **O que é:** o "npm das skills", com 1 skill, a `find-skills`, que é o caçador de skills.
- **Vale a pena:** sim, a `find-skills` e o comando de busca. Não tem "skill creator"; essa nós já temos pela plataforma.
- **Bônus:** o teste mostrou 2 skills nossas que outros programas não enxergam, com correção simples.

**Para responder:**

- "pode instalar a find-skills" (com a categoria nova `ferramentas`, ou diga outro nome);
- "pode corrigir as 2 skills" (entra junto com a proposta das 4);
- ou o que quer mudar.
