package com.yelloyew.ewaweather.domain

import android.content.Context
import android.util.Log
import androidx.work.*
import com.yelloyew.ewaweather.data.AppWorker
import com.yelloyew.ewaweather.data.WeatherPreferences
import com.yelloyew.ewaweather.data.database.ForecastRepository
import com.yelloyew.ewaweather.data.database.model.ForecastRoom
import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.RequestParams
import com.yelloyew.ewaweather.domain.model.Weather
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "tag11"

@Singleton
class WeatherManager @Inject constructor(
    private val weatherRepo: WeatherRepo,
    private val weatherPrefs: WeatherPreferences,
    private val forecastRepository: ForecastRepository,
    @ApplicationContext private val context: Context
) {

    private var stockRequestParams : RequestParams? = RequestParams(55.75, 37.61, "ru")

    init {
        setupWorker()
        if (weatherPrefs.getRequestParams() != null){
            stockRequestParams = weatherPrefs.getRequestParams()
        }
    }

    fun setRequestParams(requestParams: RequestParams){
        stockRequestParams = requestParams
        weatherPrefs.setRequestParams(requestParams)
    }

    private suspend fun getRequestParams() : RequestParams?{
        val requestParams = weatherPrefs.getRequestParams()
        return requestParams ?: stockRequestParams
    }

    suspend fun getWeather(): Weather? {
        var weather: Weather? = weatherPrefs.getLastWeather()
        // api.openweathermap.org update data each 10 minutes
        val currentTime: LocalDateTime = LocalDateTime.now().minusMinutes(1)
        if (weather == null) {
            weather = weatherRepo.getNetworkWeather(getRequestParams())
            if (weather == null){
                return null
            } else {
                weatherPrefs.setLastWeather(weather)
            }
        } else if (currentTime >= weather.date) {
            Log.d(TAG, "$currentTime, ${weather.date}")
            weather = weatherRepo.getNetworkWeather(getRequestParams())
            weatherPrefs.setLastWeather(weather!!)
        }
        return weather
    }

    suspend fun getForecast(): List<Forecast> {
        var forecasts: MutableList<Forecast> = forecastRoomListToForecastList(forecastRepository.getForecasts())
        val lastUpdateTime = weatherPrefs.getForecastUpdateTime()
        val currentTime: LocalDateTime = LocalDateTime.now().minusMinutes(1)
        if (forecasts == emptyList<Forecast>()) {
            forecasts = weatherRepo.getNetworkForecast(getRequestParams())
            if (forecasts == emptyList<Forecast>()) {
                return forecasts
            }
            for (i in forecasts) {
                forecastRepository.addForecast(forecastToForecastRoom(i))
            }
        } else if (currentTime >= lastUpdateTime) {
            Log.d(TAG, "$currentTime, $lastUpdateTime")
            val newForecast = weatherRepo.getNetworkForecast(getRequestParams())
            for (i in 0 until forecasts.size) {
                loop@ for (y in newForecast) {
                    if (forecasts[i] == y) {
                        break@loop
                    } else if (i == forecasts.size - 1) {
                        forecastRepository.deleteForecast(forecasts[i].date)
                        forecastRepository.updateForecast(forecastToForecastRoom(y))
                        forecasts[i] = y
                    }
                }
            }
        }
        return forecasts
    }

    suspend fun canOpenForecast(): Boolean {
        return getForecast() != emptyList<Forecast>()
    }

    private fun forecastToForecastRoom(forecast: Forecast): ForecastRoom {
        val date = forecast.date
        val morningTemp: Int = forecast.morning_temp
        val morningCloud: Float = forecast.morning_cloud
        val morningHumidity: Int = forecast.morning_humidity
        val morningPressure: Int = forecast.morning_pressure
        val dayTemp: Int = forecast.day_temp
        val dayCloud: Float = forecast.day_cloud
        val dayHumidity: Int = forecast.day_humidity
        val dayPressure: Int = forecast.day_pressure
        val eveningTemp: Int = forecast.evening_temp
        val eveningCloud: Float = forecast.evening_cloud
        val eveningHumidity: Int = forecast.evening_humidity
        val eveningPressure: Int = forecast.evening_pressure
        val nightTemp: Int = forecast.night_temp
        val nightCloud: Float = forecast.night_cloud
        val nightHumidity: Int = forecast.night_humidity
        val nightPressure: Int = forecast.night_pressure
        return ForecastRoom(
            date,
            morningTemp, morningCloud, morningHumidity, morningPressure,
            dayTemp, dayCloud, dayHumidity, dayPressure,
            eveningTemp, eveningCloud, eveningHumidity, eveningPressure,
            nightTemp, nightCloud, nightHumidity, nightPressure
        )
    }

    private fun forecastRoomListToForecastList(forecastRoomList: List<ForecastRoom>): MutableList<Forecast> {
        val forecasts: MutableList<Forecast> = mutableListOf()
        forecastRoomList.map { forecastRoom ->
            val date = forecastRoom.date
            val morningTemp: Int = forecastRoom.morning_temp
            val morningCloud: Float = forecastRoom.morning_cloud
            val morningHumidity: Int = forecastRoom.morning_humidity
            val morningPressure: Int = forecastRoom.morning_pressure
            val dayTemp: Int = forecastRoom.day_temp
            val dayCloud: Float = forecastRoom.day_cloud
            val dayHumidity: Int = forecastRoom.day_humidity
            val dayPressure: Int = forecastRoom.day_pressure
            val eveningTemp: Int = forecastRoom.evening_temp
            val eveningCloud: Float = forecastRoom.evening_cloud
            val eveningHumidity: Int = forecastRoom.evening_humidity
            val eveningPressure: Int = forecastRoom.evening_pressure
            val nightTemp: Int = forecastRoom.night_temp
            val nightCloud: Float = forecastRoom.night_cloud
            val nightHumidity: Int = forecastRoom.night_humidity
            val nightPressure: Int = forecastRoom.night_pressure
            forecasts.add(
                Forecast(
                    date,
                    morningTemp, morningCloud, morningHumidity, morningPressure,
                    dayTemp, dayCloud, dayHumidity, dayPressure,
                    eveningTemp, eveningCloud, eveningHumidity, eveningPressure,
                    nightTemp, nightCloud, nightHumidity, nightPressure
                )
            )
        }
        return forecasts
    }

    private fun setupWorker() {
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