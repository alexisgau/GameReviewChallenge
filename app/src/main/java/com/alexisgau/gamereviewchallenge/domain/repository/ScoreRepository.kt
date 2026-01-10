package com.alexisgau.gamereviewchallenge.domain.repository

import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import kotlinx.coroutines.flow.Flow

interface ScoreRepository {

    fun observeHighScore(mode: GameMode): Flow<Int>

    suspend fun saveScoreIfBest(mode: GameMode, score: Int)

}