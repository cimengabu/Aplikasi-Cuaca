package com.example.cuacaku.data

import com.example.cuacaku.data.remote.AirQualityResponse
import com.example.cuacaku.data.remote.GeoResult
import com.example.cuacaku.data.remote.NetworkModule
import com.example.cuacaku.data.remote.WeatherResponse
import java.util.Locale

// =====================================================
// Repository — penghubung antara API remote dan ViewModel
// =====================================================
class WeatherRepository {

    /** Mengambil data prakiraan cuaca berdasarkan koordinat */
    suspend fun forecast(lat: Double, lon: Double): WeatherResponse {
        return NetworkModule.weatherApi.getForecast(latitude = lat, longitude = lon)
    }

    /**
     * Mengambil data kualitas udara (AQI).
     * Mengembalikan null jika API gagal, agar tidak memutus tampilan utama.
     */
    suspend fun airQuality(lat: Double, lon: Double): AirQualityResponse? {
        return try {
            NetworkModule.airQualityApi.getAirQuality(latitude = lat, longitude = lon)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Mencari kota berdasarkan kata kunci.
     * Mengembalikan list tanpa duplikat (nama dan koordinat yang sama).
     */
    suspend fun searchCity(query: String, lang: String): List<GeoResult> {
        return try {
            val response = NetworkModule.geocodingApi.search(name = query, language = lang)
            val raw = response.results ?: emptyList()
            raw.distinctBy {
                val latRounded = String.format(Locale.US, "%.2f", it.latitude)
                val lonRounded = String.format(Locale.US, "%.2f", it.longitude)
                "${it.name.trim().lowercase()}_${it.country?.trim()?.lowercase()}_${latRounded}_${lonRounded}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
