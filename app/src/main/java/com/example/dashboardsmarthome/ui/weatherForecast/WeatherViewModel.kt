package com.example.dashboardsmarthome.ui.weatherForecast

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

    fun getWeatherForecast(adm4Code: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getWeather(adm4Code)
                _weatherResponse.value = response
            } catch (e: Exception) {
                _weatherResponse.value = null
                e.printStackTrace()
            }
        }
    }
}