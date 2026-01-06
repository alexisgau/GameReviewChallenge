package com.alexisgau.gamereviewchallenge.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alexisgau.gamereviewchallenge.ui.menu.MenuScreen
import com.alexisgau.gamereviewchallenge.ui.score.ScoreScreen
import com.alexisgau.gamereviewchallenge.ui.startGame.GameMode
import com.alexisgau.gamereviewchallenge.ui.startGame.StartGameScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MenuRoute // Arrancamos en el Menú
    ) {

        // --- 1. MENÚ PRINCIPAL ---
        composable<MenuRoute> {
            // Placeholder del Menú (para probar los botones)
            MenuScreen(

                onPlaySurvival = {navController.navigate(GameRoute(GameMode.SURVIVAL))},
                onPlayHardcore = {navController.navigate(GameRoute(GameMode.HARDCORE))},
                onPlayFree = {navController.navigate(GameRoute(GameMode.FREE))},
                onPlayGoodReviews = {},
                onPlayBadReviews = {}
            )
        }

        // --- 2. PANTALLA DE JUEGO ---
        composable<GameRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<GameRoute>()

            StartGameScreen(
                gameMode = route.mode,
                onBackClick = { navController.popBackStack() },
                onNavigateToScore = { score, correctAnswers ->
                    // Navegar al Score
                    navController.navigate(
                        ScoreRoute(
                            score = score,
                            correctAnswers = correctAnswers,
                            isNewRecord = false // Aquí iría tu lógica real
                        )
                    ) {
                        // TRUCO PRO: Borramos el historial hasta el Menú (exclusivo)
                        // Esto elimina la partida actual de la pila "Atrás"
                        popUpTo(MenuRoute) { inclusive = false }
                    }
                }
            )
        }

        // --- 3. PANTALLA DE PUNTAJE ---
        composable<ScoreRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ScoreRoute>()

            ScoreScreen(
                score = route.score,
                correctAnswers = route.correctAnswers,
                isNewHighScore = route.isNewRecord,
                onRetryClick = {
                    // Volver a jugar (Survival por defecto)
                    navController.navigate(GameRoute(GameMode.HARDCORE)) {
                        // Borramos el Score actual de la pila para que no se acumulen
                        popUpTo(MenuRoute) { inclusive = false }
                    }
                },
                onMenuClick = {
                    // Volver al Menú y limpiar TODO el historial de juego
                    navController.navigate(MenuRoute) {
                        popUpTo(MenuRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}

// Un Menú temporal simple para que no te de error si no creaste el archivo MenuScreen aún
@Composable
fun MenuScreenPlaceholder(onPlaySurvival: () -> Unit, onPlayFree: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onPlaySurvival) { Text("Jugar Survival") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onPlayFree) { Text("Jugar Libre") }
    }
}


