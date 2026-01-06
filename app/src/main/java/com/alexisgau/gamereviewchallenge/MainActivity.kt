package com.alexisgau.gamereviewchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alexisgau.gamereviewchallenge.ui.navigation.NavigationWrapper
import com.alexisgau.gamereviewchallenge.ui.score.ScoreScreen
import com.alexisgau.gamereviewchallenge.ui.startGame.GameMode
import com.alexisgau.gamereviewchallenge.ui.startGame.StartGameScreen
import com.alexisgau.gamereviewchallenge.ui.theme.GameReviewChallengeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameReviewChallengeTheme {
//                StartGameScreen(gameMode = GameMode.HARDCORE, onNavigateToScore = {})
//                ScoreScreen(score = 1200, correctAnswers = 8, isNewHighScore = false, onRetryClick = {}, onMenuClick = {})

                NavigationWrapper()
            }
        }
    }
}


