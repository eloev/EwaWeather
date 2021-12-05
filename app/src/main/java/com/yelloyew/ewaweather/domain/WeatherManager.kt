package com.yelloyew.ewaweather.domain

import android.util.Log
import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.Weather
import kotlinx.coroutines.delay
import javax.inject.Inject

private const val TAG = "tag11"

class WeatherManager @Inject constructor(
    private val weatherRepo: WeatherRepo
) {
    init {
        Log.d(TAG, this.toString())
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
}