package com.example.cuacaku.util

import kotlin.math.roundToInt

// =====================================================
// Konversi Satuan Suhu dan Kecepatan Angin
// =====================================================

enum class TempUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F");

    companion object {
        fun fromString(value: String): TempUnit = try {
            valueOf(value.uppercase())
        } catch (_: Exception) {
            CELSIUS
        }
    }
}

enum class WindUnit(val label: String) {
    KMH("km/h"),
    MPH("mph");

    companion object {
        fun fromString(value: String): WindUnit = try {
            valueOf(value.uppercase())
        } catch (_: Exception) {
            KMH
        }
    }
}

object UnitConverter {
    /** Mengonversi suhu celcius ke satuan yang dipilih (dengan simbol °C / °F) */
    fun formatTemperature(celsius: Double, unit: TempUnit): String {
        val value = when (unit) {
            TempUnit.CELSIUS -> celsius
            TempUnit.FAHRENHEIT -> (celsius * 9.0 / 5.0) + 32.0
        }
        return "${value.roundToInt()}${unit.symbol}"
    }

    /** Mengonversi suhu celcius ke angka bulat tanpa simbol */
    fun toTargetTemp(celsius: Double, unit: TempUnit): Int {
        val value = when (unit) {
            TempUnit.CELSIUS -> celsius
            TempUnit.FAHRENHEIT -> (celsius * 9.0 / 5.0) + 32.0
        }
        return value.roundToInt()
    }

    /** Mengonversi kecepatan angin (km/h) ke satuan yang dipilih */
    fun formatWindSpeed(kmh: Double, unit: WindUnit): String {
        val value = when (unit) {
            WindUnit.KMH -> kmh
            WindUnit.MPH -> kmh * 0.621371
        }
        return "${value.roundToInt()} ${unit.label}"
    }
}
