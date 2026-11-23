package com.example.cuacaku.util

import com.example.cuacaku.R

// =====================================================
// Data class hasil penentuan kondisi cuaca yang akurat
// =====================================================
data class ResolvedWeather(
    val labelRes: Int,
    val emoji: String,
    val effectiveCode: Int
)

// =====================================================
// Pemetaan & Koreksi Kondisi Cuaca WMO Berdasarkan Curah Hujan & Tutupan Awan
// =====================================================

private val RAIN_CODES = setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99)
private val SNOW_CODES = setOf(71, 73, 75, 77, 85, 86)
private val FOG_CODES = setOf(45, 48)

/**
 * Menentukan kondisi cuaca saat ini secara akurat.
 * Jika kode cuaca mengindikasikan gerimis/hujan/badai tetapi curah hujan < 0.2 mm,
 * maka kondisi diturunkan menjadi Cerah, Berawan sebagian, atau Berawan sesuai cloudCover.
 */
fun resolveCurrentWeather(
    code: Int,
    precipitation: Double,
    cloudCover: Int,
    isDay: Boolean = true
): ResolvedWeather {
    // 1. Tangani kode gerimis / hujan / badai
    if (code in RAIN_CODES) {
        if (precipitation < 0.2) {
            // Curah hujan hampir nol (< 0.2 mm): Jangan tampilkan gerimis/hujan!
            return fallbackByCloudCover(cloudCover, isDay)
        }
        // Ada curah hujan nyata (>= 0.2 mm)
        return when {
            code in setOf(95, 96, 99) -> ResolvedWeather(R.string.w_thunder, "⛈️", 95)
            precipitation > 7.5 -> ResolvedWeather(R.string.w_heavy_rain, "🌧️", 65)
            precipitation > 1.0 -> ResolvedWeather(R.string.w_rain, "🌧️", 61)
            else -> ResolvedWeather(R.string.w_drizzle, "🌦️", 51)
        }
    }

    // 2. Salju
    if (code in SNOW_CODES) {
        return ResolvedWeather(R.string.w_snow, "❄️", 71)
    }

    // 3. Kabut
    if (code in FOG_CODES) {
        return ResolvedWeather(R.string.w_fog, "🌫️", 45)
    }

    // 4. Kondisi tanpa hujan (Cerah / Berawan sebagian / Berawan)
    return when (code) {
        0 -> { // Cerah
            if (cloudCover > 60) ResolvedWeather(R.string.w_cloudy, "☁️", 3)
            else if (cloudCover >= 20) ResolvedWeather(
                R.string.w_partly,
                if (isDay) "⛅" else "☁️",
                1
            )
            else ResolvedWeather(
                if (isDay) R.string.w_clear else R.string.w_clear_night,
                if (isDay) "☀️" else "🌙",
                0
            )
        }
        1, 2 -> { // Berawan sebagian
            if (cloudCover > 60) ResolvedWeather(R.string.w_cloudy, "☁️", 3)
            else if (cloudCover < 20) ResolvedWeather(
                if (isDay) R.string.w_clear else R.string.w_clear_night,
                if (isDay) "☀️" else "🌙",
                0
            )
            else ResolvedWeather(
                R.string.w_partly,
                if (isDay) "⛅" else "☁️",
                1
            )
        }
        3 -> { // Berawan
            if (cloudCover < 20) ResolvedWeather(
                if (isDay) R.string.w_clear else R.string.w_clear_night,
                if (isDay) "☀️" else "🌙",
                0
            )
            else if (cloudCover <= 60) ResolvedWeather(
                R.string.w_partly,
                if (isDay) "⛅" else "☁️",
                1
            )
            else ResolvedWeather(R.string.w_cloudy, "☁️", 3)
        }
        else -> fallbackByCloudCover(cloudCover, isDay)
    }
}

/**
 * Menentukan kondisi cuaca prakiraan per jam.
 */
fun resolveHourlyWeather(
    code: Int,
    precipitation: Double?,
    cloudCover: Int?,
    isDay: Boolean = true
): ResolvedWeather {
    val precip = precipitation ?: 0.0
    val cloud = cloudCover ?: 0
    return resolveCurrentWeather(code, precip, cloud, isDay)
}

/**
 * Menentukan kondisi cuaca prakiraan harian.
 * Jika peluang hujan < 30% dan precipitation_sum < 1.0 mm, jangan tampilkan hujan.
 */
fun resolveDailyWeather(
    code: Int,
    rainProb: Int?,
    precipitationSum: Double?
): ResolvedWeather {
    val prob = rainProb ?: 0
    val sum = precipitationSum ?: 0.0

    if (code in RAIN_CODES) {
        if (prob < 30 && sum < 1.0) {
            // Curah hujan dan probabilitas rendah: jangan tampilkan hujan
            return ResolvedWeather(R.string.w_partly, "⛅", 1)
        }
        return when {
            code in setOf(95, 96, 99) -> ResolvedWeather(R.string.w_thunder, "⛈️", 95)
            sum > 7.5 -> ResolvedWeather(R.string.w_heavy_rain, "🌧️", 65)
            sum > 1.0 -> ResolvedWeather(R.string.w_rain, "🌧️", 61)
            else -> ResolvedWeather(R.string.w_drizzle, "🌦️", 51)
        }
    }

    if (code in SNOW_CODES) return ResolvedWeather(R.string.w_snow, "❄️", 71)
    if (code in FOG_CODES) return ResolvedWeather(R.string.w_fog, "🌫️", 45)

    return when (code) {
        0 -> ResolvedWeather(R.string.w_clear, "☀️", 0)
        1, 2 -> ResolvedWeather(R.string.w_partly, "⛅", 1)
        3 -> ResolvedWeather(R.string.w_cloudy, "☁️", 3)
        else -> ResolvedWeather(R.string.w_unknown, "🌡️", -1)
    }
}

private fun fallbackByCloudCover(cloudCover: Int, isDay: Boolean): ResolvedWeather {
    return when {
        cloudCover < 20 -> ResolvedWeather(
            if (isDay) R.string.w_clear else R.string.w_clear_night,
            if (isDay) "☀️" else "🌙",
            0
        )
        cloudCover <= 60 -> ResolvedWeather(
            R.string.w_partly,
            if (isDay) "⛅" else "☁️",
            1
        )
        else -> ResolvedWeather(R.string.w_cloudy, "☁️", 3)
    }
}

/** Fungsi lama untuk backwards compatibility */
fun weatherLabel(code: Int, isDay: Boolean = true): Pair<Int, String> {
    val resolved = resolveCurrentWeather(code, precipitation = 0.5, cloudCover = 50, isDay = isDay)
    return resolved.labelRes to resolved.emoji
}
