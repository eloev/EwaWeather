package com.yelloyew.ewaweather.domain.model

import java.time.LocalDateTime

data class Weather(
    val id: Int,
    val description: String,
    val city: String,
    val temperature: String,
    val pressure: String,
    val humility: String,
    val date: LocalDateTime
)