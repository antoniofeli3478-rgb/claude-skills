package br.com.daniel.meufinanceiro

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import br.com.daniel.meufinanceiro.domain.Alerts
import br.com.daniel.meufinanceiro.domain.Dates
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Keys
import java.util.concurrent.TimeUnit

/** Checks the smart alerts twice a day and shows at most ONE notification per day (the most important new one). */
class AlertWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as MeuFinanceiroApp
        val l = app.repo.snapshot()
        if (!l.settingBool(Keys.ALERT_NOTIFY)) return Result.success()
        val today = Dates.today()
        if (l.setting(Keys.ALERT_LAST_NOTIFIED) == today.toString()) return Result.success()
        val alert = Alerts.compute(FinanceCalc(l, today)).firstOrNull() ?: return Result.success()
        if (notify(applicationContext, alert.title, alert.body)) app.repo.setSetting(Keys.ALERT_LAST_NOTIFIED, today.toString())
        return Result.success()
    }

    companion object {
        private const val CHANNEL = "alertas"
        private const val WORK = "alertas-financeiros"

        fun schedule(context: Context, enabled: Boolean) {
            runCatching {
                val wm = WorkManager.getInstance(context)
                if (enabled) wm.enqueueUniquePeriodicWork(WORK, ExistingPeriodicWorkPolicy.KEEP, PeriodicWorkRequestBuilder<AlertWorker>(12, TimeUnit.HOURS).build())
                else wm.cancelUniqueWork(WORK)
            }
        }

        fun notify(context: Context, title: String, body: String): Boolean {
            if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return false
            val nm = context.getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(CHANNEL) == null) nm.createNotificationChannel(NotificationChannel(CHANNEL, "Alertas financeiros", NotificationManager.IMPORTANCE_DEFAULT))
            val open = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_IMMUTABLE)
            val n = NotificationCompat.Builder(context, CHANNEL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title).setContentText(body).setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(open).setAutoCancel(true).build()
            return runCatching { NotificationManagerCompat.from(context).notify(1001, n); true }.getOrDefault(false)
        }
    }
}
