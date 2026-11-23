package com.example.cuacaku.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuacaku.data.WeatherRepository
import com.example.cuacaku.data.remote.GeoResult
import com.example.cuacaku.data.remote.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// =====================================================
// Sealed interface untuk merepresentasikan state UI cuaca
// =====================================================
sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Error(val message: String) : WeatherUiState
    data class Success(val data: WeatherResponse) : WeatherUiState
}

// =====================================================
// ViewModel — mengelola data cuaca dan pencarian kota
// =====================================================
class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    // State cuaca (Loading / Error / Success)
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    // Hasil pencarian kota
    private val _searchResults = MutableStateFlow<List<GeoResult>>(emptyList())
    val searchResults: StateFlow<List<GeoResult>> = _searchResults

    // Nama kota yang sedang ditampilkan
    var cityName = mutableStateOf("Jakarta")
        private set

    // Koordinat kota aktif (default: Jakarta)
    private var currentLat = -6.2146
    private var currentLon = 106.8451

    init {
        // Muat data saat ViewModel pertama kali dibuat
        refresh()
    }

    /**
     * Memuat ulang data cuaca dari koordinat saat ini.
     * Mengubah state ke Loading, lalu Success atau Error.
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val response = repository.forecast(currentLat, currentLon)
                _uiState.value = WeatherUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Ganti kota aktif, kosongkan hasil pencarian, lalu refresh data.
     */
    fun load(lat: Double, lon: Double, name: String) {
        currentLat = lat
        currentLon = lon
        cityName.value = name
        _searchResults.value = emptyList()
        refresh()
    }

    /**
     * Cari kota berdasarkan kata kunci.
     * Abaikan jika query kosong. List kosong jika gagal.
     */
    fun search(query: String, lang: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            try {
                _searchResults.value = repository.searchCity(query, lang)
            } catch (_: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }
}
