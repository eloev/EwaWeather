package com.yelloyew.ewaweather.presentation.ui.second.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yelloyew.ewaweather.R
import com.yelloyew.ewaweather.domain.model.Forecast
import com.yelloyew.ewaweather.databinding.ItemNextdaysBinding
import java.time.format.DateTimeFormatter

class WeatherNextDaysRecycler : RecyclerView.Adapter<WeatherNextDaysRecycler.ViewHolder>() {
    private var forecasts: List<Forecast> = mutableListOf()

    fun setData(newWeather: List<Forecast>) {
        forecasts = newWeather
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNextdaysBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecast = forecasts[position]
        holder.bind(forecast)
    }

    override fun getItemCount() = forecasts.size


    inner class ViewHolder(private val bindingItem: ItemNextdaysBinding) :
        RecyclerView.ViewHolder(bindingItem.root) {
        private lateinit var forecast: Forecast

        fun bind(forecast: Forecast) {
            this.forecast = forecast
            val pressure = itemView.context.getString(R.string.pressure)
            val humidity = itemView.context.getString(R.string.humility)
            with(bindingItem) {
                dateTextview.text = forecast.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                morningTemperatureTextview.text = ("t " + forecast.morning_temp.toString() + "°С")
                morningHumidityTextview.text =
                    (humidity + " " + forecast.morning_humidity.toString())
                morningPressureTextview.text =
                    (pressure + " " + forecast.morning_pressure.toString())
                dayTemperatureTextview.text = ("t " + forecast.day_temp.toString() + "°С")
                dayHumidityTextview.text = (humidity + " " + forecast.day_humidity.toString())
                dayPressureTextview.text = (pressure + " " + forecast.day_pressure.toString())
                eveningTemperatureTextview.text = ("t " + forecast.evening_temp.toString() + "°С")
                eveningHumidityTextview.text =
                    (humidity + " " + forecast.evening_humidity.toString())
                eveningPressureTextview.text =
                    (pressure + " " + forecast.evening_pressure.toString())
                nightTemperatureTextview.text = ("t " + forecast.night_temp.toString() + "°С")
                nightHumidityTextview.text = (humidity + " " + forecast.night_humidity.toString())
                nightPressureTextview.text = (pressure + " " + forecast.night_pressure.toString())
            }
        }
    }
}