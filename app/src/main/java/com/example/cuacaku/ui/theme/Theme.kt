package com.example.cuacaku.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// =====================================================
// Palet warna untuk mode terang
// =====================================================
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF42A5F5),
    onSecondary = Color.White,
    background = Color(0xFFF2F6FC),
    onBackground = Color(0xFF1A2533),
    surface = Color.White,
    onSurface = Color(0xFF1A2533),
    surfaceVariant = Color(0xFFE8EEF5),
    onSurfaceVariant = Color(0xFF44546A),
    outline = Color(0xFFB0BEC5)
)

// =====================================================
// Palet warna untuk mode gelap
// =====================================================
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFF64B5F6),
    onSecondary = Color(0xFF0D47A1),
    background = Color(0xFF0E1621),
    onBackground = Color(0xFFE1E6ED),
    surface = Color(0xFF1A2533),
    onSurface = Color(0xFFE1E6ED),
    surfaceVariant = Color(0xFF243040),
    onSurfaceVariant = Color(0xFFB0BEC5),
    outline = Color(0xFF546E7A)
)

// =====================================================
// Tema utama aplikasi CuacaKu
// =====================================================
@Composable
fun CuacaKuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Pilih skema warna berdasarkan mode terang/gelap
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
