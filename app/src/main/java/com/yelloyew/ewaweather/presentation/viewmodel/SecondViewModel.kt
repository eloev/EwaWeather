package com.yelloyew.ewaweather.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yelloyew.ewaweather.data.network.YaWeatherClient
import com.yelloyew.ewaweather.data.network.model.YandexWeather
import retrofit2.Call
import retrofit2.Response
import java.util.*

private const val TAG = "tag7"

class SecondViewModel : ViewModel() {

    private var weatherNextDays: MutableLiveData<YandexWeather> = MutableLiveData()
    private val language = Locale.getDefault().language

    fun getWeatherNextDay(): MutableLiveData<YandexWeather> {
        YaWeatherClient.apiInstance
            .weatherSevenDays(language = language)
            .enqueue(object : retrofit2.Callback<YandexWeather> {
                override fun onResponse(
                    call: Call<YandexWeather>,
                    response: Response<YandexWeather>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, response.toString())
                        weatherNextDays.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<YandexWeather>, t: Throwable) {
                    Log.d(TAG, "error " + t.message.toString())
                }
            })
        return weatherNextDays
    }
}