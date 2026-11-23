package com.example.cuacaku

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuacaku.ui.FavoritesViewModel
import com.example.cuacaku.ui.SettingsViewModel
import com.example.cuacaku.ui.WeatherViewModel
import com.example.cuacaku.ui.navigation.AppNavigation
import com.example.cuacaku.ui.theme.CuacaKuTheme

// =====================================================
// MainActivity — Activity utama aplikasi CuacaKu
// Turunan AppCompatActivity untuk mendukung DayNight & per-app language
// =====================================================
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()

            // Menyesuaikan tema terang / gelap berdasarkan pengaturan yang dipilih
            val isDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            CuacaKuTheme(darkTheme = isDarkTheme) {
                val weatherViewModel: WeatherViewModel = viewModel()
                val favoritesViewModel: FavoritesViewModel = viewModel()

                AppNavigation(
                    weatherViewModel = weatherViewModel,
                    favoritesViewModel = favoritesViewModel,
                    settingsViewModel = settingsViewModel,
                    onApplyTheme = { mode -> applyTheme(mode) },
                    onApplyLanguage = { langTag -> applyLanguage(langTag) }
                )
            }
        }
    }

    /** Menerapkan mode tema langsung lewat AppCompatDelegate */
    private fun applyTheme(mode: String) {
        val nightMode = when (mode) {
            "LIGHT" -> AppCompatDelegate.MODE_NIGHT_NO
            "DARK" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    /** Menerapkan bahasa aplikasi langsung lewat AppCompatDelegate */
    private fun applyLanguage(langTag: String) {
        val locales = LocaleListCompat.forLanguageTags(langTag)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
