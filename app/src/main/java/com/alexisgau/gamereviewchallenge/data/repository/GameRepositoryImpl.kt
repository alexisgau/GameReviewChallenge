package com.alexisgau.gamereviewchallenge.data.repository

import com.alexisgau.gamereviewchallenge.data.mapper.toDomain
import com.alexisgau.gamereviewchallenge.data.remote.dto.GameRequestDto
import com.alexisgau.gamereviewchallenge.data.remote.source.GameRemoteDataSource
import com.alexisgau.gamereviewchallenge.domain.model.Game
import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import com.alexisgau.gamereviewchallenge.domain.repository.GameRepository

class GameRepositoryImpl(
    private val remoteDataSource: GameRemoteDataSource,
) : GameRepository {

    override suspend fun getNextBatch(
        amount: Int,
        mode: GameMode,
        excludedIds: List<Long>,
    ): Result<List<Game>> {

        return try {
            //  Preparamos el DTO de petición
            val requestDto = GameRequestDto(
                limit = amount,
                excludedIds = excludedIds
            )

            // Pedimos los datos al DataSource (Delegamos la red)
            val gameDtos = remoteDataSource.fetchGameBatch(requestDto, mode)

            // Mapeamos a Dominio (Responsabilidad del Repo)
            val domainGames = gameDtos.map { it.toDomain() }

            Result.success(domainGames)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }


    }
}


