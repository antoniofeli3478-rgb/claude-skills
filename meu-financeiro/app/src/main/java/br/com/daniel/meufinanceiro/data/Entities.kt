package br.com.daniel.meufinanceiro.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/*
 * Data model rules (see PASSO-A-PASSO):
 *  - Money is Long cents, dates are epochDay, months are yyyymm Int.
 *  - Balances are never stored: they are always recomputed from the ledger, so they can't drift.
 *  - Enum-like fields are stored as String constants (stable in the DB and in backups).
 */

object AccountKind { const val BANK = "BANK"; const val CASH = "CASH" }

@Serializable
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val kind: String = AccountKind.BANK,
    val initialBalanceCents: Long = 0,
    /** Conta usada no dia a dia: recebe o alerta de saldo mínimo. */
    val isDaily: Boolean = false,
    val note: String = "",
    val color: Long = 0xFF3DDC97,
    val archived: Boolean = false,
    val sortOrder: Int = 0,
)

object CycleRule {
    /** Compras a partir do "melhor dia de compra" vão para a próxima fatura. */
    const val BEST_DAY = "BEST_DAY"
    /** Compras depois do dia de fechamento vão para a próxima fatura. */
    const val CLOSING = "CLOSING"
}

@Serializable
@Entity(tableName = "cards")
data class CreditCard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val bank: String,
    val closingDay: Int,
    val bestPurchaseDay: Int,
    /** Null until Daniel sets it: never invented. */
    val dueDay: Int? = null,
    val cycleRule: String = CycleRule.BEST_DAY,
    val limitCents: Long? = null,
    val paymentAccountId: Long? = null,
    val archived: Boolean = false,
)

@Serializable
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Long,
    /** Despesas desta categoria saem da caixinha Estoque NFC (reinvestimento, não gasto pessoal). */
    val isNfcStock: Boolean = false,
    val archived: Boolean = false,
    val sortOrder: Int = 0,
)

object SourceType {
    const val TIO_REX = "TIO_REX"
    const val RECREACAO = "RECREACAO"
    const val PIPOCA = "PIPOCA"
    const val NFC = "NFC"
    const val OTHER = "OTHER"
    // v2: money that is NOT earned income
    const val FAMILY = "FAMILY"
    const val LOAN = "LOAN"
    const val HELP = "HELP"
    const val REFUND = "REFUND"
    val notEarned = setOf(FAMILY, LOAN, HELP, REFUND)
}

@Serializable
@Entity(tableName = "income_sources")
data class IncomeSource(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String = SourceType.OTHER,
    val defaultAmountCents: Long = 0,
    val defaultHours: Double = 0.0,
    val color: Long,
    val archived: Boolean = false,
    val sortOrder: Int = 0,
    /** v2: true = renda de trabalho/venda (entra no lucro). false = família, empréstimo, ajuda, reembolso. */
    @ColumnInfo(defaultValue = "1") val isEarned: Boolean = true,
)

object BoxKind {
    const val SPENDING = "SPENDING"
    const val EMERGENCY = "EMERGENCY"
    const val ABROAD = "ABROAD"
    const val NFC_STOCK = "NFC_STOCK"
    const val COMPUTER = "COMPUTER"
    const val OTHER = "OTHER"
}

@Serializable
@Entity(tableName = "boxes")
data class Box(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val kind: String = BoxKind.OTHER,
    /** Meta (mínima, quando há faixa). Null = sem meta definida. */
    val goalCents: Long? = null,
    /** Meta máxima opcional (ex.: computador entre R$ 4.000 e R$ 6.000). */
    val goalMaxCents: Long? = null,
    val targetDate: Long? = null,
    /** Reserva de emergência: meta = N meses de gastos médios (quando houver dados). */
    val emergencyMonths: Int? = null,
    val color: Long,
    val note: String = "",
    val archived: Boolean = false,
    val sortOrder: Int = 0,
    /** v2: banco onde o dinheiro desta caixinha fica guardado (ex.: caixinhas do Nubank). */
    val accountId: Long? = null,
)

@Serializable
@Entity(
    tableName = "distribution_rules",
    foreignKeys = [ForeignKey(entity = Box::class, parentColumns = ["id"], childColumns = ["boxId"], onDelete = ForeignKey.CASCADE)],
)
data class DistributionRule(
    @PrimaryKey val boxId: Long,
    /** 5000 = 50%. The active rules always sum to 10000. */
    val basisPoints: Int,
)

object TxnType {
    const val INCOME = "INCOME"
    const val EXPENSE = "EXPENSE"
    const val TRANSFER = "TRANSFER"
    const val CARD_PAYMENT = "CARD_PAYMENT"
    /** Correção manual de saldo (não é receita nem despesa). amountCents pode ser negativo. */
    const val ADJUSTMENT = "ADJUSTMENT"
    /** v2: devolução de empréstimo (sai dinheiro, mas não é despesa). */
    const val LOAN_PAYMENT = "LOAN_PAYMENT"
}

object PayMethod {
    const val CASH = "CASH"
    const val PIX = "PIX"
    const val DEBIT = "DEBIT"
    const val CREDIT = "CREDIT"
    const val TRANSFER = "TRANSFER"
    const val OTHER = "OTHER"
    val all = listOf(CASH, PIX, DEBIT, CREDIT, TRANSFER, OTHER)
    fun label(m: String) = when (m) {
        CASH -> "Dinheiro"; PIX -> "Pix"; DEBIT -> "Débito"; CREDIT -> "Crédito"
        TRANSFER -> "Transferência"; else -> "Outros"
    }
}

object DistMode {
    /** Usa os percentuais padrão (50/25/25). */
    const val DEFAULT = "DEFAULT"
    /** Não divide: fica como "dinheiro sem caixinha". */
    const val NONE = "NONE"
    /** Divisão personalizada desta entrada (linhas em BoxMovement). */
    const val CUSTOM = "CUSTOM"
}

/** Every movement of real money in an account (or cash). Credit-card purchases live in CardPurchase instead. */
@Serializable
@Entity(
    tableName = "transactions",
    indices = [Index("date"), Index("type"), Index("accountId"), Index("workId"), Index("nfcSaleId"), Index("billId"), Index("cardId")],
)
data class Txn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    /** Always positive, except ADJUSTMENT which is signed. */
    val amountCents: Long,
    val date: Long,
    val description: String,
    val accountId: Long? = null,
    val toAccountId: Long? = null,
    val categoryId: Long? = null,
    val sourceId: Long? = null,
    val method: String = PayMethod.PIX,
    /** CARD_PAYMENT: card + invoice month (yyyymm) being paid. */
    val cardId: Long? = null,
    val invoiceKey: Int? = null,
    /** Expense paying a bill ("Contas a pagar") for month billMonth (yyyymm). */
    val billId: Long? = null,
    val billMonth: Int? = null,
    /** Income receiving a work entry / NFC sale. */
    val workId: Long? = null,
    val nfcSaleId: Long? = null,
    /** Expense paying an NFC batch (stock purchase). */
    val nfcBatchId: Long? = null,
    val distMode: String = DistMode.DEFAULT,
    /** Expense: which box paid for it. */
    val boxId: Long? = null,
    val recurring: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    /** v2: empréstimo ligado (entrada do empréstimo ou devolução). */
    val loanId: Long? = null,
)

object MoveKind {
    const val DISTRIBUTION = "DISTRIBUTION"
    const val SPEND = "SPEND"
    const val DEPOSIT = "DEPOSIT"
    const val WITHDRAW = "WITHDRAW"
    const val TRANSFER = "TRANSFER"
}

/** Envelope ("caixinha") ledger. Rows tied to a transaction or card purchase are deleted with it. */
@Serializable
@Entity(
    tableName = "box_movements",
    foreignKeys = [
        ForeignKey(entity = Box::class, parentColumns = ["id"], childColumns = ["boxId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Txn::class, parentColumns = ["id"], childColumns = ["txnId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CardPurchase::class, parentColumns = ["id"], childColumns = ["cardPurchaseId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("boxId"), Index("txnId"), Index("cardPurchaseId"), Index("groupId")],
)
data class BoxMovement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val boxId: Long,
    /** Signed: + enters the box, - leaves it. */
    val amountCents: Long,
    val date: Long,
    val kind: String,
    val txnId: Long? = null,
    val cardPurchaseId: Long? = null,
    /** Links the two sides of a box-to-box transfer. */
    val groupId: String? = null,
    val note: String = "",
)

@Serializable
@Entity(tableName = "card_purchases", indices = [Index("cardId"), Index("date"), Index("billId")])
data class CardPurchase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val description: String,
    val totalCents: Long,
    val installments: Int = 1,
    val date: Long,
    val categoryId: Long? = null,
    val boxId: Long? = null,
    val billId: Long? = null,
    val billMonth: Int? = null,
    val nfcBatchId: Long? = null,
    val recurring: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    /** v2: custo ligado a um trabalho / fonte de renda (entra na rentabilidade). */
    val workId: Long? = null,
    val sourceId: Long? = null,
)

@Serializable
@Entity(
    tableName = "card_installments",
    foreignKeys = [ForeignKey(entity = CardPurchase::class, parentColumns = ["id"], childColumns = ["purchaseId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("purchaseId"), Index("invoiceKey")],
)
data class CardInstallment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val purchaseId: Long,
    val number: Int,
    val amountCents: Long,
    /** yyyymm of the month the invoice closes. */
    val invoiceKey: Int,
)

/** A job / shift / event to be paid (Tio Rex, recreação, pipoca, outros). Payments are INCOME Txns with workId. */
@Serializable
@Entity(tableName = "work_entries", indices = [Index("sourceId"), Index("date")])
data class WorkEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: Long,
    val date: Long,
    val title: String = "",
    val hours: Double = 0.0,
    /** Faturamento total do evento (pode ser dividido com outras pessoas). Informativo. */
    val grossCents: Long = 0,
    /** Custos que eu paguei para este trabalho. Informativo: não vira despesa (evita duplicar). */
    val costsCents: Long = 0,
    /** Quanto é meu: o valor que tenho a receber. */
    val myShareCents: Long,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
@Entity(tableName = "nfc_batches")
data class NfcBatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val quantity: Int,
    val totalCostCents: Long,
    val supplier: String = "",
    val notes: String = "",
)

@Serializable
@Entity(tableName = "nfc_customers")
data class NfcCustomer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val business: String = "",
    val phone: String = "",
    val notes: String = "",
)

@Serializable
@Entity(tableName = "nfc_sales", indices = [Index("customerId"), Index("date")])
data class NfcSale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val customerId: Long? = null,
    val quantity: Int,
    val unitPriceCents: Long,
    /** Custo médio por unidade no momento da venda (congelado). */
    val unitCostCents: Long,
    /** Outros custos da venda (frete, embalagem...). */
    val extraCostsCents: Long = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** Valor de referência (editável). 0 = a configurar. */
    val amountCents: Long,
    val dueDay: Int? = null,
    val active: Boolean = false,
    val variable: Boolean = false,
    val categoryId: Long? = null,
    val method: String = PayMethod.PIX,
    val accountId: Long? = null,
    val cardId: Long? = null,
    val notes: String = "",
    val sortOrder: Int = 0,
)

@Serializable
@Entity(tableName = "settings")
data class Setting(@PrimaryKey val key: String, val value: String)

/* ======================= v2 ======================= */

/** Empréstimo recebido (dinheiro que precisa ser devolvido). */
@Serializable
@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val person: String,
    val principalCents: Long,
    val date: Long,
    val dueDate: Long? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

/** Conversa com a assistente (histórico persistente). */
@Serializable
@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** user | assistant | system */
    val role: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
    val actionId: Long? = null,
)

object ActionStatus {
    const val PENDING = "PENDING"
    const val DONE = "DONE"
    const val CANCELLED = "CANCELLED"
    const val UNDONE = "UNDONE"
    const val FAILED = "FAILED"
}

/** Toda ação que a assistente propõe ou executa fica registrada aqui (com dados para desfazer). */
@Serializable
@Entity(tableName = "assistant_actions")
data class AssistantAction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val tool: String,
    /** Texto em português mostrado ao usuário (o que vai mudar, valor anterior e novo). */
    val summary: String,
    val status: String,
    /** JSON com os parâmetros da ação. */
    val payload: String,
    /** JSON com o necessário para desfazer (ids criados, valores anteriores). */
    val undo: String? = null,
    val result: String? = null,
)
