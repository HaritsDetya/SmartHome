package com.example.dashboardsmarthome.dataAPI

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val lokasi: LokasiGlobal,
    val data: List<WeatherDataWrapper>
)

data class LokasiGlobal(
    val adm1: String,
    val adm2: String,
    val adm3: String,
    val adm4: String,
    val provinsi: String,
    val kotkab: String,
    val kecamatan: String,
    val desa: String,
    val lon: Double,
    val lat: Double,
    val timezone: String
)

data class WeatherDataWrapper(
    val lokasi: LokasiDetail,
    val cuaca: List<List<Cuaca>>
)

data class LokasiDetail(
    val adm1: String,
    val adm2: String,
    val adm3: String,
    val adm4: String,
    val provinsi: String,
    val kotkab: String,
    val kecamatan: String,
    val desa: String,
    val lon: Double,
    val lat: Double,
    val timezone: String,
    val type: String
)

data class Cuaca(
    val datetime: String,
    val t: Int,
    val tcc: Int,
    val tp: Double,
    val weather: Int,
    @SerializedName("weather_desc")
    val weatherDesc: String,
    @SerializedName("weather_desc_en")
    val weatherDescEn: String,
    @SerializedName("wd_deg")
    val wdDeg: Int,
    @SerializedName("wd")
    val wd: String,
    @SerializedName("wd_to")
    val wdTo: String,
    val ws: Double,
    val hu: Int,
    val vs: Int,
    @SerializedName("vs_text")
    val vsText: String,
    @SerializedName("time_index")
    val timeIndex: String,
    @SerializedName("analysis_date")
    val analysisDate: String,
    val image: String,
    @SerializedName("utc_datetime")
    val utcDatetime: String,
    @SerializedName("local_datetime")
    val localDatetime: String?
)