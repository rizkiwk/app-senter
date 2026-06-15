package com.smile.senter.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "senter_prefs")

/**
 * Penyimpanan lokal ringan via DataStore Preferences — sengaja BUKAN SQLite/Room.
 * Hanya beberapa setting key-value, tidak ada data relasional/riwayat.
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")      // tidak diset = ikut sistem
        val AUTO_OFF = booleanPreferencesKey("auto_off_on_exit")
    }

    /** null = ikut tema sistem; true/false = override manual. */
    val darkThemeOverride: Flow<Boolean?> =
        context.dataStore.data.map { it[Keys.DARK_THEME] }

    val autoOffOnExit: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.AUTO_OFF] ?: true }

    suspend fun setDarkTheme(value: Boolean?) {
        context.dataStore.edit { prefs ->
            if (value == null) prefs.remove(Keys.DARK_THEME) else prefs[Keys.DARK_THEME] = value
        }
    }

    suspend fun setAutoOff(value: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_OFF] = value }
    }
}
