//package com.example.dashboardsmarthome.dataAPI
//
//import retrofit2.Response
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//interface WeatherApiService {
//    @GET("data/2.5/weather")
//    suspend fun getCurrentWeather(
//        @Query("q") city: String,
//        @Query("appid") apiKey: String,
//        @Query("units") units: String = "metric"
//    ): Response<WeatherResponse>
//
//    @GET("data/2.5/forecast")
//    suspend fun getForecast(
//        @Query("q") city: String,
//        @Query("appid") apiKey: String,
//        @Query("units") units: String = "metric"
//    ): Response<ForecastResponse>
//}
