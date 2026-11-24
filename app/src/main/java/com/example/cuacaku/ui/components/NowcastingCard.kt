package com.example.cuacaku.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R
import com.example.cuacaku.data.remote.Minutely15
import com.example.cuacaku.util.resolveHourlyWeather
import java.util.Locale

// =====================================================
// NowcastingCard — Prediksi Hujan Jangka Pendek (Menit ke Depan)
// Translucent frosted glass look ala Xiaomi
// =====================================================
@Composable
fun NowcastingCard(
    minutely15: Minutely15?,
    modifier: Modifier = Modifier
) {
    if (minutely15 == null || minutely15.precipitation.isEmpty()) return

    val precipList = minutely15.precipitation.take(8)
    val codeList = minutely15.weatherCode.take(8)

    // Analisis data presipitasi 60-120 menit ke depan
    val firstPrecip = precipList.firstOrNull() ?: 0.0
    val rainStartIndex = precipList.indexOfFirst { it >= 0.2 }
    val rainStopIndex = if (firstPrecip >= 0.2) {
        precipList.indexOfFirst { it < 0.2 }
    } else -1

    val (summaryTextRes, summaryColor) = when {
        // Saat ini kering, tetapi diprediksi hujan dalam waktu dekat
        firstPrecip < 0.2 && rainStartIndex in 1..4 -> {
            val minutes = rainStartIndex * 15
            Pair(
                stringResource(R.string.nowcast_rain_in_minutes, minutes),
                Color(0xFFFFB74D) // Oranye peringatan
            )
        }
        // Saat ini hujan, tetapi diprediksi reda
        firstPrecip >= 0.2 && rainStopIndex in 1..4 -> {
            val minutes = rainStopIndex * 15
            Pair(
                stringResource(R.string.nowcast_rain_stopping_in_minutes, minutes),
                Color(0xFF81C784) // Hijau melegakan
            )
        }
        // Sedang hujan berkelanjutan
        firstPrecip >= 0.2 -> {
            Pair(
                stringResource(R.string.nowcast_rain_continuing),
                Color(0xFF64B5F6) // Biru hujan
            )
        }
        // Tidak ada hujan dalam 1-2 jam ke depan
        else -> {
            Pair(
                stringResource(R.string.nowcast_no_rain),
                Color.White.copy(alpha = 0.9f)
            )
        }
    }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Judul
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.nowcast_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Banner Kesimpulan Nowcasting
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = summaryColor.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(summaryColor)
                    )
                    Text(
                        text = summaryTextRes,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = summaryColor
                    )
                }
            }

            // Deretan Blok Interval 15 Menit
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(precipList) { index, precip ->
                    val minutesAhead = (index + 1) * 15
                    val code = codeList.getOrElse(index) { 0 }
                    val weatherInfo = resolveHourlyWeather(code, precip, 40)

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (precip >= 0.2) {
                            Color.White.copy(alpha = 0.18f)
                        } else {
                            Color.White.copy(alpha = 0.08f)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .width(68.dp)
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "+${minutesAhead}m",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = weatherInfo.emoji,
                                fontSize = 20.sp
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f mm", precip),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = if (precip >= 0.2) FontWeight.Bold else FontWeight.Normal,
                                color = if (precip >= 0.2) Color(0xFF64B5F6) else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}
