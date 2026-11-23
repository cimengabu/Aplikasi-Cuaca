package com.example.cuacaku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.data.remote.Hourly
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.UnitConverter
import com.example.cuacaku.util.resolveHourlyWeather
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// =====================================================
// HourlyRow — Scroll horizontal per jam untuk 24 jam ke depan
// Menggunakan resolusi cuaca akurat berbasis curah hujan & awan
// =====================================================
@Composable
fun HourlyRow(
    hourly: Hourly,
    tempUnit: TempUnit,
    modifier: Modifier = Modifier
) {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    val startIndex = hourly.time.indexOfFirst { timeStr ->
        try {
            LocalDateTime.parse(timeStr, formatter) >= now
        } catch (_: Exception) { false }
    }.coerceAtLeast(0)

    val endIndex = (startIndex + 24).coerceAtMost(hourly.time.size)
    val hours = (startIndex until endIndex).toList()

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(hours) { i ->
            val parsedTime = try {
                LocalDateTime.parse(hourly.time[i], formatter)
            } catch (_: Exception) { null }

            val timeDisplay = parsedTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                ?: hourly.time[i].takeLast(5)

            // Tentukan apakah jam tersebut siang atau malam (06:00 - 18:00)
            val isDay = parsedTime?.let { it.hour in 6..18 } ?: true

            // Gunakan resolveHourlyWeather agar label & emoji akurat
            val resolved = resolveHourlyWeather(
                code = hourly.weatherCode[i],
                precipitation = hourly.precipitation?.getOrNull(i),
                cloudCover = hourly.cloudCover?.getOrNull(i),
                isDay = isDay
            )

            val tempFormatted = UnitConverter.formatTemperature(hourly.temperature[i], tempUnit)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timeDisplay,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(text = resolved.emoji, fontSize = 24.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = tempFormatted,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
