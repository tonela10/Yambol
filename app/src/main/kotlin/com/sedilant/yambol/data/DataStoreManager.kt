package com.sedilant.yambol.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "currentTeam")

class DataStoreManager(private val context: Context) {
    private val CURRENT_TEAM_KEY_STRING = stringPreferencesKey("current_team_id")

    suspend fun saveCurrentTeam(teamId: String?) {
        context.dataStore.edit { preferences ->
            if (teamId != null) {
                preferences[CURRENT_TEAM_KEY_STRING] = teamId
            } else {
                preferences.remove(CURRENT_TEAM_KEY_STRING)
            }
        }
    }

    val currentTeam: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENT_TEAM_KEY_STRING]
        }
}
