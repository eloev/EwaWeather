package com.yelloyew.ewaweather.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.yelloyew.ewaweather.data.database.ForecastDatabase
import com.yelloyew.ewaweather.data.database.model.ForecastRoom
import com.yelloyew.ewaweather.data.network.ForecastService
import com.yelloyew.ewaweather.data.network.WeatherService
import com.yelloyew.ewaweather.domain.Repository
import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.domain.model.RequestParams
import com.yelloyew.ewaweather.domain.model.Weather
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import retrofit2.HttpException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@BoundTo(supertype = Repository::class, component = SingletonComponent::class)
class RepositoryImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val forecastService: ForecastService,
    @ApplicationContext private val context: Context
) : Repository {

    private val database: ForecastDatabase = Room.databaseBuilder(
        context.applicationContext,
        ForecastDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val forecastDao = database.forecastDao()

    override suspend fun getNetworkWeather(requestParams: RequestParams?): Weather? {
        try {
            val latitude: Double = requestParams!!.latitude
            val longitude: Double = requestParams.longitude
            val language: String = requestParams.language
            val response = weatherService.weatherNow(
                latitude = latitude,
                longitude = longitude,
                language = language
            )
            return if (response.isSuccessful) {
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
        val latitude: Double = requestParams!!.latitude
        val longitude: Double = requestParams.longitude
        val language: String = requestParams.language
        val forecasts = mutableListOf<Forecast>()
        try {
            val response = forecastService.weatherSevenDays(
                latitude = latitude,
                longitude = longitude,
                language = language
            )
            if (response.isSuccessful) {
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

    override suspend fun getForecasts(): MutableList<Forecast> =
        forecastRoomListToForecastList(forecastDao.getForecasts())

    override suspend fun addForecast(forecast: Forecast) =
        forecastDao.addForecast(forecastToForecastRoom(forecast))

    override suspend fun deleteForecast(date: LocalDate) = forecastDao.deleteForecast(date)

    override suspend fun updateForecast(forecast: Forecast) =
        forecastDao.updateForecast(forecastToForecastRoom(forecast))

    private fun forecastToForecastRoom(forecast: Forecast): ForecastRoom {
        val date = forecast.date
        val morningTemp: Int = forecast.morning_temp
        val morningCloud: Float = forecast.morning_cloud
        val morningHumidity: Int = forecast.morning_humidity
        val morningPressure: Int = forecast.morning_pressure
        val dayTemp: Int = forecast.day_temp
        val dayCloud: Float = forecast.day_cloud
        val dayHumidity: Int = forecast.day_humidity
        val dayPressure: Int = forecast.day_pressure
        val eveningTemp: Int = forecast.evening_temp
        val eveningCloud: Float = forecast.evening_cloud
        val eveningHumidity: Int = forecast.evening_humidity
        val eveningPressure: Int = forecast.evening_pressure
        val nightTemp: Int = forecast.night_temp
        val nightCloud: Float = forecast.night_cloud
        val nightHumidity: Int = forecast.night_humidity
        val nightPressure: Int = forecast.night_pressure
        return ForecastRoom(
            date,
            morningTemp, morningCloud, morningHumidity, morningPressure,
            dayTemp, dayCloud, dayHumidity, dayPressure,
            eveningTemp, eveningCloud, eveningHumidity, eveningPressure,
            nightTemp, nightCloud, nightHumidity, nightPressure
        )
    }

    private fun forecastRoomListToForecastList(forecastRoomList: List<ForecastRoom>): MutableList<Forecast> {
        val forecasts: MutableList<Forecast> = mutableListOf()
        forecastRoomList.map { forecastRoom ->
            val date = forecastRoom.date
            val morningTemp: Int = forecastRoom.morning_temp
            val morningCloud: Float = forecastRoom.morning_cloud
            val morningHumidity: Int = forecastRoom.morning_humidity
            val morningPressure: Int = forecastRoom.morning_pressure
            val dayTemp: Int = forecastRoom.day_temp
            val dayCloud: Float = forecastRoom.day_cloud
            val dayHumidity: Int = forecastRoom.day_humidity
            val dayPressure: Int = forecastRoom.day_pressure
            val eveningTemp: Int = forecastRoom.evening_temp
            val eveningCloud: Float = forecastRoom.evening_cloud
            val eveningHumidity: Int = forecastRoom.evening_humidity
            val eveningPressure: Int = forecastRoom.evening_pressure
            val nightTemp: Int = forecastRoom.night_temp
            val nightCloud: Float = forecastRoom.night_cloud
            val nightHumidity: Int = forecastRoom.night_humidity
            val nightPressure: Int = forecastRoom.night_pressure
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
        return forecasts
    }

    companion object{
        private const val DATABASE_NAME = "saved_forecasts"
        private const val TAG = "tag10 RepositoryImpl"
    }
}