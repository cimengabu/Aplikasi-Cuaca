package com.example.cuacaku.util

import com.example.cuacaku.R

// =====================================================
// Pemetaan weather_code WMO ke label string resource & emoji
// =====================================================

/** Mengembalikan pasangan (string resource id, emoji) dari kode cuaca WMO */
fun weatherLabel(code: Int): Pair<Int, String> = when (code) {
    0       -> R.string.w_clear to "☀️"
    1, 2    -> R.string.w_partly to "⛅"
    3       -> R.string.w_cloudy to "☁️"
    45, 48  -> R.string.w_fog to "🌫️"
    51, 53, 55, 56, 57
            -> R.string.w_drizzle to "🌦️"
    61, 63, 65, 66, 67, 80, 81, 82
            -> R.string.w_rain to "🌧️"
    71, 73, 75, 77, 85, 86
            -> R.string.w_snow to "❄️"
    95, 96, 99
            -> R.string.w_thunder to "⛈️"
    else    -> R.string.w_unknown to "🌡️"
}
