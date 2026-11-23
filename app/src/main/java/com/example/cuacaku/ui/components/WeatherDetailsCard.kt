package com.example.cuacaku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.cuacaku.util.DirectionHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

// =====================================================
// WeatherDetailsCard — Grid 2-kolom kartu detail cuaca
// Termasuk Curah Hujan (mm) dan Tutupan Awan (%)
// =====================================================
@Composable
fun WeatherDetailsCard(
    pressureMsl: Double,
    cloudCover: Int,
    precipitation: Double,
    windDirection: Double,
    visibility: Double,
    uvIndexMax: Double?,
    sunrise: String?,
    sunset: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.details_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Baris 1: Tekanan Udara & Tutupan Awan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailItem(
                emoji = "🌡️",
                title = stringResource(R.string.pressure),
                value = "${pressureMsl.roundToInt()} hPa",
                modifier = Modifier.weight(1f)
            )
            DetailItem(
                emoji = "☁️",
                title = stringResource(R.string.cloud_cover),
                value = "$cloudCover%",
                modifier = Modifier.weight(1f)
            )
        }

        // Baris 2: Curah Hujan & Jarak Pandang (Masalah 1)
        val visibilityKm = (visibility / 1000.0)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailItem(
                emoji = "🌧️",
                title = stringResource(R.string.precipitation_label),
                value = String.format(Locale.US, "%.1f mm", precipitation),
                modifier = Modifier.weight(1f)
            )
            DetailItem(
                emoji = "👁️",
                title = stringResource(R.string.visibility),
                value = String.format(Locale.US, "%.1f km", visibilityKm),
                modifier = Modifier.weight(1f)
            )
        }

        // Baris 3: Arah Angin & Indeks UV
        val windDirRes = DirectionHelper.getWindDirectionRes(windDirection)
        val (uvLabelRes, uvColor) = getUvCategory(uvIndexMax ?: 0.0)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailItem(
                emoji = "🧭",
                title = stringResource(R.string.wind_direction),
                value = stringResource(windDirRes),
                subtitle = "${windDirection.roundToInt()}°",
                modifier = Modifier.weight(1f)
            )

            // Indeks UV Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "☀️", fontSize = 20.sp)
                        Spacer(Modifier.padding(horizontal = 4.dp))
                        Text(
                            text = stringResource(R.string.uv_index),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uvIndexMax?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(uvColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(uvLabelRes),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Baris 4: Matahari Terbit & Matahari Terbenam
        val formattedSunrise = formatSunTime(sunrise)
        val formattedSunset = formatSunTime(sunset)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailItem(
                emoji = "🌅",
                title = stringResource(R.string.sunrise),
                value = formattedSunrise,
                modifier = Modifier.weight(1f)
            )
            DetailItem(
                emoji = "🌇",
                title = stringResource(R.string.sunset),
                value = formattedSunset,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DetailItem(
    emoji: String,
    title: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 20.sp)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(Modifier.padding(horizontal = 3.dp))
                    Text(
                        text = "($subtitle)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

private fun getUvCategory(uv: Double): Pair<Int, Color> = when {
    uv < 3.0 -> R.string.uv_low to Color(0xFF4CAF50)
    uv < 6.0 -> R.string.uv_moderate to Color(0xFFFBC02D)
    uv < 8.0 -> R.string.uv_high to Color(0xFFFF9800)
    uv < 11.0 -> R.string.uv_very_high to Color(0xFFF44336)
    else -> R.string.uv_extreme to Color(0xFF9C27B0)
}

private fun formatSunTime(isoTime: String?): String {
    if (isoTime == null) return "--:--"
    return try {
        val parsed = LocalDateTime.parse(isoTime)
        parsed.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        isoTime.takeLast(5)
    }
}
