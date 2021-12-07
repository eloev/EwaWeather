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

private const val TAG = "tag11"

@Singleton
class WeatherManager @Inject constructor(
    private val repository: Repository,
    private val dataPreferences: DataPreferences,
    private val scheduler: Scheduler
) {

    private var stockRequestParams: RequestParams? =
        dataPreferences.getRequestParams() ?: RequestParams(55.75, 37.61, "ru")

    init {
        setupWorker()
    }

    fun setRequestParams(requestParams: RequestParams) {
        stockRequestParams = requestParams
        dataPreferences.setRequestParams(requestParams)
    }

    private fun getRequestParams(): RequestParams? {
        val requestParams = dataPreferences.getRequestParams()
        return requestParams ?: stockRequestParams
    }

    suspend fun getWeather(): Weather? {
        var weather: Weather? = repository.getNetworkWeather(getRequestParams())
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
        var forecasts: MutableList<Forecast> = repository.getNetworkForecast(getRequestParams())
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

    private fun setupWorker() {
        CoroutineScope(Dispatchers.IO).launch {
            scheduler.startScheduler()
        }
    }
}