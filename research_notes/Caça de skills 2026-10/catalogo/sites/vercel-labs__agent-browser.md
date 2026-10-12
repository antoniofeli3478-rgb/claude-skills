---
nome: "agent-browser"
repositorio: "vercel-labs/agent-browser"
caminho_no_repo: "não verificado"
autor: "Vercel Labs"
licenca: "Apache-2.0"
instalacoes: "1 milhão (skills.sh find, 11/10/2026)"
commit_ou_versao: "10/10/2026 (hash não consta na pesquisa); a next-dev-loop pede a versão 0.31.1 ou mais nova"
data_verificada: "2026-10-11"
categoria_nossa: "sites"
nota: 4
status: "so-referencia"
onde_entrou: "—"
fontes_locais: ["research_notes/Caça de skills 2026-10/sites/pesquisa-A.md (tabela e 'Não recomendadas agora')", "research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md (ficaram só como referência)"]
---
# agent-browser

## O que é
Um "robô de navegador" da Vercel Labs, para o agente abrir páginas, clicar e ver o resultado. Tem 1 milhão de instalações, a maior da lista. Mas o `SKILL.md` é só um **"stub"** (um bilhete): manda baixar o conteúdo real do programa com `agent-browser skills get core`.

## O que aproveitamos
- Nada copiado. Aprendizado útil: o número de instalações sozinho não diz se a skill vale guardar.
- Fica anotado como dependência da `next-dev-loop` (que pede o `agent-browser` 0.31.1 ou mais novo).

## O que NÃO aproveitamos e por quê
- Guardar a skill "intacta" no repositório **não guarda nada útil**: o conteúdo de verdade vem do programa, na hora.
- Exige um **binário em Rust** e o Chrome.
- Já temos o caminho para "ver o site" com `webapp-testing` (proposta) e com a Playwright CLI (ficha de outra agente).
- Nota 4.

## Riscos e cuidados
- **Baixa e executa conteúdo de fora:** o stub manda buscar o conteúdo real por comando do programa. Por isso, mesmo se instalada, o que ela ensina pode mudar sem ninguém ver.
- Exige instalar um binário e o Chrome no container; só com o "sim" do Daniel.
- Telemetria e envio de dados: não verificado.

## Transcrição: o que aprendemos (nas nossas palavras)
- Skill "stub": o arquivo é uma casca que aponta para o programa. Vale checar o `SKILL.md` antes de contar instalações.
- Pesquisa A: ferramenta popular, licença Apache-2.0, atualizada em 10/10/2026.
- É requisito de outra skill (a `next-dev-loop`), o que pode obrigar a instalá-la no futuro.
- Detalhes do programa: não verificado.

## Fontes
- research_notes/Caça de skills 2026-10/sites/pesquisa-A.md
- research_notes/Caça de skills 2026-10/sites/TRANSCRICAO.md
