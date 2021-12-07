package com.yelloyew.ewaweather.data

import android.util.Log
import com.yelloyew.ewaweather.data.network.ForecastService
import com.yelloyew.ewaweather.data.network.WeatherService
import com.yelloyew.ewaweather.domain.WeatherRepo
import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.RequestParams
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
    private val weatherPrefs: WeatherPreferences
) : WeatherRepo {

    override suspend fun getNetworkWeather(requestParams: RequestParams?): Weather? {
        try {
            val latitude : Double = requestParams!!.latitude
            val longitude : Double = requestParams.longitude
            val language : String = requestParams.language
            val response = weatherService.weatherNow(
                latitude = latitude,
                longitude = longitude,
                language = language
            )
            return if (response.isSuccessful) {
                Log.d(TAG, response.body().toString())
                Weather(
                    id = response.body()!!.weather[0].id,
                    description = response.body()!!.weather[0].description,
                    city = response.body()!!.name,
                    temperature = response.body()!!.main["temp"]!!.roundToInt().toString(),
                    pressure = response.body()!!.main["pressure"].toString(),
                    humility = response.body()!!.main["humidity"].toString(),
                    date = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(response.body()!!.dt),
                        TimeZone.getDefault().toZoneId()
                    )
                )
            } else {
                Log.d(TAG, HttpException(response).toString())
                return null
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            return null
        }
    }

    override suspend fun getNetworkForecast(requestParams: RequestParams?): MutableList<Forecast> {
        val latitude : Double = requestParams!!.latitude
        val longitude : Double = requestParams.longitude
        val language : String = requestParams.language
        val forecasts = mutableListOf<Forecast>()
        try {
            val response = forecastService.weatherSevenDays(
                latitude = latitude,
                longitude = longitude,
                language = language
            )
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
                Log.d(TAG, "update time for forecast")
                return forecasts
            } else {
                Log.d(TAG, HttpException(response).toString())
                return forecasts
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            return forecasts
        }
    }
}