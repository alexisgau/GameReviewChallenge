package com.alexisgau.gamereviewchallenge.data.repository

import android.content.Context
import android.util.Log
import com.alexisgau.gamereviewchallenge.data.mapper.toDomain
import com.alexisgau.gamereviewchallenge.data.remote.dto.GameDto
import com.alexisgau.gamereviewchallenge.data.remote.source.GameRemoteDataSource
import com.alexisgau.gamereviewchallenge.domain.model.Game
import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import com.alexisgau.gamereviewchallenge.domain.repository.GameRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class GameRepositoryImpl(
    private val context: Context,
    private val remoteDataSource: GameRemoteDataSource,
    private val json: Json // Necesario para leer/escribir el archivo
) : GameRepository {

    private var gamesCache: List<Game>? = null
    private val CACHE_FILE_NAME = "games_offline_cache.json"

    override suspend fun getNextBatch(
        amount: Int,
        mode: GameMode,
        excludedIds: List<Long>,
    ): Result<List<Game>> {
        return try {
            //ESTRATEGIA: Memoria -> Red (y guardar) -> Disco
            val allGames = gamesCache
                ?: fetchFromNetworkAndSave()
                ?: loadFromDisk()


            if (allGames == null) {
                return Result.failure(Exception("No internet and no local cache found."))
            }

            // Guardamos en RAM para que la próxima vez sea instantáneo
            gamesCache = allGames

            // LÓGICA DE FILTRADO
            val filteredGames = when (mode) {
                GameMode.BAD_REVIEWS -> {
                    allGames.filter { game ->
                        game.id !in excludedIds && game.reviews.any { !it.isPositive }
                    }.map { game ->
                        game.copy(reviews = game.reviews.filter { !it.isPositive }.shuffled())
                    }
                }

                GameMode.GOOD_REVIEWS -> {
                    allGames.filter { game ->
                        game.id !in excludedIds && game.reviews.any { it.isPositive }
                    }.map { game ->
                        game.copy(reviews = game.reviews.filter { it.isPositive }.shuffled())
                    }
                }

                else -> {
                    allGames.filter { it.id !in excludedIds }
                }
            }

            val result = filteredGames.shuffled().take(amount)
            Result.success(result)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // --- FUNCIONES DE SOPORTE ---

    private suspend fun fetchFromNetworkAndSave(): List<Game>? {
        return try {
            Log.d("GameRepo", "Intentando descargar de GitHub...")
            val dtos = remoteDataSource.fetchAllGames()

            saveToDisk(dtos)

            dtos.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("GameRepo", "Falló la red: ${e.message}")
            null
        }
    }

    private fun saveToDisk(dtos: List<GameDto>) {
        try {
            val jsonString = json.encodeToString(dtos)
            context.openFileOutput(CACHE_FILE_NAME, Context.MODE_PRIVATE).use {
                it.write(jsonString.toByteArray())
            }
            Log.d("GameRepo", "Cache guardado en disco correctamente.")
        } catch (e: Exception) {
            Log.e("GameRepo", "Error guardando cache: ${e.message}")
        }
    }

    private fun loadFromDisk(): List<Game>? {
        Log.d("GameRepo", "Buscando en disco...")
        val file = File(context.filesDir, CACHE_FILE_NAME)
        if (!file.exists()) {
            Log.d("GameRepo", "No existe archivo de caché.")
            return null
        }

        return try {
            val jsonString = file.readText()
            val dtos = json.decodeFromString<List<GameDto>>(jsonString)
            Log.d("GameRepo", "Recuperados ${dtos.size} juegos del disco.")
            dtos.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("GameRepo", "El archivo de caché estaba corrupto.")
            null
        }
    }
}


