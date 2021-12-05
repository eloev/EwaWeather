package com.yelloyew.ewaweather.presentation

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.yelloyew.ewaweather.data.AppWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class EwaWeather : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        setupWorker()
    }

    private fun setupWorker(){
        CoroutineScope(Dispatchers.IO).launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .build()
            val periodicRequest = PeriodicWorkRequest
                .Builder(AppWorker::class.java, 1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "com.yelloyew.ewaweather.DATA_UPDATE",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }
    }
}


