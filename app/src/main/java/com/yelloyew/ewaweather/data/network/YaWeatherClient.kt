package com.yelloyew.ewaweather.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.weather.yandex.ru/v2/"

object YaWeatherClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInstance: YandexWeatherService = retrofit.create(YandexWeatherService::class.java)
}