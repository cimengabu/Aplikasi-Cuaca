package com.example.cuacaku.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R
import com.example.cuacaku.util.WeatherTips

// =====================================================
// WeatherTipsCard — Kartu "Saran Hari Ini" berbasis data cuaca aktual
// =====================================================
@Composable
fun WeatherTipsCard(
    rainProbMax: Int?,
    uvMax: Double?,
    maxTemp: Double,
    minTemp: Double,
    windSpeed: Double,
    aqi: Int?,
    weatherCode: Int,
    modifier: Modifier = Modifier
) {
    val tips = WeatherTips.generateTips(
        rainProbMax = rainProbMax,
        uvMax = uvMax,
        maxTemp = maxTemp,
        minTemp = minTemp,
        windSpeed = windSpeed,
        aqi = aqi,
        weatherCode = weatherCode
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "💡 ${stringResource(R.string.tips_title)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tips.forEach { tipRes ->
                    val emoji = when (tipRes) {
                        R.string.tip_rain -> "☂️"
                        R.string.tip_uv -> "🧴"
                        R.string.tip_heat -> "🥤"
                        R.string.tip_cold -> "🧥"
                        R.string.tip_wind -> "🚗"
                        R.string.tip_aqi -> "😷"
                        else -> "✨"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(tipRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}
