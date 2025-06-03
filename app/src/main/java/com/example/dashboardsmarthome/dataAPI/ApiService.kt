package com.example.dashboardsmarthome.dataAPI

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("prakiraan-cuaca")
    suspend fun getWeatherForecast(@Query("adm4") adm4Code: String): WeatherResponse
}


