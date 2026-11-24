package com.example.cuacaku.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

// =====================================================
// Kategori Efek Animasi Cuaca
// =====================================================
private enum class WeatherType {
    SUNNY,
    CLEAR_NIGHT,
    CLOUDY,
    RAIN,
    THUNDERSTORM,
    SNOW,
    FOG
}

private fun resolveWeatherType(code: Int, isDay: Boolean): WeatherType {
    return when {
        code in setOf(95, 96, 99) -> WeatherType.THUNDERSTORM
        code in setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82) -> WeatherType.RAIN
        code in setOf(71, 73, 75, 77, 85, 86) -> WeatherType.SNOW
        code in setOf(45, 48) -> WeatherType.FOG
        code in setOf(1, 2, 3) -> WeatherType.CLOUDY
        isDay -> WeatherType.SUNNY
        else -> WeatherType.CLEAR_NIGHT
    }
}

// =====================================================
// WeatherAnimatedBackground — Latar Belakang Animasi Real-Time
// Meniru aplikasi cuaca bawaan HP (iOS / Samsung One UI / Pixel)
// =====================================================
@Composable
fun WeatherAnimatedBackground(
    weatherCode: Int,
    isDay: Boolean,
    modifier: Modifier = Modifier
) {
    val type = remember(weatherCode, isDay) { resolveWeatherType(weatherCode, isDay) }

    // Warna gradasi latar belakang dinamis berdasarkan kondisi cuaca
    val backgroundColors = remember(type, isDay) {
        when (type) {
            WeatherType.SUNNY -> listOf(
                Color(0xFF2196F3),
                Color(0xFF4FC3F7),
                Color(0xFFFFE082)
            )
            WeatherType.CLEAR_NIGHT -> listOf(
                Color(0xFF0D1B2A),
                Color(0xFF1B263B),
                Color(0xFF0F172A)
            )
            WeatherType.CLOUDY -> if (isDay) {
                listOf(
                    Color(0xFF607D8B),
                    Color(0xFF78909C),
                    Color(0xFF90A4AE)
                )
            } else {
                listOf(
                    Color(0xFF1E293B),
                    Color(0xFF334155),
                    Color(0xFF1E293B)
                )
            }
            WeatherType.RAIN -> if (isDay) {
                listOf(
                    Color(0xFF37474F),
                    Color(0xFF455A64),
                    Color(0xFF546E7A)
                )
            } else {
                listOf(
                    Color(0xFF0F172A),
                    Color(0xFF1E293B),
                    Color(0xFF0F172A)
                )
            }
            WeatherType.THUNDERSTORM -> listOf(
                Color(0xFF181824),
                Color(0xFF22223B),
                Color(0xFF12121A)
            )
            WeatherType.SNOW -> listOf(
                Color(0xFF64B5F6),
                Color(0xFF90CAF9),
                Color(0xFFE0F7FA)
            )
            WeatherType.FOG -> listOf(
                Color(0xFF78909C),
                Color(0xFF90A4AE),
                Color(0xFFB0BEC5)
            )
        }
    }

    // Transisi animasi tak terbatas (60fps mulus)
    val infiniteTransition = rememberInfiniteTransition(label = "weather_anim")

    // Progress animasi linear berkelanjutan (0f .. 1f berulang)
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    // Denyut sinar matahari untuk cuaca cerah
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sun_pulse"
    )

    // Kilat petir untuk badai
    val lightningAlpha = remember { Animatable(0f) }
    if (type == WeatherType.THUNDERSTORM) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(Random.nextLong(3000, 7000))
                // Flash pertama
                lightningAlpha.snapTo(0.7f)
                delay(60)
                lightningAlpha.snapTo(0.1f)
                delay(40)
                // Flash kedua
                lightningAlpha.snapTo(0.9f)
                delay(90)
                lightningAlpha.animateTo(0f, tween(300))
            }
        }
    }

    // Partikel hujan
    val raindrops = remember {
        List(65) {
            Raindrop(
                xNorm = Random.nextFloat(),
                yOffset = Random.nextFloat(),
                length = Random.nextFloat() * 22f + 18f,
                speed = Random.nextFloat() * 0.4f + 0.8f,
                alpha = Random.nextFloat() * 0.4f + 0.3f
            )
        }
    }

    // Partikel bintang malam hari
    val stars = remember {
        List(50) {
            Star(
                xNorm = Random.nextFloat(),
                yNorm = Random.nextFloat() * 0.65f, // Bintang hanya di langit atas
                radius = Random.nextFloat() * 2.2f + 1f,
                baseAlpha = Random.nextFloat() * 0.6f + 0.3f,
                speed = Random.nextFloat() * 2f + 1f
            )
        }
    }

    // Partikel salju
    val snowflakes = remember {
        List(55) {
            Snowflake(
                xNorm = Random.nextFloat(),
                yOffset = Random.nextFloat(),
                radius = Random.nextFloat() * 3.5f + 2f,
                speed = Random.nextFloat() * 0.25f + 0.4f,
                swingSpeed = Random.nextFloat() * 3f + 1f,
                alpha = Random.nextFloat() * 0.45f + 0.4f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Gambar Gradasi Langit Dinamis
            drawRect(
                brush = Brush.verticalGradient(
                    colors = backgroundColors,
                    startY = 0f,
                    endY = height
                ),
                size = size
            )

            // 2. Gambar Efek Spesifik Berdasarkan Cuaca
            when (type) {
                WeatherType.SUNNY -> {
                    // Sinar & Aura Matahari di Kanan Atas
                    val sunCenter = Offset(width * 0.82f, height * 0.12f)
                    val baseRadius = width * 0.38f * sunPulse

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x66FFF59D),
                                Color(0x33FFE082),
                                Color.Transparent
                            ),
                            center = sunCenter,
                            radius = baseRadius * 1.5f
                        ),
                        center = sunCenter,
                        radius = baseRadius * 1.5f
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xAAFFFFFF),
                                Color(0x66FFF176),
                                Color.Transparent
                            ),
                            center = sunCenter,
                            radius = baseRadius * 0.6f
                        ),
                        center = sunCenter,
                        radius = baseRadius * 0.6f
                    )
                }

                WeatherType.CLEAR_NIGHT -> {
                    // Bintang Berkelip
                    stars.forEach { star ->
                        val twinkle = (sin((animProgress * 6.28f * star.speed) + star.xNorm * 10f) + 1f) / 2f
                        val alpha = (star.baseAlpha * (0.4f + 0.6f * twinkle)).coerceIn(0f, 1f)

                        drawCircle(
                            color = Color.White.copy(alpha = alpha),
                            radius = star.radius,
                            center = Offset(star.xNorm * width, star.yNorm * height)
                        )
                    }

                    // Cahaya Bulan Lembut di Kanan Atas
                    val moonCenter = Offset(width * 0.8f, height * 0.12f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x44E0E7FF),
                                Color(0x11818CF8),
                                Color.Transparent
                            ),
                            center = moonCenter,
                            radius = width * 0.35f
                        ),
                        center = moonCenter,
                        radius = width * 0.35f
                    )
                }

                WeatherType.CLOUDY -> {
                    // Awan Berarak Pelan di Langit
                    drawDriftingClouds(width, height, animProgress, isDay)
                }

                WeatherType.RAIN, WeatherType.THUNDERSTORM -> {
                    // Butiran Hujan Jatuh Menukik
                    raindrops.forEach { drop ->
                        val progressWithOffset = (animProgress * drop.speed + drop.yOffset) % 1f
                        val y = progressWithOffset * (height + 100f) - 50f
                        val x = (drop.xNorm * width - (progressWithOffset * 60f)) % width
                        val actualX = if (x < 0) x + width else x

                        drawLine(
                            color = Color(0xCCB0BEC5).copy(alpha = drop.alpha),
                            start = Offset(actualX, y),
                            end = Offset(actualX - 8f, y + drop.length),
                            strokeWidth = 2f
                        )
                    }

                    // Awan Mendung di Atas
                    drawDriftingClouds(width, height, animProgress * 0.5f, isDay = false)

                    // Kilatan Petir (Jika Thunderstorm)
                    if (lightningAlpha.value > 0f) {
                        drawRect(
                            color = Color(0xEEFFFFFF).copy(alpha = lightningAlpha.value),
                            size = size
                        )
                    }
                }

                WeatherType.SNOW -> {
                    // Kepingan Salju Melayang Perlahan & Bergoyang
                    snowflakes.forEach { flake ->
                        val progressWithOffset = (animProgress * flake.speed + flake.yOffset) % 1f
                        val y = progressWithOffset * (height + 50f) - 20f
                        val sway = sin(animProgress * 6.28f * flake.swingSpeed + flake.yOffset * 10f) * 20f
                        val x = (flake.xNorm * width + sway) % width
                        val actualX = if (x < 0) x + width else x

                        drawCircle(
                            color = Color.White.copy(alpha = flake.alpha),
                            radius = flake.radius,
                            center = Offset(actualX, y)
                        )
                    }
                }

                WeatherType.FOG -> {
                    // Lapisan Kabut Horizontal yang Berombak
                    val fogAlpha = (sin(animProgress * 6.28f) * 0.15f + 0.35f).coerceIn(0.2f, 0.5f)
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x99CFD8DC).copy(alpha = fogAlpha),
                                Color(0x77B0BEC5).copy(alpha = fogAlpha * 0.7f),
                                Color.Transparent
                            ),
                            startY = height * 0.2f,
                            endY = height * 0.85f
                        ),
                        size = size
                    )
                }
            }
        }
    }
}

// =====================================================
// Helper Penggambar Awan Mengapung
// =====================================================
private fun DrawScope.drawDriftingClouds(
    width: Float,
    height: Float,
    progress: Float,
    isDay: Boolean
) {
    val cloudColor = if (isDay) Color(0x33FFFFFF) else Color(0x221E293B)
    val cloudColorFront = if (isDay) Color(0x44FFFFFF) else Color(0x33334155)

    // Layer Awan 1 (Belakang, Bergerak Pelan)
    val offset1 = (progress * width * 0.4f) % (width + 300f)
    drawCloudShape(
        centerX = offset1 - 100f,
        centerY = height * 0.15f,
        radius = width * 0.28f,
        color = cloudColor
    )
    drawCloudShape(
        centerX = offset1 + width * 0.45f,
        centerY = height * 0.18f,
        radius = width * 0.32f,
        color = cloudColor
    )

    // Layer Awan 2 (Depan)
    val offset2 = ((progress * 1.4f) * width * 0.4f) % (width + 350f)
    drawCloudShape(
        centerX = width - offset2 + 100f,
        centerY = height * 0.08f,
        radius = width * 0.22f,
        color = cloudColorFront
    )
}

private fun DrawScope.drawCloudShape(
    centerX: Float,
    centerY: Float,
    radius: Float,
    color: Color
) {
    drawCircle(
        color = color,
        radius = radius,
        center = Offset(centerX, centerY)
    )
    drawCircle(
        color = color,
        radius = radius * 0.75f,
        center = Offset(centerX - radius * 0.65f, centerY + radius * 0.2f)
    )
    drawCircle(
        color = color,
        radius = radius * 0.8f,
        center = Offset(centerX + radius * 0.65f, centerY + radius * 0.15f)
    )
}

// Data class partikel
private data class Raindrop(
    val xNorm: Float,
    val yOffset: Float,
    val length: Float,
    val speed: Float,
    val alpha: Float
)

private data class Star(
    val xNorm: Float,
    val yNorm: Float,
    val radius: Float,
    val baseAlpha: Float,
    val speed: Float
)

private data class Snowflake(
    val xNorm: Float,
    val yOffset: Float,
    val radius: Float,
    val speed: Float,
    val swingSpeed: Float,
    val alpha: Float
)
