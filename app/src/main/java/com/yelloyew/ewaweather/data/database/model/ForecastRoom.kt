package com.yelloyew.ewaweather.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class ForecastRoom(
    @PrimaryKey val date: LocalDate,
    val morning_temp: Int,
    val morning_cloud: Float,
    val morning_humidity: Int,
    val morning_pressure: Int,
    val day_temp: Int,
    val day_cloud: Float,
    val day_humidity: Int,
    val day_pressure: Int,
    val evening_temp: Int,
    val evening_cloud: Float,
    val evening_humidity: Int,
    val evening_pressure: Int,
    val night_temp: Int,
    val night_cloud: Float,
    val night_humidity: Int,
    val night_pressure: Int
)