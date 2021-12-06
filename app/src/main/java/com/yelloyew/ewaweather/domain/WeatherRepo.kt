package com.yelloyew.ewaweather.domain

import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.Weather

interface WeatherRepo {

    suspend fun getNetworkWeather() : Weather?

    suspend fun getNetworkForecast() : MutableList<Forecast>
}