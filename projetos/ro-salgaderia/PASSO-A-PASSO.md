# Projeto Rô Salgaderia (Serrana, SP): passo a passo

Este arquivo guarda só o que foi feito, como foi feito e onde estão as habilidades. Ele não tem cópia de skills nem código.

## Onde está cada coisa

- **Pesquisa sobre a empresa** (português): `reports/Rô Salgaderia, pesquisa (2026-11-07).md`.
- **Notas brutas e fontes:** `research_notes/Rô Salgaderia 2026-11/notas.md`.
- **Código do site:** ainda não existe. Quando começar, vai numa branch própria (por exemplo `ro-salgaderia-site`), nunca na `main`.

## Habilidades para o site (abra só a que precisar)

| Para quê | Habilidade | Caminho |
|---|---|---|
| Passo a passo completo do site, com as aprovações | combo-site-premium | `skills/combos/combo-site-premium/SKILL.md` |
| Escolher e baixar o template | referencia-sites | `skills/sites/referencia-sites/SKILL.md` |
| Planejar páginas e seções | site-architecture | `skills/sites/site-architecture/SKILL.md` |
| Textos (título, botão do WhatsApp, história) | combo-copy-humanizada | `skills/combos/combo-copy-humanizada/SKILL.md` |
| Página que vende (pedido e encomenda) | cro | `skills/sites/cro/SKILL.md` |
| Visual (DESIGN.md) | ui-ux-pro-max | `skills/design/ui-ux-pro-max/SKILL.md` |
| SEO local (Google, "salgaderia em Serrana") | seo-audit e web-quality | `skills/sites/seo-audit/SKILL.md` · `skills/sites/web-quality/web-quality-audit/SKILL.md` |
| Prints no celular | webapp-testing | `skills/sites/webapp-testing/SKILL.md` |
| Hospedagem (cliente: nada de Vercel grátis) | notas | `research_notes/Caça de skills 2026-10/hospedagem/pesquisa.md` |

## O que já foi feito

1. **07/11/2026: pesquisa da empresa.**
   - O nome ditado foi "Arroz Salgaderia"; o certo é **Rô Salgaderia**.
   - Achei: CNPJ, as 2 unidades (a Unidade 2 fica na Rua N. Sra. das Dores, 814, de frente para a Praça Matriz), o WhatsApp (16) 99240-8408, as redes, o iFood, as notas e as avaliações. Está tudo no relatório.
2. **07/11/2026: baixei os repositórios de site.**
   - São 15 repositórios do GitHub, todos com licença MIT, cerca de 135 MB.
   - Ficaram **no container**, em `<scratchpad>/site-ro-salgaderia/referencias/_clones/`, fora do repositório (regra: links em vez de cópias).
   - O container é temporário. Para baixar de novo numa sessão nova, use os comandos abaixo.

## Repositórios baixados (commit de 07/11/2026)

Os 9 do `fontes.txt` (commit fixado):

| Nome | Para quê |
|---|---|
| launch-ui | landing page em Next 16 |
| portfolio-magicui | portfólio animado |
| astrowind | site institucional em Astro, a **melhor base para a Rô** |
| saas-starter | SaaS com login e pagamento |
| commerce | loja |
| once-ui-starter | starter minimalista |
| shadcn-landing | landing simples |
| page-ui | componentes de landing (42 MB) |
| awesome-design-md | fichas DESIGN.md de marcas |

Os componentes da seção B da referencia-sites (MIT, último commit):

| Repositório | Commit |
|---|---|
| magicuidesign/magicui | `37ce829f18dd7d0a7219531a4249b664419933ea` |
| kokonut-labs/kokonutui | `9976111a38abe05a47229867611db02646b597ef` |
| markmead/hyperui | `637323c8d3046645ac5f28edc2074d18f22a2b94` |
| themesberg/flowbite | `232ebdb33a9e37b31b293b9988d89b862ee121e5` |
| satnaing/astro-paper | `6f61e2dc0e45bbcefc05f4055f293cd7f81bcb03` |
| shadcnstudio/shadcn-studio | `72a20331970218848103184b61e1efa9d0e08dea` (só a parte aberta é MIT) |

Ficaram de fora de propósito:

- `vercel/examples`: enorme, e serve só para integrações;
- os pagos, os restritos e os sem licença da tabela D (só link).

Comandos para baixar de novo:

```bash
D=<pasta-do-projeto>/referencias/_clones; mkdir -p $D
S=<claude-skills>/skills/sites/referencia-sites/scripts/baixar-referencia.sh
for n in launch-ui portfolio-magicui astrowind saas-starter commerce once-ui-starter shadcn-landing page-ui awesome-design-md; do bash $S $n $D; done
cd $D && for r in magicuidesign/magicui kokonut-labs/kokonutui markmead/hyperui themesberg/flowbite satnaing/astro-paper shadcnstudio/shadcn-studio; do git clone -q --depth 1 https://github.com/$r.git; done
```

## Aprendizados

- **Nome ditado por voz pode vir errado.** Se a busca exata não achar nada, procure nomes com som parecido ("a Rô" → "Arroz").
- **Daqui do container**, Google, DuckDuckGo, Brave, iFood, Restaurant Guru, Facebook e Instagram bloqueiam o acesso automático. O que funcionou:
  - WebSearch (modo extended);
  - BrasilAPI (CNPJ);
  - Guia Fácil;
  - OpenStreetMap (Nominatim), para conferir o endereço.
- **Não tente ler o Instagram por API interna.** O ambiente bloqueia.

## Próximo passo

1. Pedir à dona: cardápio com preços, horários, fotos, logo, o ano certo de fundação (1986 ou 1988) e qual Instagram é o oficial.
2. Se o Daniel aprovar o site, seguir o `combo-site-premium` com o `astrowind`: uma página, botão de WhatsApp, cardápio, encomenda de festa, as 2 unidades com mapa e SEO local.
