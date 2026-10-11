# Aprendizados: find-skills

Adendos datados. O mais novo fica embaixo. O arquivo oficial (`SKILL.md`) fica intacto.

## 11/10/2026: entrada na base

- **Para que serve:** o "caçador de skills". Antes de fazer algo do zero, procurar se já existe uma skill pronta no catálogo público https://skills.sh (ranking por número de instalações) com o programa `npx skills`.
- **REGRAS NOSSAS (valem mais que o texto original da skill):**
  1. **Procure primeiro no nosso índice** (`guia/HABILIDADES.txt`). Só vá ao catálogo se nada servir.
  2. **Nunca instale direto no assistente.** O original manda rodar `npx skills add ... -g -y`; aqui **não**. Mostre ao Daniel o nome, o que faz, o autor, as instalações e o link, e peça aprovação.
  3. **Depois de aprovada,** a skill entra no repositório pela receita "Instalar uma skill de terceiros" do `guia/LEIA-ANTES-DE-USAR.md`: na pasta da categoria certa, sem alterar o original, com `LICENSE`, `ORIGEM.md`, `APRENDIZADOS.md` e linha no índice.
  4. **Leia a skill inteira antes de recomendar.** Número de instalações não é garantia: uma skill é um texto que a IA obedece.
- **Comandos testados no container (11/10/2026), todos funcionam:**
  ```bash
  npx -y skills@1.7.2 find remotion                 # procura no catálogo
  npx -y skills@1.7.2 add dono/repo --list          # lista as skills de um repositório, sem instalar
  ```
  Use uma pasta temporária vazia como diretório de trabalho.
- **O nosso repositório também funciona com o programa:** `npx skills add danielvieirasilva0257-droid/claude-skills --list` lista as nossas skills, inclusive dentro das pastas de categoria.
  - Em 11/10/2026 ele não achava duas: `remotion-video` (dois-pontos na descrição quebravam o YAML; corrigido com a descrição entre aspas) e `motion-video` (o `SKILL.md` ficava só na pasta oculta `.claude/`; corrigido com um `SKILL.md` de entrada na pasta da skill).
  - **Regra:** ao criar ou mudar uma skill, coloque a `description` entre aspas se tiver dois-pontos, e confira com o comando `--list` acima.
- **Achados da primeira busca (11/10/2026), candidatas para avaliar com o Daniel:** `remotion-dev/skills@remotion-best-practices` (oficial do Remotion, 599 mil), `coreyhaines31/marketingskills@ad-creative` e `@copy-editing` (mesmo autor da nossa `copywriting`). Relatório completo: `reports/Repositório vercel-labs skills.md`.
