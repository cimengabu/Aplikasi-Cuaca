package com.example.cuacaku.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cuacaku.R
import com.example.cuacaku.ui.WeatherUiState
import com.example.cuacaku.ui.WeatherViewModel
import com.example.cuacaku.ui.components.AirQualityCard
import com.example.cuacaku.ui.components.CityResultItem
import com.example.cuacaku.ui.components.CurrentWeatherCard
import com.example.cuacaku.ui.components.DailyCard
import com.example.cuacaku.ui.components.DayDetailBottomSheet
import com.example.cuacaku.ui.components.HourlyRow
import com.example.cuacaku.ui.components.HourlyTemperatureChart
import com.example.cuacaku.ui.components.SelectedDayData
import com.example.cuacaku.ui.components.StatsRow
import com.example.cuacaku.ui.components.WeatherDetailsCard
import com.example.cuacaku.ui.components.WeatherTipsCard
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// =====================================================
// HomeScreen — Dashboard utama aplikasi CuacaKu
// =====================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val tempUnit by viewModel.tempUnit.collectAsStateWithLifecycle()
    val windUnit by viewModel.windUnit.collectAsStateWithLifecycle()
    val cityName by viewModel.cityName

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Teks pencarian disimpan dengan rememberSaveable
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Bottom sheet detail hari
    var selectedDay by remember { mutableStateOf<SelectedDayData?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentLang = Locale.getDefault().language

    // GPS Location Request Launcher (Tahap 3)
    val myLocationLabel = stringResource(R.string.my_location)
    val permissionDeniedMsg = stringResource(R.string.location_permission_denied)

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchGpsLocation(context, viewModel, myLocationLabel)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(permissionDeniedMsg)
            }
        }
    }

    // Modal Bottom Sheet Detail Hari
    selectedDay?.let { dayData ->
        DayDetailBottomSheet(
            data = dayData,
            tempUnit = tempUnit,
            sheetState = sheetState,
            onDismiss = { selectedDay = null }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh(silent = true) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Header: Judul & Tombol Lokasi GPS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        // Tombol Lokasi GPS
                        IconButton(
                            onClick = {
                                val hasFine = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                val hasCoarse = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasFine || hasCoarse) {
                                    fetchGpsLocation(context, viewModel, myLocationLabel)
                                } else {
                                    locationLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = stringResource(R.string.use_my_location),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // 2. Kolom Pencarian Kota
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text(stringResource(R.string.search_city)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    viewModel.search(searchQuery, currentLang)
                                    keyboardController?.hide()
                                }
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.search(searchQuery, currentLang)
                                keyboardController?.hide()
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(stringResource(R.string.search))
                        }
                    }
                }

                // 3. Hasil Pencarian Kota
                items(searchResults, key = { "${it.latitude},${it.longitude}" }) { result ->
                    CityResultItem(
                        result = result,
                        onClick = {
                            viewModel.load(result.latitude, result.longitude, result.name)
                            searchQuery = ""
                        }
                    )
                }

                // 4. Konten Berdasarkan State
                when (val state = uiState) {
                    is WeatherUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    is WeatherUiState.Error -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(R.string.error_network),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(Modifier.height(14.dp))
                                    Button(
                                        onClick = { viewModel.refresh() },
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text(stringResource(R.string.retry))
                                    }
                                }
                            }
                        }
                    }

                    is WeatherUiState.Success -> {
                        val data = state.data
                        val aqi = state.aqi

                        // Banner Mode Offline jika data bersumber dari cache
                        if (state.isOffline && state.cacheTime != null) {
                            item {
                                val timeFormat = SimpleDateFormat("HH:mm dd MMM", Locale.getDefault())
                                val timeText = timeFormat.format(Date(state.cacheTime))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFFA000))
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.offline_banner, timeText),
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // 5. Kartu Cuaca Utama
                        item {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically()
                            ) {
                                CurrentWeatherCard(
                                    cityName = cityName,
                                    temperature = data.current.temperature,
                                    weatherCode = data.current.weatherCode,
                                    isDay = data.current.isDay == 1,
                                    tempUnit = tempUnit,
                                    isFavorite = isFavorite,
                                    onToggleFavorite = { viewModel.toggleFavorite() },
                                    lastUpdatedTime = state.lastUpdatedTime,
                                    isOffline = state.isOffline
                                )
                            }
                        }

                        // 6. Kartu Statistik Utama (Kelembapan, Angin, Terasa)
                        item {
                            StatsRow(
                                humidity = data.current.humidity,
                                windSpeed = data.current.windSpeed,
                                feelsLike = data.current.feelsLike,
                                tempUnit = tempUnit,
                                windUnit = windUnit
                            )
                        }

                        // 7. Kartu Saran Cuaca Hari Ini (Tips Cuaca Otomatis)
                        item {
                            WeatherTipsCard(
                                rainProbMax = data.daily.rain.firstOrNull(),
                                uvMax = data.daily.uvIndexMax?.firstOrNull(),
                                maxTemp = data.daily.maxTemp.firstOrNull() ?: 30.0,
                                minTemp = data.daily.minTemp.firstOrNull() ?: 24.0,
                                windSpeed = data.current.windSpeed,
                                aqi = aqi?.current?.usAqi,
                                weatherCode = data.current.weatherCode
                            )
                        }

                        // 8. Judul & Scroll Horizontal Per Jam
                        item {
                            Text(
                                text = stringResource(R.string.hourly_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        item {
                            HourlyRow(
                                hourly = data.hourly,
                                tempUnit = tempUnit
                            )
                        }

                        // 9. Grafik Tren Suhu Canvas (Tahap 2)
                        item {
                            HourlyTemperatureChart(
                                hourly = data.hourly,
                                tempUnit = tempUnit
                            )
                        }

                        // 10. Kartu Kualitas Udara (AQI) (Tahap 2)
                        item {
                            AirQualityCard(airQuality = aqi)
                        }

                        // 11. Grid Detail Cuaca Tambahan (Tekanan, Awan, Arah Angin, Jarak Pandang, UV, Matahari)
                        item {
                            WeatherDetailsCard(
                                pressureMsl = data.current.pressureMsl,
                                cloudCover = data.current.cloudCover,
                                windDirection = data.current.windDirection,
                                visibility = data.current.visibility,
                                uvIndexMax = data.daily.uvIndexMax?.firstOrNull(),
                                sunrise = data.daily.sunrise?.firstOrNull(),
                                sunset = data.daily.sunset?.firstOrNull()
                            )
                        }

                        // 12. Judul & Daftar Prakiraan 7 Hari
                        item {
                            Text(
                                text = stringResource(R.string.daily_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        itemsIndexed(data.daily.time) { index, dateStr ->
                            DailyCard(
                                dateStr = dateStr,
                                weatherCode = data.daily.weatherCode[index],
                                maxTemp = data.daily.maxTemp[index],
                                minTemp = data.daily.minTemp[index],
                                rain = data.daily.rain[index],
                                tempUnit = tempUnit,
                                onClick = {
                                    selectedDay = SelectedDayData(
                                        dateStr = dateStr,
                                        weatherCode = data.daily.weatherCode[index],
                                        maxTemp = data.daily.maxTemp[index],
                                        minTemp = data.daily.minTemp[index],
                                        rainProb = data.daily.rain[index],
                                        precipitationSum = data.daily.precipitationSum?.getOrNull(index),
                                        uvIndexMax = data.daily.uvIndexMax?.getOrNull(index),
                                        sunrise = data.daily.sunrise?.getOrNull(index),
                                        sunset = data.daily.sunset?.getOrNull(index)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchGpsLocation(
    context: Context,
    viewModel: WeatherViewModel,
    labelMyLocation: String
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
        if (loc != null) {
            viewModel.load(loc.latitude, loc.longitude, labelMyLocation)
        }
    }
}
