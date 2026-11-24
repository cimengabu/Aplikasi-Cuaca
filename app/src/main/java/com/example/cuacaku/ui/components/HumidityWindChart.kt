package com.example.cuacaku.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =====================================================
// HumidityWindChart — Kartu Kelembapan & Embun
// Menampilkan ring kelembapan animasi + info dew point
// =====================================================
@Composable
fun HumidityWindChart(
    humidity: Int,
    feelsLike: Double,
    temperature: Double,
    modifier: Modifier = Modifier
) {
    val animProg = remember { Animatable(0f) }

    LaunchedEffect(humidity) {
        animProg.animateTo(humidity / 100f, animationSpec = tween(1000))
    }

    // Hitung dew point (formula Magnus)
    val dewPoint = run {
        val a = 17.625; val b = 243.04
        val alpha = (a * temperature / (b + temperature)) + kotlin.math.ln(humidity / 100.0)
        (b * alpha / (a - alpha))
    }

    val humidityColor = when {
        humidity < 30 -> Color(0xFFFF7043) // Sangat Kering
        humidity < 50 -> Color(0xFFFFCA28) // Nyaman
        humidity < 70 -> Color(0xFF66BB6A) // Optimal
        humidity < 85 -> Color(0xFF42A5F5) // Lembap
        else          -> Color(0xFF5C6BC0) // Sangat Lembap
    }

    val humidLabel = when {
        humidity < 30 -> "Sangat Kering"
        humidity < 50 -> "Nyaman"
        humidity < 70 -> "Optimal"
        humidity < 85 -> "Lembap"
        else          -> "Sangat Lembap"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x3B101C30)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "💧 Kelembapan & Embun",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Ring kelembapan
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(100.dp)
                ) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        val stroke = 10.dp.toPx()
                        val radius = size.width / 2f - stroke / 2f

                        // Track
                        drawCircle(
                            color = Color.White.copy(alpha = 0.1f),
                            radius = radius,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )

                        // Progress arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(humidityColor.copy(alpha = 0.3f), humidityColor)
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * animProg.value,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$humidity%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "RH",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // Info detail
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    HumidInfoRow("🌡️", "Suhu", String.format("%.1f°C", temperature))
                    HumidInfoRow("🤔", "Terasa", String.format("%.1f°C", feelsLike))
                    HumidInfoRow("🌫️", "Titik Embun", String.format("%.1f°C", dewPoint))
                    HumidInfoRow("💧", "Status", humidLabel, labelColor = humidityColor)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Skala visual kelembapan
            Column {
                Text(
                    text = "Skala Kelembapan",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(6.dp))
                Canvas(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                    val gradientBrush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFF7043),
                            Color(0xFFFFCA28),
                            Color(0xFF66BB6A),
                            Color(0xFF42A5F5),
                            Color(0xFF5C6BC0)
                        )
                    )
                    drawRoundRect(brush = gradientBrush, cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
                    // Indikator posisi
                    val x = size.width * (humidity / 100f)
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = Offset(x.coerceIn(5.dp.toPx(), size.width - 5.dp.toPx()), size.height / 2f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kering", fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f))
                    Text("Optimal", fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f))
                    Text("Lembap", fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
private fun HumidInfoRow(
    emoji: String,
    label: String,
    value: String,
    labelColor: Color = Color.White
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 14.sp)
        Spacer(Modifier.width(6.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = labelColor)
        }
    }
}

@Suppress("unused")
private fun DrawScope.drawSmoothLine(points: List<Offset>, color: Color, strokeWidth: Float) {
    if (points.size < 2) return
    val path = Path()
    path.moveTo(points[0].x, points[0].y)
    for (i in 1 until points.size) {
        val prev = points[i - 1]
        val curr = points[i]
        val cx = (prev.x + curr.x) / 2f
        path.cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
    }
    drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
}
