package com.example.dashboardsmarthome.ui.weatherForecast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dashboardsmarthome.BottomNavFrameActivity
import com.example.dashboardsmarthome.databinding.ActivityWeatherForecastBinding

class WeatherForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherForecastBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: WeatherAdapter

    private val MAX_FORECAST_ITEMS_TO_DISPLAY = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        adapter = WeatherAdapter(emptyList())

        binding.recyclerViewForecast.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewForecast.adapter = adapter

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavFrameActivity::class.java)
            startActivity(intent)
            finish()
        }

        val adm4Code = intent.getStringExtra("adm4_code") ?: "34.04.07.2001"

        observeViewModel()
        viewModel.getWeatherForecast(adm4Code)
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(this) { weatherResponse ->
            if (weatherResponse != null && weatherResponse.data.isNotEmpty()) {
                val weatherDataWrapper = weatherResponse.data.firstOrNull()

                if (weatherDataWrapper != null && weatherDataWrapper.cuaca.isNotEmpty()) {
                    val allFlattenedForecasts = weatherDataWrapper.cuaca.flatten()
                    Log.d("WeatherForecast", "Total item perkiraan cuaca (per 3 jam) tersedia: ${allFlattenedForecasts.size}")

                    val forecastsToDisplay = allFlattenedForecasts.take(MAX_FORECAST_ITEMS_TO_DISPLAY)
                    Log.d("WeatherForecast", "Jumlah item yang akan ditampilkan (setelah take): ${forecastsToDisplay.size}")

                    if (forecastsToDisplay.isNotEmpty()) {
                        adapter.updateData(forecastsToDisplay)
                        binding.recyclerViewForecast.visibility = View.VISIBLE
                        supportActionBar?.title = "${weatherResponse.lokasi.desa}, ${weatherResponse.lokasi.kotkab}"
                        binding.textEmptyMessage.visibility = View.GONE
                    } else {
                        showEmptyMessage("Tidak ada data cuaca yang relevan tersedia.")
                    }
                } else {
                    showEmptyMessage("Data perkiraan cuaca tidak ditemukan dalam respons.")
                }
            } else {
                showEmptyMessage("Gagal memuat data cuaca atau respons kosong.")
            }
        }
    }

    private fun showEmptyMessage(message: String) {
        binding.recyclerViewForecast.visibility = View.GONE
        binding.textEmptyMessage.visibility = View.VISIBLE
        binding.textEmptyMessage.text = message
    }
}