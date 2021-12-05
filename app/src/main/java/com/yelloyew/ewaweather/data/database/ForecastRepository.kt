package com.yelloyew.ewaweather.data.database

import com.yelloyew.ewaweather.domain.model.Forecast
import java.time.LocalDate

interface ForecastRepository {

    suspend fun getForecasts() : MutableList<Forecast>

    suspend fun addForecast(forecast: Forecast)

    suspend fun deleteForecast(date: LocalDate)

    suspend fun updateForecast(forecast: Forecast)
}