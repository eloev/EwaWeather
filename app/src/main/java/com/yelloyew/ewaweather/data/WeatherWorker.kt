package com.yelloyew.ewaweather.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yelloyew.ewaweather.data.network.WeatherClient
import com.yelloyew.ewaweather.data.network.model.WeatherResponse
import retrofit2.Call
import retrofit2.Response
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "worker"

class WeatherWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        WeatherClient.apiInstance
            .weatherNow()
            .enqueue(object : retrofit2.Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val items = StringBuilder()
                        items.append(response.body()!!.name + ",")
                        items.append(response.body()!!.main["temp"].toString() + ",")
                        items.append(response.body()!!.main["pressure"].toString() + ",")
                        items.append(response.body()!!.main["humidity"].toString() + ",")
                        items.append(getCurrentDate(response.body()!!.dt))
                        WeatherPreferences.setLastWeather(context, items.toString())
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.d(TAG, "error " + t.message.toString())

                }
            })

        return Result.success()
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(data: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val date = Date(data * 1000)
        sdf.format(date)
        return sdf.format(date)
    }
}