package com.example.dashboardsmarthome.dataAPI

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String
)

data class Main(
    val temp: Double
)

data class Weather(
    val main: String,
    val icon: String
)

