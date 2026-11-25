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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

// =====================================================
// Kategori Cuaca untuk Animasi Latar Belakang
// =====================================================
private enum class WeatherEffectType {
    SUNNY,
    CLEAR_NIGHT,
    CLOUDY,
    RAIN,
    THUNDERSTORM,
    SNOW,
    FOG
}

/**
 * Menentukan tipe efek cuaca yang AKURAT berdasarkan:
 * - weatherCode  : kode WMO mentah dari API
 * - precipitation: curah hujan aktual (mm). Jika < 0.2 mm, kode hujan/badai diabaikan
 * - cloudCover   : persentase tutupan awan (0–100)
 * - isDay        : siang/malam
 *
 * Ini memastikan background TIDAK menampilkan hujan/badai ketika curah hujan nyata hampir nol.
 */
private fun resolveEffectType(
    code: Int,
    isDay: Boolean,
    precipitation: Double = 0.0,
    cloudCover: Int = 0
): WeatherEffectType {
    // ---- Badai petir ----
    if (code in setOf(95, 96, 99)) {
        // Hanya tampilkan badai jika memang ada curah hujan
        return if (precipitation >= 0.2) WeatherEffectType.THUNDERSTORM
        else byCloudCover(cloudCover, isDay)
    }

    // ---- Hujan / gerimis ----
    if (code in setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82)) {
        return if (precipitation >= 0.2) WeatherEffectType.RAIN
        else byCloudCover(cloudCover, isDay)
    }

    // ---- Salju ----
    if (code in setOf(71, 73, 75, 77, 85, 86)) return WeatherEffectType.SNOW

    // ---- Kabut ----
    if (code in setOf(45, 48)) return WeatherEffectType.FOG

    // ---- Berawan (kode 2 & 3) ----
    if (code in setOf(2, 3)) return byCloudCover(cloudCover, isDay)

    // ---- Berawan sebagian (kode 1) atau cerah (kode 0) ----
    return byCloudCover(cloudCover, isDay)
}

/** Menentukan tipe efek berdasarkan persentase tutupan awan */
private fun byCloudCover(cloudCover: Int, isDay: Boolean): WeatherEffectType = when {
    cloudCover >= 70 -> WeatherEffectType.CLOUDY
    cloudCover >= 25 -> if (isDay) WeatherEffectType.SUNNY else WeatherEffectType.CLOUDY
    isDay            -> WeatherEffectType.SUNNY
    else             -> WeatherEffectType.CLEAR_NIGHT
}

// =====================================================
// WeatherAnimatedBackground — Latar Belakang Animasi Real-Time
// Dibuat persis seperti aplikasi cuaca bawaan Xiaomi / HyperOS / One UI
// =====================================================
@Composable
fun WeatherAnimatedBackground(
    weatherCode: Int,
    isDay: Boolean,
    precipitation: Double = 0.0,
    cloudCover: Int = 0,
    modifier: Modifier = Modifier
) {
    val effectType = remember(weatherCode, isDay, precipitation, cloudCover) {
        resolveEffectType(weatherCode, isDay, precipitation, cloudCover)
    }

    // Warna gradasi dasar langit atmosferik — akurat per kondisi
    val skyColors = remember(effectType, isDay, cloudCover) {
        when (effectType) {
            WeatherEffectType.THUNDERSTORM -> listOf(
                Color(0xFF0A1020), // Sangat gelap badai
                Color(0xFF131B2D),
                Color(0xFF0F1726)
            )
            WeatherEffectType.RAIN -> listOf(
                Color(0xFF1A2535), // Langit hujan biru kelabu
                Color(0xFF243040),
                Color(0xFF1C2838)
            )
            WeatherEffectType.SUNNY -> when {
                cloudCover >= 50 -> listOf(  // Cerah berawan
                    Color(0xFF3A7FBD),
                    Color(0xFF5A9FD4),
                    Color(0xFF7BBDE0)
                )
                cloudCover >= 25 -> listOf(  // Cerah sebagian
                    Color(0xFF1E88E5),
                    Color(0xFF42A5F5),
                    Color(0xFF80CBFF)
                )
                else -> listOf(              // Cerah penuh
                    Color(0xFF1565C0),
                    Color(0xFF1E88E5),
                    Color(0xFF64B5F6)
                )
            }
            WeatherEffectType.CLEAR_NIGHT -> when {
                cloudCover >= 50 -> listOf(
                    Color(0xFF141E2B),
                    Color(0xFF1A2638),
                    Color(0xFF141E2B)
                )
                else -> listOf(
                    Color(0xFF090E17),
                    Color(0xFF111A29),
                    Color(0xFF0D1420)
                )
            }
            WeatherEffectType.CLOUDY -> if (isDay) {
                listOf(Color(0xFF455A64), Color(0xFF607D8B), Color(0xFF78909C))
            } else {
                listOf(Color(0xFF182230), Color(0xFF243242), Color(0xFF182230))
            }
            WeatherEffectType.SNOW -> listOf(
                Color(0xFF4A6572),
                Color(0xFF6A8595),
                Color(0xFF8AA5B5)
            )
            WeatherEffectType.FOG -> listOf(
                Color(0xFF546E7A),
                Color(0xFF78909C),
                Color(0xFF90A4AE)
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "weather_anim")

    // Progress animasi linear (kecepatan stabil untuk partikel)
    val animProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_progress"
    )

    // Pergerakan awan mengapung perlahan (durasi lebih lambat)
    val cloudProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 35000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_progress"
    )

    // Kilatan petir realistis saat badai
    val lightning = remember { Animatable(0f) }
    if (effectType == WeatherEffectType.THUNDERSTORM) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(Random.nextLong(3500, 7500))
                lightning.snapTo(0.65f)
                delay(50)
                lightning.snapTo(0.1f)
                delay(40)
                lightning.snapTo(0.85f)
                delay(70)
                lightning.animateTo(0f, tween(250))
            }
        }
    }

    // Partikel hujan (garis miring tipis tajam khas Xiaomi Weather)
    val raindrops = remember {
        List(80) {
            Raindrop(
                xNorm = Random.nextFloat(),
                yOffset = Random.nextFloat(),
                length = Random.nextFloat() * 26f + 20f,
                speed = Random.nextFloat() * 0.5f + 1.1f,
                alpha = Random.nextFloat() * 0.45f + 0.35f,
                slant = Random.nextFloat() * 3f + 4f
            )
        }
    }

    // Partikel bintang malam hari
    val stars = remember {
        List(50) {
            Star(
                xNorm = Random.nextFloat(),
                yNorm = Random.nextFloat() * 0.65f,
                radius = Random.nextFloat() * 1.8f + 1f,
                baseAlpha = Random.nextFloat() * 0.6f + 0.3f,
                speed = Random.nextFloat() * 2f + 1f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Gambar Gradasi Langit Penuh
            drawRect(
                brush = Brush.verticalGradient(
                    colors = skyColors,
                    startY = 0f,
                    endY = height
                ),
                size = size
            )

            // 2. Gambar Gumpalan Awan Volumetrik (Persis Screenshot)
            if (effectType == WeatherEffectType.RAIN || effectType == WeatherEffectType.THUNDERSTORM || effectType == WeatherEffectType.CLOUDY) {
                drawMoodyStormClouds(width, height, cloudProgress, isDark = !isDay)
            }

            // 3. Efek Spesifik Cuaca
            when (effectType) {
                WeatherEffectType.RAIN, WeatherEffectType.THUNDERSTORM -> {
                    // Garis-garis Hujan Menukik
                    raindrops.forEach { drop ->
                        val progress = (animProgress * drop.speed + drop.yOffset) % 1f
                        val y = progress * (height + 100f) - 50f
                        val x = (drop.xNorm * width - (progress * drop.slant * 8f)) % width
                        val actualX = if (x < 0) x + width else x

                        drawLine(
                            color = Color(0xFFD4E4F7).copy(alpha = drop.alpha),
                            start = Offset(actualX, y),
                            end = Offset(actualX - drop.slant, y + drop.length),
                            strokeWidth = 1.6f
                        )
                    }

                    // Kilatan Petir Menerangi Langit
                    if (lightning.value > 0f) {
                        drawRect(
                            color = Color(0xFFA5C8F7).copy(alpha = lightning.value),
                            size = size
                        )
                    }
                }

                WeatherEffectType.CLEAR_NIGHT -> {
                    // Bintang Berkelip
                    stars.forEach { star ->
                        val twinkle = (sin(animProgress * 6.28f * star.speed + star.xNorm * 10f) + 1f) / 2f
                        val alpha = (star.baseAlpha * (0.3f + 0.7f * twinkle)).coerceIn(0f, 1f)
                        drawCircle(
                            color = Color.White.copy(alpha = alpha),
                            radius = star.radius,
                            center = Offset(star.xNorm * width, star.yNorm * height)
                        )
                    }
                    // Cahaya Bulan Lembut
                    val moonCenter = Offset(width * 0.82f, height * 0.12f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x33D1E0FF), Color.Transparent),
                            center = moonCenter,
                            radius = width * 0.35f
                        ),
                        center = moonCenter,
                        radius = width * 0.35f
                    )
                }

                WeatherEffectType.SUNNY -> {
                    val sunCenter = Offset(width * 0.82f, height * 0.12f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x66FFF59D), Color(0x22FFE082), Color.Transparent),
                            center = sunCenter,
                            radius = width * 0.45f
                        ),
                        center = sunCenter,
                        radius = width * 0.45f
                    )
                }

                WeatherEffectType.SNOW -> {
                    raindrops.take(45).forEach { flake ->
                        val progress = (animProgress * flake.speed * 0.4f + flake.yOffset) % 1f
                        val y = progress * (height + 50f) - 20f
                        val sway = sin(animProgress * 6.28f + flake.yOffset * 10f) * 15f
                        val x = (flake.xNorm * width + sway) % width
                        val actualX = if (x < 0) x + width else x

                        drawCircle(
                            color = Color.White.copy(alpha = flake.alpha),
                            radius = 3.2f,
                            center = Offset(actualX, y)
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

// =====================================================
// Helper Penggambar Awan Badai Volumetrik (Persis Screenshot)
// =====================================================
private fun DrawScope.drawMoodyStormClouds(
    width: Float,
    height: Float,
    progress: Float,
    isDark: Boolean
) {
    val cloudBase = if (isDark) Color(0xFF121B2A) else Color(0xFF4A5C68)
    val cloudHighlight = if (isDark) Color(0xFF223147) else Color(0xFF758A99)
    val cloudDark = if (isDark) Color(0xFF0A101A) else Color(0xFF2E3D47)

    val drift = (progress * width * 0.25f) % (width * 0.5f)

    // Awan Lapisan Atas (Paling Gelap & Tebal di Belakang Judul & Suhu)
    drawCircle(
        color = cloudDark.copy(alpha = 0.85f),
        radius = width * 0.42f,
        center = Offset(width * 0.2f + drift, height * 0.08f)
    )
    drawCircle(
        color = cloudHighlight.copy(alpha = 0.5f),
        radius = width * 0.48f,
        center = Offset(width * 0.75f - drift, height * 0.12f)
    )
    drawCircle(
        color = cloudBase.copy(alpha = 0.9f),
        radius = width * 0.55f,
        center = Offset(width * 0.5f, height * 0.05f)
    )

    // Awan Lapisan Tengah (Di Bawah Suhu Utama)
    drawCircle(
        color = cloudHighlight.copy(alpha = 0.35f),
        radius = width * 0.45f,
        center = Offset(width * 0.15f - drift * 0.5f, height * 0.26f)
    )
    drawCircle(
        color = cloudDark.copy(alpha = 0.7f),
        radius = width * 0.5f,
        center = Offset(width * 0.85f + drift * 0.5f, height * 0.24f)
    )
}

private data class Raindrop(
    val xNorm: Float,
    val yOffset: Float,
    val length: Float,
    val speed: Float,
    val alpha: Float,
    val slant: Float
)

private data class Star(
    val xNorm: Float,
    val yNorm: Float,
    val radius: Float,
    val baseAlpha: Float,
    val speed: Float
)
