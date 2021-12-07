package com.yelloyew.ewaweather.domain

import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.RequestParams
import com.yelloyew.ewaweather.domain.model.Weather

interface WeatherRepo {

    suspend fun getNetworkWeather(requestParams: RequestParams?) : Weather?

    suspend fun getNetworkForecast(requestParams: RequestParams?) : MutableList<Forecast>
}