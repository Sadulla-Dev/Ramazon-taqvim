package com.example.ramazontaqvim.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import java.util.concurrent.TimeUnit

// ════════════════════════════════════════════════════════════════════
//  WORKER — har 15 daqiqada widget yangilanadi
//  (Android minimal 15 daqiqa qo'yadi WorkManager uchun)
//  Countdown uchun AlarmManager ishlatiladi
// ════════════════════════════════════════════════════════════════════
class RamazonWidgetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        RamazonGlanceWidget().updateAll(context)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "ramazon_widget_update"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<RamazonWidgetWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(Constraints.NONE)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}