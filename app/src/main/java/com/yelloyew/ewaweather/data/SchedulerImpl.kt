package com.yelloyew.ewaweather.data

import android.content.Context
import androidx.work.*
import com.yelloyew.ewaweather.domain.Scheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@BoundTo(supertype = Scheduler::class, component = SingletonComponent::class)
class SchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : Scheduler {

    override suspend fun startScheduler() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build()
        val periodicRequest = PeriodicWorkRequest
            .Builder(AppWorker::class.java, 1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "com.yelloyew.ewaweather.DATA_UPDATE",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }
}