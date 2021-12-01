package com.yelloyew.ewaweather.data.network.model

data class YandexForecasts(
    val date: String,
    val parts: Map<String, YandexParts>
)