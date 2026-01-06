package com.alexisgau.gamereviewchallenge.domain.model

data class Game(
    val id: Long,
   val title: String,
   val genre: String,
   val imageUrl: String = "",
    val  reviews : List<Review> = emptyList()
)
