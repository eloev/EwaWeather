package com.yelloyew.ewaweather.domain

import androidx.work.*
import com.yelloyew.ewaweather.data.DataPreferences
import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.RequestParams
import com.yelloyew.ewaweather.domain.model.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherManager @Inject constructor(
    private val repository: Repository,
    private val dataPreferences: DataPreferences,
    private val scheduler: Scheduler
) {

    var requestParams: RequestParams
        get() {
            return dataPreferences.getRequestParams() ?: RequestParams(55.75, 37.61, "ru")
        }
        set(requestParams) {
            dataPreferences.setRequestParams(requestParams)
        }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            setupWorker()
        }
    }

    suspend fun getWeather(): Weather? {
        var weather: Weather? = repository.getNetworkWeather(requestParams)
        if (weather == null) {
            weather = dataPreferences.getLastWeather()
            if (weather == null) {
                return null
            }
        } else {
            dataPreferences.setLastWeather(weather)
        }
        return weather
    }

    suspend fun getForecast(): List<Forecast> {
        var forecasts: MutableList<Forecast> = repository.getNetworkForecast(requestParams)
        if (forecasts == emptyList<Forecast>()) {
            forecasts = repository.getForecasts().toMutableList()
            if (forecasts == emptyList<Forecast>()) {
                return forecasts
            }
        } else {
            val oldForecasts = repository.getForecasts().toMutableList()
            for (i in forecasts.indices) {
                loop@ for (y in oldForecasts) {
                    if (forecasts[i] == y) {
                        break@loop
                    } else if (i == forecasts.size - 1) {
                        repository.deleteForecast(y.date)
                        repository.updateForecast(forecasts[i])
                    }
                }
            }
        }
        return forecasts
    }

    suspend fun canOpenForecast(): Boolean {
        return getForecast() != emptyList<Forecast>()
    }

    private suspend fun setupWorker() {
        scheduler.startScheduler()
    }
}