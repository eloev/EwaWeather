package com.yelloyew.ewaweather.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yelloyew.ewaweather.data.database.model.ForecastRoom
import java.time.LocalDate

@Dao
interface ForecastDAO {

    @Query("SELECT * FROM forecastRoom")
    suspend fun getForecasts(): MutableList<ForecastRoom>

    @Query("SELECT * FROM forecastRoom WHERE date=(:date)")
    suspend fun getForecast(date: LocalDate) : ForecastRoom

    @Insert
    suspend fun addForecast(forecastRoom: ForecastRoom)

    @Query("DELETE FROM forecastRoom WHERE date=(:date)")
    suspend fun deleteForecast(date: LocalDate)

    @Update
    suspend fun updateForecast(forecastRoom: ForecastRoom)
}