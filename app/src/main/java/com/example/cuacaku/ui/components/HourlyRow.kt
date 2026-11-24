package com.example.cuacaku.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.cuacaku.data.remote.Hourly
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.UnitConverter
import com.example.cuacaku.util.resolveHourlyWeather
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// =====================================================
// HourlyRow — Kartu Prakiraan Per Jam Persis Screenshot Xiaomi / HyperOS
// Single unified translucent card dengan deretan jam horizontal
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

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x3B101C30) // Translucent dark glass
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            itemsIndexed(hours) { relativeIndex, i ->
                val parsedTime = try {
                    LocalDateTime.parse(hourly.time[i], formatter)
                } catch (_: Exception) { null }

                // Item pertama menampilkan "Sekarang", selanjutnya menampilkan jam "21.00"
                val timeDisplay = if (relativeIndex == 0) {
                    "Sekarang"
                } else {
                    parsedTime?.format(DateTimeFormatter.ofPattern("HH.mm"))
                        ?: hourly.time[i].takeLast(5).replace(":", ".")
                }

                val isDay = parsedTime?.let { it.hour in 6..18 } ?: true

                val resolved = resolveHourlyWeather(
                    code = hourly.weatherCode[i],
                    precipitation = hourly.precipitation?.getOrNull(i),
                    cloudCover = hourly.cloudCover?.getOrNull(i),
                    isDay = isDay
                )

                val tempFormatted = UnitConverter.formatTemperature(hourly.temperature[i], tempUnit)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.width(52.dp)
                ) {
                    // Waktu (Sekarang / 21.00)
                    Text(
                        text = timeDisplay,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )

                    // Ikon Cuaca
                    Text(
                        text = resolved.emoji,
                        fontSize = 24.sp
                    )

                    // Suhu (26°)
                    Text(
                        text = tempFormatted,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}
