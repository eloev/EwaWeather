package com.yelloyew.ewaweather.domain

import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.RequestParams
import com.yelloyew.ewaweather.domain.model.Weather
import java.time.LocalDate

interface Repository {

    suspend fun getNetworkWeather(requestParams: RequestParams?) : Weather?

    suspend fun getNetworkForecast(requestParams: RequestParams?) : MutableList<Forecast>

    suspend fun getForecasts() : List<Forecast>

    suspend fun addForecast(forecast: Forecast)

    suspend fun deleteForecast(date: LocalDate)

    suspend fun updateForecast(forecast: Forecast)
}