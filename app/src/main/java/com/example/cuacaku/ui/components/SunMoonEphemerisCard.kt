package com.example.cuacaku.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class MoonPhaseInfo(
    val phaseName: String,
    val emoji: String,
    val illuminationPercent: Int,
    val daysToFullMoon: Int
)

/**
 * Menghitung fase bulan berdasarkan siklus sinodik astronomis (~29.53 hari).
 */
fun calculateMoonPhase(date: Date = Date()): MoonPhaseInfo {
    // Referensi Bulan Baru yang diketahui: 11 Januari 2024, 11:57 UTC (1704974220000L)
    val refNewMoonMs = 1704974220000L
    val synodicMonthMs = 29.53058867 * 86400000.0

    val diffMs = (date.time - refNewMoonMs).toDouble()
    val cycleProgress = ((diffMs % synodicMonthMs) + synodicMonthMs) % synodicMonthMs
    val cycleFraction = cycleProgress / synodicMonthMs // 0.0 sampai 1.0

    val illumination = (((1.0 - cos(cycleFraction * 2 * PI)) / 2.0) * 100.0).toInt().coerceIn(0, 100)

    val (name, emoji) = when {
        cycleFraction < 0.03 || cycleFraction >= 0.97 -> "Bulan Baru" to "🌑"
        cycleFraction < 0.22 -> "Sabit Awal" to "🌒"
        cycleFraction < 0.28 -> "Perbani Awal" to "🌓"
        cycleFraction < 0.47 -> "Cembung Awal" to "🌔"
        cycleFraction < 0.53 -> "Bulan Purnama" to "🌕"
        cycleFraction < 0.72 -> "Cembung Akhir" to "🌖"
        cycleFraction < 0.78 -> "Perbani Akhir" to "🌗"
        else -> "Sabit Akhir" to "🌘"
    }

    val daysToFull = (((0.5 - cycleFraction + 1.0) % 1.0) * 29.53).toInt()

    return MoonPhaseInfo(
        phaseName = name,
        emoji = emoji,
        illuminationPercent = illumination,
        daysToFullMoon = daysToFull
    )
}

// =====================================================
// SunMoonEphemerisCard — Pelacak Matahari & Fase Bulan Lengkap
// Menampilkan daylight progress dan fase bulan saat ini
// =====================================================
@Composable
fun SunMoonEphemerisCard(
    sunriseStr: String?,
    sunsetStr: String?,
    modifier: Modifier = Modifier
) {
    val moonInfo = calculateMoonPhase()

    // Format jam terbit & terbenam (format input Open-Meteo: "2026-11-24T05:32")
    val sunriseTime = sunriseStr?.substringAfter("T")?.take(5) ?: "05:30"
    val sunsetTime = sunsetStr?.substringAfter("T")?.take(5) ?: "17:45"

    // Menghitung progress posisi matahari siang hari (0.0 sampai 1.0)
    val now = Calendar.getInstance()
    val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

    val sunriseMinutes = runCatching {
        val parts = sunriseTime.split(":")
        parts[0].toInt() * 60 + parts[1].toInt()
    }.getOrDefault(330)

    val sunsetMinutes = runCatching {
        val parts = sunsetTime.split(":")
        parts[0].toInt() * 60 + parts[1].toInt()
    }.getOrDefault(1065)

    val daylightProgress = when {
        currentMinutes < sunriseMinutes -> 0f
        currentMinutes > sunsetMinutes -> 1f
        else -> (currentMinutes - sunriseMinutes).toFloat() / (sunsetMinutes - sunriseMinutes).toFloat()
    }.coerceIn(0f, 1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x3B101C30)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "☀️🌙", fontSize = 18.sp)
                Text(
                    text = "Matahari & Bulan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(Modifier.height(14.dp))

            // 1. Busur Progres Matahari
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                ) {
                    val w = size.width
                    val h = size.height

                    // Garis dasar horizontal
                    drawLine(
                        color = Color.White.copy(alpha = 0.15f),
                        start = Offset(20f, h - 10f),
                        end = Offset(w - 20f, h - 10f),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Busur lengkung matahari
                    val arcRadius = (w - 60f) / 2f
                    val arcCenter = Offset(w / 2f, h - 10f)

                    drawArc(
                        color = Color(0xFFFFD54F).copy(alpha = 0.35f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(arcCenter.x - arcRadius, arcCenter.y - arcRadius),
                        size = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Posisi matahari aktif di sepanjang busur
                    val angleRad = (PI + daylightProgress * PI).toFloat()
                    val sunX = arcCenter.x + arcRadius * cos(angleRad)
                    val sunY = arcCenter.y + arcRadius * sin(angleRad)

                    // Aura matahari
                    drawCircle(
                        color = Color(0x55FFA000),
                        radius = 14.dp.toPx(),
                        center = Offset(sunX, sunY)
                    )
                    // Titik matahari
                    drawCircle(
                        color = Color(0xFFFFD54F),
                        radius = 7.dp.toPx(),
                        center = Offset(sunX, sunY)
                    )
                }
            }

            // Waktu Terbit & Terbenam
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Terbit",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f))
                    )
                    Text(
                        text = sunriseTime,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val statusText = if (daylightProgress in 0.01f..0.99f) "Matahari Bersinar" else "Malam Hari"
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Terbenam",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f))
                    )
                    Text(
                        text = sunsetTime,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
            Spacer(Modifier.height(14.dp))

            // 2. Info Fase Bulan
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = moonInfo.emoji, fontSize = 34.sp)
                    Column {
                        Text(
                            text = moonInfo.phaseName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Iluminasi: ${moonInfo.illuminationPercent}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Purnama Berikutnya",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = if (moonInfo.daysToFullMoon == 0) "Malam Ini!" else "${moonInfo.daysToFullMoon} hari lagi",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF81D4FA),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
