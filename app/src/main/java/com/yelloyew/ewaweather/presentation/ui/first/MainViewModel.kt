package com.yelloyew.ewaweather.presentation.ui.first

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yelloyew.ewaweather.domain.WeatherManager
import com.yelloyew.ewaweather.domain.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherManager: WeatherManager
) : ViewModel() {

    private var weather: MutableLiveData<Weather> = MutableLiveData()
    // private val language = Locale.getDefault().language

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }

    fun getWeatherNow(): MutableLiveData<Weather> {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            weather.postValue(weatherManager.getWeather())
        }
        return weather
    }

    suspend fun getForecastScreen(): Boolean {
        return weatherManager.getForecastScreen()
    }
}