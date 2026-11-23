package com.example.cuacaku.ui

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cuacaku.R
import java.util.Locale

// =====================================================
// HomeScreen — halaman dashboard utama (satu LazyColumn)
// =====================================================
@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    onToggleTheme: () -> Unit,
    onChangeLanguage: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val cityName by viewModel.cityName

    // Teks pencarian & status dialog — saveable agar aman saat rotasi
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    // Ambil bahasa aplikasi saat ini untuk parameter geocoding
    val currentLang = Locale.getDefault().language // "in" atau "en"

    val keyboardController = LocalSoftwareKeyboardController.current

    // Tampilkan dialog bahasa jika terbuka
    if (showLanguageDialog) {
        LanguageDialog(
            onDismiss = { showLanguageDialog = false },
            onSelect = { lang ->
                showLanguageDialog = false
                onChangeLanguage(lang)
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            // ─────────────────────────────────────
            // 1. Header: judul + tombol bahasa & tema
            // ─────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    // Tombol ganti bahasa
                    IconButton(onClick = { showLanguageDialog = true }) {
                        Text(text = "🌐", fontSize = 24.sp)
                    }
                    // Tombol ganti tema terang/gelap
                    IconButton(onClick = onToggleTheme) {
                        Text(text = "🌓", fontSize = 24.sp)
                    }
                }
            }

            // ─────────────────────────────────────
            // 2. Kolom pencarian kota
            // ─────────────────────────────────────
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

            // ─────────────────────────────────────
            // 3. Hasil pencarian kota
            // ─────────────────────────────────────
            items(searchResults, key = { "${it.latitude},${it.longitude}" }) { result ->
                CityResultItem(
                    result = result,
                    onClick = {
                        viewModel.load(result.latitude, result.longitude, result.name)
                        searchQuery = "" // Kosongkan kolom pencarian
                    }
                )
            }

            // ─────────────────────────────────────
            // 4. Konten sesuai state
            // ─────────────────────────────────────
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
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
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.layout.Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.error_network),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(Modifier.height(12.dp))
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

                    // 5. Kartu cuaca utama
                    item {
                        CurrentWeatherCard(
                            cityName = cityName,
                            temperature = data.current.temperature,
                            weatherCode = data.current.weatherCode
                        )
                    }

                    // 6. Statistik (kelembapan, angin, terasa)
                    item {
                        StatsRow(
                            humidity = data.current.humidity,
                            windSpeed = data.current.windSpeed,
                            feelsLike = data.current.feelsLike
                        )
                    }

                    // 7. Judul prakiraan per jam
                    item {
                        Text(
                            text = stringResource(R.string.hourly_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // 8. Scroll horizontal per jam
                    item {
                        HourlyRow(hourly = data.hourly)
                    }

                    // 9. Judul prakiraan 7 hari
                    item {
                        Text(
                            text = stringResource(R.string.daily_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // 10. Daftar prakiraan harian
                    itemsIndexed(data.daily.time) { index, dateStr ->
                        DailyCard(
                            dateStr = dateStr,
                            weatherCode = data.daily.weatherCode[index],
                            maxTemp = data.daily.maxTemp[index],
                            minTemp = data.daily.minTemp[index],
                            rain = data.daily.rain[index]
                        )
                    }
                }
            }
        }
    }
}
