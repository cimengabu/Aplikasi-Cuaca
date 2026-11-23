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
    val weatherCode: Int,
    @SerializedName("is_day")
    val isDay: Int = 1,
    @SerializedName("pressure_msl")
    val pressureMsl: Double = 1013.25,
    @SerializedName("cloud_cover")
    val cloudCover: Int = 0,
    @SerializedName("wind_direction_10m")
    val windDirection: Double = 0.0,
    @SerializedName("visibility")
    val visibility: Double = 10000.0
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
    val rain: List<Int?>,
    @SerializedName("sunrise")
    val sunrise: List<String>? = null,
    @SerializedName("sunset")
    val sunset: List<String>? = null,
    @SerializedName("uv_index_max")
    val uvIndexMax: List<Double>? = null,
    @SerializedName("precipitation_sum")
    val precipitationSum: List<Double>? = null
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

// =====================================================
// Data class untuk respon Air Quality API (AQI)
// =====================================================

/** Respon utama dari endpoint /v1/air-quality */
data class AirQualityResponse(
    val current: AirQualityCurrent?
)

/** Data kualitas udara saat ini */
data class AirQualityCurrent(
    val time: String? = null,
    @SerializedName("us_aqi")
    val usAqi: Int? = null,
    @SerializedName("pm10")
    val pm10: Double? = null,
    @SerializedName("pm2_5")
    val pm25: Double? = null
)
