package com.example.cuacaku.ui.radar

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cuacaku.R
import java.util.Locale

// =====================================================
// RadarScreen — Halaman Peta Radar Cuaca Interaktif (Jetpack Compose)
// =====================================================
@Composable
fun RadarScreen(
    radarViewModel: RadarViewModel,
    cityLat: Double,
    cityLon: Double,
    cityName: String,
    modifier: Modifier = Modifier
) {
    val uiState by radarViewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    // Kelola lifecycle playback saat berpindah tab
    DisposableEffect(Unit) {
        onDispose {
            radarViewModel.stopPlayback()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Halaman
        RadarHeader(
            cityName = cityName,
            cityLat = cityLat,
            cityLon = cityLon,
            onRefresh = { radarViewModel.loadRadar() }
        )

        // Konten utama berdasarkan status UI
        when (val state = uiState) {
            is RadarUiState.Loading -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = stringResource(R.string.radar_loading),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is RadarUiState.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.radar_error_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { radarViewModel.loadRadar() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }

            is RadarUiState.Success -> {
                RadarMapViewCard(
                    host = state.host,
                    frames = state.frames,
                    currentIndex = state.currentFrameIndex,
                    cityLat = cityLat,
                    cityLon = cityLon,
                    cityName = cityName,
                    isDark = isDark
                )

                // Panel Kontrol Pemutar Animasi Radar
                RadarControlsCard(
                    frames = state.frames,
                    currentIndex = state.currentFrameIndex,
                    isPlaying = state.isPlaying,
                    onTogglePlay = { radarViewModel.togglePlay() },
                    onSelectIndex = { radarViewModel.selectFrame(it) }
                )
            }
        }

        // Legenda Skala Warna Intensitas Hujan Radar
        RadarLegendCard()

        // Catatan Penjelasan Teknologi Radar untuk Tugas & Presentasi
        RadarInfoCard()
    }
}

// =====================================================
// Header Radar
// =====================================================
@Composable
private fun RadarHeader(
    cityName: String,
    cityLat: Double,
    cityLon: Double,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.radar_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$cityName (📍 ${String.format(Locale.US, "%.2f, %.2f", cityLat, cityLon)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            IconButton(
                onClick = onRefresh,
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.refresh)
                )
            }
        }
    }
}

// =====================================================
// Kartu Peta Radar (WebView + Leaflet + RainViewer Tiles)
// =====================================================
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RadarMapViewCard(
    host: String,
    frames: List<RadarFrameItem>,
    currentIndex: Int,
    cityLat: Double,
    cityLon: Double,
    cityName: String,
    isDark: Boolean
) {
    val currentFrame = frames.getOrNull(currentIndex)
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Evaluasi javascript saat index frame berubah (tanpa reload halaman)
    LaunchedEffect(currentFrame?.path) {
        currentFrame?.let { frame ->
            val radarUrl = "$host${frame.path}/256/{z}/{x}/{y}/2/1_1.png"
            webViewRef?.evaluateJavascript("updateRadar('$radarUrl');", null)
        }
    }

    // Update pusat peta jika koordinat kota berubah
    LaunchedEffect(cityLat, cityLon) {
        webViewRef?.evaluateJavascript("centerCity($cityLat, $cityLon, '$cityName');", null)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val initialPath = currentFrame?.path ?: ""
            val initialRadarUrl = if (initialPath.isNotEmpty()) "$host$initialPath/256/{z}/{x}/{y}/2/1_1.png" else ""

            val htmlContent = remember(cityLat, cityLon, isDark) {
                buildLeafletHtml(
                    lat = cityLat,
                    lon = cityLon,
                    cityName = cityName,
                    initialRadarUrl = initialRadarUrl,
                    isDark = isDark
                )
            }

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        webViewClient = object : WebViewClient() {}
                        loadDataWithBaseURL("https://unpkg.com", htmlContent, "text/html", "UTF-8", null)
                        webViewRef = this
                    }
                },
                update = { wv ->
                    webViewRef = wv
                },
                modifier = Modifier.fillMaxSize()
            )

            // Tombol Melayang untuk Pusatkan ke Lokasi
            FilledIconButton(
                onClick = {
                    webViewRef?.evaluateJavascript("centerCity($cityLat, $cityLon, '$cityName');", null)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(40.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = stringResource(R.string.radar_center_my_location),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Lencana Waktu Frame di Sudut Kiri Bawah Peta
            currentFrame?.let { frame ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (frame.isNowcast) Color(0xFFFF9800) else Color(0xFF4CAF50))
                        )
                        Text(
                            text = if (frame.isNowcast) {
                                "${stringResource(R.string.radar_nowcast)} (${frame.timeFormatted})"
                            } else {
                                "${stringResource(R.string.radar_live)} ${frame.timeFormatted}"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// =====================================================
// Panel Kontrol Pemutar Animasi Radar (Play/Pause + Slider)
// =====================================================
@Composable
private fun RadarControlsCard(
    frames: List<RadarFrameItem>,
    currentIndex: Int,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onSelectIndex: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.radar_timeline_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${currentIndex + 1} / ${frames.size} frame",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            // Slider linimasa waktu
            if (frames.size > 1) {
                Slider(
                    value = currentIndex.toFloat(),
                    onValueChange = { onSelectIndex(it.toInt().coerceIn(0, frames.lastIndex)) },
                    valueRange = 0f..frames.lastIndex.toFloat(),
                    steps = (frames.size - 2).coerceAtLeast(0),
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Tombol Play / Pause dan Info Frame Saat Ini
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilledIconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    val frame = frames.getOrNull(currentIndex)
                    Text(
                        text = frame?.timeFormatted ?: "--:--",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (frame?.isNowcast == true) stringResource(R.string.radar_tag_nowcast) else stringResource(R.string.radar_tag_recorded),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (frame?.isNowcast == true) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

// =====================================================
// Legenda Skala Warna Intensitas Hujan Radar
// =====================================================
@Composable
private fun RadarLegendCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.radar_legend_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem(color = Color(0xFF64B5F6), label = stringResource(R.string.radar_drizzle))
                LegendItem(color = Color(0xFF81C784), label = stringResource(R.string.radar_light_rain))
                LegendItem(color = Color(0xFFFFD54F), label = stringResource(R.string.radar_moderate_rain))
                LegendItem(color = Color(0xFFE57373), label = stringResource(R.string.radar_heavy_rain))
                LegendItem(color = Color(0xFFBA68C8), label = stringResource(R.string.radar_storm))
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =====================================================
// Kartu Info Teknis Radar (Untuk Nilai Presentasi)
// =====================================================
@Composable
private fun RadarInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "💡 " + stringResource(R.string.radar_info_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = stringResource(R.string.radar_info_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                lineHeight = 18.sp
            )
        }
    }
}

// =====================================================
// Helper Pembuat HTML Leaflet Map dengan RainViewer Overlay
// =====================================================
private fun buildLeafletHtml(
    lat: Double,
    lon: Double,
    cityName: String,
    initialRadarUrl: String,
    isDark: Boolean
): String {
    val escapedCityName = cityName.replace("'", "\\'")
    val baseTileUrl = if (isDark) {
        "https://cartodb-basemaps-a.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png"
    } else {
        "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
    }

    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        html, body, #map {
            width: 100%;
            height: 100%;
            margin: 0;
            padding: 0;
            background: ${if (isDark) "#1c1b1f" else "#fef7ff"};
        }
        .leaflet-control-attribution {
            font-size: 8px !important;
            opacity: 0.6;
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var map = L.map('map', {
            zoomControl: false,
            attributionControl: true
        }).setView([$lat, $lon], 8);

        L.tileLayer('$baseTileUrl', {
            maxZoom: 18,
            attribution: '&copy; OpenStreetMap'
        }).addTo(map);

        var radarLayer = null;
        function updateRadar(url) {
            if (!url) return;
            if (radarLayer) {
                map.removeLayer(radarLayer);
            }
            radarLayer = L.tileLayer(url, {
                opacity: 0.72,
                zIndex: 100
            }).addTo(map);
        }

        var cityMarker = L.circleMarker([$lat, $lon], {
            radius: 8,
            fillColor: "#2196F3",
            color: "#ffffff",
            weight: 2,
            opacity: 1,
            fillOpacity: 0.9
        }).addTo(map).bindPopup('$escapedCityName');

        function centerCity(newLat, newLon, newName) {
            map.setView([newLat, newLon], 8);
            if (cityMarker) {
                cityMarker.setLatLng([newLat, newLon]).setPopupContent(newName);
            }
        }

        ${if (initialRadarUrl.isNotEmpty()) "updateRadar('$initialRadarUrl');" else ""}
    </script>
</body>
</html>
    """.trimIndent()
}
