package com.yelloyew.ewaweather.data

import com.yelloyew.ewaweather.domain.model.Weather
import java.time.LocalDateTime

interface WeatherPrefs {

    suspend fun setLastWeather(weather: Weather)

    suspend fun getLastWeather() : Weather?

    suspend fun setForecastUpdateTime()

    suspend fun getForecastUpdateTime() : LocalDateTime?
}