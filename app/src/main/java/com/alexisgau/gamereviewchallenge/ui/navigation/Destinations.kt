package com.alexisgau.gamereviewchallenge.ui.navigation


import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import kotlinx.serialization.Serializable


// 1. Ruta del Menú Principal
@Serializable
object MenuRoute

// 2. Ruta del Juego (Recibe el modo: Survival o Free)
@Serializable
data class GameRoute(val mode: GameMode)

// 3. Ruta del Puntaje (Recibe los resultados)
@Serializable
data class ScoreRoute(
    val score: Int,
    val correctAnswers: Int,
    val isNewRecord: Boolean = false,
    val gameMode: GameMode
)