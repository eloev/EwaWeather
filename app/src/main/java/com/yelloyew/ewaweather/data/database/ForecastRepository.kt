package com.yelloyew.ewaweather.data.database

import com.yelloyew.ewaweather.data.database.model.ForecastRoom
import java.time.LocalDate

interface ForecastRepository {

    suspend fun getForecasts() : List<ForecastRoom>

    suspend fun addForecast(forecast: ForecastRoom)

    suspend fun deleteForecast(date: LocalDate)

    suspend fun updateForecast(forecast: ForecastRoom)
}