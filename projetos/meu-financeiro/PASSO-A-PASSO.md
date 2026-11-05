# Meu Financeiro: passo a passo

App Android privado de gestão financeira pessoal do **Antônio Felipe** (nome correto do usuário; a 1.0 dizia "Daniel" por engano), feito **do zero**. Nenhum código, tela ou banco dos apps financeiros antigos do GitHub dele foi aberto, copiado ou usado (minha-financa, Minhasfinancas e Finan-as ficaram intocados, a pedido dele).

- **Onde está o código:** pasta `meu-financeiro/` na branch `ccr-ed0fdef6-j49nzi` (não vai para a `main`).
- **APK pronto:** `meu-financeiro/apk/MeuFinanceiro-1.1.0.apk` (com o SHA-256 ao lado). A 1.0.0 está no histórico do git (commit `f9744da`).
- **Guia de instalação para o Daniel:** `meu-financeiro/README.md`.

## 1. Situação (05/11/2026): versão 1.0.0 entregue

| Item | Estado |
|---|---|
| Compilação release assinada (v2 + v3), zipalign ok, APK universal (todos os processadores) | feito e verificado com `apksigner` e `zipalign -c` |
| 48 testes automáticos | todos passando (`./gradlew testDebugUnitTest`) |
| App abre (Android 8, 11, 14 e 15 simulados no Robolectric, tela do tamanho do S23 FE) | testado |
| Dados continuam salvos depois de fechar (banco reaberto do disco) | testado |
| Instalação num aparelho ou emulador real | **não testada aqui**: o container não tem KVM, então o emulador não roda. Falta o Daniel instalar no S23 FE |
| Chamada real ao Claude (IA opcional) | compila e trata erros, mas **não foi testada com chave real** (não havia chave) |

## 2. Por que essas escolhas (arquitetura)

- **Kotlin nativo + Jetpack Compose + Room (SQLite).** É o caminho oficial do Android e o mais confiável para instalar e abrir. Nada de WebView ou framework híbrido.
- **ID próprio `br.com.daniel.meufinanceiro`**, sem split de APK, R8 desligado (bibliotecas com reflexão, como o SDK da Anthropic, quebram quando minificadas: "instala mas não abre"). minSdk 26, targetSdk 35.
- **Dinheiro sempre em centavos (Long).** Datas em epochDay, meses em yyyymm.
- **Saldos nunca são gravados**: são sempre recalculados a partir dos lançamentos (`domain/Ledger.kt`, `FinanceCalc`). Assim não existe saldo dessincronizado.
- **Toda gravação é uma transação única** (`data/FinanceRepository.kt`): compra parcelada + parcelas + saída da caixinha entram juntas ou nada entra.
- **Sem `fallbackToDestructiveMigration`**: uma mudança futura de banco exige migração de verdade, para nunca apagar dados.
- **Gráficos desenhados no próprio Compose** (sem biblioteca de gráficos), com cores validadas pelo script da skill `dataviz` e textura listrada nas barras de saída (verde × vermelho não depende só da cor).

### Pastas do código (`meu-financeiro/app/src/main/java/br/com/daniel/meufinanceiro/`)
| Pasta | O que tem |
|---|---|
| `data/` | Entidades Room, DAO, banco, dados iniciais (`Seed.kt`), repositório (todas as gravações) |
| `domain/` | Regras puras e testadas: `Money`, `Dates`, `Rules` (divisão 50/25/25, ciclo do cartão, parcelas, NFC), `Ledger` (calculadora), `QuickEntryParser` (lançamento por texto), `Insights` (analista local) |
| `ui/` | Tema claro/escuro, componentes, gráficos, navegação e telas |
| `ai/` | Análise opcional com Claude (SDK Java oficial da Anthropic, modelo `claude-opus-5-5`) |
| `backup/` | Backup JSON, restauração, cópias automáticas diárias, CSV e relatório HTML |
| `security/` | PIN (hash PBKDF2) e cofre da chave da API (Android Keystore) |

## 3. Regras financeiras (todas com teste em `app/src/test/`)
1. Entrada recebida → dividida pelas regras (padrão 50% Gastos pessoais, 25% Reserva, 25% Sair do Brasil). Sobra de centavo vai para a primeira caixinha. Pode "Não dividir" ou "Personalizar" em cada entrada.
2. Venda NFC paga → o **custo das plaquinhas volta para a caixinha Estoque NFC** e só o lucro é dividido. Compra de lote sai da caixinha Estoque NFC e aparece como "reinvestimento", não como gasto pessoal.
3. Débito/Pix/dinheiro → sai da conta e da caixinha. **Crédito → não mexe na conta**: entra na fatura e sai da caixinha na hora da compra.
4. **Pagar fatura** → sai da conta, não cria despesa (as compras já contaram).
5. Parcelado R$ 300 em 3x → 3 parcelas de R$ 100 em 3 faturas; nos relatórios, R$ 100 por mês.
6. Transferência entre contas, transferência entre caixinhas e ajuste de saldo **não são receita nem despesa**.
7. Trabalho/venda não paga fica "a receber" e **não conta como recebido** até registrar o pagamento.
8. Invariante conferida: `contas − dívida do cartão − caixinhas = dinheiro sem caixinha`.
9. Cartão Inter: melhor dia 14 e fechamento 20 (como o Daniel informou), **vencimento não inventado** (fica "a configurar"). O app avisa que 14/20 parece inconsistente e deixa escolher qual dia separa as faturas (padrão: melhor dia de compra).
10. Contas fixas (pai R$ 300, academia R$ 160, Spotify R$ 13, Muay Thai ~R$ 130, Claude a configurar, estoque NFC variável) começam **desativadas e sem vencimento**.
11. Reserva de emergência: meta = N meses × gasto médio, só quando houver 2 meses completos de dados (antes disso o app diz que faltam dados).

## 4. Comandos usados
```bash
# SDK (o container não tem Android SDK; foi baixado em /opt/android-sdk)
curl -sSLo cmd.zip https://dl.google.com/android/repository/commandlinetools-linux-16111833_latest.zip
sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0"
# Build e testes (na pasta meu-financeiro/)
./gradlew testDebugUnitTest assembleRelease
# Verificação do APK
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
zipalign -c -P 16 4 app/build/outputs/apk/release/app-release.apk
aapt2 dump badging app/build/outputs/apk/release/app-release.apk
# Prints das telas para revisão visual
./gradlew testDebugUnitTest --tests '*ScreenshotTest*' -Dscreens=true
```

## 5. Aprendizados (para não repensar da próxima vez)
- **Maven Central dá erro 429 neste container.** Use o espelho do Google `https://maven-central.storage-download.googleapis.com/maven2/` no `settings.gradle.kts` e, para o Robolectric, a propriedade `robolectric.dependency.repo.url` com o mesmo endereço.
- **Sem KVM não há emulador.** O teste de "abre?" foi feito com Robolectric + Compose UI Test rodando a `MainActivity` de verdade (Application, banco em disco e navegação reais).
- **Robolectric cria um Application novo a cada teste**, e lendo o banco por `ApplicationProvider` o teste pegava a instância errada. Nos testes de interface, pegue o repositório pelo `ViewModelProvider(rule.activity)`.
- `captureToImage()` trava no Robolectric. Para prints, desenhe a `decorView` num `Bitmap` com `@GraphicsMode(NATIVE)`.
- SDK Java da Anthropic 3.2.0: a exceção de chave inválida é `UnauthorizedException` (não `AuthenticationException`).
- Vermelho × verde no tema escuro reprova no teste de daltonismo. Tons usados nos gráficos: escuro `#1E9C6C`/`#DB6655`, claro `#0B8A57`/`#C9483E`, mais a textura listrada nas saídas.

## 6. Chave de assinatura (IMPORTANTE)
- Fica **fora do repositório**: `/root/.config/meufinanceiro/release.jks` + `keystore.properties` (este container) e com o Daniel (arquivo enviado no chat).
- Sem ela, uma versão nova não instala por cima. Seria preciso desinstalar e perder os dados, a não ser que o Daniel tenha um backup.
- Em container novo: peça os dois arquivos ao Daniel, coloque em `/root/.config/meufinanceiro/` e copie o `keystore.properties` para `meu-financeiro/`.

## 7. Próximos passos
1. Daniel instala no S23 FE e confirma que abre (checklist no README).
2. Daniel confere no app do Inter o fechamento e o vencimento reais e ajusta em Cartão → Ajustes.
3. Testar a análise com Claude usando uma chave real.
4. Ideias para a versão 1.1: lembrete por notificação de contas a vencer, widget de lançamento rápido, metas com data-alvo (Sair do Brasil em 3 a 5 anos), mais de um cartão na tela do cartão.

## 8. Habilidades usadas
- `dataviz` (plataforma): cores e regras dos gráficos, validação de daltonismo.
- `claude-api` (plataforma): integração com o Claude (SDK Java, modelo, erros).
- `skills/design/ui-ux-pro-max/SKILL.md`: princípios de app "Operar" (leitura em cima, ação embaixo, alvos de toque de 48dp, barra inferior com até 5 itens).
- Avaliada, mas **não adicionada** (precisa de aprovação): `impeccable` (fork no GitHub do Daniel), skill de design de interface que também cobre telas Android nativas.

---

## 9. Versão 1.1.0 (05/11/2026): atualização pedida depois do uso real

A 1.0.0 instalou e abriu no S23 FE. A 1.1.0 foi feita **sobre** a 1.0 (sem reconstruir) e **instala por cima**: mesmo `applicationId` e mesma chave (SHA-256 do certificado `41535a7e…`), com `versionCode` 2.

### Proteção dos dados (prioridade máxima)
- Banco v1 → v2 com `MIGRATION_1_2` que **só acrescenta**: `income_sources.isEarned` (padrão 1), `boxes.accountId`, `transactions.loanId`, `card_purchases.workId/sourceId` e as tabelas `loans`, `chat_messages` e `assistant_actions`. Nenhuma linha é alterada ou apagada.
- Antes de abrir o banco, o app copia os arquivos brutos (`.db`, `-wal`, `-shm`) para `files/backups/banco-antes-da-v1.1/` (uma vez).
- `Seed.upgrade` (idempotente, marcado por `data_version=2`): cria as fontes "não renda" (família, empréstimo, ajuda, reembolso), as categorias de material NFC e o nome, e liga as caixinhas sem banco ao Nubank. Mostra o card "Confira seus saldos" em vez de corrigir algo sozinho.
- Testes: `MigrationTest` cria um banco com o **esquema v1** e dados reais, migra, valida o esquema e confere que todas as linhas e todos os saldos são idênticos. Também testa a cópia bruta.

### Correção crítica: saldo livre × reservado × patrimônio
- **Causa do erro na 1.0:** a tela inicial mostrava "Saldo total disponível" = soma das contas, e as caixinhas eram envelopes virtuais nunca descontados.
- **Agora (`FinanceCalc.position()`)**, uma única fonte de verdade, usada no Início, nos relatórios, nos alertas e na assistente:
  - livre = contas − caixinhas (cada uma no mínimo 0) − cartão comprometido;
  - reservado = caixinhas;
  - patrimônio = contas − cartão − empréstimos.
- O saldo da conta é o **total no banco, incluindo as caixinhas**. Cada caixinha diz em qual banco fica (`accountId`), e a tela de contas mostra "em caixinhas / fora delas".
- A antiga caixinha "Gastos pessoais" virou o próprio saldo livre: os 50% ficam livres, sem movimento de caixinha, e as despesas pessoais não saem de caixinha. Os movimentos antigos dela continuam no banco, só são ignorados.

### Lucro real e rentabilidade
- Despesas e compras no cartão podem ser **custo de um trabalho** (`workId`/`sourceId`). `profitability(period)` calcula, por fonte, faturamento, custos, lucro, margem, horas, lucro por hora e o que falta receber.
- NFC: lucro = faturamento − custo das unidades vendidas − custos extras (o material entra pelo custo médio dos lotes).
- Entradas de família, empréstimo, ajuda e reembolso **não são renda**: não entram no lucro nem na divisão 50/25/25. Empréstimos têm controle de devolução (`LOAN_PAYMENT`, que não é despesa).

### Assistente com ferramentas (`assistant/`)
- `AssistantTools`: 22 ferramentas reais. Consultas rodam direto. Registros simples só rodam direto se "registros simples sem confirmação" estiver ligado. Edição, exclusão, metas, limites, nome e empréstimo **sempre** pedem confirmação, mostrando o valor anterior e o novo. Tudo fica em `assistant_actions`, com dados para **desfazer**.
- `Assistant`: com a chave da API, usa o Claude (`claude-opus-5-5`, tool calling, até 8 rodadas por mensagem). Sem chave, usa o **modo offline**, com as mesmas ferramentas e regras. A conversa fica salva (`chat_messages`).
- Alertas (`domain/Alerts.kt`): 9 tipos, cada um pode ser desligado. Os que comparam com a média exigem histórico mínimo. Notificação no celular opcional (WorkManager a cada 12 h, no máximo 1 por dia).

### Testes da 1.1 (77 no total, todos passando)
- Os **6 cenários pedidos**: caixinhas 42/170/212, Tio Rex 80−15−5 = 60, plaquinha 89,90−14 = 75,90, empréstimo de 30 da mãe, compra de 100 no crédito, e "Quanto posso gastar?" na assistente.
- Migração (3), assistente (11: confirmação, desfazer, ambiguidade, custo ligado ao trabalho), lançamento rápido v2 (9) e abertura do app em 4 versões do Android com navegação por todas as telas.

### O que NÃO foi testado
- Instalação real por cima da 1.0 no celular (não há emulador aqui). A migração foi testada com um banco no formato v1.
- Chamada real ao Claude (não havia chave). A montagem das ferramentas para a API foi testada sem enviar.

### Aprendizados novos
- `MigrationTestHelper` no Robolectric só acha os JSON do esquema se estiverem nos assets do **debug** (`sourceSets["debug"].assets.srcDir("schemas")`).
- `@ColumnInfo(defaultValue = "1")` é obrigatório quando a migração usa `DEFAULT 1`, senão a validação do Room falha.
- SDK Java da Anthropic: `jsonMapper()` fica em `com.anthropic.core` (função de topo), e `params.addMessage(response)` devolve a resposta inteira ao histórico do loop de ferramentas.
- Push de tag é recusado pelo proxy do container: use o histórico da branch como marco.
