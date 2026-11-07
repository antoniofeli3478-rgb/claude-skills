# Rô Salgaderia (Serrana-SP), raw research notes, 2026-11-07

User asked for "Arroz Salgaderia" (voice dictation); confirmed by user: **Rô Salgaderia**.
Final report (pt-BR): `reports/Rô Salgaderia, pesquisa (2026-11-07).md`.

## Search log
- "Arroz Salgaderia" in any spelling: zero hits (only rice recipes). Don't search that again.
- "Salgaderia Serrana instagram" surfaced `@rosalgaderia · Serrana, SP` → the lead.
- DuckDuckGo html (anomaly page), Google (JS page), Brave (429), Mojeek (403): unusable from this container.
- Restaurant Guru: 503 "Suspicious activity". iFood: 403. Econodata: 403. Facebook: 400. UaiRango: 525 (Cloudflare origin error). Instagram: login wall.
- Instagram private API (i.instagram.com web_profile_info): blocked by the environment's safety classifier; do not retry.
- What worked: WebSearch (extended), BrasilAPI CNPJ, Guia Fácil (curl), Nominatim (OSM).

## Facts and sources
- CNPJ 42.567.852/0001-05 (BrasilAPI `https://brasilapi.com.br/api/cnpj/v1/42567852000105`):
  razão social NAGIB DE ALMEIDA ISSA; fantasia RO SALGADERIA; ATIVA; start 2021-07-02;
  CNAE 5611203 (lanchonetes...), secondary "Fornecimento de alimentos preparados preponderantemente para consumo domiciliar";
  MICRO EMPRESA; capital R$ 5000; Empresário (Individual); Simples=True, MEI=False; bairro CENTRO, CEP 14150000, street blank.
- Econodata (via search snippet): RO SALGADERIA, Rua Nossa Senhora das Dores, 814 - Centro; administrator Nagib de Almeida Issa.
  https://www.econodata.com.br/consulta-empresa/42567852000105-NAGIB-DE-ALMEIDA-ISSA
- Instagram @rosalgaderia (search snippet): "Dedicação em família desde 1988"; "Unidade 1 Rua Vicente de Paula Lima 435 / Unidade 2 Rua nossa Senhora das Dores 814 / Encomendas Aqui"; ~1,470 followers, 108 posts. https://www.instagram.com/rosalgaderia/
- Instagram @romassasesalgados (snippet): "Desde 1986 levando sabor com os melhores salgados em Serrana"; "Encomendas para #festa WhatsApp (16) 99240-8408". https://www.instagram.com/romassasesalgados/
- Facebook "Rô Massas & Salgados": "há mais de 30 anos oferecendo os melhores salgados e deliciosas massas para o seu almoço". https://www.facebook.com/romassasesalgados/
- Guia Fácil (fetched): "Rô Massas & Salgados | Lanchonetes | Serrana, SP (16) 99240-8408", R. Vicente de Paula Lima. https://guiafacil.com/encontre/lanchonetes/serrana/sp
- Restaurant Guru (snippets): 2.1/5, 261 reviews, 60 photos; R. Vicente de Paula Lima, 435; price R$40–60/person; dishes coxinha, pão de queijo, pastéis, queijadas, café ("bastilla" is likely an auto-tag); Google 4.7, Facebook 4.6; earlier list 11/2025: 3.3 with 319 votes; 09/2026: 2.1 with 263 votes; reviewer: family >35 years in the trade. https://restaurantguru.com.br/Ro-Massas-and-Salgados-Serrana
- iFood (snippets): "ro-massas--salgados-centro", aggregateRating 4.9 / 98, min order R$ 10, item Fanta Laranja 350ml.
  https://www.ifood.com.br/delivery/serrana-sp/ro-massas--salgados-centro/81e1808c-280d-4542-9e7e-02783d4a1bb8
- UaiRango: "Rô Massas e Salgados", Rua Nossa Senhora das Dores, 814 - Centro; rating 0 / 11 reviews. https://www.uairango.com.br/sp/romassas-serrana
- anota.ai "Ro Salgados" (https://pedido.anota.ai/loja/ro-salgados-1): appeared in search, NOT confirmed as the same shop.

## Geography (Nominatim / OSM, 2026-11-07)
- Igreja Matriz de Nossa Senhora das Dores: -21.21188, -47.59596, Rua Barão do Rio Branco 339, Centro.
- Praça Armando Sales de Oliveira (= Praça Matriz, has "Fonte da Praça Matriz"): polygon lon -47.59685..-47.59563, lat -21.21234..-21.21155.
- Rua Nossa Senhora das Dores: LineString lon -47.60311..-47.59356, lat -21.21295..-21.21108 → crosses/borders the square band. #814 consistent with "em frente à Praça Matriz" (house number not in OSM).
- Rua Vicente de Paula Lima (centre segment): ~-21.2088, -47.5976, 2–3 blocks north.

## Other salgaderias in Serrana (not Rô; competitors)
- Salgaderia Dovana, Av. Deolinda Rosa 1014 (Tripadvisor, Facebook).
- Salgaderia Mini Rodoviária, R. Vicente de Paula Lima 1685 (Restaurant Guru 4.7).
- Salgaderia Bom Apetite (Facebook), Dyé Salgados (iFood, R. Ezídio Uzuelli 206, Jd. Paranoá).

## Open questions for the owner
Founding year (1986 vs 1988), who "Rô" is, hours per unit, current menu + prices, cento de salgados price, own delivery?, photos, logo files, which Instagram is official.
