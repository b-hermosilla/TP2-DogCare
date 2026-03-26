package com.example.dogcare.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {

    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LAST_FILTER = stringPreferencesKey("last_filter")
    }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE] ?: false
        }

    val lastFilterFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_FILTER] ?: "ALL"
        }

    suspend fun saveDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = isDark
        }
    }

    suspend fun saveFilter(filter: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_FILTER] = filter
        }
    }
}
