//package com.example.dashboardsmarthome.dataAPI
//
//import android.telecom.Call
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Path
//
//interface APIService {
//    @GET("events?active=1")
//    fun getActiveEvents(): Call<HomeDatabase>
//
//    @GET("events?active=0")
//    fun getFinishedEvents(): Call<HomeDatabase>
//
//    @GET("events/{id}")
//    fun getEventDetail(@Path("id") id: String): Call<HomeDatabase>
//
//    companion object {
//        private const val BASE_URL = ""
//
//        fun create(): APIService {
//            return Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(APIService::class.java)
//        }
//    }
//}