package com.example.cuacaku.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.data.remote.Daily
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.UnitConverter
import com.example.cuacaku.util.resolveDailyWeather
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

// =====================================================
// WeeklyOverviewCard — Kartu ringkasan mingguan
// Menampilkan 7 hari dengan visual temp gradient
// =====================================================
@Composable
fun WeeklyOverviewCard(
    daily: Daily,
    tempUnit: TempUnit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Ambil semua temps untuk normalisasi skala
    val allMin = daily.minTemp.minOrNull() ?: 0.0
    val allMax = daily.maxTemp.maxOrNull() ?: 40.0
    val range = (allMax - allMin).coerceAtLeast(1.0)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x3B101C30)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📅 Ringkasan Mingguan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${daily.time.size} hari",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.height(14.dp))

            daily.time.take(7).forEachIndexed { i, dateStr ->
                val minT = daily.minTemp.getOrNull(i) ?: return@forEachIndexed
                val maxT = daily.maxTemp.getOrNull(i) ?: return@forEachIndexed
                val code = daily.weatherCode.getOrNull(i) ?: 0
                val rain = daily.rain.getOrNull(i)
                val resolved = resolveDailyWeather(code, rain, daily.precipitationSum?.getOrNull(i))

                val dayLabel = try {
                    val date = LocalDate.parse(dateStr, dateFormatter)
                    if (i == 0) "Hari ini"
                    else if (i == 1) "Besok"
                    else date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                } catch (_: Exception) { dateStr }

                val minFrac = ((minT - allMin) / range).toFloat()
                val maxFrac = ((maxT - allMin) / range).toFloat()

                WeeklyDayRow(
                    dayLabel = dayLabel,
                    emoji = resolved.emoji,
                    minTemp = UnitConverter.formatTemperature(minT, tempUnit),
                    maxTemp = UnitConverter.formatTemperature(maxT, tempUnit),
                    rainProb = rain,
                    minFrac = minFrac,
                    maxFrac = maxFrac,
                    isToday = i == 0
                )

                if (i < daily.time.size - 1 && i < 6) {
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun WeeklyDayRow(
    dayLabel: String,
    emoji: String,
    minTemp: String,
    maxTemp: String,
    rainProb: Int?,
    minFrac: Float,
    maxFrac: Float,
    isToday: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Nama hari
        Text(
            text = dayLabel,
            modifier = Modifier.width(60.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) Color.White else Color.White.copy(alpha = 0.75f),
            fontSize = 12.sp
        )

        // Emoji cuaca
        Text(emoji, fontSize = 18.sp, modifier = Modifier.width(26.dp))

        // Probabilitas hujan
        if (rainProb != null && rainProb > 0) {
            Text(
                text = "$rainProb%",
                fontSize = 10.sp,
                color = Color(0xFF90CAF9),
                modifier = Modifier.width(28.dp)
            )
        } else {
            Spacer(Modifier.width(28.dp))
        }

        // Suhu min
        Text(
            text = minTemp,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF90CAF9),
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End,
            fontSize = 12.sp
        )

        // Bar suhu gradient
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            val barColor = when {
                maxFrac > 0.75f -> Brush.horizontalGradient(listOf(Color(0xFFFF9800), Color(0xFFFF5722)))
                maxFrac > 0.5f  -> Brush.horizontalGradient(listOf(Color(0xFFFFB74D), Color(0xFFFF9800)))
                else            -> Brush.horizontalGradient(listOf(Color(0xFF64B5F6), Color(0xFF42A5F5)))
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = maxFrac)
                    .padding(start = (minFrac * 100).toInt().dp.coerceAtLeast(0.dp))
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }

        // Suhu max
        Text(
            text = maxTemp,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFFFB74D),
            modifier = Modifier.width(36.dp),
            fontSize = 12.sp
        )
    }
}
