package com.alexisgau.gamereviewchallenge.ui.startGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisgau.gamereviewchallenge.domain.model.Game
import com.alexisgau.gamereviewchallenge.domain.model.Review
import com.alexisgau.gamereviewchallenge.ui.startGame.GameEvent.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// Estado único de la pantalla (Best Practice: Single Source of Truth)
data class GameUiState(
    val score: Int = 0,
    val correctAnswersCount: Int = 0,
    val gameMode: GameMode = GameMode.HARDCORE,
    // --- NUEVO: Vidas actuales ---
    val currentLives: Int = 0,
    // Para saber si mostramos corazones (en Free es infinito)
    val maxLives: Int = 0,
    val currentGame: Game? = null,
    val options: List<Game> = emptyList(),
    val selectedGameId: Long? = null,
    val isAnswerCorrect: Boolean? = null,
    val isInputLocked: Boolean = false
)
@Serializable
enum class GameMode {
    HARDCORE, // Suma puntos, si erras pierdes
    FREE,      // Infinito, no pierdes nunca
    GENRE,    // Juego de un género específico
    SURVIVAL,
    BAD_REVIEWS,
    GOOD_REVIEWS,
}

sealed interface GameEvent {
    data class GameOver(val finalScore: Int, val correctAnswersCount: Int) : GameEvent
}

class StartGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()

    private val _gameEvents = Channel<GameEvent>()
    val gameEvents = _gameEvents.receiveAsFlow()
    private var cachedNextGame: Game? = null

    // Datos Mockeados (Idealmente esto vendría de un Repository)

    val allReviews = listOf(Review("ESTE JUEGO ME PARECIO UNA BORONGUITA", false), Review("TA BUENO", true), Review("Me gusta", true))
    private val allGames = listOf(
        // ... (Tu lista completa de juegos aquí) ...
        Game(1L, "Hades", "Roguelike", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&s", allReviews),
        Game(2L, "Stardew Valley", "Simulation", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&s", allReviews),
        Game(3L, "Terraria", "Sandbox", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&s", allReviews),
        Game(4L, "Hollow Knight", "Metroidvania", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&sg", allReviews),
        Game(5L, "Elden Ring", "RPG", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&s", allReviews),
        Game(6L, "Cyberpunk 2077", "RPG", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&s", allReviews),
        Game(7L, "The Witcher 3", "RPG", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&s", allReviews),
        Game(8L, "Baldur's Gate 3", "RPG", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&s", allReviews),
        Game(9L, "Portal 2", "Puzzle", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&s", allReviews),
        Game(10L, "Minecraft", "Survival", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaOBiMrkNUGrpFUw5SSrEV7Un8Ur0hurSmBg&s", allReviews)
    )

    init {
        startGame()
    }

    fun setGameMode(mode: GameMode) {
        // Configuramos vidas iniciales
        val initialLives = when (mode) {
            GameMode.SURVIVAL -> 3
            GameMode.HARDCORE -> 1
            GameMode.FREE -> -1 // -1 significa Infinito
            // Hardcore y los demás modos de "filtro" suelen ser muerte súbita (1 vida)
            // Si quieres que GENRE tenga 3 vidas, ponlo arriba con SURVIVAL
            else -> 1
        }

        _uiState.update {
            it.copy(
                gameMode = mode,
                score = 0,
                correctAnswersCount = 0,
                currentLives = initialLives,
                maxLives = initialLives
            )
        }

        // AQUÍ ES DONDE ESCALARÁS LOS DATOS (GENRE, BAD_REVIEWS):
        // reloadGamesData(mode) <--- Función futura para filtrar la lista 'allGames'
    }

    private fun startGame() {
        viewModelScope.launch {
            val firstGame = allGames.random()
            loadLevel(firstGame)
            prefetchNextGame()
        }
    }

    private fun loadLevel(game: Game) {
        // Generamos distractores
        val distractors = allGames.filter { it.id != game.id }
            .shuffled()
            .take(2)

        val options = (distractors + game).shuffled()

        // Actualizamos estado y desbloqueamos input
        _uiState.update {
            it.copy(
                currentGame = game,
                options = options,
                selectedGameId = null,
                isAnswerCorrect = null,
                isInputLocked = false
            )
        }
    }

    fun onOptionSelected(selectedId: Long) {
        val currentState = _uiState.value
        if (currentState.isInputLocked) return

        val isCorrect = selectedId == currentState.currentGame?.id

        _uiState.update {
            it.copy(selectedGameId = selectedId, isAnswerCorrect = isCorrect, isInputLocked = true)
        }

        viewModelScope.launch {
            delay(1500)

            if (isCorrect) {
                // --- ACIERTO ---
                // Sumamos puntos en todos los modos menos FREE (opcional)
                val points = if (currentState.gameMode == GameMode.FREE) 0 else 100

                _uiState.update {
                    it.copy(
                        score = it.score + points,
                        correctAnswersCount = it.correctAnswersCount + 1
                    )
                }
                goToNextLevel()

            } else {
                // --- ERROR ---

                // Caso FREE: No importa nada, seguimos
                if (currentState.gameMode == GameMode.FREE) {
                    goToNextLevel()
                    return@launch
                }

                // Caso SURVIVAL / HARDCORE / OTROS: Restamos vida
                val newLives = currentState.currentLives - 1

                _uiState.update { it.copy(currentLives = newLives) }

                if (newLives <= 0) {
                    // GAME OVER
                    _gameEvents.send(
                        GameEvent.GameOver(
                            finalScore = currentState.score,
                            correctAnswersCount = currentState.correctAnswersCount
                        )
                    )
                } else {
                    // Aún le quedan vidas (Survival), seguimos jugando
                    goToNextLevel()
                }
            }
        }
    }

    private fun goToNextLevel() {
        val nextGame = cachedNextGame ?: allGames.random()
        loadLevel(nextGame)

        // Lanzamos prefetch del subsiguiente
        viewModelScope.launch { prefetchNextGame() }
    }

    private suspend fun prefetchNextGame() {
        cachedNextGame = allGames.random()
    }

//    fun checkHighScore(currentScore: Int) {
//        viewModelScope.launch {
//            val oldRecord = repository.getHighScore() // Viene del Back/DataStore
//            if (currentScore > oldRecord) {
//                repository.saveNewHighScore(currentScore) // Guardar en Back
//                // Avisar a la UI que muestre el badge
//            }
//        }
//    }
}