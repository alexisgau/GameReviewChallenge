package com.alexisgau.gamereviewchallenge.ui.startGame

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexisgau.gamereviewchallenge.domain.model.Game
import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import com.alexisgau.gamereviewchallenge.domain.model.Review
import com.alexisgau.gamereviewchallenge.domain.repository.GameRepository
import com.alexisgau.gamereviewchallenge.domain.repository.ScoreRepository
import com.alexisgau.gamereviewchallenge.ui.navigation.GameRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class GameUiState(
    val isLoading: Boolean = true,
    val score: Int = 0,
    val correctAnswersCount: Int = 0,
    val gameMode: GameMode = GameMode.HARDCORE,
    val currentLives: Int = 0,
    val maxLives: Int = 0,
    val currentGame: Game? = null,
    val options: List<Game> = emptyList(),
    val selectedGameId: Long? = null,
    val isAnswerCorrect: Boolean? = null,
    val isInputLocked: Boolean = false,
    val activeHint: GameHint? = null,
    val hintCost: Int = 50,
    val errorMessage: String? = null
)

sealed interface GameHint {
    data class Genre(val text: String) : GameHint
    data class ExtraReview(val review: Review) : GameHint
}

sealed interface GameEvent {
    data class GameOver(val finalScore: Int, val isNewRecord: Boolean, val correctAnswersCount: Int) : GameEvent
}

class StartGameViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()

    private val _gameEvents = Channel<GameEvent>()
    val gameEvents = _gameEvents.receiveAsFlow()

    // Almacén de juegos cargados
    private val availableGames = mutableListOf<Game>()
    private val distractorPool = mutableListOf<Game>()
    private val playedGameIds = mutableSetOf<Long>()

    private var currentHighScoreForMode: Int = 0

    init {
        // Obtenemos el modo desde los argumentos de navegación
        val route = savedStateHandle.toRoute<GameRoute>()
        val mode = route.mode


        viewModelScope.launch {
            scoreRepository.observeHighScore(mode).collect { highScore ->
                currentHighScoreForMode = highScore
            }
        }


        initializeGame(mode)
    }

    private fun initializeGame(mode: GameMode) {
        val initialLives = getInitialLivesForMode(mode)

        _uiState.update {
            it.copy(
                gameMode = mode,
                currentLives = initialLives,
                maxLives = initialLives,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            loadGameBatch()
        }
    }

    private fun getInitialLivesForMode(mode: GameMode): Int {
        return when (mode) {
            GameMode.SURVIVAL, GameMode.BAD_REVIEWS, GameMode.GOOD_REVIEWS -> 3
            GameMode.HARDCORE -> 1
            GameMode.FREE -> -1 // Infinitas
            else -> 1
        }
    }

    private fun startGameRound() {
        if (availableGames.isEmpty()) {
            // Si nos quedamos sin juegos, pedimos más y mostramos carga
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch { loadGameBatch() }
            return
        }

        // Prefetch: Si quedan pocos juegos, cargamos el siguiente lote en segundo plano
        if (availableGames.size < 5) {
            viewModelScope.launch { loadGameBatch() }
        }

        // Seleccionamos el juego correcto
        val correctGame = availableGames.removeAt(0)

        // Generamos distractores válidos (que no sean el juego correcto)
        val validDistractors = distractorPool.filter { it.id != correctGame.id }

        if (validDistractors.size < 2) {
            // Caso borde: No hay suficientes distractores. Recargamos.
            viewModelScope.launch { loadGameBatch() }
            return
        }

        // Elegimos 2 distractores al azar
        val distractors = validDistractors.shuffled().take(2)
        val options = (distractors + correctGame).shuffled()

        _uiState.update {
            it.copy(
                currentGame = correctGame,
                options = options,
                selectedGameId = null,
                isAnswerCorrect = null,
                isInputLocked = false,
                activeHint = null,
                isLoading = false
            )
        }
    }

    fun onOptionSelected(selectedId: Long) {
        val currentState = _uiState.value
        if (currentState.isInputLocked) return

        val isCorrect = selectedId == currentState.currentGame?.id

        _uiState.update {
            it.copy(
                selectedGameId = selectedId,
                isAnswerCorrect = isCorrect,
                isInputLocked = true
            )
        }

        viewModelScope.launch {
            // Pequeña pausa para que el usuario vea si acertó o falló
            delay(1500)

            if (isCorrect) {
                handleCorrectAnswer()
            } else {
                handleWrongAnswer()
            }
        }
    }

    private fun handleCorrectAnswer() {
        val currentState = _uiState.value
        // En modo FREE no damos puntos
        val points = if (currentState.gameMode == GameMode.FREE) 0 else 125

        _uiState.update {
            it.copy(
                score = it.score + points,
                correctAnswersCount = it.correctAnswersCount + 1
            )
        }
        startGameRound()
    }

    private suspend fun handleWrongAnswer() {
        val currentState = _uiState.value

        if (currentState.gameMode == GameMode.FREE) {
            // En modo libre no se pierden vidas
            startGameRound()
            return
        }

        val newLives = currentState.currentLives - 1
        _uiState.update { it.copy(currentLives = newLives) }

        if (newLives <= 0) {
            val finalScore = currentState.score
            val isRecord = finalScore > currentHighScoreForMode

            if (isRecord) {
                scoreRepository.saveScoreIfBest(currentState.gameMode, finalScore)
            }


            // Game Over
            _gameEvents.send(
                GameEvent.GameOver(
                    finalScore = currentState.score,
                    isNewRecord = isRecord,
                    correctAnswersCount = currentState.correctAnswersCount
                )
            )
        } else {
            // Sigue jugando
            startGameRound()
        }
    }

    fun onRequestHint() {
        val currentState = _uiState.value

        // Solo permitido en Survival (puedes cambiar esta regla si quieres)
        if (currentState.gameMode != GameMode.SURVIVAL) return

        // Validaciones
        if (currentState.score < currentState.hintCost) return
        if (currentState.activeHint != null || currentState.isInputLocked) return

        val newScore = currentState.score - currentState.hintCost
        val game = currentState.currentGame ?: return

        // Lógica simple para elegir pista: 50% Género, 50% Reseña extra
        // (Asegurarse de que haya reseñas disponibles si elige esa opción)
        val showGenre = Math.random() < 0.5

        val hint: GameHint = if (showGenre && game.genres.isNotEmpty()) {
            GameHint.Genre(game.genres.joinToString(", "))
        } else if (game.reviews.isNotEmpty()) {
            val extraReview = game.reviews.random()
            GameHint.ExtraReview(extraReview)
        } else {
            // Fallback si no hay info (raro, pero posible)
            GameHint.Genre("Mystery Game")
        }

        _uiState.update {
            it.copy(score = newScore, activeHint = hint)
        }
    }

    fun onRetryLoad() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch { loadGameBatch() }
    }

    private suspend fun loadGameBatch() {
        val currentMode = _uiState.value.gameMode
        val excludedList = playedGameIds.toList()

        val result = gameRepository.getNextBatch(
            amount = 20,
            mode = currentMode,
            excludedIds = excludedList
        )

        result.onSuccess { newGames ->
            if (newGames.isEmpty()) {
                // Si el backend devuelve lista vacía (se acabaron los juegos o error silencioso)
                _uiState.update { it.copy(errorMessage = "No more games available.") }
                return@onSuccess
            }

            // Agregamos juegos a la cola
            availableGames.addAll(newGames)

            // Registramos IDs para no repetir
            playedGameIds.addAll(newGames.map { it.id })

            // Llenamos el pool de distractores
            distractorPool.addAll(newGames)

            // Si estábamos esperando juegos para empezar la ronda...
            if (_uiState.value.currentGame == null) {
                startGameRound()
            }
        }.onFailure { error ->
            // Manejo de error en UI
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Failed to load games. Check your connection."
                )
            }
            error.printStackTrace()
        }
    }
}

