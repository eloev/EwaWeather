package com.yelloyew.ewaweather.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yelloyew.ewaweather.R
import com.yelloyew.ewaweather.data.model.YaWeather
import com.yelloyew.ewaweather.data.network.model.YandexWeather
import com.yelloyew.ewaweather.databinding.ItemNextdaysBinding

class WeatherNextDaysRecycler : RecyclerView.Adapter<WeatherNextDaysRecycler.ViewHolder>() {
    private var yaWeather: MutableList<YaWeather> = mutableListOf()

    fun setData(newWeather: YandexWeather) {
        for ((i, _) in newWeather.forecasts.withIndex()) {
            newWeather.forecasts[i].apply {
                val date = date.replace('-', '.').split(".").reversed()
                    .joinToString(".")
                val morningTemp = parts["morning"]!!.temp_avg
                val morningCloud = parts["morning"]!!.cloudness
                val morningHumidity = parts["morning"]!!.humidity
                val morningPressure = parts["morning"]!!.pressure_mm
                val dayTemp = parts["day"]!!.temp_avg
                val dayCloud = parts["day"]!!.cloudness
                val dayHumidity = parts["day"]!!.humidity
                val dayPressure = parts["day"]!!.pressure_mm
                val eveningTemp = parts["evening"]!!.temp_avg
                val eveningCloud = parts["evening"]!!.cloudness
                val eveningHumidity = parts["evening"]!!.humidity
                val eveningPressure = parts["evening"]!!.pressure_mm
                val nightTemp = parts["night"]!!.temp_avg
                val nightCloud = parts["night"]!!.cloudness
                val nightHumidity = parts["night"]!!.humidity
                val nightPressure = parts["night"]!!.pressure_mm
                yaWeather.add(
                    YaWeather(
                        date,
                        morningTemp, morningCloud, morningHumidity, morningPressure,
                        dayTemp, dayCloud, dayHumidity, dayPressure,
                        eveningTemp, eveningCloud, eveningHumidity, eveningPressure,
                        nightTemp, nightCloud, nightHumidity, nightPressure
                    )
                )
            }

        }
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
        val weather = yaWeather[position]
        holder.bind(weather)
    }

    override fun getItemCount() = yaWeather.size


    inner class ViewHolder(private val bindingItem: ItemNextdaysBinding) :
        RecyclerView.ViewHolder(bindingItem.root) {
        private lateinit var weather: YaWeather

        fun bind(weather: YaWeather) {
            this.weather = weather
            val pressure = itemView.context.getString(R.string.pressure)
            val humidity = itemView.context.getString(R.string.humility)
            bindingItem.apply {
                dateTextview.text = weather.date
                morningTemperatureTextview.text = ("t " + weather.morning_temp.toString() + "°С")
                morningHumidityTextview.text =
                    (humidity + " " + weather.morning_humidity.toString())
                morningPressureTextview.text =
                    (pressure + " " + weather.morning_pressure.toString())
                dayTemperatureTextview.text = ("t " + weather.day_temp.toString() + "°С")
                dayHumidityTextview.text = (humidity + " " + weather.day_humidity.toString())
                dayPressureTextview.text = (pressure + " " + weather.day_pressure.toString())
                eveningTemperatureTextview.text = ("t " + weather.evening_temp.toString() + "°С")
                eveningHumidityTextview.text =
                    (humidity + " " + weather.evening_humidity.toString())
                eveningPressureTextview.text =
                    (pressure + " " + weather.evening_pressure.toString())
                nightTemperatureTextview.text = ("t " + weather.night_temp.toString() + "°С")
                nightHumidityTextview.text = (humidity + " " + weather.night_humidity.toString())
                nightPressureTextview.text = (pressure + " " + weather.night_pressure.toString())
            }

            itemView.setOnClickListener {
            }
        }
    }
}