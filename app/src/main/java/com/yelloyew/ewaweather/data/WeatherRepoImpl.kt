package com.yelloyew.ewaweather.data

import android.util.Log
import com.yelloyew.ewaweather.data.database.ForecastRepository
import com.yelloyew.ewaweather.data.network.ForecastService
import com.yelloyew.ewaweather.data.network.WeatherService
import com.yelloyew.ewaweather.domain.WeatherRepo
import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.Weather
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import retrofit2.HttpException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TAG = "tag10 WeatherRepoImpl"

@BoundTo(supertype = WeatherRepo::class, component = SingletonComponent::class)
class WeatherRepoImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val forecastService: ForecastService,
    private val weatherPrefs: WeatherPreferences,
    private val forecastRepository: ForecastRepository
) : WeatherRepo {

    override suspend fun getWeather(): Weather? {
        var weather: Weather? = weatherPrefs.getLastWeather()
        // open weather update data each 10 minutes
        val currentTime: LocalDateTime = LocalDateTime.now().plusMinutes(8)
        if (weather == null) {
            weather = getNetworkWeather()
            if (weather == null){
                return null
            }
        } else if (currentTime <= weather.date) {
            Log.d(TAG, "$currentTime, ${weather.date}")
            weather = getNetworkWeather()
        }
        if (weather != null) {
            weatherPrefs.setLastWeather(weather)
        }
        return weather
    }

    private suspend fun getNetworkWeather(): Weather? {
        try {
            val response = weatherService.weatherNow()
            return if (response.isSuccessful) {
                Log.d(TAG, response.toString())
                Weather(
                    response.body()!!.name,
                    response.body()!!.main["temp"]!!.roundToInt().toString(),
                    response.body()!!.main["pressure"].toString(),
                    response.body()!!.main["humidity"].toString(),
                    LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(response.body()!!.dt),
                        TimeZone.getDefault().toZoneId()
                    )
                )
            } else {
                Log.d(TAG, HttpException(response).toString())
                return null
            }
        }
        catch (e: Exception){
            Log.d(TAG, e.toString())
            return null
        }
    }

    override suspend fun getForecast(): MutableList<Forecast> {
        var forecasts = forecastRepository.getForecasts()
        val lastUpdateTime = weatherPrefs.getForecastUpdateTime()
        val currentTime: LocalDateTime = LocalDateTime.now().plusMinutes(15)
        if (forecasts == emptyList<Forecast>()) {
            forecasts = getNetworkForecast()
            if (forecasts == emptyList<Forecast>()){
                return forecasts
            }
            for (i in forecasts) {
                forecastRepository.addForecast(i)
            }
        } else if (currentTime <= lastUpdateTime) {
            Log.d(TAG, "$currentTime, $lastUpdateTime")
            val newForecast = getNetworkForecast()
            for (i in 0 until forecasts.size) {
                loop@ for (y in newForecast) {
                    if (forecasts[i] == y) {
                        break@loop
                    } else if (i == forecasts.size - 1) {
                        forecastRepository.deleteForecast(forecasts[i].date)
                        forecastRepository.updateForecast(y)
                        forecasts[i] = y
                    }
                }
            }
        }
        return forecasts
    }

    private suspend fun getNetworkForecast(): MutableList<Forecast> {
        val forecasts = mutableListOf<Forecast>()
        try {
            val response = forecastService.weatherSevenDays()
            if (response.isSuccessful) {
                Log.d(TAG, response.toString())
                for ((i, _) in response.body()!!.forecasts.withIndex()) {
                    with(response.body()!!.forecasts[i]) {
                        val date =
                            LocalDate.parse(response.body()!!.forecasts[i].date)!!
                        val morningTemp = parts["morning"]!!.temp_avg
                        val morningCloud = parts["morning"]!!.cloudness
                        val morningHumidity = parts["morning"]!!.humidity
                        val morningPressure = parts["morning"]!!.pressure_mm
                        val dayTemp = parts["day"]!!.temp_avg
                        val dayCloud = parts["day"]!!.cloudness
                        val dayHumidity = parts["day"]!!.humidity
                        val dayPressure = parts["day"]!!.pressure_mm
                        val eveningTemp = parts["evening"]!!.temp_avg
                        val eveningCloud = parts["evening"]!!.cloudness
                        val eveningHumidity = parts["evening"]!!.humidity
                        val eveningPressure = parts["evening"]!!.pressure_mm
                        val nightTemp = parts["night"]!!.temp_avg
                        val nightCloud = parts["night"]!!.cloudness
                        val nightHumidity = parts["night"]!!.humidity
                        val nightPressure = parts["night"]!!.pressure_mm
                        forecasts.add(
                            Forecast(
                                date,
                                morningTemp, morningCloud, morningHumidity, morningPressure,
                                dayTemp, dayCloud, dayHumidity, dayPressure,
                                eveningTemp, eveningCloud, eveningHumidity, eveningPressure,
                                nightTemp, nightCloud, nightHumidity, nightPressure
                            )
                        )
                    }
                }
                weatherPrefs.setForecastUpdateTime()
                return forecasts
            } else {
                Log.d(TAG, HttpException(response).toString())
                return forecasts
            }
        }
        catch (e: Exception){
            Log.d(TAG, e.toString())
            return forecasts
        }
    }
}