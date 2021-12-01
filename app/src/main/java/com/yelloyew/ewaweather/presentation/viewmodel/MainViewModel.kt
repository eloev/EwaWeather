package com.yelloyew.ewaweather.presentation.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yelloyew.ewaweather.data.network.WeatherClient
import com.yelloyew.ewaweather.data.network.model.WeatherResponse
import retrofit2.Call
import retrofit2.Response
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

private const val TAG = "tag6"

class MainViewModel : ViewModel() {

    private var weather: MutableLiveData<String> = MutableLiveData()
    private val language = Locale.getDefault().language

    fun getWeatherNow(): MutableLiveData<String> {
        WeatherClient.apiInstance
            .weatherNow(language = language)
            .enqueue(object : retrofit2.Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, response.toString())
                        val items = StringBuilder()
                        items.append(response.body()!!.name + ",")
                        items.append(response.body()!!.main["temp"]!!.roundToInt().toString() + ",")
                        items.append(response.body()!!.main["pressure"].toString() + ",")
                        items.append(response.body()!!.main["humidity"].toString() + ",")
                        items.append(getCurrentDate(response.body()!!.dt))
                        weather.postValue(items.toString())
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.d(TAG, "error " + t.message.toString())
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            getWeatherNow()
                        },
                        5000
                    )
                }
            })
        return weather
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(data: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val date = Date(data * 1000)
        sdf.format(date)
        return sdf.format(date)
    }
}