package com.alexisgau.gamereviewchallenge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alexisgau.gamereviewchallenge.domain.model.GameMode
import com.alexisgau.gamereviewchallenge.ui.menu.MenuScreen
import com.alexisgau.gamereviewchallenge.ui.score.ScoreScreen
import com.alexisgau.gamereviewchallenge.ui.startGame.StartGameScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MenuRoute
    ) {

        composable<MenuRoute> {
            MenuScreen(
                onPlaySurvival = { navController.navigate(GameRoute(GameMode.SURVIVAL)) },
                onPlayHardcore = { navController.navigate(GameRoute(GameMode.HARDCORE)) },
                onPlayFree = { navController.navigate(GameRoute(GameMode.FREE)) },
                onPlayGoodReviews = { navController.navigate(GameRoute(GameMode.GOOD_REVIEWS)) },
                onPlayBadReviews = { navController.navigate(GameRoute(GameMode.BAD_REVIEWS)) }
            )
        }

        composable<GameRoute> { backStackEntry ->
            StartGameScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToScore = { score, correctAnswers,isRecord, gameMode ->
                    navController.navigate(
                        ScoreRoute(
                            score = score,
                            correctAnswers = correctAnswers,
                            isNewRecord = isRecord,
                            gameMode = gameMode
                        )
                    ) {
                        // Borramos el historial hasta el Menú (exclusivo)
                        popUpTo(MenuRoute) { inclusive = false }
                    }
                }
            )
        }

        //  PANTALLA DE PUNTAJE ---
        composable<ScoreRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ScoreRoute>()

            ScoreScreen(
                score = route.score,
                correctAnswers = route.correctAnswers,
                gameMode = route.gameMode,
                isNewHighScore = route.isNewRecord,
                onRetryClick = {
                    navController.navigate(GameRoute(route.gameMode)) {
                        // para que al dar "Atrás" en la nueva partida, vuelvas al Menú y no al Score viejo.
                        popUpTo(MenuRoute) { inclusive = false }
                    }
                },
                onMenuClick = {
                    // Volver al Menú y limpiar  el historial de juego
                    navController.navigate(MenuRoute) {
                        popUpTo(MenuRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}

