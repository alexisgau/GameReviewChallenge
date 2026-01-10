package com.alexisgau.gamereviewchallenge.ui.startGame

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import com.alexisgau.gamereviewchallenge.ui.components.GameOptionsGrid
import com.alexisgau.gamereviewchallenge.ui.components.LivesIndicator
import com.alexisgau.gamereviewchallenge.ui.components.ReviewCard
import com.alexisgau.gamereviewchallenge.ui.theme.SteamAction
import com.alexisgau.gamereviewchallenge.ui.theme.SteamBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamCardBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamNegative
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextPrimary
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun StartGameScreen(
    modifier: Modifier = Modifier,
    onNavigateToScore: (Int, Int,Boolean, GameMode) -> Unit,
    onBackClick: () -> Unit = {},
) {
    val viewModel: StartGameViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.gameEvents.collect { event ->
            when (event) {
                is GameEvent.GameOver -> onNavigateToScore(
                    event.finalScore,
                    event.correctAnswersCount,
                    event.isNewRecord,
                    uiState.gameMode
                )
            }
        }
    }

    Scaffold(containerColor = SteamBackground) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            //  Loading
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SteamAction)
                }
            }
            // 2. Error
            else if (uiState.errorMessage != null) {
                ErrorView(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.onRetryLoad() }
                )
            }
            //Game Content
            else {
                GameContent(
                    uiState = uiState,
                    onOptionSelected = viewModel::onOptionSelected,
                    onBackClick = onBackClick
                )
            }
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = SteamNegative, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = SteamAction)
        ) {
            Text("Retry")
        }
    }
}

@Composable
fun GameContent(
    uiState: GameUiState,
    onOptionSelected: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header: Vidas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (uiState.gameMode != GameMode.FREE) {
                    LivesIndicator(currentLives = uiState.currentLives, maxLives = uiState.maxLives)
                }
            }

            Spacer(modifier = Modifier.height(72.dp))

            // Títulos
            Text(
                text = "Guess the Game!",
                style = MaterialTheme.typography.headlineMedium,
                color = SteamTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Lea la reseña a continuación y seleccione la portada correspondiente.",
                style = MaterialTheme.typography.bodyMedium,
                color = SteamTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Grid de Opciones
            if (uiState.options.isNotEmpty()) {
                GameOptionsGrid(
                    games = uiState.options,
                    selectedGameId = uiState.selectedGameId,
                    correctGameId = uiState.currentGame?.id,
                    isAnswerCorrect = uiState.isAnswerCorrect,
                    onGameSelected = onOptionSelected
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Review Card y Pistas
            uiState.currentGame?.let { game ->
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)) {

                    val review =
                        remember(game) { if (game.reviews.isNotEmpty()) game.reviews.random() else null }

                    if (review != null) {
                        ReviewCard(
                            modifier = Modifier.fillMaxWidth(),
                            hoursPlayed = review.hours,
                            reviewText = review.content,
                            isPositive = review.isPositive
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(SteamCardBackground, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Loading review...", color = SteamTextSecondary)
                        }
                    }

                    // Pistas
                    AnimatedVisibility(visible = uiState.activeHint != null) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            HorizontalDivider(color = SteamTextSecondary.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "HINT UNLOCKED",
                                style = MaterialTheme.typography.labelSmall,
                                color = SteamAction
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            when (val hint = uiState.activeHint) {
                                is GameHint.Genre -> GenreHintCard(genre = hint.text)
                                is GameHint.ExtraReview -> ReviewCard(
                                    userName = "Hint User",
                                    hoursPlayed = hint.review.hours,
                                    reviewText = hint.review.content,
                                    isPositive = hint.review.isPositive
                                )

                                null -> {}
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }

        // Botón Atrás
        BackButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            onClick = onBackClick
        )
    }
}

@Composable
fun BackButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0xFF1e1a2e))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}


@Composable
fun HintButton(cost: Int, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFC107).copy(alpha = 0.2f),
            contentColor = Color(0xFFFFC107)
        ),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, Color(0xFFFFC107).copy(alpha = 0.5f)),
        modifier = Modifier.height(36.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Get Hint (-$cost pts)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GenreHintCard(genre: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162438)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = SteamTextSecondary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Genero",
                    style = MaterialTheme.typography.labelSmall,
                    color = SteamTextSecondary
                )
                Text(
                    genre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}