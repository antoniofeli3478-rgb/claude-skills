# Aprendizados: motion-oficial

Adendos datados. O mais novo fica embaixo. Os arquivos oficiais ficam intactos.

## 09/10/2026: entrada na base

- **Para que serve:** guia de boas práticas para animar com o Motion 14 (antigo Framer Motion). Leia o `best-practices/index.md` e depois o `best-practices/react.md` (ou `motion.md`, `vue.md`, `base-ui.md`).
- **Quando usar com as nossas skills:**
  - `button-animation` traz receitas prontas e testadas.
  - Esta traz as regras oficiais e a forma de consultar a documentação.
  - Para animar com Motion, abra primeiro esta e depois a nossa.
- **Servidor de documentação** (grátis, sem conta):
  - Endereço: `https://mcp.motion.dev`.
  - O instalador oficial registra o endereço como servidor HTTP chamado `motion`.
  - Comando equivalente no Claude Code: `claude mcp add --transport http motion https://mcp.motion.dev`. **Ainda não foi testado neste container.**
  - Ferramentas: `search-motion-docs` (busca na documentação e nos exemplos) e `generate-css-easing` (molas e quiques em CSS).
- **Sem o servidor:** a pasta `best-practices/` funciona sozinha, e o `https://motion.dev/llms.txt` indexa toda a documentação.
- **Partes pagas (Motion+):**
  - código-fonte dos exemplos e do Motion UI;
  - auditoria MotionScore;
  - editor visual de transições.
  - Quando algo disso for pedido, diga isso com clareza e não reconstrua o código de memória, como a própria skill manda.
- **Regra de ouro oficial:** nunca importar de `framer-motion`. Use `motion/react` (ou `motion/react-client` em server components) e não deixe os dois pacotes instalados juntos.
- **Instalação em outro projeto:** `npx motion-ai` instala a skill e configura os servidores sozinho.
