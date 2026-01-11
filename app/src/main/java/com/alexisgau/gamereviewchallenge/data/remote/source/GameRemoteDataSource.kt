package com.alexisgau.gamereviewchallenge.data.remote.source

import com.alexisgau.gamereviewchallenge.data.remote.dto.GameDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface GameRemoteDataSource {
    suspend fun fetchAllGames(): List<GameDto>
}

class KtorGameRemoteDataSource(
    private val client: HttpClient
) : GameRemoteDataSource {

    private val DATA_URL =
        "https://raw.githubusercontent.com/alexisgau/GameReviewChallenge/refs/heads/master/api/games.json"


    override suspend fun fetchAllGames(): List<GameDto> {
        return client.get(DATA_URL).body()
    }
}