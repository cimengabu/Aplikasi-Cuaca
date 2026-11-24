package com.example.cuacaku.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R
import com.example.cuacaku.data.remote.AirQualityResponse
import com.example.cuacaku.util.AqiHelper

// =====================================================
// AirQualityCard — Kartu Kualitas Udara Persis Screenshot Xiaomi / HyperOS
// Translucent frosted glass dengan Gauge Lingkaran di kanan
// =====================================================
@Composable
fun AirQualityCard(
    airQuality: AirQualityResponse?,
    modifier: Modifier = Modifier
) {
    val aqiVal = airQuality?.current?.usAqi ?: return
    val category = AqiHelper.getAqiCategory(aqiVal)
    val categoryName = stringResource(category.labelRes)

    // Deskripsi singkat dampak kesehatan sesuai standar AQI
    val descriptionText = when {
        aqiVal <= 50 -> "Kualitas udara sangat memuaskan dan risiko kesehatan udara hampir tidak ada."
        aqiVal <= 100 -> "Kualitas udara dapat diterima; namun, beberapa polutan mungkin sedikit memengaruhi individu sensitif."
        aqiVal <= 150 -> "Anggota kelompok sensitif mungkin mengalami efek kesehatan. Masyarakat umum cenderung tidak terdampak."
        aqiVal <= 200 -> "Semua orang mungkin mulai mengalami efek kesehatan; kelompok sensitif dapat mengalami efek lebih serius."
        else -> "Peringatan darurat kesehatan: seluruh populasi kemungkinan besar terdampak."
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x3B101C30) // Translucent dark glass
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sisi Kiri: Judul Status & Penjelasan Singkat
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = category.color,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Text(
                    text = descriptionText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Sisi Kanan: Circular Gauge Indikator Skor AQI (Persis Screenshot)
            Box(
                modifier = Modifier.size(68.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 6.dp.toPx()
                    val radius = (size.minDimension - stroke) / 2

                    // Lingkaran Jalur (Track)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.15f),
                        radius = radius,
                        style = Stroke(width = stroke)
                    )

                    // Busur Nilai AQI
                    val sweepAngle = ((aqiVal.toFloat() / 300f).coerceIn(0.08f, 1f)) * 360f
                    drawArc(
                        color = category.color,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }

                // Angka Skor AQI di Tengah Lingkaran
                Text(
                    text = "$aqiVal",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 22.sp
                    )
                )
            }
        }
    }
}
