package com.alexisgau.gamereviewchallenge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexisgau.gamereviewchallenge.domain.model.Game
import com.alexisgau.gamereviewchallenge.ui.theme.SteamNegative
import com.alexisgau.gamereviewchallenge.ui.theme.SteamPositive
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextSecondary


@Composable
fun GameOptionsGrid(
    games: List<Game>,
    selectedGameId: Long?,
    correctGameId: Long?,
    isAnswerCorrect: Boolean?,
    onGameSelected: (Long) -> Unit,
) {
    val isResultState = selectedGameId != null
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
fun GenreHintCard(genre: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162438)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Close, contentDescription = null, tint = SteamTextSecondary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Game Genre",
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



