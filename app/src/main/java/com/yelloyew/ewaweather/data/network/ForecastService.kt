package com.yelloyew.ewaweather.data.network

import com.yelloyew.ewaweather.data.network.model.YandexWeather
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ForecastService {

    @Headers("X-Yandex-API-Key: 64d1b01c-af63-4f41-abb1-6cb31ad20be9")
    @GET("forecast?")
    suspend fun weatherSevenDays(
        @Query("lat") latitude : Float = 55.75f,
        @Query("lon") longitude : Float = 37.61f,
        @Query("lang") language : String = "ru",
        @Query("limit") count_days : Int = 7,
        @Query("hours") per_hour : Boolean = false
    ) : Response<YandexWeather>
}