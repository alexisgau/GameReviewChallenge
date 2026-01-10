package com.alexisgau.gamereviewchallenge.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    @SerialName("id") val id: Long,

    @SerialName("name") val name: String,

    @SerialName("genre") val genres: List<String>,

    @SerialName("imageUrl") val imageUrl: String,

    @SerialName("reviews") val reviews: List<ReviewDto>
)

@Serializable
data class ReviewDto(
    @SerialName("text") val text: String,
    @SerialName("positive") val positive: Boolean,
    @SerialName("hours") val hours: Double? = null
)