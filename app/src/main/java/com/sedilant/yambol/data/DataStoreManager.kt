package com.sedilant.yambol.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "currentTeam")

class DataStoreManager(private val context: Context) {
    private val CURRENT_TEAM_KEY = longPreferencesKey("current_team")

    suspend fun saveCurrentTeam(teamId: Long?) {
        context.dataStore.edit { preferences ->
            if (teamId != null) {
                preferences[CURRENT_TEAM_KEY] = teamId
            } else {
                preferences.remove(CURRENT_TEAM_KEY)
            }
        }
    }

    val currentTeam: Flow<Long?> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENT_TEAM_KEY]
        }
}
