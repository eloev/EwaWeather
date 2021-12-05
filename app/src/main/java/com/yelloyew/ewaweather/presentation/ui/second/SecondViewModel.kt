package com.yelloyew.ewaweather.presentation.ui.second

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yelloyew.ewaweather.domain.WeatherManager
import com.yelloyew.ewaweather.domain.WeatherRepo
import com.yelloyew.ewaweather.domain.model.Forecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondViewModel @Inject constructor(
    private val weatherManager: WeatherManager
): ViewModel() {

    var forecasts: MutableLiveData<MutableList<Forecast>> = MutableLiveData()

    fun getWeatherNextDay(): MutableLiveData<MutableList<Forecast>> {
        viewModelScope.launch (Dispatchers.IO){
            forecasts.postValue(weatherManager.getForecast())
        }
        return forecasts
    }
}