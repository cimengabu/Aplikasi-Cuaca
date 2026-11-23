package com.example.cuacaku.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cuacaku.util.TempUnit
import com.example.cuacaku.util.WindUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_pref")

// =====================================================
// Repository untuk pengaturan aplikasi berbasis DataStore
// =====================================================
class SettingsRepository(private val context: Context) {

    companion object {
        val KEY_TEMP_UNIT = stringPreferencesKey("temp_unit")
        val KEY_WIND_UNIT = stringPreferencesKey("wind_unit")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_LAST_CITY_NAME = stringPreferencesKey("last_city_name")
        val KEY_LAST_LAT = doublePreferencesKey("last_city_lat")
        val KEY_LAST_LON = doublePreferencesKey("last_city_lon")
        val KEY_HAS_SAVED_CITY = booleanPreferencesKey("has_saved_city")
    }

    /** Flow satuan suhu (default CELSIUS) */
    val tempUnitFlow: Flow<TempUnit> = context.settingsDataStore.data.map { pref ->
        TempUnit.fromString(pref[KEY_TEMP_UNIT] ?: TempUnit.CELSIUS.name)
    }

    /** Flow satuan kecepatan angin (default KMH) */
    val windUnitFlow: Flow<WindUnit> = context.settingsDataStore.data.map { pref ->
        WindUnit.fromString(pref[KEY_WIND_UNIT] ?: WindUnit.KMH.name)
    }

    /** Flow mode tema: "SYSTEM", "LIGHT", atau "DARK" (default SYSTEM) */
    val themeModeFlow: Flow<String> = context.settingsDataStore.data.map { pref ->
        pref[KEY_THEME_MODE] ?: "SYSTEM"
    }

    /** Flow bahasa aplikasi (default "in") */
    val languageFlow: Flow<String> = context.settingsDataStore.data.map { pref ->
        pref[KEY_LANGUAGE] ?: "in"
    }

    /** Flow kota terakhir yang dibuka */
    val lastCityFlow: Flow<Triple<String, Double, Double>> = context.settingsDataStore.data.map { pref ->
        val name = pref[KEY_LAST_CITY_NAME] ?: "Jakarta"
        val lat = pref[KEY_LAST_LAT] ?: -6.2146
        val lon = pref[KEY_LAST_LON] ?: 106.8451
        Triple(name, lat, lon)
    }

    /** Menandakan apakah pengguna sudah pernah memilih/menyimpan kota */
    val hasSavedCityFlow: Flow<Boolean> = context.settingsDataStore.data.map { pref ->
        pref[KEY_HAS_SAVED_CITY] ?: false
    }

    suspend fun setTempUnit(unit: TempUnit) {
        context.settingsDataStore.edit { pref ->
            pref[KEY_TEMP_UNIT] = unit.name
        }
    }

    suspend fun setWindUnit(unit: WindUnit) {
        context.settingsDataStore.edit { pref ->
            pref[KEY_WIND_UNIT] = unit.name
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.settingsDataStore.edit { pref ->
            pref[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setLanguage(lang: String) {
        context.settingsDataStore.edit { pref ->
            pref[KEY_LANGUAGE] = lang
        }
    }

    suspend fun setLastCity(name: String, lat: Double, lon: Double) {
        context.settingsDataStore.edit { pref ->
            pref[KEY_HAS_SAVED_CITY] = true
            pref[KEY_LAST_CITY_NAME] = name
            pref[KEY_LAST_LAT] = lat
            pref[KEY_LAST_LON] = lon
        }
    }
}
