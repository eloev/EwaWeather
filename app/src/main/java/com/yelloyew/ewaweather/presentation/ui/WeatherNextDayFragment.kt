package com.yelloyew.ewaweather.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yelloyew.ewaweather.databinding.FragmentWeatherNextDayBinding
import com.yelloyew.ewaweather.presentation.adapter.WeatherNextDaysRecycler
import com.yelloyew.ewaweather.presentation.viewmodel.SecondViewModel

class WeatherNextDayFragment : Fragment() {

    private var _binding: FragmentWeatherNextDayBinding? = null
    private val binding get() = _binding!!

    private var adapter = WeatherNextDaysRecycler()

    private val viewModel: SecondViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherNextDayBinding.inflate(inflater, container, false)

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

            refresh.isRefreshing = true
            refresh.setOnRefreshListener {
                refresh.isRefreshing = false
                viewModel.getWeatherNextDay()
            }
        }

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getWeatherNextDay().observe(
            viewLifecycleOwner,{
                adapter.setData(it)
                binding.refresh.isRefreshing = false
            }
        )

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}