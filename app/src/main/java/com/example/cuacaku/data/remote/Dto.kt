package com.example.cuacaku.data.remote

import com.google.gson.annotations.SerializedName

// =====================================================
// Data class untuk respon Forecast API (Open-Meteo)
// =====================================================

/** Respon utama dari endpoint /v1/forecast */
data class WeatherResponse(
    val current: Current,
    val hourly: Hourly,
    val daily: Daily
)

/** Data cuaca saat ini */
data class Current(
    val time: String,
    @SerializedName("temperature_2m")
    val temperature: Double,
    @SerializedName("apparent_temperature")
    val feelsLike: Double,
    @SerializedName("relative_humidity_2m")
    val humidity: Int,
    @SerializedName("wind_speed_10m")
    val windSpeed: Double,
    @SerializedName("weather_code")
    val weatherCode: Int
)

/** Data prakiraan per jam */
data class Hourly(
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperature: List<Double>,
    @SerializedName("weather_code")
    val weatherCode: List<Int>
)

/** Data prakiraan harian (7 hari) */
data class Daily(
    val time: List<String>,
    @SerializedName("weather_code")
    val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max")
    val maxTemp: List<Double>,
    @SerializedName("temperature_2m_min")
    val minTemp: List<Double>,
    @SerializedName("precipitation_probability_max")
    val rain: List<Int?>
)

// =====================================================
// Data class untuk respon Geocoding API (cari kota)
// =====================================================

/** Respon utama dari endpoint /v1/search */
data class GeocodingResponse(
    val results: List<GeoResult>?
)

/** Satu hasil pencarian kota */
data class GeoResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
)
