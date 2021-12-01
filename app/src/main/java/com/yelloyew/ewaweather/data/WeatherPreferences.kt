package com.yelloyew.ewaweather.data

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

private const val PREF_LAST_WEATHER = "lastWeather"

object WeatherPreferences {

    fun getLastWeather(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_LAST_WEATHER, "")!!
    }

    fun setLastWeather(context: Context, response: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(PREF_LAST_WEATHER, response) }
    }
}