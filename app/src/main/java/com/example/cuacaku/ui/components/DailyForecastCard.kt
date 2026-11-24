package com.example.cuacaku.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R
import com.example.cuacaku.data.remote.Daily
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.UnitConverter
import com.example.cuacaku.util.resolveDailyWeather
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// =====================================================
// DailyForecastCard — Prakiraan Cuaca Beberapa Hari ke Depan
// Tampilan Persis Screenshot Xiaomi (Tab Garis / Daftar + Kurva Suhu Bawah)
// =====================================================
@Composable
fun DailyForecastCard(
    daily: Daily,
    tempUnit: TempUnit,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isGraphMode by remember { mutableStateOf(true) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x3B101C30) // Translucent dark glass
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 14.dp)
        ) {
            // Header: Judul & Toggle Pill "Garis" / "Daftar"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Prakiraan cuaca beberapa hari ke depan",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )

                // Toggle Switch Pill (Garis / Daftar)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isGraphMode) Color.White.copy(alpha = 0.25f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { isGraphMode = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Garis",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isGraphMode) Color.White else Color.White.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    if (!isGraphMode) Color.White.copy(alpha = 0.25f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { isGraphMode = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Daftar",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (!isGraphMode) Color.White else Color.White.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            val count = daily.time.size.coerceAtMost(7)
            val daysList = (0 until count).toList()

            if (isGraphMode) {
                // Tampilan Mode Garis Horizontal (Persis Screenshot)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        daysList.forEach { index ->
                            val dateParsed = try {
                                LocalDate.parse(daily.time[index])
                            } catch (_: Exception) { LocalDate.now() }

                            val dayTitle = when (index) {
                                0 -> "Hari ini"
                                1 -> "Besok"
                                else -> dateParsed.format(DateTimeFormatter.ofPattern("EEE", Locale("id")))
                            }
                            val dateSub = dateParsed.format(DateTimeFormatter.ofPattern("dd MMM", Locale("id")))

                            val resolved = resolveDailyWeather(
                                code = daily.weatherCode[index],
                                rainProb = daily.rain.getOrNull(index),
                                precipitationSum = daily.precipitationSum?.getOrNull(index)
                            )

                            val conditionName = stringResource(resolved.labelRes)
                            val rainProb = daily.rain.getOrNull(index) ?: 0
                            val maxTempFormatted = UnitConverter.formatTemperature(daily.maxTemp[index], tempUnit)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .width(76.dp)
                                    .clickable { onDayClick(index) }
                                    .padding(vertical = 4.dp)
                            ) {
                                // Nama Hari & Tanggal
                                Text(
                                    text = dayTitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = dateSub,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.65f),
                                        fontSize = 11.sp
                                    )
                                )

                                // Nama Kondisi Singkat
                                Text(
                                    text = conditionName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.height(30.dp)
                                )

                                // Ikon Cuaca
                                Text(
                                    text = resolved.emoji,
                                    fontSize = 24.sp
                                )

                                // Probabilitas Hujan (%)
                                Text(
                                    text = if (rainProb > 0) "$rainProb%" else "-",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (rainProb >= 40) Color(0xFF64B5F6) else Color.White.copy(alpha = 0.5f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )

                                // Suhu Max
                                Text(
                                    text = maxTempFormatted,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Garis Kurva Suhu Bawah (Canvas Curve Line)
                    val maxTemps = daysList.map { daily.maxTemp[it] }
                    DailyTemperatureCurve(
                        temperatures = maxTemps,
                        columnWidth = 92f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    )
                }
            } else {
                // Tampilan Mode Daftar Vertikal
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    daysList.forEach { index ->
                        val dateParsed = try {
                            LocalDate.parse(daily.time[index])
                        } catch (_: Exception) { LocalDate.now() }

                        val dayTitle = when (index) {
                            0 -> "Hari ini"
                            1 -> "Besok"
                            else -> dateParsed.format(DateTimeFormatter.ofPattern("EEEE, dd MMM", Locale("id")))
                        }

                        val resolved = resolveDailyWeather(
                            code = daily.weatherCode[index],
                            rainProb = daily.rain.getOrNull(index),
                            precipitationSum = daily.precipitationSum?.getOrNull(index)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDayClick(index) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = dayTitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.weight(1.3f)
                            )
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = resolved.emoji, fontSize = 20.sp)
                                Text(
                                    text = stringResource(resolved.labelRes),
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f)),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${UnitConverter.formatTemperature(daily.minTemp[index], tempUnit)} / ${UnitConverter.formatTemperature(daily.maxTemp[index], tempUnit)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// =====================================================
// Canvas Kurva Suhu Halus Bawah
// =====================================================
@Composable
private fun DailyTemperatureCurve(
    temperatures: List<Double>,
    columnWidth: Float,
    modifier: Modifier = Modifier
) {
    if (temperatures.size < 2) return

    val minTemp = temperatures.minOrNull() ?: 20.0
    val maxTemp = temperatures.maxOrNull() ?: 35.0
    val tempRange = (maxTemp - minTemp).coerceAtLeast(1.0)

    Canvas(modifier = modifier) {
        val totalWidth = size.width
        val canvasHeight = size.height
        val stepX = totalWidth / (temperatures.size)

        val points = temperatures.mapIndexed { i, temp ->
            val x = (i + 0.5f) * stepX
            val normalizedY = ((maxTemp - temp) / tempRange).toFloat()
            val y = 8f + normalizedY * (canvasHeight - 20f)
            Offset(x, y)
        }

        // Gambar Garis Penghubung Kurva
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val cX = (p0.x + p1.x) / 2f
                cubicTo(cX, p0.y, cX, p1.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.5f),
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )

        // Gambar Titik Lingkaran di Tiap Titik Suhu
        points.forEach { point ->
            drawCircle(
                color = Color.White,
                radius = 3.5f,
                center = point
            )
        }
    }
}
