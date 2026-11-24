package com.example.cuacaku.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.data.remote.Hourly
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// =====================================================
// HourlyPrecipitationChart — Bar chart curah hujan per jam
// Terinspirasi dari screenshot aplikasi cuaca Xiaomi
// =====================================================
@Composable
fun HourlyPrecipitationChart(
    hourly: Hourly,
    modifier: Modifier = Modifier
) {
    val precipitation = hourly.precipitation ?: return
    val probabilities = hourly.precipitationProbability

    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    val startIndex = hourly.time.indexOfFirst { timeStr ->
        try { LocalDateTime.parse(timeStr, formatter) >= now } catch (_: Exception) { false }
    }.coerceAtLeast(0)

    val endIndex = (startIndex + 24).coerceAtMost(hourly.time.size)
    val hours = (startIndex until endIndex).toList()

    if (hours.isEmpty()) return

    val maxPrecip = hours.mapNotNull { precipitation.getOrNull(it) }.maxOrNull()?.coerceAtLeast(0.1) ?: 1.0
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(900))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x3B101C30)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🌧️ Curah Hujan (mm)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "24 jam ke depan",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(hours) { relIdx, i ->
                    val parsedTime = try {
                        LocalDateTime.parse(hourly.time[i], formatter)
                    } catch (_: Exception) { null }

                    val timeLabel = if (relIdx == 0) "Skrg"
                    else parsedTime?.format(DateTimeFormatter.ofPattern("HH")) + ":00"

                    val precip = precipitation.getOrNull(i) ?: 0.0
                    val prob = probabilities?.getOrNull(i) ?: 0
                    val barFraction = ((precip / maxPrecip) * animProgress.value).toFloat().coerceIn(0f, 1f)
                    val minBarFraction = if (precip > 0) 0.05f else 0f

                    val barColor = when {
                        precip >= 10.0 -> Color(0xFF1565C0)
                        precip >= 5.0  -> Color(0xFF1E88E5)
                        precip >= 1.0  -> Color(0xFF42A5F5)
                        precip > 0.0   -> Color(0xFF90CAF9)
                        else           -> Color.White.copy(alpha = 0.1f)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(32.dp)
                    ) {
                        // Nilai mm (hanya jika > 0)
                        Text(
                            text = if (precip > 0) String.format("%.1f", precip) else "",
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(2.dp))

                        // Bar chart
                        Canvas(
                            modifier = Modifier
                                .width(14.dp)
                                .height(60.dp)
                        ) {
                            val barH = (size.height * barFraction).coerceAtLeast(size.height * minBarFraction)
                            if (barH > 0) {
                                drawRoundRect(
                                    color = barColor,
                                    topLeft = Offset(0f, size.height - barH),
                                    size = Size(size.width, barH),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                            }
                            // Track bar (background)
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.08f),
                                topLeft = Offset(0f, 0f),
                                size = Size(size.width, size.height),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        // Label jam
                        Text(
                            text = timeLabel,
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )

                        // Probabilitas
                        if (prob > 0) {
                            Text(
                                text = "$prob%",
                                fontSize = 8.sp,
                                color = Color(0xFF90CAF9)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    Color(0xFF90CAF9) to "Ringan",
                    Color(0xFF42A5F5) to "Sedang",
                    Color(0xFF1E88E5) to "Lebat"
                ).forEach { (color, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.width(8.dp).height(8.dp)) {
                            drawCircle(color)
                        }
                        Spacer(Modifier.width(3.dp))
                        Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.65f))
                    }
                }
            }
        }
    }
}
