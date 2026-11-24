package com.example.cuacaku.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =====================================================
// FeelsLikeDetailCard — Kartu "Terasa Seperti" lengkap
// Menampilkan ring animasi sensasi suhu + penjelasan faktor
// =====================================================
@Composable
fun FeelsLikeDetailCard(
    temperature: Double,
    feelsLike: Double,
    humidity: Int,
    windSpeed: Double,
    modifier: Modifier = Modifier
) {
    val diff = feelsLike - temperature

    val (feelDesc, feelEmoji, feelColor) = when {
        diff <= -5.0 -> Triple("Terasa Jauh Lebih Dingin", "🥶", Color(0xFF42A5F5))
        diff <= -2.0 -> Triple("Terasa Lebih Dingin", "😰", Color(0xFF64B5F6))
        diff <= 2.0  -> Triple("Terasa Sama", "😊", Color(0xFF66BB6A))
        diff <= 5.0  -> Triple("Terasa Lebih Hangat", "🥵", Color(0xFFFFCA28))
        else         -> Triple("Terasa Jauh Lebih Panas", "🔥", Color(0xFFFF5722))
    }

    // Normalisasi 0..1 (range -20 sampai 50 derajat)
    val normalizedFeels = ((feelsLike + 20) / 70.0).toFloat().coerceIn(0f, 1f)
    val animProg = remember { Animatable(0f) }

    LaunchedEffect(feelsLike) {
        animProg.animateTo(normalizedFeels, animationSpec = tween(900))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x3B101C30)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "🤔 Sensasi Suhu",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ring animasi
                Canvas(modifier = Modifier.size(90.dp)) {
                    val strokeW = 10.dp.toPx()
                    val radius = size.width / 2f - strokeW / 2f

                    // Track
                    drawCircle(
                        color = Color.White.copy(alpha = 0.1f),
                        radius = radius,
                        style = Stroke(strokeW, cap = StrokeCap.Round)
                    )

                    // Arc warna
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color(0xFF42A5F5), Color(0xFF66BB6A), Color(0xFFFFCA28), Color(0xFFFF5722))
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * animProg.value,
                        useCenter = false,
                        style = Stroke(strokeW, cap = StrokeCap.Round)
                    )

                    // Center text drawn via Compose below
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = feelEmoji,
                            fontSize = 28.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = String.format("%.1f°C", feelsLike),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = feelColor,
                                fontSize = 24.sp
                            )
                            Text(
                                text = feelDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Faktor-faktor penyebab
            Text(
                text = "Faktor yang Mempengaruhi",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeelFactorChip(
                    emoji = "🌡️",
                    label = "Suhu Aktual",
                    value = String.format("%.1f°C", temperature),
                    modifier = Modifier.weight(1f)
                )
                FeelFactorChip(
                    emoji = "💧",
                    label = "Kelembapan",
                    value = "$humidity%",
                    modifier = Modifier.weight(1f)
                )
                FeelFactorChip(
                    emoji = "💨",
                    label = "Angin",
                    value = String.format("%.1f km/j", windSpeed),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            // Penjelasan
            val explanation = buildString {
                when {
                    windSpeed > 20 && humidity > 70 ->
                        append("Angin kencang dikombinasikan kelembapan tinggi membuat suhu terasa berbeda signifikan.")
                    windSpeed > 20 ->
                        append("Efek Wind Chill: angin kencang mempercepat kehilangan panas tubuh.")
                    humidity > 80 ->
                        append("Kelembapan tinggi menghambat penguapan keringat, membuat tubuh terasa lebih panas.")
                    humidity < 30 ->
                        append("Udara kering meningkatkan penguapan, membuat suhu terasa lebih sejuk.")
                    else ->
                        append("Kondisi angin dan kelembapan saat ini relatif nyaman untuk aktivitas luar ruangan.")
                }
            }

            Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun FeelFactorChip(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 16.sp)
            Spacer(Modifier.height(2.dp))
            Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
