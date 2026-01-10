package com.alexisgau.gamereviewchallenge.data.remote.source

import com.alexisgau.gamereviewchallenge.data.remote.dto.GameDto
import com.alexisgau.gamereviewchallenge.data.remote.dto.GameRequestDto
import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface GameRemoteDataSource {
    suspend fun fetchGameBatch(request: GameRequestDto, mode: GameMode): List<GameDto>
}

class KtorGameRemoteDataSource(
    private val client: HttpClient
) : GameRemoteDataSource {

    override suspend fun fetchGameBatch(request: GameRequestDto, mode: GameMode): List<GameDto> {
        val endpoint = when (mode) {
            GameMode.BAD_REVIEWS -> "bad-reviews-batch"
            GameMode.GOOD_REVIEWS -> "good-reviews-batch"
            else -> "next-batch"
        }

        return client.post("api/games/$endpoint") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}