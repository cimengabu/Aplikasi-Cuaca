package com.example.cuacaku.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R

data class ActivityIndexItem(
    val icon: String,
    val title: String,
    val status: String,
    val statusColor: Color,
    val detailExplanation: String
)

// =====================================================
// LifestyleIndicesCard — Kartu Indeks Gaya Hidup & Aktivitas Luar
// Menghitung rekomendasi jogging, bersepeda, cuci mobil, jemur pakaian, dsb.
// =====================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LifestyleIndicesCard(
    temperature: Double,
    rainProb: Int?,
    windSpeed: Double,
    humidity: Int,
    cloudCover: Int,
    uvIndex: Double?,
    aqi: Int?,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf<ActivityIndexItem?>(null) }

    // 1. Jogging
    val jogging = when {
        (rainProb ?: 0) >= 50 || (aqi ?: 0) >= 150 -> ActivityIndexItem(
            icon = "🏃",
            title = "Jogging",
            status = "Kurang Baik",
            statusColor = Color(0xFFFF5252),
            detailExplanation = "Peluang hujan tinggi (${rainProb ?: 0}%) atau kualitas udara kurang sehat (AQI $aqi). Disarankan berolahraga di dalam ruangan."
        )
        temperature in 18.0..28.0 && (aqi ?: 0) <= 80 -> ActivityIndexItem(
            icon = "🏃",
            title = "Jogging",
            status = "Sangat Baik",
            statusColor = Color(0xFF4CAF50),
            detailExplanation = "Suhu sejuk nyaman ($temperature°C) dan udara bersih. Sangat optimal untuk lari atau jalan santai."
        )
        else -> ActivityIndexItem(
            icon = "🏃",
            title = "Jogging",
            status = "Cukup",
            statusColor = Color(0xFFFFB300),
            detailExplanation = "Kondisi cukup bersahabat. Perhatikan kecukupan cairan tubuh dan gunakan pakaian yang nyaman."
        )
    }

    // 2. Bersepeda
    val cycling = when {
        windSpeed >= 30.0 || (rainProb ?: 0) >= 60 -> ActivityIndexItem(
            icon = "🚴",
            title = "Bersepeda",
            status = "Tinggi Resiko",
            statusColor = Color(0xFFFF5252),
            detailExplanation = "Angin cukup kencang (${windSpeed.toInt()} km/h) atau jalanan berpotensi basah licin. Tunda gowes jauh."
        )
        windSpeed <= 15.0 && (rainProb ?: 0) <= 25 -> ActivityIndexItem(
            icon = "🚴",
            title = "Bersepeda",
            status = "Sangat Cocok",
            statusColor = Color(0xFF4CAF50),
            detailExplanation = "Angin sepoi tenang dan langit cerah. Momen ideal untuk gowes santai bersama teman."
        )
        else -> ActivityIndexItem(
            icon = "🚴",
            title = "Bersepeda",
            status = "Baik",
            statusColor = Color(0xFF29B6F6),
            detailExplanation = "Kondisi mendukung untuk bersepeda santai dengan kecepatan sedang."
        )
    }

    // 3. Cuci Mobil
    val carWash = when {
        (rainProb ?: 0) >= 40 -> ActivityIndexItem(
            icon = "🚗",
            title = "Cuci Mobil",
            status = "Tunda Dulu",
            statusColor = Color(0xFFFF7043),
            detailExplanation = "Kemungkinan hujan tinggi (${rainProb ?: 0}%). Kendaraan berisiko kotor kembali terkena cipratan air hujan."
        )
        (rainProb ?: 0) <= 15 -> ActivityIndexItem(
            icon = "🚗",
            title = "Cuci Mobil",
            status = "Sangat Dianjurkan",
            statusColor = Color(0xFF4CAF50),
            detailExplanation = "Prakiraan cuaca minim hujan. Hasil cuci mobil akan bertahan bersih lebih lama."
        )
        else -> ActivityIndexItem(
            icon = "🚗",
            title = "Cuci Mobil",
            status = "Sedang",
            statusColor = Color(0xFFFFB300),
            detailExplanation = "Cuaca relatif stabil namun terdapat kemungkinan awan hujan lokal."
        )
    }

    // 4. Jemur Pakaian
    val laundry = when {
        (rainProb ?: 0) >= 45 || humidity >= 88 -> ActivityIndexItem(
            icon = "👕",
            title = "Jemur Pakaian",
            status = "Rentan Basah",
            statusColor = Color(0xFFFF5252),
            detailExplanation = "Kelembaban udara sangat tinggi ($humidity%) atau berisiko hujan. Jemur di tempat beratap."
        )
        cloudCover <= 30 && temperature >= 27.0 -> ActivityIndexItem(
            icon = "👕",
            title = "Jemur Pakaian",
            status = "Cepat Kering",
            statusColor = Color(0xFF4CAF50),
            detailExplanation = "Sinar matahari melimpah dan suhu hangat. Pakaian akan cepat kering sempurna."
        )
        else -> ActivityIndexItem(
            icon = "👕",
            title = "Jemur Pakaian",
            status = "Normal",
            statusColor = Color(0xFF29B6F6),
            detailExplanation = "Pakaian kering dalam durasi waktu normal sehari-hari."
        )
    }

    // 5. Sunscreen & Kacamata
    val uv = uvIndex ?: 0.0
    val sunProtection = when {
        uv >= 8.0 -> ActivityIndexItem(
            icon = "🕶️",
            title = "Tabir Surya",
            status = "Wajib Pakai",
            statusColor = Color(0xFFFF4081),
            detailExplanation = "Indeks UV sangat tinggi ($uv). Wajib memakai tabir surya SPF 30+, kacamata hitam, dan topi lebar."
        )
        uv >= 3.0 -> ActivityIndexItem(
            icon = "🕶️",
            title = "Tabir Surya",
            status = "Disarankan",
            statusColor = Color(0xFFFFB300),
            detailExplanation = "Indeks UV moderat ($uv). Disarankan perlindungan kulit saat berada di bawah terik matahari siang."
        )
        else -> ActivityIndexItem(
            icon = "🕶️",
            title = "Tabir Surya",
            status = "Aman",
            statusColor = Color(0xFF4CAF50),
            detailExplanation = "Radiasi ultraviolet rendah ($uv). Paparan sinar matahari aman untuk kulit."
        )
    }

    // 6. Aktivitas Nyamuk
    val mosquito = when {
        humidity >= 75 && temperature in 25.0..32.0 -> ActivityIndexItem(
            icon = "🦟",
            title = "Nyamuk",
            status = "Aktivitas Tinggi",
            statusColor = Color(0xFFFF7043),
            detailExplanation = "Kombinasi kelembaban tinggi ($humidity%) dan suhu hangat ideal bagi perkembangbiakan nyamuk. Gunakan lotion anti nyamuk."
        )
        temperature <= 18.0 || humidity <= 45 -> ActivityIndexItem(
            icon = "🦟",
            title = "Nyamuk",
            status = "Rendah",
            statusColor = Color(0xFF4CAF50),
            detailExplanation = "Suhu sejuk atau udara kering menekan keaktifan serangga pengganggu."
        )
        else -> ActivityIndexItem(
            icon = "🦟",
            title = "Nyamuk",
            status = "Sedang",
            statusColor = Color(0xFF29B6F6),
            detailExplanation = "Populasi dan aktivitas serangga dalam batas rata-rata wajar."
        )
    }

    val items = listOf(jogging, cycling, carWash, laundry, sunProtection, mosquito)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x3B101C30)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🎯", fontSize = 18.sp)
                Text(
                    text = "Indeks Gaya Hidup & Aktivitas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(Modifier.height(14.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = 3
            ) {
                items.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .clickable { selectedItem = item }
                            .padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = item.icon, fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(item.statusColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.status,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = item.statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog rincian jika item aktivitas diklik
    selectedItem?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            icon = { Text(text = item.icon, fontSize = 32.sp) },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(item.statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = item.status,
                            color = item.statusColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            },
            text = {
                Text(
                    text = item.detailExplanation,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { selectedItem = null }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}
