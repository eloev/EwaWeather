package com.yelloyew.ewaweather.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yelloyew.ewaweather.domain.model.Forecast
import java.time.LocalDate

@Dao
interface ForecastDAO {

    @Query("SELECT * FROM forecast")
    suspend fun getForecasts(): MutableList<Forecast>

    @Query("SELECT * FROM forecast WHERE date=(:date)")
    suspend fun getForecast(date: LocalDate) : Forecast

    @Insert
    suspend fun addForecast(forecast: Forecast)

    @Query("DELETE FROM forecast WHERE date=(:date)")
    suspend fun deleteForecast(date: LocalDate)

    @Update
    suspend fun updateForecast(forecast: Forecast)
}