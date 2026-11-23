package com.example.cuacaku.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuacaku.data.WeatherRepository
import com.example.cuacaku.data.local.FavoriteCity
import com.example.cuacaku.data.local.FavoritesRepository
import com.example.cuacaku.data.local.SettingsRepository
import com.example.cuacaku.util.TempUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Data kota favorit beserta cuaca ringkasnya */
data class FavoriteWeatherItem(
    val city: FavoriteCity,
    val temperature: Double? = null,
    val weatherCode: Int? = null,
    val isLoading: Boolean = false
)

// =====================================================
// ViewModel untuk halaman Kota Favorit
// =====================================================
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesRepo = FavoritesRepository(application)
    private val weatherRepo = WeatherRepository()
    private val settingsRepo = SettingsRepository(application)

    private val _favoriteItems = MutableStateFlow<List<FavoriteWeatherItem>>(emptyList())
    val favoriteItems: StateFlow<List<FavoriteWeatherItem>> = _favoriteItems

    val tempUnit: StateFlow<TempUnit> = settingsRepo.tempUnitFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TempUnit.CELSIUS
    )

    init {
        // Amati perubahan kota favorit di DataStore
        viewModelScope.launch {
            favoritesRepo.favoritesFlow.collect { cities ->
                loadWeatherForCities(cities)
            }
        }
    }

    private fun loadWeatherForCities(cities: List<FavoriteCity>) {
        viewModelScope.launch {
            // Tampilkan list terlebih dahulu
            val currentItems = cities.map { city ->
                val existing = _favoriteItems.value.firstOrNull { it.city.lat == city.lat && it.city.lon == city.lon }
                existing ?: FavoriteWeatherItem(city = city, isLoading = true)
            }
            _favoriteItems.value = currentItems

            // Muat data cuaca untuk masing-masing kota secara asinkron
            val updated = currentItems.map { item ->
                try {
                    val res = weatherRepo.forecast(item.city.lat, item.city.lon)
                    item.copy(
                        temperature = res.current.temperature,
                        weatherCode = res.current.weatherCode,
                        isLoading = false
                    )
                } catch (_: Exception) {
                    item.copy(isLoading = false)
                }
            }
            _favoriteItems.value = updated
        }
    }

    fun removeFavorite(city: FavoriteCity) {
        viewModelScope.launch {
            favoritesRepo.removeFavorite(city)
        }
    }
}
