package com.yelloyew.ewaweather.data.network.model

data class WeatherResponse(
    val weather: ArrayList<WeatherWeather>,
    val main: Map<String, Double>,
    val name: String,
    val dt: Long
)