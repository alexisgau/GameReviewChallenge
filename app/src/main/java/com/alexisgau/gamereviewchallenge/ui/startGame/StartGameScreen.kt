package com.alexisgau.gamereviewchallenge.ui.startGame

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.alexisgau.gamereviewchallenge.domain.model.Game
import com.alexisgau.gamereviewchallenge.ui.theme.SteamBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamCardBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamContentBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamNegative
import com.alexisgau.gamereviewchallenge.ui.theme.SteamPositive
import com.alexisgau.gamereviewchallenge.ui.theme.SteamReviewCardBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextPrimary
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextSecondary

@Composable
fun StartGameScreen(modifier: Modifier = Modifier,gameMode: GameMode,onNavigateToScore: (Int,Int) -> Unit, onBackClick: () -> Unit = {}) {
    // Nota: En una app real usarías koinViewModel() o hiltViewModel()
    val viewModel: StartGameViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(gameMode) {
        viewModel.setGameMode(gameMode)
    }


    LaunchedEffect(Unit) {
        viewModel.gameEvents.collect { event ->
            when(event) {
                is GameEvent.GameOver -> onNavigateToScore(event.finalScore, event.correctAnswersCount)
            }
        }
    }

    Scaffold(
        containerColor = SteamBackground
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. CONTENIDO PRINCIPAL
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp), // Ajusta según tu BackButton
                    horizontalArrangement = Arrangement.End, // A la derecha
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Solo mostramos vidas si NO es modo libre
                    if (uiState.gameMode != GameMode.FREE) {
                        LivesIndicator(
                            currentLives = uiState.currentLives,
                            maxLives = uiState.maxLives
                        )
                    }
                }
                Spacer(modifier = Modifier.height(72.dp))

                Text(
                    text = "Guess the Game!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = SteamTextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Read the review below and select the matching cover.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SteamTextSecondary,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(24.dp))


                if (uiState.options.isNotEmpty()) {
                    GameOptionsGrid(
                        games = uiState.options,
                        selectedGameId = uiState.selectedGameId,
                        correctGameId = uiState.currentGame?.id,
                        isAnswerCorrect = uiState.isAnswerCorrect,
                        onGameSelected = { viewModel.onOptionSelected(it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    uiState.currentGame?.let { game ->
                        val review = remember(game) {
                            if (game.reviews.isNotEmpty()) game.reviews.random() else null
                        }

                        if (review != null) {
                            ReviewCard(
                                reviewText = review.content,
                                isPositive = review.isPositive,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SteamCardBackground, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Cargando...", color = SteamTextSecondary)
                            }
                        }
                    }
                }
            }

            BackButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                onClick = onBackClick
            )
        }
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
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
fun GameOptionsGrid(
    games: List<Game>,
    selectedGameId: Long?,
    correctGameId: Long?,
    isAnswerCorrect: Boolean?,
    onGameSelected: (Long) -> Unit,
) {
    val isResultState = selectedGameId != null

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        games.forEach { game ->
            Box(modifier = Modifier.weight(1f)) {
                val isSelected = game.id == selectedGameId

                val borderColor = when {
                    isSelected && isAnswerCorrect == true -> SteamPositive
                    isSelected && isAnswerCorrect == false -> SteamNegative
                    else -> Color.Transparent
                }

                val isFadedOut = isResultState && game.id != correctGameId


                val shouldElevate = isResultState && game.id == correctGameId

                GameOptionCard(
                    title = game.title,
                    imageUrl = game.imageUrl,
                    borderColor = borderColor,
                    isSelected = isSelected,
                    isCorrect = isAnswerCorrect == true,
                    isFadedOut = isFadedOut,
                    shouldElevate = shouldElevate,
                    onClick = { onGameSelected(game.id) }
                )
            }
        }
    }
}

@Composable
fun GameOptionCard(
    title: String,
    imageUrl: String?,
    borderColor: Color,
    isSelected: Boolean,
    isCorrect: Boolean,
    isFadedOut: Boolean,
    shouldElevate: Boolean,
    onClick: () -> Unit,
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isFadedOut) 0.3f else 1f,
        animationSpec = tween(durationMillis = 300), label = "alpha"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else SteamTextSecondary,
        animationSpec = tween(durationMillis = 300), label = "color"
    )

    // --- 2. USAMOS shouldElevate PARA ACTIVAR LA ANIMACIÓN ---
    val targetScale = if (shouldElevate) 1.08f else 1f
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "scale"
    )

    val targetElevation = if (shouldElevate) 12.dp else 0.dp
    val animatedElevation by animateDpAsState(
        targetValue = targetElevation,
        animationSpec = tween(durationMillis = 300), label = "elevation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .alpha(animatedAlpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.65f)
                .scale(animatedScale)
                .shadow(
                    elevation = animatedElevation,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(12.dp))
                .background(SteamContentBackground)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(borderColor.copy(alpha = 0.4f))
                        .border(3.dp, borderColor, RoundedCornerShape(12.dp))
                )
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                        .background(borderColor, CircleShape)
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            color = animatedTextColor,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReviewCard(
    modifier: Modifier = Modifier,
    userName: String = "Steam User",
    postedTime: String = "Played recently", // Texto genérico o dato real si lo tienes
    reviewText: String,
    isPositive: Boolean,
) {
    val SteamAvatarBlue = Color(0xFF4B64F2)
    val SteamTextSecondary = Color(0xFF7c889c)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SteamReviewCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Avatar + Nombre + Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SteamAvatarBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.first().uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = userName, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = postedTime, color = SteamTextSecondary, fontSize = 12.sp)
                    }
                }

                // Badge Positivo/Negativo
                val (statusColor, icon, text) = if (isPositive) {
                    Triple(SteamPositive, Icons.Default.ThumbUp, "POSITIVE")
                } else {
                    Triple(SteamNegative, Icons.Default.Warning, "NEGATIVE")
                }

                Row(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = text,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFF324055) // Muy sutil
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = reviewText,
                color = Color(0XFFcbd5e1),
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun LivesIndicator(currentLives: Int, maxLives: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Dibujamos tantos corazones como vidas máximas (o fijas en 3 para estética)
        // Y los pintamos según si están activos o perdidos
        repeat(maxLives) { index ->
            val isActive = index < currentLives
            Icon(
                imageVector = Icons.Default.Favorite, // Corazón
                contentDescription = null,
                tint = if (isActive) SteamPositive else Color.Gray.copy(alpha = 0.3f), // Rojo o Gris apagado
                modifier = Modifier.size(24.dp) // Tamaño sutil
            )
        }
    }
}