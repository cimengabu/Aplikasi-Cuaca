package com.example.cuacaku.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites_pref")

/** Model data kota favorit yang disimpan di lokal */
data class FavoriteCity(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String? = null,
    val admin1: String? = null
)

// =====================================================
// Repository untuk menyimpan daftar kota favorit (DataStore JSON)
// =====================================================
class FavoritesRepository(private val context: Context) {

    private val gson = Gson()

    companion object {
        val KEY_FAVORITES_JSON = stringPreferencesKey("favorites_json")
    }

    /** Flow daftar kota favorit */
    val favoritesFlow: Flow<List<FavoriteCity>> = context.favoritesDataStore.data.map { pref ->
        val json = pref[KEY_FAVORITES_JSON] ?: return@map emptyList()
        try {
            val type = object : TypeToken<List<FavoriteCity>>() {}.type
            gson.fromJson<List<FavoriteCity>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /** Menambah kota ke daftar favorit (mencegah duplikat berdasarkan lat/lon) */
    suspend fun addFavorite(city: FavoriteCity) {
        val currentList = favoritesFlow.first().toMutableList()
        if (currentList.none { it.lat == city.lat && it.lon == city.lon }) {
            currentList.add(0, city)
            saveList(currentList)
        }
    }

    /** Menghapus kota dari daftar favorit */
    suspend fun removeFavorite(city: FavoriteCity) {
        val currentList = favoritesFlow.first().toMutableList()
        currentList.removeAll { it.lat == city.lat && it.lon == city.lon }
        saveList(currentList)
    }

    /** Memeriksa apakah suatu koordinat ada di daftar favorit */
    suspend fun isFavorite(lat: Double, lon: Double): Boolean {
        val list = favoritesFlow.first()
        return list.any { it.lat == lat && it.lon == lon }
    }

    private suspend fun saveList(list: List<FavoriteCity>) {
        val json = gson.toJson(list)
        context.favoritesDataStore.edit { pref ->
            pref[KEY_FAVORITES_JSON] = json
        }
    }
}
