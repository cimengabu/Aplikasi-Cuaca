package com.example.cuacaku.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.cuacaku.R
import com.example.cuacaku.ui.FavoritesViewModel
import com.example.cuacaku.ui.SettingsViewModel
import com.example.cuacaku.ui.WeatherViewModel
import com.example.cuacaku.ui.favorites.FavoritesScreen
import com.example.cuacaku.ui.home.HomeScreen
import com.example.cuacaku.ui.settings.SettingsScreen

enum class AppScreen(val titleRes: Int, val icon: ImageVector) {
    HOME(R.string.nav_home, Icons.Default.Home),
    FAVORITES(R.string.nav_favorites, Icons.Default.Star),
    SETTINGS(R.string.nav_settings, Icons.Default.Settings)
}

// =====================================================
// AppNavigation — Struktur navigasi utama dengan 3 Tab Wajib
// =====================================================
@Composable
fun AppNavigation(
    weatherViewModel: WeatherViewModel,
    favoritesViewModel: FavoritesViewModel,
    settingsViewModel: SettingsViewModel,
    onApplyTheme: (String) -> Unit,
    onApplyLanguage: (String) -> Unit
) {
    // Tab terpilih disimpan dengan rememberSaveable agar tetap tersimpan saat rotasi layar
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                AppScreen.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = stringResource(screen.titleRes)
                            )
                        },
                        label = { Text(stringResource(screen.titleRes)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Animasi transisi halus crossfade saat berpindah halaman
            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                when (screen) {
                    AppScreen.HOME -> {
                        HomeScreen(viewModel = weatherViewModel)
                    }
                    AppScreen.FAVORITES -> {
                        FavoritesScreen(
                            viewModel = favoritesViewModel,
                            onSelectCity = { lat, lon, name ->
                                weatherViewModel.load(lat, lon, name)
                                currentScreen = AppScreen.HOME
                            }
                        )
                    }
                    AppScreen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onApplyTheme = onApplyTheme,
                            onApplyLanguage = onApplyLanguage
                        )
                    }
                }
            }
        }
    }
}
