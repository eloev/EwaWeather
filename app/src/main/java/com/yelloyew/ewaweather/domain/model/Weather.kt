package com.yelloyew.ewaweather.domain.model

import java.time.LocalDateTime

data class Weather(
    val city: String,
    val temperature: String,
    val pressure: String,
    val humility: String,
    val date: LocalDateTime
)