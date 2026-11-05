package br.com.daniel.meufinanceiro

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.daniel.meufinanceiro.domain.Keys
import br.com.daniel.meufinanceiro.ui.AppRoot
import br.com.daniel.meufinanceiro.ui.MainViewModel
import br.com.daniel.meufinanceiro.ui.screens.LockScreen
import br.com.daniel.meufinanceiro.ui.theme.MeuFinanceiroTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: MainViewModel = viewModel()
            val ledger by vm.ledger.collectAsStateWithLifecycle()
            val ready by vm.ready.collectAsStateWithLifecycle()
            val l = ledger
            MeuFinanceiroTheme(l?.setting(Keys.THEME)) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    if (l != null && ready) {
                        val pinSet = l.setting(Keys.PIN_HASH) != null
                        var locked by rememberSaveable { mutableStateOf(pinSet) }
                        var stoppedAt by remember { mutableLongStateOf(0L) }
                        DisposableEffect(Unit) {
                            val obs = LifecycleEventObserver { _, e ->
                                if (e == Lifecycle.Event.ON_STOP) stoppedAt = System.currentTimeMillis()
                                if (e == Lifecycle.Event.ON_START && stoppedAt > 0 && System.currentTimeMillis() - stoppedAt > 60_000) locked = true
                            }
                            ProcessLifecycleOwner.get().lifecycle.addObserver(obs)
                            onDispose { ProcessLifecycleOwner.get().lifecycle.removeObserver(obs) }
                        }
                        val hide = l.settingBool(Keys.HIDE_IN_RECENTS)
                        LaunchedEffect(hide) {
                            if (hide) window.addFlags(WindowManager.LayoutParams.FLAG_SECURE) else window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        }
                        if (locked && pinSet) {
                            val bio = l.settingBool(Keys.BIOMETRIC) && canUseBiometric()
                            LockScreen(
                                biometricAvailable = bio,
                                onBiometric = { promptBiometric { locked = false } },
                                checkPin = { pin -> vm.checkPin(pin).also { if (it) locked = false } },
                            )
                            LaunchedEffect(Unit) { if (bio) promptBiometric { locked = false } }
                        } else {
                            AppRoot(vm, l, canUseBiometric = canUseBiometric())
                        }
                    }
                }
            }
        }
    }

    fun canUseBiometric(): Boolean =
        BiometricManager.from(this).canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

    private fun promptBiometric(onSuccess: () -> Unit) {
        if (!canUseBiometric()) return
        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = onSuccess()
        })
        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Meu Financeiro")
                .setSubtitle("Desbloqueie para ver suas finanças")
                .setNegativeButtonText("Usar PIN")
                .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK)
                .build()
        )
    }
}
