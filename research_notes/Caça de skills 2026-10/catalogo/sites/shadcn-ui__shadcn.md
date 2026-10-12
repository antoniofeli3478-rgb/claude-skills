---
nome: "shadcn (skill)"
repositorio: "shadcn-ui/ui"
caminho_no_repo: "skills/shadcn"
autor: "shadcn"
licenca: "MIT (LICENSE.md da raiz do repositório)"
instalacoes: "372,3 mil (skills.sh find, 11/10/2026)"
commit_ou_versao: "02cad61166c4c20edbc8a3f71bd720eed4c1f49b (11/10/2026)"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: 9
status: "instalada-intacta"
onde_entrou: "skills/sites/shadcn/ (cópia intacta) (instalada na main em 12/10/2026, PR #21)"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-A.md (tabela e seção 3.2)", "research_notes/Caça de skills 2026-10/sites/pesquisa-B.md (item 4 e 5)", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md", "scratchpad/hunter/sites/PROPOSTA/skills/sites/shadcn/ORIGEM.md, APRENDIZADOS.md"]
---
# shadcn (skill)

## O que é
A skill oficial do shadcn/ui. Ensina o Claude a montar a interface do site com os componentes do shadcn (React + Tailwind): botões, formulários, menus, temas. Usa a ferramenta de linha de comando oficial do shadcn. **Não é o MCP do shadcn/ui**: esse tem ficha própria, feita por outra agente.

## O que aproveitamos
- Entra **intacta**: `SKILL.md`, `cli.md`, `customization.md`, `design-system.md`, `design-system-page.md`, `mcp.md`, `registry.md`, `rules/` (6 arquivos), `assets/`, `scripts/`, `agents/` e `evals/` (21 arquivos, 220 KB).
- O `design-system.md` monta o design system **a partir de um `DESIGN.md`**: mapeia os tokens para as variáveis do shadcn e gera uma página de conferência com contraste. É o que sai do passo 3 da `ui-ux-pro-max`.
- Regras com exemplo errado/certo em `rules/`: `gap-*` em vez de `space-y-*`, cores só por token (`bg-primary`), formulário com `FieldGroup` e `Field` e `aria-invalid` (`forms.md`), Dialog sempre com título, Button sem `isLoading` (usar `Spinner`).
- Tabela "qual componente usar para cada coisa".
- Onde entra: passo 4 "projeto e componentes" do `combo-site-premium`, depois do `DESIGN.md` aprovado.

## O que NÃO aproveitamos e por quê
- Ficou de fora a outra skill da mesma pasta, `migrate-radix-to-base` (migração de projetos antigos; não faz falta agora).
- Divisão com as nossas: a estrutura do componente é do `shadcn`; a animação do botão é da `button-animation`; molas e curvas são da `motion-oficial`. A skill não decide cor, fonte nem estilo.
- Blocos e registries de terceiros (Magic UI, Launch UI, Kokonut UI) não vêm junto: ver `colecoes-de-sites.md`.

## Riscos e cuidados
- **Executa um comando sozinha ao carregar:** o `SKILL.md` tem a linha `!npx shadcn@latest info --json`. O Claude Code roda isso automaticamente. Baixa a ferramenta na versão `@latest` (sem versão fixa) e lê o projeto.
- O cabeçalho pré-aprova `npx shadcn@latest *` (`allowed-tools`).
- Precisa de Node e de rede. Não é paga.
- Regra nossa: carregar a skill **só dentro da pasta do projeto do site**.
- Registries de terceiros: ler o código antes de instalar (o próprio shadcn avisa).

## Transcrição: o que aprendemos (nas nossas palavras)
- A skill manda sempre usar a ferramenta oficial (`add`, `search`, `docs`, `info`) em vez de copiar código à mão.
- Estilo por **tokens**, nunca cor solta: o tema do site fica num lugar só e todo componente usa o mesmo.
- Espaço entre itens com `gap-*`; formulário sempre com `FieldGroup` e `Field`; erro marcado com `aria-invalid`.
- Acessibilidade vem embutida nas regras: Dialog sempre com título.
- Botão em carregamento usa `Spinner`, não uma propriedade `isLoading`.
- O arquivo `design-system.md` transforma um `DESIGN.md` em tema do shadcn e gera uma página para conferir contraste. Isso liga o nosso fluxo de design ao código.
- Pesquisa B: a ferramenta reconhece 435 registries da comunidade; revisar o código de terceiros sempre.

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-A.md
- research_notes/Caça de skills 2026-10/sites/pesquisa-B.md
- research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md
- https://ui.shadcn.com/docs/skills (citado na pesquisa B; não reaberto)
