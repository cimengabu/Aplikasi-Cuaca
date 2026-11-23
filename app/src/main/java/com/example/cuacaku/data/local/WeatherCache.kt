package com.example.cuacaku.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cuacaku.data.remote.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

private val Context.cacheDataStore: DataStore<Preferences> by preferencesDataStore(name = "weather_cache_pref")

// =====================================================
// Cache offline untuk respon cuaca terakhir
// =====================================================
class WeatherCache(private val context: Context) {

    private val gson = Gson()

    companion object {
        val KEY_CACHE_JSON = stringPreferencesKey("last_weather_json")
        val KEY_CACHE_TIME = longPreferencesKey("last_weather_time")
        val KEY_CACHE_CITY = stringPreferencesKey("last_weather_city")
        val KEY_CACHE_LAT = stringPreferencesKey("last_weather_lat")
        val KEY_CACHE_LON = stringPreferencesKey("last_weather_lon")
    }

    /** Menyimpan respon cuaca ke cache offline */
    suspend fun saveCache(cityName: String, lat: Double, lon: Double, response: WeatherResponse) {
        val json = gson.toJson(response)
        val currentTime = System.currentTimeMillis()
        context.cacheDataStore.edit { pref ->
            pref[KEY_CACHE_JSON] = json
            pref[KEY_CACHE_TIME] = currentTime
            pref[KEY_CACHE_CITY] = cityName
            pref[KEY_CACHE_LAT] = lat.toString()
            pref[KEY_CACHE_LON] = lon.toString()
        }
    }

    /**
     * Mengambil cache jika tersedia.
     * Mengembalikan Triple(WeatherResponse, timestamp, cityName) atau null.
     */
    suspend fun getCache(): Triple<WeatherResponse, Long, String>? {
        val pref = context.cacheDataStore.data.first()
        val json = pref[KEY_CACHE_JSON] ?: return null
        val time = pref[KEY_CACHE_TIME] ?: return null
        val city = pref[KEY_CACHE_CITY] ?: "Kota"
        return try {
            val response = gson.fromJson(json, WeatherResponse::class.java)
            Triple(response, time, city)
        } catch (_: Exception) {
            null
        }
    }
}
