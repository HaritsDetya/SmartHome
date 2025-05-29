package com.example.dashboardsmarthome.dataAPI

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("publik/prakiraan-cuaca")
    suspend fun getWeather(
        @Query("adm4") kodeAdm: String
    ): WeatherResponse
}


