package com.example.cuacaku.util

import androidx.compose.ui.graphics.Color
import com.example.cuacaku.R

// =====================================================
// Helper untuk interpretasi Air Quality Index (US AQI)
// =====================================================

enum class AqiCategory(
    val labelRes: Int,
    val color: Color
) {
    GOOD(R.string.aqi_good, Color(0xFF4CAF50)),
    MODERATE(R.string.aqi_moderate, Color(0xFFFBC02D)),
    UNHEALTHY_SENSITIVE(R.string.aqi_unhealthy_sensitive, Color(0xFFFF9800)),
    UNHEALTHY(R.string.aqi_unhealthy, Color(0xFFF44336)),
    VERY_UNHEALTHY(R.string.aqi_very_unhealthy, Color(0xFF9C27B0)),
    HAZARDOUS(R.string.aqi_hazardous, Color(0xFF795548))
}

object AqiHelper {
    /** Mengelompokkan nilai AQI ke dalam kategori standar US EPA */
    fun getAqiCategory(aqi: Int): AqiCategory = when {
        aqi <= 50 -> AqiCategory.GOOD
        aqi <= 100 -> AqiCategory.MODERATE
        aqi <= 150 -> AqiCategory.UNHEALTHY_SENSITIVE
        aqi <= 200 -> AqiCategory.UNHEALTHY
        aqi <= 300 -> AqiCategory.VERY_UNHEALTHY
        else -> AqiCategory.HAZARDOUS
    }
}
