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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================
// WindCompassCard — Kompas arah angin animasi
// Menampilkan arah angin, kecepatan, dan kategori
// =====================================================
@Composable
fun WindCompassCard(
    windSpeed: Double,
    windDirection: Double,
    windUnit: com.example.cuacaku.util.WindUnit,
    modifier: Modifier = Modifier
) {
    val animAngle = remember { Animatable(0f) }

    LaunchedEffect(windDirection) {
        animAngle.animateTo(
            targetValue = windDirection.toFloat(),
            animationSpec = tween(1000)
        )
    }

    val windSpeedDisplay = com.example.cuacaku.util.UnitConverter.formatWindSpeed(windSpeed, windUnit)

    val (categoryLabel, categoryColor) = when {
        windSpeed < 5    -> "Tenang"      to Color(0xFF4CAF50)
        windSpeed < 15   -> "Sepoi-sepoi" to Color(0xFF8BC34A)
        windSpeed < 29   -> "Angin Sedang" to Color(0xFFFFC107)
        windSpeed < 50   -> "Angin Kencang" to Color(0xFFFF9800)
        windSpeed < 75   -> "Sangat Kencang" to Color(0xFFF44336)
        else             -> "Badai" to Color(0xFF9C27B0)
    }

    val directionText = when ((windDirection.toInt() + 22) / 45 % 8) {
        0 -> "U" ; 1 -> "TL" ; 2 -> "T" ; 3 -> "TG"
        4 -> "S" ; 5 -> "BD" ; 6 -> "B" ; 7 -> "BL"
        else -> "U"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x3B101C30)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "💨 Angin",
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
                // Kompas Animasi
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
                    Canvas(modifier = Modifier.size(110.dp)) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val radius = size.width / 2f - 4.dp.toPx()

                        // Lingkaran luar
                        drawCircle(
                            color = Color.White.copy(alpha = 0.1f),
                            radius = radius,
                            style = Stroke(width = 1.5.dp.toPx())
                        )

                        // Tick marks
                        for (i in 0 until 8) {
                            val a = (i * 45f - 90f) * (PI / 180f)
                            val inner = radius - 8.dp.toPx()
                            val outer = radius
                            drawLine(
                                color = Color.White.copy(alpha = 0.3f),
                                start = Offset(cx + (inner * cos(a)).toFloat(), cy + (inner * sin(a)).toFloat()),
                                end = Offset(cx + (outer * cos(a)).toFloat(), cy + (outer * sin(a)).toFloat()),
                                strokeWidth = 1.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // Panah angin
                        val arrowAngle = (animAngle.value - 90f) * (PI / 180f)
                        val arrowLen = radius - 20.dp.toPx()

                        // Tail (asal angin)
                        val tailAngle = arrowAngle + PI
                        drawLine(
                            color = Color.White.copy(alpha = 0.3f),
                            start = Offset(cx, cy),
                            end = Offset(
                                cx + (arrowLen * 0.4f * cos(tailAngle)).toFloat(),
                                cy + (arrowLen * 0.4f * sin(tailAngle)).toFloat()
                            ),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Panah utama (arah angin pergi)
                        drawLine(
                            color = Color(0xFF42A5F5),
                            start = Offset(cx, cy),
                            end = Offset(
                                cx + (arrowLen * cos(arrowAngle)).toFloat(),
                                cy + (arrowLen * sin(arrowAngle)).toFloat()
                            ),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Kepala panah
                        val tipX = cx + (arrowLen * cos(arrowAngle)).toFloat()
                        val tipY = cy + (arrowLen * sin(arrowAngle)).toFloat()
                        val arrowHeadLen = 10.dp.toPx()
                        for (side in listOf(PI * 0.75, -PI * 0.75)) {
                            val ha = arrowAngle + side
                            drawLine(
                                color = Color(0xFF42A5F5),
                                start = Offset(tipX, tipY),
                                end = Offset(
                                    tipX + (arrowHeadLen * cos(ha)).toFloat(),
                                    tipY + (arrowHeadLen * sin(ha)).toFloat()
                                ),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // Titik tengah
                        drawCircle(color = Color.White, radius = 4.dp.toPx())
                    }

                    // Label arah di atas kompas (N/S/E/W)
                }

                // Info kecepatan & kategori
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = windSpeedDisplay,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 28.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = directionText,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Text(
                            text = categoryLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Legend arah
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("U" to "Utara", "T" to "Timur", "S" to "Selatan", "B" to "Barat").forEach { (abbr, full) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(abbr, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.85f))
                        Text(full, fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}
