package com.example.cuacaku.util

import com.example.cuacaku.R

// =====================================================
// Generator tips cuaca harian berdasarkan data aktual
// =====================================================

object WeatherTips {
    /**
     * Menghasilkan 2-4 tips kontekstual berdasarkan data cuaca & AQI hari ini.
     * Mengembalikan list string resource id.
     */
    fun generateTips(
        rainProbMax: Int?,
        uvMax: Double?,
        maxTemp: Double,
        minTemp: Double,
        windSpeed: Double,
        aqi: Int?,
        weatherCode: Int
    ): List<Int> {
        val tips = mutableListOf<Int>()

        // 1. Hujan
        if ((rainProbMax != null && rainProbMax >= 40) || weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82, 95, 96, 99)) {
            tips.add(R.string.tip_rain)
        }

        // 2. Indeks UV tinggi
        if (uvMax != null && uvMax >= 6.0) {
            tips.add(R.string.tip_uv)
        }

        // 3. Suhu panas
        if (maxTemp >= 32.0) {
            tips.add(R.string.tip_heat)
        }

        // 4. Suhu dingin
        if (minTemp <= 19.0) {
            tips.add(R.string.tip_cold)
        }

        // 5. Angin kencang
        if (windSpeed >= 20.0) {
            tips.add(R.string.tip_wind)
        }

        // 6. Kualitas udara buruk
        if (aqi != null && aqi > 100) {
            tips.add(R.string.tip_aqi)
        }

        // Jika tips masih kurang dari 2, tambahkan tips cuaca cerah/ramah
        if (tips.size < 2) {
            tips.add(R.string.tip_clear)
        }

        // Batasi maksimal 4 tips agar tampilan tetap rapi
        return tips.distinct().take(4)
    }
}
