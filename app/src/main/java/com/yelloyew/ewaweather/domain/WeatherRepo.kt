package com.yelloyew.ewaweather.domain

import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.Weather

interface WeatherRepo {

    suspend fun getWeather() : Weather?

    suspend fun getForecast() : MutableList<Forecast>
}