package com.alexisgau.gamereviewchallenge.data.repository

import com.alexisgau.gamereviewchallenge.data.local.ScoreStorage
import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import com.alexisgau.gamereviewchallenge.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

class ScoreRepositoryImpl(
private val scoreStorage: ScoreStorage
) : ScoreRepository {

    override fun observeHighScore(mode: GameMode): Flow<Int> {
        return scoreStorage.getHighScore(mode)
    }

    override suspend fun saveScoreIfBest(mode: GameMode, score: Int) {
        scoreStorage.saveHighScore(mode, score)
    }
}