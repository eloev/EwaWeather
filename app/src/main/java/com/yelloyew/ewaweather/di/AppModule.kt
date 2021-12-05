package com.yelloyew.ewaweather.di

import com.yelloyew.ewaweather.data.network.ForecastService
import com.yelloyew.ewaweather.data.network.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun weatherClient(): WeatherService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherService::class.java)
    }

    @Singleton
    @Provides
    fun forecastClient(): ForecastService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weather.yandex.ru/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ForecastService::class.java)

    }
}