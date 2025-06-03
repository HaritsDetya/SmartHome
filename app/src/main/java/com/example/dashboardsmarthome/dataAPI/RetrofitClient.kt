package com.example.dashboardsmarthome.dataAPI

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BMKG_BASE_URL = "https://api.bmkg.go.id/publik/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BMKG_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

