package com.yelloyew.ewaweather.data.model

data class YaWeather(
    val date: String,
    val morning_temp: Int,
    val morning_cloud: Int,
    val morning_humidity: Int,
    val morning_pressure: Int,
    val day_temp: Int,
    val day_cloud: Int,
    val day_humidity: Int,
    val day_pressure: Int,
    val evening_temp: Int,
    val evening_cloud: Int,
    val evening_humidity: Int,
    val evening_pressure: Int,
    val night_temp: Int,
    val night_cloud: Int,
    val night_humidity: Int,
    val night_pressure: Int
)