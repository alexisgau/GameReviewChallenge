package com.alexisgau.gamereviewchallenge.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameRequestDto(
    @SerialName("limit") val limit: Int = 20,
    @SerialName("excludedIds") val excludedIds: List<Long> = emptyList()
)
