package com.example.cuacaku.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuacaku.data.local.SettingsRepository
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.WindUnit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// =====================================================
// ViewModel untuk halaman Pengaturan
// =====================================================
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)

    val tempUnit: StateFlow<TempUnit> = settingsRepo.tempUnitFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TempUnit.CELSIUS
    )

    val windUnit: StateFlow<WindUnit> = settingsRepo.windUnitFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        WindUnit.KMH
    )

    val themeMode: StateFlow<String> = settingsRepo.themeModeFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "SYSTEM"
    )

    val language: StateFlow<String> = settingsRepo.languageFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "in"
    )

    fun setTempUnit(unit: TempUnit) {
        viewModelScope.launch {
            settingsRepo.setTempUnit(unit)
        }
    }

    fun setWindUnit(unit: WindUnit) {
        viewModelScope.launch {
            settingsRepo.setWindUnit(unit)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepo.setThemeMode(mode)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsRepo.setLanguage(lang)
        }
    }
}
