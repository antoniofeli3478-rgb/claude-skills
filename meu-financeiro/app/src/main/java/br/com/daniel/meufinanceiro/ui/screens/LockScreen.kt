package br.com.daniel.meufinanceiro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.com.daniel.meufinanceiro.ui.components.AppIcons
import br.com.daniel.meufinanceiro.ui.theme.LocalFin

@Composable
fun LockScreen(biometricAvailable: Boolean, onBiometric: () -> Unit, checkPin: (String) -> Boolean) {
    val fin = LocalFin.current
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    fun press(d: String) {
        error = false
        if (pin.length < 8) pin += d
        if (pin.length >= 4 && checkPin(pin)) pin = ""
        else if (pin.length == 8) { error = true; pin = "" }
    }
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        Icon(AppIcons.Shield, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp))
        Spacer(Modifier.height(12.dp))
        Text("Meu Financeiro", style = MaterialTheme.typography.headlineSmall)
        Text(if (error) "PIN incorreto" else "Digite seu PIN", color = if (error) fin.expense else fin.muted, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.semantics { contentDescription = "${pin.length} dígitos digitados" }) {
            repeat(maxOf(4, pin.length)) { i ->
                Box(Modifier.size(14.dp).clip(CircleShape).background(if (i < pin.length) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant))
            }
        }
        Spacer(Modifier.height(28.dp))
        listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("bio", "0", "del")).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                row.forEach { k ->
                    Box(
                        Modifier.size(72.dp).clip(CircleShape)
                            .background(if (k == "bio" || k == "del") MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceContainerHigh)
                            .clickable(enabled = k != "bio" || biometricAvailable) {
                                when (k) { "del" -> pin = pin.dropLast(1); "bio" -> onBiometric(); else -> press(k) }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        when (k) {
                            "del" -> Text("Apagar", style = MaterialTheme.typography.labelMedium)
                            "bio" -> if (biometricAvailable) Icon(AppIcons.Shield, "Usar biometria", tint = MaterialTheme.colorScheme.primary)
                            else -> Text(k, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }
        }
        if (biometricAvailable) TextButton(onClick = onBiometric) { Text("Usar digital / rosto") }
    }
}
