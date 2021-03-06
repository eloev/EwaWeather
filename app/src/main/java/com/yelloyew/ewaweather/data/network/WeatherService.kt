package com.yelloyew.ewaweather.data.network

import com.yelloyew.ewaweather.data.network.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather?")
    suspend fun weatherNow(
        @Query("lat") latitude : Double = 55.75,
        @Query("lon") longitude : Double = 37.61,
        @Query("appid") api_key : String = "78c5d42c8f3f8fa663f0f461c1264e94",
        @Query("lang") language : String = "ru",
        @Query("mode") response_format : String = "json",
        @Query("units") units : String = "metric"
    ) : Response<WeatherResponse>
}