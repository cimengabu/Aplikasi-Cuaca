package com.example.cuacaku.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuacaku.data.WeatherRepository
import com.example.cuacaku.data.local.FavoriteCity
import com.example.cuacaku.data.local.FavoritesRepository
import com.example.cuacaku.data.local.SettingsRepository
import com.example.cuacaku.data.local.WeatherCache
import com.example.cuacaku.data.remote.AirQualityResponse
import com.example.cuacaku.data.remote.GeoResult
import com.example.cuacaku.data.remote.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// =====================================================
// Sealed interface untuk state UI Cuaca di Beranda
// =====================================================
sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Error(val message: String) : WeatherUiState
    data class Success(
        val data: WeatherResponse,
        val aqi: AirQualityResponse? = null,
        val isOffline: Boolean = false,
        val cacheTime: Long? = null,
        val lastUpdatedTime: String = ""
    ) : WeatherUiState
}

// =====================================================
// WeatherViewModel — mengelola data cuaca, cache, & status favorit kota aktif
// =====================================================
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository()
    private val settingsRepo = SettingsRepository(application)
    private val favoritesRepo = FavoritesRepository(application)
    private val weatherCache = WeatherCache(application)

    // State data cuaca
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    // Status pull-to-refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Hasil pencarian kota
    private val _searchResults = MutableStateFlow<List<GeoResult>>(emptyList())
    val searchResults: StateFlow<List<GeoResult>> = _searchResults

    // Status apakah kota aktif saat ini masuk favorit
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    // Nama & koordinat kota aktif
    var cityName = mutableStateOf("Jakarta")
        private set
    var currentLat = -6.2146
        private set
    var currentLon = 106.8451
        private set

    // Satuan yang sedang aktif
    val tempUnit = settingsRepo.tempUnitFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.example.cuacaku.util.TempUnit.CELSIUS
    )
    val windUnit = settingsRepo.windUnitFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.example.cuacaku.util.WindUnit.KMH
    )

    init {
        // Ambil kota terakhir yang disimpan di lokal saat aplikasi dibuka
        viewModelScope.launch {
            val (savedName, savedLat, savedLon) = settingsRepo.lastCityFlow.first()
            currentLat = savedLat
            currentLon = savedLon
            cityName.value = savedName
            checkFavoriteStatus()
            refresh(silent = false)
        }
    }

    /**
     * Memuat ulang data cuaca dan kualitas udara.
     * Menggunakan cache offline bila koneksi internet bermasalah.
     */
    fun refresh(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                if (_uiState.value !is WeatherUiState.Success) {
                    _uiState.value = WeatherUiState.Loading
                }
            }
            _isRefreshing.value = true

            try {
                val forecastData = repository.forecast(currentLat, currentLon)
                val aqiData = repository.airQuality(currentLat, currentLon)

                // Simpan respon ke cache offline
                weatherCache.saveCache(cityName.value, currentLat, currentLon, forecastData)

                val currentTimeStr = try {
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                } catch (_: Exception) { "" }

                _uiState.value = WeatherUiState.Success(
                    data = forecastData,
                    aqi = aqiData,
                    isOffline = false,
                    lastUpdatedTime = currentTimeStr
                )
            } catch (e: Exception) {
                // Cek apakah ada data cache offline yang bisa ditampilkan
                val cached = weatherCache.getCache()
                if (cached != null) {
                    _uiState.value = WeatherUiState.Success(
                        data = cached.first,
                        aqi = null,
                        isOffline = true,
                        cacheTime = cached.second
                    )
                } else {
                    _uiState.value = WeatherUiState.Error(e.message ?: "Unknown error")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /** Ganti kota aktif */
    fun load(lat: Double, lon: Double, name: String) {
        currentLat = lat
        currentLon = lon
        cityName.value = name
        _searchResults.value = emptyList()

        viewModelScope.launch {
            settingsRepo.setLastCity(name, lat, lon)
            checkFavoriteStatus()
        }
        refresh(silent = false)
    }

    /** Toggle kota aktif ke / dari daftar favorit */
    fun toggleFavorite() {
        viewModelScope.launch {
            val city = FavoriteCity(
                name = cityName.value,
                lat = currentLat,
                lon = currentLon
            )
            if (_isFavorite.value) {
                favoritesRepo.removeFavorite(city)
                _isFavorite.value = false
            } else {
                favoritesRepo.addFavorite(city)
                _isFavorite.value = true
            }
        }
    }

    private suspend fun checkFavoriteStatus() {
        _isFavorite.value = favoritesRepo.isFavorite(currentLat, currentLon)
    }

    /** Cari kota */
    fun search(query: String, lang: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                _searchResults.value = repository.searchCity(query, lang)
            } catch (_: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }
}
