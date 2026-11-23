package com.example.cuacaku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.UnitConverter
import com.example.cuacaku.util.resolveCurrentWeather
import java.util.Locale

// =====================================================
// CurrentWeatherCard — Kartu cuaca utama dengan label akurat & koordinat
// =====================================================
@Composable
fun CurrentWeatherCard(
    cityName: String,
    latitude: Double,
    longitude: Double,
    temperature: Double,
    weatherCode: Int,
    precipitation: Double,
    cloudCover: Int,
    isDay: Boolean,
    tempUnit: TempUnit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    lastUpdatedTime: String,
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    // Penentuan label cuaca akurat berbasis weather_code, precipitation, dan cloud_cover
    val resolved = resolveCurrentWeather(
        code = weatherCode,
        precipitation = precipitation,
        cloudCover = cloudCover,
        isDay = isDay
    )

    val isDark = isSystemInDarkTheme()

    // Gradasi dinamis berdasarkan kondisi cuaca hasil resolusi
    val gradientColors = when {
        // Cerah siang
        isDay && resolved.effectiveCode == 0 -> {
            if (isDark) listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
            else listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
        }
        // Cerah malam
        !isDay && resolved.effectiveCode == 0 -> {
            if (isDark) listOf(Color(0xFF1A237E), Color(0xFF0B1038))
            else listOf(Color(0xFF283593), Color(0xFF1A237E))
        }
        // Berawan sebagian / Berawan
        resolved.effectiveCode in 1..3 -> {
            if (isDark) listOf(Color(0xFF37474F), Color(0xFF212121))
            else listOf(Color(0xFF607D8B), Color(0xFF455A64))
        }
        // Hujan & Badai
        resolved.effectiveCode in listOf(51, 61, 65, 95) -> {
            if (isDark) listOf(Color(0xFF263238), Color(0xFF102027))
            else listOf(Color(0xFF455A64), Color(0xFF263238))
        }
        // Salju
        resolved.effectiveCode == 71 -> {
            if (isDark) listOf(Color(0xFF3949AB), Color(0xFF1A237E))
            else listOf(Color(0xFF64B5F6), Color(0xFF1E88E5))
        }
        // Berkabut
        resolved.effectiveCode == 45 -> {
            if (isDark) listOf(Color(0xFF424242), Color(0xFF212121))
            else listOf(Color(0xFF78909C), Color(0xFF546E7A))
        }
        else -> {
            if (isDark) listOf(Color(0xFF1A3A5C), Color(0xFF0E1E30))
            else listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
        }
    }

    val gradient = Brush.verticalGradient(gradientColors)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(gradient)
            .padding(vertical = 24.dp, horizontal = 20.dp)
    ) {
        // Tombol Favorite bintang di pojok kanan atas
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = if (isFavorite) stringResource(R.string.remove_from_favorites) else stringResource(R.string.add_to_favorites),
                tint = if (isFavorite) Color(0xFFFFD54F) else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nama kota
            Text(
                text = cityName,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Koordinat geografis kecil (Masalah 2d)
            val coordText = String.format(Locale.US, "📍 %.2f, %.2f", latitude, longitude)
            Text(
                text = coordText,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(Modifier.height(8.dp))

            // Emoji cuaca akurat
            Text(text = resolved.emoji, fontSize = 68.sp)
            Spacer(Modifier.height(4.dp))

            // Suhu saat ini
            val tempFormatted = UnitConverter.formatTemperature(temperature, tempUnit)
            Text(
                text = tempFormatted,
                color = Color.White,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))

            // Label kondisi cuaca akurat
            Text(
                text = stringResource(resolved.labelRes),
                color = Color.White.copy(alpha = 0.95f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            // Nilai curah hujan (mm) & tutupan awan (%) agar jelas bagi pengguna & guru (Masalah 1)
            val precipStr = String.format(Locale.US, "%.1f", precipitation)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${stringResource(R.string.precipitation_val, precipStr)}  •  ${stringResource(R.string.cloud_cover_val, cloudCover)}",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            // Info terakhir diperbarui / status offline
            if (lastUpdatedTime.isNotEmpty() && !isOffline) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.last_updated, lastUpdatedTime),
                    color = Color.White.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
