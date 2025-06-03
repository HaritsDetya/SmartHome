package com.example.dashboardsmarthome.ui.weatherForecast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashboardsmarthome.dataAPI.WeatherResponse
import com.example.dashboardsmarthome.dataAPI.RetrofitClient
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _weatherResponse = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> = _weatherResponse

    private val TAG = "WeatherViewModel"

    fun getWeatherForecast(adm4Code: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getWeatherForecast(adm4Code)
                _weatherResponse.value = response
                Log.d(TAG, "Weather data fetched successfully: ${response.lokasi.desa}")
            } catch (e: Exception) {
                _weatherResponse.value = null
                Log.e(TAG, "Error fetching weather data: ${e.message}", e)
            }
        }
    }
}