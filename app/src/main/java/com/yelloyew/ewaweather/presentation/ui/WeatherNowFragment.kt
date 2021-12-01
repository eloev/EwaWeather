package com.yelloyew.ewaweather.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.yelloyew.ewaweather.data.WeatherPreferences
import com.yelloyew.ewaweather.data.WeatherWorker
import com.yelloyew.ewaweather.databinding.FragmentWeatherNowBinding
import com.yelloyew.ewaweather.presentation.viewmodel.MainViewModel
import java.util.concurrent.TimeUnit
import com.yelloyew.ewaweather.R

private const val DATA_UPDATE = "DATA_UPDATE"

class WeatherNowFragment : Fragment() {

    private var _binding: FragmentWeatherNowBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherNowBinding.inflate(inflater, container, false)
        updateData()

        val whatIsTheWeatherLikeToday = WeatherPreferences.getLastWeather(requireContext())
        setDataToLayout(whatIsTheWeatherLikeToday)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)


        binding.apply {
            linearRoot.setOnClickListener {
                val action = WeatherNowFragmentDirections.actionWeatherNowFragmentToWeatherNextDayFragment()
                findNavController().navigate(action)
            }
            refresh.isRefreshing = true
            refresh.setOnRefreshListener {
                refresh.isRefreshing = false
                viewModel.getWeatherNow()
            }
        }

        viewModel.getWeatherNow().observe(
            viewLifecycleOwner,{
                setDataToLayout(it)
                binding.refresh.isRefreshing = false
            }
        )

        return binding.root
    }

    private fun setDataToLayout(string: String){
        // имеет вот такой вид и в таком же порядке собирается
        // Москва,-2.35,947.0,91.0,30-11-2021 19:31
        // name=Москва, temp=-2.35, pressure=974.0, humidity=91.0, date=30-11-2021 19:31

        val items: List<String> = string.split(",")
        if (items.size == 5){
            binding.cityTextview.text = items[0]
            binding.temperatureTextview.text = ("t " + items[1] + "°С")
            binding.pressureTextview.text = (getString(R.string.pressure) + " " + items[2])
            binding.humidityTextview.text = (getString(R.string.humility) + " " + items[3])
            binding.dateTextview.text = items[4]
            WeatherPreferences.setLastWeather(requireContext(), string)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateData(){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build()
        val periodicRequest = PeriodicWorkRequest
            .Builder(WeatherWorker::class.java, 1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context!!)
            .enqueueUniquePeriodicWork(
                DATA_UPDATE,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
    }
}