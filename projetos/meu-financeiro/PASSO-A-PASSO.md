# Meu Financeiro: passo a passo

App Android privado de gestão financeira pessoal do Daniel, feito **do zero**. Nenhum código, tela ou banco dos apps financeiros antigos do GitHub dele foi aberto, copiado ou usado (minha-financa, Minhasfinancas e Finan-as ficaram intocados, a pedido dele).

- **Onde está o código:** pasta `meu-financeiro/` na branch `ccr-ed0fdef6-j49nzi` (não vai para a `main`).
- **APK pronto:** `meu-financeiro/apk/MeuFinanceiro-1.0.0.apk` (com o SHA-256 ao lado).
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
