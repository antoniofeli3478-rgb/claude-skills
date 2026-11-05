# Meu Financeiro

App Android privado de gestão financeira do Daniel. Feito do zero em Kotlin + Jetpack Compose + Room. Funciona sem internet e não envia dados para lugar nenhum (a única exceção é a análise opcional com o Claude, que você liga e dispara manualmente).

## Como instalar no celular (S23 FE ou qualquer Android 8 ou mais novo)

1. Baixe o arquivo `apk/MeuFinanceiro-1.0.0.apk` (18,7 MB).
2. **Confira se o download veio inteiro.** Este é o ponto que dava problema nos apps antigos:
   - o arquivo precisa ter **18.734.928 bytes** (o celular mostra 18,7 MB ou 17,9 MB, dependendo do app que exibe o tamanho);
   - se quiser conferir de verdade, o SHA-256 está em `apk/MeuFinanceiro-1.0.0.apk.sha256`;
   - se o tamanho for menor, apague o arquivo e baixe de novo (de preferência no Wi-Fi).
3. Toque no arquivo. Se o Android pedir, permita "Instalar apps desconhecidos" para o app que você usou para abrir o arquivo (Meus Arquivos ou o navegador).
4. Toque em **Instalar** e depois em **Abrir**.

O app tem um identificador próprio (`br.com.daniel.meufinanceiro`). Ele não conflita com nenhum app financeiro antigo, que pode continuar instalado ou ser apagado, tanto faz.

### Se aparecer "app não instalado" ou "conflito de pacote"
Só acontece se já existir um Meu Financeiro instalado com **outra assinatura** (por exemplo, uma versão de teste). Desinstale o antigo e instale de novo. Versões futuras assinadas com a mesma chave atualizam por cima, sem perder os dados.

## Primeiros passos dentro do app
1. **Configurações → Contas e saldos → Atualizar saldo** em cada conta (Nubank, Inter, dinheiro). O valor entra como "dinheiro sem caixinha".
2. **Caixinhas → Distribuir** para dizer quanto do que você já tem está em cada caixinha.
3. **Cartão → Ajustes**: confira o melhor dia de compra, o fechamento e **defina o vencimento**.
4. **Contas a pagar**: ative as contas que estão valendo e defina o vencimento de cada uma.
5. **Plaquinhas NFC → Novo lote → "Só registrar o estoque"**: cadastre as plaquinhas que você tem hoje.
6. **Configurações → PIN e biometria** (recomendado) e **Backup** (faça toda semana).

## Para desenvolvedor
- Build: `./gradlew assembleRelease` (precisa do Android SDK 35 e do `keystore.properties`, que **não** fica no repositório).
- Testes: `./gradlew testDebugUnitTest` (48 testes: cálculos, banco de dados, abertura do app no Android 8, 11, 14 e 15, navegação por todas as telas e persistência).
- Prints das telas: `./gradlew testDebugUnitTest --tests '*ScreenshotTest*' -Dscreens=true` (os PNGs vão para `app/build/screens/`).
- A arquitetura e as regras de cálculo estão em `../projetos/meu-financeiro/PASSO-A-PASSO.md`.
