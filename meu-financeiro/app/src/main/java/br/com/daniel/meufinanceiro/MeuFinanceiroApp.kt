package br.com.daniel.meufinanceiro

import android.app.Application
import br.com.daniel.meufinanceiro.backup.BackupManager
import br.com.daniel.meufinanceiro.data.AppDatabase
import br.com.daniel.meufinanceiro.data.FinanceRepository
import br.com.daniel.meufinanceiro.security.SecretStore

/** Manual dependency container (no DI framework: fewer moving parts). */
class MeuFinanceiroApp : Application() {
    val db: AppDatabase by lazy { AppDatabase.build(this) }
    val repo: FinanceRepository by lazy { FinanceRepository(db) }
    val backup: BackupManager by lazy { BackupManager(this, repo) }
    val secrets: SecretStore by lazy { SecretStore(this) }
}
