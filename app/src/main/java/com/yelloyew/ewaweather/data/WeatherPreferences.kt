package com.yelloyew.ewaweather.data

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.yelloyew.ewaweather.domain.model.Weather
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import javax.inject.Inject

private const val PREF_LAST_WEATHER = "lastWeather"
private const val PREF_LAST_FORECAST_UPDATE = "lastForecastUpdate"

class WeatherPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getLastWeather(): Weather? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val item = prefs.getString(PREF_LAST_WEATHER, "")!!
        if (item.isNotBlank()){
            val items = item.split(",")
            // имеет такой вид и в таком же порядке собирается
            // Москва,-2.35,947.0,91.0,30-11-2021 19:31
            // city=Москва, temp=-2.35, pressure=974.0, humility=91.0, date=30-11-2021 19:31
            if (items.size == 5) {
                return Weather(
                    city = items[0],
                    temperature = items[1],
                    pressure = items[2],
                    humility = items[3],
                    date = LocalDateTime.parse(items[4])
                )
            }
        }
        return null
    }

    fun setLastWeather(weather: Weather) {
        val output = StringBuilder()
        with(weather) {
            output.append("$city,")
            output.append("$temperature,")
            output.append("$pressure,")
            output.append("$humility,")
            output.append("$date")
        }
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(PREF_LAST_WEATHER, output.toString()) }
    }

     fun getForecastUpdateTime(): LocalDateTime? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val item = prefs.getString(PREF_LAST_FORECAST_UPDATE, "")!!
        return if (item.isNotBlank()){
            LocalDateTime.parse(item)
        } else {
            null
        }
    }

    fun setForecastUpdateTime() {
        val output = LocalDateTime.now().format(ISO_DATE_TIME)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(PREF_LAST_FORECAST_UPDATE, output.toString()) }
    }
}