package com.yelloyew.ewaweather.domain

import android.content.Context
import android.util.Log
import androidx.work.*
import com.yelloyew.ewaweather.data.AppWorker
import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.Weather
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "tag11"

class WeatherManager @Inject constructor(
    private val weatherRepo: WeatherRepo,
    @ApplicationContext private val context: Context
) {
    init {
        setupWorker()
    }

    suspend fun getWeather(): Weather {
        val weather = weatherRepo.getWeather()
        if (weather == null) {
            delay(5000L)
            getWeather()
        }
        // почему не weather -> потому что при первом запросе он ещё может быть null
        // поток не успевает?
        return weatherRepo.getWeather()!!
    }

    suspend fun getForecast(): MutableList<Forecast> {
        val forecasts = weatherRepo.getForecast()
        if (forecasts == emptyList<Forecast>()) {
            delay(5000L)
            getForecast()
        }
        return forecasts
    }

    suspend fun getForecastScreen(): Boolean {
        val forecasts = weatherRepo.getForecast()
        if (forecasts == emptyList<Forecast>()) {
            return false
        } else
            Log.d(TAG, weatherRepo.getForecast().size.toString())
        return weatherRepo.getForecast().size == 7
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
            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                "com.yelloyew.ewaweather.DATA_UPDATE",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }
    }
}