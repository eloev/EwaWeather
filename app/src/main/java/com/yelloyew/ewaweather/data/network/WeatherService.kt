package com.yelloyew.ewaweather.data.network

import com.yelloyew.ewaweather.data.network.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather?")
    fun weatherNow(
        @Query("lat") latitude : Float = 55.75f,
        @Query("lon") longitude : Float = 37.61f,
        @Query("appid") api_key : String = "78c5d42c8f3f8fa663f0f461c1264e94",
        @Query("lang") language : String = "ru",
        @Query("mode") response_format : String = "json",
        @Query("units") units : String = "metric"
    ) : Call<WeatherResponse>
}