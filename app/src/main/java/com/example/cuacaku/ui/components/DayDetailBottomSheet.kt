package com.example.cuacaku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.UnitConverter
import com.example.cuacaku.util.weatherLabel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

data class SelectedDayData(
    val dateStr: String,
    val weatherCode: Int,
    val maxTemp: Double,
    val minTemp: Double,
    val rainProb: Int?,
    val precipitationSum: Double?,
    val uvIndexMax: Double?,
    val sunrise: String?,
    val sunset: String?
)

// =====================================================
// DayDetailBottomSheet — Modal bottom sheet berisi detail prakiraan 1 hari
// =====================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailBottomSheet(
    data: SelectedDayData,
    tempUnit: TempUnit,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val (labelRes, emoji) = weatherLabel(data.weatherCode, isDay = true)

    val formattedDate = try {
        val date = LocalDate.parse(data.dateStr)
        val locale = Locale.getDefault()
        val dayName = date.dayOfWeek.getDisplayName(JavaTextStyle.FULL, locale)
        val month = date.month.getDisplayName(JavaTextStyle.FULL, locale)
        "$dayName, ${date.dayOfMonth} $month ${date.year}"
    } catch (_: Exception) {
        data.dateStr
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Judul & Tanggal
            Text(
                text = stringResource(R.string.day_detail_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))

            // Header Cuaca & Suhu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = emoji, fontSize = 54.sp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val maxFormatted = UnitConverter.formatTemperature(data.maxTemp, tempUnit)
                    val minFormatted = UnitConverter.formatTemperature(data.minTemp, tempUnit)
                    Text(
                        text = "$maxFormatted / $minFormatted",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Rincian Detail Tambahan
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Peluang Hujan
                DetailRow(
                    emoji = "🌧️",
                    label = stringResource(R.string.rain_probability),
                    value = "${data.rainProb ?: 0}%"
                )

                // Total Curah Hujan
                DetailRow(
                    emoji = "💧",
                    label = stringResource(R.string.precipitation_sum),
                    value = "${data.precipitationSum?.let { String.format("%.1f", it) } ?: "0.0"} mm"
                )

                // Indeks UV Maksimum
                DetailRow(
                    emoji = "☀️",
                    label = stringResource(R.string.uv_index),
                    value = data.uvIndexMax?.let { String.format("%.1f", it) } ?: "-"
                )

                // Matahari Terbit
                DetailRow(
                    emoji = "🌅",
                    label = stringResource(R.string.sunrise),
                    value = formatTime(data.sunrise)
                )

                // Matahari Terbenam
                DetailRow(
                    emoji = "🌇",
                    label = stringResource(R.string.sunset),
                    value = formatTime(data.sunset)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Tombol Tutup
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.close))
            }
        }
    }
}

@Composable
private fun DetailRow(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatTime(isoStr: String?): String {
    if (isoStr == null) return "--:--"
    return try {
        val dt = LocalDateTime.parse(isoStr)
        dt.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        isoStr.takeLast(5)
    }
}
