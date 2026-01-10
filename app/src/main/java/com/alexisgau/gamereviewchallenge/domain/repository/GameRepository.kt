package com.alexisgau.gamereviewchallenge.domain.repository

import com.alexisgau.gamereviewchallenge.domain.model.Game
import com.alexisgau.gamereviewchallenge.domain.model.GameMode

/**
 * Obtiene un lote de juegos desde el servidor.
 *
 * @param amount Cantidad de juegos a pedir.
 * @param mode El modo de juego actual (afecta el endpoint o filtro en backend).
 * @param excludedIds Lista de IDs que el usuario ya ha jugado para no repetirlos.
 * @return Result con la lista de [Game] o error.
 */
interface GameRepository {
    suspend fun getNextBatch(
        amount: Int,
        mode: GameMode,
        excludedIds: List<Long>
    ): Result<List<Game>>
}