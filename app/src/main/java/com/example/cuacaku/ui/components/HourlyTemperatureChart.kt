package com.example.cuacaku.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R
import com.example.cuacaku.data.remote.Hourly
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.UnitConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// =====================================================
// HourlyTemperatureChart — Grafik tren suhu 24 jam dengan Canvas
// =====================================================
@Composable
fun HourlyTemperatureChart(
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
    val slice = (startIndex until endIndex).toList()
    if (slice.size < 2) return

    val temps = slice.map { UnitConverter.toTargetTemp(hourly.temperature[it], tempUnit) }
    val times = slice.map {
        try {
            LocalDateTime.parse(hourly.time[it], formatter)
                .format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (_: Exception) {
            hourly.time[it].takeLast(5)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val subtextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f).toArgb()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.temp_chart_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))

            // Box horizontal scroll agar grafik tidak berdesakan
            val scrollState = rememberScrollState()
            val pointSpacing = 58.dp
            val totalWidth = pointSpacing * slice.size

            Box(modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
                Canvas(
                    modifier = Modifier
                        .width(totalWidth)
                        .height(160.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    val paddingTop = 40f
                    val paddingBottom = 45f
                    val chartHeight = height - paddingTop - paddingBottom

                    val minTemp = (temps.minOrNull() ?: 0) - 2
                    val maxTemp = (temps.maxOrNull() ?: 30) + 2
                    val tempRange = (maxTemp - minTemp).coerceAtLeast(1)

                    val stepX = width / (temps.size - 1)

                    val points = temps.mapIndexed { index, temp ->
                        val x = index * stepX
                        val norm = (temp - minTemp).toFloat() / tempRange.toFloat()
                        val y = height - paddingBottom - (norm * chartHeight)
                        Offset(x, y)
                    }

                    // 1. Gambar Area Gradasi di Bawah Garis
                    val fillPath = Path().apply {
                        moveTo(points.first().x, height - paddingBottom)
                        lineTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val cx = (p1.x + p2.x) / 2
                            cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                        }
                        lineTo(points.last().x, height - paddingBottom)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.35f),
                                primaryColor.copy(alpha = 0.0f)
                            ),
                            startY = paddingTop,
                            endY = height - paddingBottom
                        )
                    )

                    // 2. Gambar Garis Kurva Halus
                    val strokePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val cx = (p1.x + p2.x) / 2
                            cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                        }
                    }

                    drawPath(
                        path = strokePath,
                        color = primaryColor,
                        style = Stroke(width = 5f, cap = StrokeCap.Round)
                    )

                    // 3. Gambar Titik dan Teks Label (nativeCanvas)
                    val textPaint = Paint().apply {
                        isAntiAlias = true
                        textSize = 28f
                        textAlign = Paint.Align.CENTER
                        color = textColor
                        isFakeBoldText = true
                    }

                    val timePaint = Paint().apply {
                        isAntiAlias = true
                        textSize = 24f
                        textAlign = Paint.Align.CENTER
                        color = subtextColor
                    }

                    points.forEachIndexed { i, p ->
                        // Titik lingkaran
                        drawCircle(
                            color = primaryColor,
                            radius = 6f,
                            center = p
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3f,
                            center = p
                        )

                        // Tampilkan label suhu di titik tertentu (tiap 2 jam atau di puncak)
                        if (i % 2 == 0 || i == 0 || i == points.size - 1) {
                            drawContext.canvas.nativeCanvas.drawText(
                                "${temps[i]}°",
                                p.x,
                                p.y - 14f,
                                textPaint
                            )
                        }

                        // Label waktu di sumbu bawah
                        drawContext.canvas.nativeCanvas.drawText(
                            times[i],
                            p.x,
                            height - 10f,
                            timePaint
                        )
                    }
                }
            }
        }
    }
}
