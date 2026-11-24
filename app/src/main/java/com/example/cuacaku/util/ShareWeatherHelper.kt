package com.example.cuacaku.util

import android.content.Context
import android.content.Intent

object ShareWeatherHelper {

    fun shareWeather(
        context: Context,
        cityName: String,
        temperature: Double,
        conditionName: String,
        conditionEmoji: String,
        feelsLike: Double,
        minTemp: Double?,
        maxTemp: Double?,
        humidity: Int,
        windSpeed: Double,
        aqiScore: Int?
    ) {
        val tempInt = temperature.toInt()
        val feelsInt = feelsLike.toInt()
        val minMaxStr = if (minTemp != null && maxTemp != null) {
            "Min: ${minTemp.toInt()}°C / Max: ${maxTemp.toInt()}°C"
        } else ""

        val aqiStr = if (aqiScore != null) "🌿 Kualitas Udara: $aqiScore" else ""

        val shareText = """
            $conditionEmoji Laporan Cuaca Skyora
            📍 $cityName
            
            🌡️ Suhu: $tempInt°C ($conditionName)
            🔥 Terasa seperti: $feelsInt°C
            ${if (minMaxStr.isNotEmpty()) "📊 $minMaxStr" else ""}
            💧 Kelembapan: $humidity%
            🍃 Angin: ${windSpeed.toInt()} km/h
            ${if (aqiStr.isNotEmpty()) aqiStr else ""}
            
            ✨ Pantau cuaca terkini secara akurat bersama Skyora!
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Cuaca $cityName")
        context.startActivity(shareIntent)
    }
}
