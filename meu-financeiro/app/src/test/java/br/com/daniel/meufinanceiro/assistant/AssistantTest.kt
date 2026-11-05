package br.com.daniel.meufinanceiro.assistant

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.daniel.meufinanceiro.data.ActionStatus
import br.com.daniel.meufinanceiro.data.AppDatabase
import br.com.daniel.meufinanceiro.data.BoxKind
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.data.TxnType
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Keys
import com.anthropic.models.messages.MessageCreateParams
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/** Assistant: offline engine + tool safety rules (the Claude engine uses the same tools). */
@RunWith(AndroidJUnit4::class)
class AssistantTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: FinanceRepository
    private lateinit var asst: Assistant
    private val today = LocalDate.of(2026, 11, 5).toEpochDay()

    @Before fun setUp() = runBlocking {
        db = AppDatabase.inMemory(ApplicationProvider.getApplicationContext())
        repo = FinanceRepository(db)
        repo.seedIfEmpty()
        asst = Assistant(repo, apiKey = { null }, today = { today })
    }
    @After fun tearDown() { db.close() }

    private fun lastAnswer() = runBlocking { repo.chat().last { it.role == "assistant" }.text }
    private fun say(t: String): String { runBlocking { asst.send(t) }; return lastAnswer() }

    /** Teste 6: "Quanto posso gastar?" usa o saldo livre e não o dinheiro reservado. */
    @Test fun teste6_quantoPossoGastar() = runBlocking {
        val l = repo.snapshot()
        val nu = l.accounts.first { it.name == "Nubank" }.id
        repo.adjustBalance(nu, 0, 21200, today)
        repo.boxDeposit(l.boxOfKind(BoxKind.EMERGENCY)!!.id, 17000, today)
        val a = say("Quanto posso gastar?")
        assertTrue(a, a.contains("R$ 42,00"))
        assertTrue(a, a.contains("Reservado nas caixinhas (não conta): R$ 170,00"))
        assertFalse(a, a.startsWith("Você pode gastar R$ 212"))
    }

    @Test fun changingTheMinimumAsksFirstShowsOldAndNewAndThenApplies() = runBlocking {
        val a = say("Mude meu limite mínimo de saldo para R$ 50.")
        assertTrue(a, a.contains("R$ 30,00") && a.contains("R$ 50,00"))
        assertEquals("3000", repo.snapshot().setting(Keys.MIN_BALANCE)) // not changed yet
        assertEquals(ActionStatus.PENDING, repo.actions().first().status)
        val b = say("Pode.")
        assertTrue(b, b.startsWith("Feito"))
        assertEquals("5000", repo.snapshot().setting(Keys.MIN_BALANCE))
        // history log + undo
        val act = repo.actions().first()
        assertEquals(ActionStatus.DONE, act.status)
        asst.undoFromUi(act.id)
        assertEquals("3000", repo.snapshot().setting(Keys.MIN_BALANCE))
        assertEquals(ActionStatus.UNDONE, repo.action(act.id)!!.status)
    }

    @Test fun registeringAnExpenseNeedsConfirmationUnlessAutoIsOn() = runBlocking {
        say("Registre um gasto de R$ 25 com alimentação no Pix.")
        assertTrue(repo.snapshot().txns.none { it.type == TxnType.EXPENSE })
        say("sim")
        val e = repo.snapshot().txns.single { it.type == TxnType.EXPENSE }
        assertEquals(2500L, e.amountCents)
        // with "registros simples sem confirmação" enabled it is immediate
        repo.setSetting(Keys.ASSISTANT_AUTO_SIMPLE, "true")
        say("gastei 10 com lanche no dinheiro")
        assertEquals(2, repo.snapshot().txns.count { it.type == TxnType.EXPENSE })
    }

    @Test fun ambiguousEntryIsNotExecuted() = runBlocking {
        val a = say("Ônibus 5 para voltar do trabalho")
        assertTrue(a, a.contains("falta"))
        assertTrue(repo.actions().isEmpty())
        val b = say("uber 15 pix") // no work mentioned: personal uber, but category clear -> proposal
        assertTrue(b, b.contains("Confirma"))
        assertFalse(b, b.contains("custo do trabalho"))
    }

    @Test fun workCostIsLinkedToTheJob() = runBlocking {
        say("Tio Rex ganhei 80"); say("sim")
        say("Uber 15 para trabalhar no Tio Rex pix"); say("sim")
        say("Ônibus 5 para voltar do trabalho no Tio Rex dinheiro"); say("sim")
        val c = FinanceCalc(repo.snapshot(), today)
        val rex = repo.snapshot().sources.first { it.type == "TIO_REX" }.id
        val p = c.profitability(br.com.daniel.meufinanceiro.domain.Period.of(br.com.daniel.meufinanceiro.domain.PeriodKind.MONTH, today)).first { it.sourceId == rex }
        assertEquals(6000L, p.netCents)
        val lucro = say("Quanto lucrei esta semana?")
        assertTrue(lucro, lucro.contains("Lucro líquido: R$ 60,00"))
        val uber = say("Quanto gastei com Uber este mês?")
        assertTrue(uber, uber.contains("R$ 15,00") && uber.contains("custos ligados aos seus trabalhos"))
    }

    @Test fun loanFromMother() = runBlocking {
        say("Minha mãe me emprestou 30 reais"); say("sim")
        val c = FinanceCalc(repo.snapshot(), today)
        assertEquals(3000L, c.loansOutstanding())
        assertEquals(0L, c.report(br.com.daniel.meufinanceiro.domain.Period.of(br.com.daniel.meufinanceiro.domain.PeriodKind.MONTH, today)).incomeCents)
    }

    @Test fun deleteNeedsConfirmationAndCanBeUndone() = runBlocking {
        val l = repo.snapshot()
        val r = repo.addExpense(FinanceRepository.ExpenseInput(4000, today, "Cinema", l.categories.first { it.name == "Lazer" }.id, "PIX", l.dailyAccount!!.id))
        val o = asst.tools.run("excluir_movimentacao", buildJsonObject { put("id", "t${r.txnId}") })
        assertTrue(o is AssistantTools.Outcome.NeedsConfirmation)
        assertEquals(1, repo.snapshot().txns.count { it.type == TxnType.EXPENSE })
        val id = (o as AssistantTools.Outcome.NeedsConfirmation).actionId
        asst.tools.confirm(id.toInt())
        assertEquals(0, repo.snapshot().txns.count { it.type == TxnType.EXPENSE })
        asst.undoFromUi(id)
        val back = repo.snapshot().txns.single { it.type == TxnType.EXPENSE }
        assertEquals(4000L, back.amountCents); assertEquals("Cinema", back.description)
    }

    @Test fun editShowsOldAndNew() = runBlocking {
        val l = repo.snapshot()
        val r = repo.addExpense(FinanceRepository.ExpenseInput(2500, today, "Almoço", l.categories.first().id, "PIX", l.dailyAccount!!.id))
        val o = asst.tools.run("editar_movimentacao", buildJsonObject { put("id", "t${r.txnId}"); put("valor", 30.0) })
        assertTrue((o as AssistantTools.Outcome.NeedsConfirmation).summary.contains("R$ 25,00 → R$ 30,00"))
        asst.tools.confirm(o.actionId.toInt())
        assertEquals(3000L, repo.snapshot().txns.single { it.type == TxnType.EXPENSE }.amountCents)
        asst.undoFromUi(o.actionId)
        assertEquals(2500L, repo.snapshot().txns.single { it.type == TxnType.EXPENSE }.amountCents)
    }

    @Test fun readToolsAnswerFromRealData() = runBlocking {
        val o = asst.tools.run("resumo_financeiro", buildJsonObject { })
        assertTrue(o is AssistantTools.Outcome.Done)
        assertTrue(o.json.contains("saldo_livre_agora"))
        val bad = asst.tools.run("registrar_despesa", buildJsonObject { put("valor", 10.0); put("descricao", "x"); put("categoria", "Inexistente"); put("forma_pagamento", "pix") })
        assertTrue(bad is AssistantTools.Outcome.Error) // never guesses a category
    }

    @Test fun claudeToolDefinitionsBuildAValidRequest() {
        val params = MessageCreateParams.builder().model(Assistant.MODEL).maxTokens(100L).addUserMessage("oi")
            .apply { asst.toolDefs().forEach { addTool(it) } }.build()
        assertEquals(asst.tools.specs.size, params.tools().get().size)
    }

    @Test fun healthSummaryHasNoInventedData() = runBlocking {
        val a = say("Como estou financeiramente?")
        assertTrue(a, a.contains("Agora") && a.contains("Ainda não há lançamentos neste mês"))
    }
}
