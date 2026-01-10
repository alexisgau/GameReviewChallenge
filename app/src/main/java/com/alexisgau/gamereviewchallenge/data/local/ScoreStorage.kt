package com.alexisgau.gamereviewchallenge.data.local

import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "game_scores")

class ScoreStorage(private val context: Context) {

    // Obtener el récord de un modo específico
    fun getHighScore(mode: GameMode): Flow<Int> {
        val key = intPreferencesKey("highscore_${mode.name}")
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: 0 // Si no hay devuelve 0
        }
    }

    // Guardar un nuevo récord (solo si es mayor al anterior)
    suspend fun saveHighScore(mode: GameMode, newScore: Int) {
        val key = intPreferencesKey("highscore_${mode.name}")
        context.dataStore.edit { preferences ->
            val currentHigh = preferences[key] ?: 0
            if (newScore > currentHigh) {
                preferences[key] = newScore
            }
        }
    }
}