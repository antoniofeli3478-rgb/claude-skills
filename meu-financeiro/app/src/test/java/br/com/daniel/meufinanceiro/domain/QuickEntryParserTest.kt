package br.com.daniel.meufinanceiro.domain

import br.com.daniel.meufinanceiro.data.Category
import br.com.daniel.meufinanceiro.data.IncomeSource
import br.com.daniel.meufinanceiro.data.PayMethod
import br.com.daniel.meufinanceiro.data.SourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickEntryParserTest {
    private val cats = listOf("Alimentação", "Transporte", "Academia", "Muay Thai", "Assinaturas", "Compras pessoais", "Estoque NFC", "Lazer", "Outros")
        .mapIndexed { i, n -> Category(id = i + 1L, name = n, color = 0) }
    private val srcs = listOf(
        IncomeSource(id = 1, name = "Plaquinhas NFC", type = SourceType.NFC, color = 0),
        IncomeSource(id = 2, name = "Tio Rex Restaurante", type = SourceType.TIO_REX, color = 0),
        IncomeSource(id = 3, name = "Recreação (festas e eventos)", type = SourceType.RECREACAO, color = 0),
        IncomeSource(id = 4, name = "Pipoca e algodão-doce", type = SourceType.PIPOCA, color = 0),
    )
    private val today = 20000L
    private fun p(s: String) = QuickEntryParser.parse(s, cats, srcs, today)

    @Test fun lunchPix() {
        val r = p("Almoço, R$ 25, Pix")
        assertFalse(r.isIncome); assertEquals(2500L, r.amountCents); assertEquals(PayMethod.PIX, r.method)
        assertEquals(1L, r.categoryId); assertEquals("Almoço", r.description)
    }

    @Test fun spentCashOnFood() {
        val r = p("Gastei 18 reais no dinheiro com alimentação")
        assertFalse(r.isIncome); assertEquals(1800L, r.amountCents); assertEquals(PayMethod.CASH, r.method); assertEquals(1L, r.categoryId)
    }

    @Test fun incomeFromTioRex() {
        val r = p("Recebi 80 do Tio Rex")
        assertTrue(r.isIncome); assertEquals(8000L, r.amountCents); assertEquals(2L, r.sourceId)
    }

    @Test fun creditInInstallments() {
        val r = p("Tênis 300 no crédito em 3x")
        assertEquals(30000L, r.amountCents); assertEquals(PayMethod.CREDIT, r.method); assertEquals(3, r.installments); assertEquals(6L, r.categoryId)
    }

    @Test fun uberYesterdayWithCents() {
        val r = p("uber 12,50 ontem")
        assertEquals(1250L, r.amountCents); assertEquals(today - 1, r.date); assertEquals(2L, r.categoryId)
    }

    @Test fun rideApp99IsNotTheAmount() {
        val r = p("99 para casa 15 reais pix")
        assertEquals(1500L, r.amountCents); assertEquals(2L, r.categoryId)
    }

    @Test fun pipocaIncome() {
        val r = p("ganhei 120 na pipoca")
        assertTrue(r.isIncome); assertEquals(12000L, r.amountCents); assertEquals(4L, r.sourceId)
    }
}

class QuickEntryParserV2Test {
    private val cats = listOf("Alimentação", "Transporte", "Estoque NFC", "NFC: acrílicos", "Família", "Outros")
        .mapIndexed { i, n -> Category(id = i + 1L, name = n, color = 0, isNfcStock = n.contains("NFC")) }
    private val srcs = listOf(
        IncomeSource(id = 1, name = "Plaquinhas NFC", type = SourceType.NFC, color = 0),
        IncomeSource(id = 2, name = "Tio Rex Restaurante", type = SourceType.TIO_REX, color = 0),
        IncomeSource(id = 3, name = "Recreação (festas e eventos)", type = SourceType.RECREACAO, color = 0),
        IncomeSource(id = 4, name = "Pipoca e algodão-doce", type = SourceType.PIPOCA, color = 0),
    )
    private fun p(s: String) = QuickEntryParser.parse(s, cats, srcs, 20000L)

    @Test fun tioRexGanhei80() { val r = p("Tio Rex ganhei 80"); assertEquals(QuickEntryParser.Kind.WORK_INCOME, r.kind); assertEquals(8000L, r.amountCents); assertEquals(2L, r.sourceId) }
    @Test fun uberParaTrabalhar() { val r = p("Uber 15 para trabalhar no Tio Rex"); assertEquals(QuickEntryParser.Kind.EXPENSE, r.kind); assertEquals(1500L, r.amountCents); assertTrue(r.workCost); assertEquals(2L, r.workSourceId); assertEquals(2L, r.categoryId) }
    @Test fun onibusSemTrabalhoDefinidoPergunta() { val r = p("Ônibus 5 para voltar do trabalho"); assertTrue(r.workCost); assertEquals(null, r.workSourceId); assertTrue(r.missing.any { it.contains("trabalho") }) }
    @Test fun almocoPix() { val r = p("Gastei 25 com almoço no Pix"); assertEquals(2500L, r.amountCents); assertEquals(PayMethod.PIX, r.method); assertEquals(1L, r.categoryId); assertFalse(r.workCost) }
    @Test fun vendiPlaquinhas() { val r = p("Vendi 2 plaquinhas por 89,90 cada"); assertEquals(QuickEntryParser.Kind.NFC_SALE, r.kind); assertEquals(2, r.quantity); assertEquals(8990L, r.unitPriceCents); assertEquals(17980L, r.amountCents) }
    @Test fun comprei10Acrilicos() { val r = p("Comprei 10 acrílicos por 37 reais"); assertEquals(QuickEntryParser.Kind.NFC_MATERIAL, r.kind); assertEquals(3700L, r.amountCents); assertEquals(4L, r.categoryId) }
    @Test fun maeEmprestou() { val r = p("Minha mãe me emprestou 30 reais"); assertEquals(QuickEntryParser.Kind.LOAN, r.kind); assertEquals(3000L, r.amountCents); assertEquals("mae", r.person) }
    @Test fun maeDeu() { val r = p("Minha mãe me deu 20 pro Uber"); assertEquals(QuickEntryParser.Kind.FAMILY, r.kind); assertEquals(2000L, r.amountCents) }
    @Test fun uberPessoalNaoECustoDeTrabalho() { val r = p("uber 18 pix"); assertFalse(r.workCost) }
}
