package com.example.cuacaku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R
import com.example.cuacaku.data.remote.GeoResult
import com.example.cuacaku.data.remote.Hourly
import com.example.cuacaku.util.weatherLabel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale
import kotlin.math.roundToInt

// =====================================================
// 1. CurrentWeatherCard — kartu cuaca utama (besar, gradasi)
// =====================================================
@Composable
fun CurrentWeatherCard(
    cityName: String,
    temperature: Double,
    weatherCode: Int,
    modifier: Modifier = Modifier
) {
    val (labelRes, emoji) = weatherLabel(weatherCode)
    val isDark = isSystemInDarkTheme()

    // Gradasi warna: biru terang (light) atau biru gelap navy (dark)
    val gradient = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF1A3A5C), Color(0xFF0E1E30)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF42A5F5), Color(0xFF1976D2)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(gradient)
            .padding(vertical = 32.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Nama kota
            Text(
                text = cityName,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            // Emoji cuaca besar
            Text(text = emoji, fontSize = 72.sp)
            Spacer(Modifier.height(4.dp))
            // Suhu saat ini (besar, tebal)
            Text(
                text = "${temperature.roundToInt()}°C",
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            // Label kondisi cuaca
            Text(
                text = stringResource(labelRes),
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// =====================================================
// 2. StatsRow — tiga kartu statistik sejajar
// =====================================================
@Composable
fun StatsRow(
    humidity: Int,
    windSpeed: Double,
    feelsLike: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Kelembapan
        StatCard(
            emoji = "💧",
            value = "$humidity%",
            label = stringResource(R.string.humidity),
            modifier = Modifier.weight(1f)
        )
        // Angin
        StatCard(
            emoji = "💨",
            value = "${windSpeed.roundToInt()} km/h",
            label = stringResource(R.string.wind),
            modifier = Modifier.weight(1f)
        )
        // Terasa seperti
        StatCard(
            emoji = "🌡️",
            value = "${feelsLike.roundToInt()}°",
            label = stringResource(R.string.feels_like),
            modifier = Modifier.weight(1f)
        )
    }
}

/** Kartu kecil untuk satu statistik */
@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

// =====================================================
// 3. HourlyRow — scroll horizontal per jam (24 jam ke depan)
// =====================================================
@Composable
fun HourlyRow(hourly: Hourly, modifier: Modifier = Modifier) {
    // Cari indeks jam pertama yang >= sekarang
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    val startIndex = hourly.time.indexOfFirst { timeStr ->
        try {
            LocalDateTime.parse(timeStr, formatter) >= now
        } catch (_: Exception) { false }
    }.coerceAtLeast(0)

    // Ambil 24 jam ke depan mulai dari startIndex
    val endIndex = (startIndex + 24).coerceAtMost(hourly.time.size)
    val hours = (startIndex until endIndex).toList()

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(hours) { i ->
            val time = try {
                LocalDateTime.parse(hourly.time[i], formatter)
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
            } catch (_: Exception) { hourly.time[i].takeLast(5) }

            val (_, emoji) = weatherLabel(hourly.weatherCode[i])
            val temp = hourly.temperature[i].roundToInt()

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(text = emoji, fontSize = 24.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "$temp°",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// =====================================================
// 4. DailyCard — kartu prakiraan satu hari
// =====================================================
@Composable
fun DailyCard(
    dateStr: String,
    weatherCode: Int,
    maxTemp: Double,
    minTemp: Double,
    rain: Int?,
    modifier: Modifier = Modifier
) {
    val (labelRes, emoji) = weatherLabel(weatherCode)

    // Format tanggal sesuai bahasa aplikasi (misal "Sen, 23 Nov")
    val formattedDate = try {
        val date = LocalDate.parse(dateStr)
        val locale = Locale.getDefault()
        val dayName = date.dayOfWeek.getDisplayName(JavaTextStyle.SHORT, locale)
        val month = date.month.getDisplayName(JavaTextStyle.SHORT, locale)
        "$dayName, ${date.dayOfMonth} $month"
    } catch (_: Exception) { dateStr }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji cuaca besar di kiri
            Text(text = emoji, fontSize = 36.sp)
            Spacer(Modifier.width(14.dp))
            // Kolom tengah: tanggal, label cuaca, probabilitas hujan
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(labelRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                // Tampilkan probabilitas hujan hanya jika tidak null
                if (rain != null) {
                    Text(
                        text = "🌧️ $rain%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            // Suhu max / min di kanan
            Text(
                text = "${maxTemp.roundToInt()}° / ${minTemp.roundToInt()}°",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// =====================================================
// 5. CityResultItem — item hasil pencarian kota
// =====================================================
@Composable
fun CityResultItem(
    result: GeoResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            // Nama kota
            Text(
                text = result.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Wilayah dan negara (gabung yang tidak null)
            val subtitle = listOfNotNull(result.admin1, result.country)
                .joinToString(", ")
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// =====================================================
// 6. LanguageDialog — dialog pilih bahasa
// =====================================================
@Composable
fun LanguageDialog(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.choose_language),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Pilihan Bahasa Indonesia
                TextButton(
                    onClick = { onSelect("in") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🇮🇩  Bahasa Indonesia",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                // Pilihan English
                TextButton(
                    onClick = { onSelect("en") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🇬🇧  English",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
