package com.example.ramazontaqvim.widget.small

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import com.example.ramazontaqvim.widget.calculateWidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ── Reload button action ─────────────────────────────────────────────
class SmallReloadWidgetAction : ActionCallback {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        SmallRamazonGlanceWidget(calculateWidgetState()).update(context, glanceId)
    }
}

// ── AlarmManager broadcast receiver ─────────────────────────────────
class SmallWidgetAlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val state = calculateWidgetState()
            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(SmallRamazonGlanceWidget::class.java)
            ids.forEach { id ->
                SmallRamazonGlanceWidget(state).update(context, id)
            }
        }
    }

    companion object {
        private const val ACTION       = "com.ramazon.widget.SMALL_UPDATE"
        private const val REQUEST_CODE = 2001

        fun start(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 1_000,
                1_000,                      // har 1 soniyada
                buildPendingIntent(context)
            )
        }

        fun stop(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.cancel(buildPendingIntent(context))
        }

        private fun buildPendingIntent(context: Context) = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, SmallWidgetAlarmReceiver::class.java).apply { action = ACTION },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}