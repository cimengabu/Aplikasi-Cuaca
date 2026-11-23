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
import com.example.cuacaku.util.weatherLabel

// =====================================================
// CurrentWeatherCard — Kartu cuaca utama dengan gradasi dinamis
// =====================================================
@Composable
fun CurrentWeatherCard(
    cityName: String,
    temperature: Double,
    weatherCode: Int,
    isDay: Boolean,
    tempUnit: TempUnit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    lastUpdatedTime: String,
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    val (labelRes, emoji) = weatherLabel(weatherCode, isDay)
    val isDark = isSystemInDarkTheme()

    // Gradasi dinamis berdasarkan kode cuaca, kondisi siang/malam, dan tema
    val gradientColors = when {
        // Cerah siang
        isDay && weatherCode == 0 -> {
            if (isDark) listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
            else listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
        }
        // Cerah malam
        !isDay && weatherCode == 0 -> {
            if (isDark) listOf(Color(0xFF1A237E), Color(0xFF0B1038))
            else listOf(Color(0xFF283593), Color(0xFF1A237E))
        }
        // Berawan (siang / malam)
        weatherCode in 1..3 -> {
            if (isDark) listOf(Color(0xFF37474F), Color(0xFF212121))
            else listOf(Color(0xFF607D8B), Color(0xFF455A64))
        }
        // Hujan & Badai
        weatherCode in listOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99) -> {
            if (isDark) listOf(Color(0xFF263238), Color(0xFF102027))
            else listOf(Color(0xFF455A64), Color(0xFF263238))
        }
        // Salju
        weatherCode in listOf(71, 73, 75, 77, 85, 86) -> {
            if (isDark) listOf(Color(0xFF3949AB), Color(0xFF1A237E))
            else listOf(Color(0xFF64B5F6), Color(0xFF1E88E5))
        }
        // Berkabut
        weatherCode in listOf(45, 48) -> {
            if (isDark) listOf(Color(0xFF424242), Color(0xFF212121))
            else listOf(Color(0xFF78909C), Color(0xFF546E7A))
        }
        // Default
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
            .padding(vertical = 28.dp, horizontal = 20.dp)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = cityName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))

            // Emoji cuaca besar
            Text(text = emoji, fontSize = 72.sp)
            Spacer(Modifier.height(4.dp))

            // Suhu saat ini sesuai unit yang dipilih
            val tempFormatted = UnitConverter.formatTemperature(temperature, tempUnit)
            Text(
                text = tempFormatted,
                color = Color.White,
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))

            // Label kondisi cuaca
            Text(
                text = stringResource(labelRes),
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // Info terakhir diperbarui / status offline
            if (lastUpdatedTime.isNotEmpty() && !isOffline) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.last_updated, lastUpdatedTime),
                    color = Color.White.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
