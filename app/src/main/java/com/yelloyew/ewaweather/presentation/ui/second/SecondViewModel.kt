package com.yelloyew.ewaweather.presentation.ui.second

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yelloyew.ewaweather.domain.WeatherManager
import com.yelloyew.ewaweather.domain.model.Forecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondViewModel @Inject constructor(
    private val weatherManager: WeatherManager
) : ViewModel() {

    private var forecasts: MutableLiveData<List<Forecast>> = MutableLiveData()

    fun getWeatherNextDay(): MutableLiveData<List<Forecast>> {
        var coroutineCompleted = false
        viewModelScope.launch(Dispatchers.IO) {
            forecasts.postValue(weatherManager.getForecast())
            while (!coroutineCompleted) {
                val newWeather = weatherManager.getForecast()
                coroutineCompleted = if (newWeather == emptyList<Forecast>()) {
                    delay(5000L)
                    false
                } else {
                    forecasts.postValue(newWeather)
                    true
                }
            }
        }
        return forecasts
    }
}