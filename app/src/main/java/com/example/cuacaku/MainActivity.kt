package com.example.cuacaku

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuacaku.ui.HomeScreen
import com.example.cuacaku.ui.WeatherViewModel
import com.example.cuacaku.ui.theme.CuacaKuTheme

// =====================================================
// MainActivity — activity utama, turunan AppCompatActivity
// =====================================================
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CuacaKuTheme {
                val viewModel: WeatherViewModel = viewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onToggleTheme = { toggleTheme() },
                    onChangeLanguage = { lang -> changeLanguage(lang) }
                )
            }
        }
    }

    /**
     * Ganti tema terang <-> gelap.
     * Membaca mode saat ini dari konfigurasi, lalu set ke kebalikannya.
     */
    private fun toggleTheme() {
        val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val newMode = if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        AppCompatDelegate.setDefaultNightMode(newMode)
    }

    /**
     * Ganti bahasa aplikasi.
     * Menerima tag bahasa ("id" atau "en") lalu terapkan lewat AppCompat.
     */
    private fun changeLanguage(langTag: String) {
        val locales = LocaleListCompat.forLanguageTags(langTag)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
