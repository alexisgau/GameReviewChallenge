package com.alexisgau.gamereviewchallenge.ui.score

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import com.alexisgau.gamereviewchallenge.ui.components.CorrectAnswersBadge
import com.alexisgau.gamereviewchallenge.ui.components.HighScoreBadge
import com.alexisgau.gamereviewchallenge.ui.theme.SteamAction
import com.alexisgau.gamereviewchallenge.ui.theme.SteamBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextPrimary
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextSecondary
import com.alexisgau.gamereviewchallenge.utils.ShareUtils

@Composable
fun ScoreScreen(
    modifier: Modifier = Modifier,
    score: Int,
    correctAnswers: Int,
    gameMode: GameMode,
    isNewHighScore: Boolean = false,
    onRetryClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current

    val backgroundBrush = Brush.radialGradient(
        colors = listOf(Color(0xFF181236), SteamBackground, Color.Black),
        radius = 1200f
    )

    Scaffold(containerColor = Color.Transparent) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(24.dp))
                Text(
                    text = "Session Complete",
                    style = MaterialTheme.typography.titleMedium,
                    color = SteamTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { ShareUtils.captureAndShare(context, view) }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = SteamTextPrimary)
                }
            }

            // Score Central
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isNewHighScore) {
                    HighScoreBadge()
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Text(
                    text = getModeTitle(gameMode).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = SteamAction,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .background(SteamAction.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = String.format("%,d", score),
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = SteamTextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "TOTAL POINTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = SteamTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(48.dp))
                CorrectAnswersBadge(count = correctAnswers)
            }

            // Footer Botones
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF4a2aef))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Again", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SteamTextSecondary.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SteamTextPrimary)
                ) {
                    Text("Volver al Menú", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

fun getModeTitle(mode: GameMode): String {
    return when (mode) {
        GameMode.SURVIVAL -> "Survival Mode"
        GameMode.HARDCORE -> "Hardcore Mode"
        GameMode.FREE -> "Free Mode"
        GameMode.BAD_REVIEWS -> "Bad Reviews"
        GameMode.GOOD_REVIEWS -> "Good Reviews"
        else -> "Challenge"
    }
}


