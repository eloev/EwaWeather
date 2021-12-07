package com.yelloyew.ewaweather.presentation.ui.first

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yelloyew.ewaweather.domain.WeatherManager
import com.yelloyew.ewaweather.domain.model.RequestParams
import com.yelloyew.ewaweather.domain.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherManager: WeatherManager
) : ViewModel() {

    private var weather: MutableLiveData<Weather> = MutableLiveData()

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }

    fun getWeatherNow(): MutableLiveData<Weather> {
        var coroutineCompleted = false
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            while (!coroutineCompleted){
                val newWeather = weatherManager.getWeather()
                coroutineCompleted = if (newWeather == null) {
                    delay(5000L)
                    false
                } else {
                    weather.postValue(newWeather!!)
                    true
                }
            }
        }
        return weather
    }

    suspend fun canOpenForecast(): Boolean {
        return weatherManager.canOpenForecast()
    }

    fun setRequestParams(requestParams: RequestParams) {
        weatherManager.setRequestParams(requestParams)
    }
}