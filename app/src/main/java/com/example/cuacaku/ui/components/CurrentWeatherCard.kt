package com.example.cuacaku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.UnitConverter
import com.example.cuacaku.util.resolveCurrentWeather
import java.util.Locale

// =====================================================
// CurrentWeatherCard — Hero Weather Section
// Tampilan tanpa box card tertutup (floating di atas background animasi, persis Xiaomi / HyperOS)
// =====================================================
@Composable
fun CurrentWeatherCard(
    cityName: String,
    latitude: Double,
    longitude: Double,
    temperature: Double,
    weatherCode: Int,
    precipitation: Double,
    cloudCover: Int,
    isDay: Boolean,
    tempUnit: TempUnit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    lastUpdatedTime: String,
    isOffline: Boolean,
    minTemp: Double? = null,
    maxTemp: Double? = null,
    aqiVal: Int? = null,
    aqiCategoryLabel: String? = null,
    modifier: Modifier = Modifier
) {
    val resolved = resolveCurrentWeather(
        code = weatherCode,
        precipitation = precipitation,
        cloudCover = cloudCover,
        isDay = isDay
    )

    val tempFormatted = UnitConverter.formatTemperature(temperature, tempUnit)
    val minTempFormatted = minTemp?.let { UnitConverter.formatTemperature(it, tempUnit) } ?: ""
    val maxTempFormatted = maxTemp?.let { UnitConverter.formatTemperature(it, tempUnit) } ?: ""

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Suhu Raksasa di Tengah (Persis Screenshot Xiaomi 26°)
        Text(
            text = tempFormatted,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 94.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                letterSpacing = (-2).sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Baris Subtitle Tunggal Persis Screenshot:
        // "Hujan sebentar  24° / 33°  Kualitas udara: 65 - Moderate"
        val conditionName = stringResource(resolved.labelRes)
        val minMaxText = if (minTempFormatted.isNotEmpty() && maxTempFormatted.isNotEmpty()) {
            "$minTempFormatted / $maxTempFormatted"
        } else ""

        val aqiSummaryText = if (aqiVal != null && !aqiCategoryLabel.isNullOrEmpty()) {
            "${stringResource(R.string.air_quality_title)}: $aqiVal - $aqiCategoryLabel"
        } else ""

        val subtitleParts = listOf(conditionName, minMaxText, aqiSummaryText).filter { it.isNotEmpty() }
        val fullSubtitle = subtitleParts.joinToString("   ")

        Text(
            text = fullSubtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.95f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        )

        // Indikator Curah Hujan & Tutupan Awan (Transparan Halus di Bawah)
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "💧 ${String.format(Locale.US, "%.1f mm", precipitation)}",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.75f))
            )
            Text(
                text = "☁️ $cloudCover%",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.75f))
            )
            if (lastUpdatedTime.isNotEmpty()) {
                Text(
                    text = "⏱️ $lastUpdatedTime",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.75f))
                )
            }
        }
    }
}
