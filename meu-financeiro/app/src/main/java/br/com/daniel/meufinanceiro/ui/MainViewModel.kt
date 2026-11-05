package br.com.daniel.meufinanceiro.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.daniel.meufinanceiro.MeuFinanceiroApp
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.domain.Ledger
import br.com.daniel.meufinanceiro.domain.Period
import br.com.daniel.meufinanceiro.domain.PeriodKind
import br.com.daniel.meufinanceiro.security.Pin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val container = app as MeuFinanceiroApp
    val repo: FinanceRepository = container.repo
    val backup = container.backup
    val secrets = container.secrets

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready

    /** Consistent snapshot of the whole database; null until the first load. */
    val ledger: StateFlow<Ledger?> = repo.ledgerFlow().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val messages = _messages.asSharedFlow()

    /** Shared period selection (Início and Relatórios). */
    val period = MutableStateFlow(Period.of(PeriodKind.MONTH))

    init {
        viewModelScope.launch {
            try {
                repo.seedIfEmpty()
                runCatching { backup.autoSnapshot() }
            } catch (e: Exception) {
                android.util.Log.e("MeuFinanceiro", "seed failed", e)
                _messages.emit("Erro ao preparar os dados: ${e.message}")
            } finally {
                _ready.value = true
            }
        }
    }

    fun toast(msg: String) { _messages.tryEmit(msg) }

    /* ---------------- Assistant ---------------- */
    val assistant = container.assistant
    val chat = MutableStateFlow<List<br.com.daniel.meufinanceiro.data.ChatMessage>>(emptyList())
    val actions = MutableStateFlow<List<br.com.daniel.meufinanceiro.data.AssistantAction>>(emptyList())
    val thinking = MutableStateFlow(false)

    fun refreshChat() = viewModelScope.launch {
        container.repoAiEnabled = ledger.value?.settingBool(Keys.AI_ENABLED) == true
        chat.value = repo.chat(); actions.value = repo.actions()
    }

    private fun assistantOp(block: suspend () -> Unit) = viewModelScope.launch {
        thinking.value = true
        try {
            container.repoAiEnabled = ledger.value?.settingBool(Keys.AI_ENABLED) == true
            block()
        } catch (e: Exception) {
            repo.addChat(br.com.daniel.meufinanceiro.data.ChatMessage(role = "assistant", text = "Desculpe, algo deu errado: ${e.message}"))
        } finally {
            chat.value = repo.chat(); actions.value = repo.actions(); thinking.value = false
        }
    }

    fun ask(text: String) = assistantOp { assistant.send(text) }
    fun confirmAction(id: Long) = assistantOp { assistant.confirmFromUi(id) }
    fun cancelAction(id: Long) = assistantOp { assistant.cancelFromUi(id) }
    fun undoAction(id: Long) = assistantOp { assistant.undoFromUi(id) }
    fun clearChat() = assistantOp { repo.clearChat() }

    fun setAlertNotifications(on: Boolean) {
        setSetting(Keys.ALERT_NOTIFY, if (on) "true" else null)
        br.com.daniel.meufinanceiro.AlertWorker.schedule(getApplication(), on)
    }

    /** Runs a write; reports success or the error message (never crashes the app). */
    fun run(success: String? = null, onDone: (() -> Unit)? = null, block: suspend FinanceRepository.() -> Unit) {
        viewModelScope.launch {
            try {
                repo.block()
                success?.let { _messages.emit(it) }
                onDone?.invoke()
            } catch (e: Exception) {
                _messages.emit(e.message ?: "Não foi possível salvar")
            }
        }
    }

    fun setSetting(key: String, value: String?) = run { setSetting(key, value) }

    fun setPin(pin: String?) = run(if (pin == null) "PIN removido" else "PIN ativado") {
        if (pin == null) {
            setSetting(Keys.PIN_HASH, null); setSetting(Keys.PIN_SALT, null); setSetting(Keys.BIOMETRIC, null)
        } else {
            val salt = Pin.newSalt()
            setSetting(Keys.PIN_SALT, salt); setSetting(Keys.PIN_HASH, Pin.hash(pin, salt))
        }
    }

    fun checkPin(pin: String): Boolean {
        val l = ledger.value ?: return false
        val salt = l.setting(Keys.PIN_SALT) ?: return true
        return Pin.hash(pin, salt) == l.setting(Keys.PIN_HASH)
    }
}
