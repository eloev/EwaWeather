package com.yelloyew.ewaweather.presentation.ui.first

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yelloyew.ewaweather.databinding.FragmentWeatherNowBinding
import com.yelloyew.ewaweather.R
import com.yelloyew.ewaweather.domain.model.Weather
import com.yelloyew.ewaweather.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeatherNowFragment : Fragment() {

    private var _binding: FragmentWeatherNowBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherNowBinding.inflate(inflater, container, false)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)


        with(binding) {
            linearRoot.setOnClickListener {
                lifecycleScope.launch {
                    if (viewModel.canOpenForecast()){
                        val action =
                            WeatherNowFragmentDirections.actionWeatherNowFragmentToWeatherNextDayFragment()
                        findNavController().navigate(action)
                    }
                    else {
                        Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            refresh.isRefreshing = true
            refresh.setOnRefreshListener {
                refresh.isRefreshing = false
                viewModel.getWeatherNow()
            }
        }

        viewModel.getWeatherNow().observe(
            viewLifecycleOwner, {
                setDataToLayout(it)
                binding.refresh.isRefreshing = false
            }
        )

        return binding.root
    }

    private fun setDataToLayout(weather: Weather) {
        with(binding){
            with(weather){
                cityTextview.text = city
                temperatureTextview.text = ("t $temperature°С")
                pressureTextview.text = (getString(R.string.pressure) + " " + pressure)
                humidityTextview.text = (getString(R.string.humility) + " " + humility)
                dateTextview.text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}