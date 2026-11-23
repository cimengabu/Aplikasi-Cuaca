package com.example.cuacaku.data

import com.example.cuacaku.data.remote.GeoResult
import com.example.cuacaku.data.remote.NetworkModule
import com.example.cuacaku.data.remote.WeatherResponse

// =====================================================
// Repository — penghubung antara API dan ViewModel
// =====================================================
class WeatherRepository {

    /** Mengambil data prakiraan cuaca berdasarkan koordinat */
    suspend fun forecast(lat: Double, lon: Double): WeatherResponse {
        return NetworkModule.weatherApi.getForecast(latitude = lat, longitude = lon)
    }

    /**
     * Mencari kota berdasarkan kata kunci.
     * Mengembalikan list kosong jika tidak ada hasil.
     */
    suspend fun searchCity(query: String, lang: String): List<GeoResult> {
        val response = NetworkModule.geocodingApi.search(name = query, language = lang)
        return response.results ?: emptyList()
    }
}
