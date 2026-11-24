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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuacaku.R
import com.example.cuacaku.data.remote.AirQualityResponse
import com.example.cuacaku.util.AqiHelper

data class PollutantData(
    val name: String,
    val formula: String,
    val value: Double?,
    val unit: String,
    val maxStandard: Double,
    val description: String
)

// =====================================================
// PollutantsBottomSheet — Lembar Rincian Polutan Udara Lengkap
// Menampilkan PM2.5, PM10, CO, NO2, SO2, O3 dengan visual bar
// =====================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollutantsBottomSheet(
    airQuality: AirQualityResponse,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val current = airQuality.current ?: return
    val aqiVal = current.usAqi ?: 50
    val category = AqiHelper.getAqiCategory(aqiVal)

    val pollutants = listOf(
        PollutantData(
            name = "Partikulat Halus",
            formula = "PM2.5",
            value = current.pm25,
            unit = "µg/m³",
            maxStandard = 50.0,
            description = "Partikel mikro yang mampu menembus jauh ke dalam alveolus paru-paru dan peredaran darah."
        ),
        PollutantData(
            name = "Partikulat Kasar",
            formula = "PM10",
            value = current.pm10,
            unit = "µg/m³",
            maxStandard = 100.0,
            description = "Debu, jelaga, dan serbuk sari yang dapat menyebabkan iritasi saluran pernapasan atas."
        ),
        PollutantData(
            name = "Karbon Monoksida",
            formula = "CO",
            value = current.carbonMonoxide,
            unit = "µg/m³",
            maxStandard = 4000.0,
            description = "Gas tidak berwarna dan tidak berbau yang dihasilkan dari pembakaran kendaraan bermotor yang tidak sempurna."
        ),
        PollutantData(
            name = "Nitrogen Dioksida",
            formula = "NO₂",
            value = current.nitrogenDioxide,
            unit = "µg/m³",
            maxStandard = 200.0,
            description = "Gas emisi kendaraan dan industri yang memicu radang saluran pernapasan dan asma."
        ),
        PollutantData(
            name = "Sulfur Dioksida",
            formula = "SO₂",
            value = current.sulphurDioxide,
            unit = "µg/m³",
            maxStandard = 100.0,
            description = "Berasal dari pembakaran bahan bakar fosil; berkontribusi pada kabut asap dan hujan asam."
        ),
        PollutantData(
            name = "Ozon Permukaan",
            formula = "O₃",
            value = current.ozone,
            unit = "µg/m³",
            maxStandard = 120.0,
            description = "Polutan sekunder hasil reaksi sinar matahari dengan asap emisi, memicu sesak napas saat siang hari terik."
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Rincian Polutan Udara",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Indeks AQI: $aqiVal (${stringResource(category.labelRes)})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = category.color,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(category.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$aqiVal",
                        fontWeight = FontWeight.Bold,
                        color = category.color
                    )
                }
            }

            HorizontalDivider()

            // Daftar Polutan
            pollutants.forEach { p ->
                val pVal = p.value ?: 0.0
                val progress = (pVal / p.maxStandard).toFloat().coerceIn(0.05f, 1f)
                val isHigh = pVal > (p.maxStandard * 0.7)
                val statusColor = if (isHigh) Color(0xFFFF5252) else Color(0xFF4CAF50)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = p.formula,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = p.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = if (p.value != null) "${String.format(java.util.Locale.US, "%.1f", pVal)} ${p.unit}" else "N/A",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = statusColor
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = statusColor,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        strokeCap = StrokeCap.Round
                    )

                    Text(
                        text = p.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
