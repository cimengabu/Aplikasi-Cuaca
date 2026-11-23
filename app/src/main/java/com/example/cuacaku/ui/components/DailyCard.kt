package com.example.cuacaku.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.UnitConverter
import com.example.cuacaku.util.resolveDailyWeather
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

// =====================================================
// DailyCard — Kartu prakiraan harian (bisa diklik untuk membuka bottom sheet)
// Menggunakan resolusi cuaca harian akurat berdasarkan peluang & curah hujan
// =====================================================
@Composable
fun DailyCard(
    dateStr: String,
    weatherCode: Int,
    maxTemp: Double,
    minTemp: Double,
    rain: Int?,
    precipitationSum: Double?,
    tempUnit: TempUnit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resolved = resolveDailyWeather(
        code = weatherCode,
        rainProb = rain,
        precipitationSum = precipitationSum
    )

    val formattedDate = try {
        val date = LocalDate.parse(dateStr)
        val locale = Locale.getDefault()
        val dayName = date.dayOfWeek.getDisplayName(JavaTextStyle.SHORT, locale)
        val month = date.month.getDisplayName(JavaTextStyle.SHORT, locale)
        "$dayName, ${date.dayOfMonth} $month"
    } catch (_: Exception) {
        dateStr
    }

    val maxFormatted = UnitConverter.formatTemperature(maxTemp, tempUnit)
    val minFormatted = UnitConverter.formatTemperature(minTemp, tempUnit)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = resolved.emoji, fontSize = 34.sp)
            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(resolved.labelRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                if (rain != null) {
                    Text(
                        text = "🌧️ $rain%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "$maxFormatted / $minFormatted",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
