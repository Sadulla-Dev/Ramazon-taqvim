package com.example.ramazontaqvim.widget.big

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.example.ramazontaqvim.data.calculateWidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReloadWidgetAction : ActionCallback {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        RamazonGlanceWidget(calculateWidgetState()).update(context, glanceId)
    }
}

class RamazonWidgetUpdateReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val state = calculateWidgetState()
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(RamazonGlanceWidget::class.java)
            glanceIds.forEach { glanceId ->
                RamazonGlanceWidget(state).update(context, glanceId)
            }
        }
    }

    companion object {
        private const val ACTION = "com.ramazon.widget.UPDATE_WIDGET"
        private const val REQUEST_CODE = 1001

        // ── AlarmManager boshlatish ──────────────────────────────
        fun start(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = buildPendingIntent(context)

            // Har 1 soniyada trigger
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 1000,
                1000,
                pendingIntent
            )
        }

        // ── AlarmManager to'xtatish ──────────────────────────────
        fun stop(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(buildPendingIntent(context))
        }

        private fun buildPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, RamazonWidgetUpdateReceiver::class.java).apply {
                action = ACTION
            }
            return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}


