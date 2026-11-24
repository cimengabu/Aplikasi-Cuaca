package com.example.cuacaku.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R

data class WeatherAlert(
    val title: String,
    val description: String,
    val severityColor: Color,
    val advice: String
)

/**
 * Mendeteksi adanya potensi kondisi cuaca ekstrem secara otomatis dari data cuaca real-time.
 */
fun detectSevereWeather(
    weatherCode: Int,
    precipitation: Double,
    windSpeed: Double,
    temperature: Double,
    uvIndex: Double?,
    aqi: Int?
): WeatherAlert? {
    return when {
        // 1. Badai petir aktif
        weatherCode in setOf(95, 96, 99) -> WeatherAlert(
            title = "Peringatan Badai Petir",
            description = "Terdeteksi aktivitas petir dan kilat di wilayah sekitar.",
            severityColor = Color(0xFFFF5252),
            advice = "Hindari berada di area terbuka dan jauhkan diri dari pepohonan tinggi atau tiang listrik."
        )
        // 2. Curah hujan sangat lebat
        precipitation >= 10.0 -> WeatherAlert(
            title = "Peringatan Hujan Lebat",
            description = "Intensitas curah hujan tinggi berpotensi genangan air.",
            severityColor = Color(0xFFFF7043),
            advice = "Waspadai genangan air di jalan raya dan kurangi kecepatan berkendara."
        )
        // 3. Angin kencang / badai angin
        windSpeed >= 35.0 -> WeatherAlert(
            title = "Peringatan Angin Kencang",
            description = "Kecepatan angin terdeteksi mencapai $windSpeed km/h.",
            severityColor = Color(0xFFFFB300),
            advice = "Waspadai dahan patah atau benda beterbangan saat beraktivitas di luar."
        )
        // 4. Kualitas udara berbahaya
        (aqi ?: 0) >= 151 -> WeatherAlert(
            title = "Peringatan Kualitas Udara Buruk",
            description = "Indeks AQI mencapai $aqi (Kategori Tidak Sehat).",
            severityColor = Color(0xFFBA68C8),
            advice = "Gunakan masker medis atau respirator N95 dan minimalkan aktivitas fisik di luar ruangan."
        )
        // 5. Radiasi UV Sangat Ekstrem
        (uvIndex ?: 0.0) >= 8.5 -> WeatherAlert(
            title = "Peringatan Radiasi UV Ekstrem",
            description = "Indeks UV maksimum mencapai $uvIndex.",
            severityColor = Color(0xFFFF4081),
            advice = "Gunakan tabir surya SPF 30+, kacamata pelindung UV, dan hindari paparan langsung matahari tengah hari."
        )
        // 6. Panas ekstrem
        temperature >= 36.0 -> WeatherAlert(
            title = "Peringatan Suhu Panas Ekstrem",
            description = "Suhu udara mencapai ${temperature.toInt()}°C.",
            severityColor = Color(0xFFFF6E40),
            advice = "Hindari dehidrasi dengan memperbanyak minum air dan gunakan pakaian tipis menyerap keringat."
        )
        else -> null
    }
}

// =====================================================
// SevereWeatherAlertCard — Kartu Peringatan Cuaca Ekstrem
// Tampil otomatis saat kondisi cuaca berbahaya terdeteksi
// =====================================================
@Composable
fun SevereWeatherAlertCard(
    alert: WeatherAlert,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isDismissed by remember { mutableStateOf(false) }

    if (isDismissed) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = alert.severityColor.copy(alpha = 0.18f)
        ),
        border = BorderStroke(1.2.dp, alert.severityColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(alert.severityColor.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = alert.severityColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = alert.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(
                        onClick = { isDismissed = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "💡 Saran Keselamatan:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = alert.severityColor
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = alert.advice,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.95f),
                            lineHeight = 17.sp
                        )
                    )
                }
            }
        }
    }
}
