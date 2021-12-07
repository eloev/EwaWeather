package com.yelloyew.ewaweather.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yelloyew.ewaweather.domain.WeatherManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "tag13 worker"

@HiltWorker
class AppWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherManager: WeatherManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        with(weatherManager) {
            getWeather()
            //getForecast()
        }
        Log.d(TAG, "worker update weather data")
        return Result.success()
    }
}