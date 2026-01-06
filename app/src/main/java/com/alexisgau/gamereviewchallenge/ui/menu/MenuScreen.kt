package com.alexisgau.gamereviewchallenge.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexisgau.gamereviewchallenge.ui.theme.*

// Data class para configurar cada botoncito del grid
data class MenuModeItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    onPlaySurvival: () -> Unit,
    onPlayHardcore: () -> Unit,
    onPlayFree: () -> Unit,
    onPlayBadReviews: () -> Unit,
    onPlayGoodReviews: () -> Unit
) {
    // Definimos los items del Grid aquí para mantener el código limpio
    val gridItems = listOf(
        MenuModeItem("Hardcore", description = "Sin vidas" , Icons.Default.Warning, SteamNegative, onPlayHardcore),
        MenuModeItem("Free Mode", description = "Vidas ilimitadas", Icons.Default.Add, SteamAction, onPlayFree),
        MenuModeItem("Bad Reviews", description = "Solo malas reviews", Icons.Default.Refresh, SteamNegativeAccent, onPlayBadReviews),
        MenuModeItem("Good Reviews", description = "Solo buenas reviews", Icons.Default.ThumbUp, SteamPositive, onPlayGoodReviews)
        // Puedes agregar GENRE aquí en el futuro
    )

    Scaffold(
        containerColor = SteamBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // 1. HEADER
            Text(
                text = "Ready to guess?",
                style = MaterialTheme.typography.headlineMedium,
                color = SteamTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. HERO CARD (Survival - El Principal)
            // Replicamos la tarjeta grande "Quick Play" de la foto
            SurvivalHeroCard(onClick = onPlaySurvival)

            Spacer(modifier = Modifier.height(32.dp))

            // 3. SECCIÓN GRID
            Text(
                text = "Select Mode",
                style = MaterialTheme.typography.titleMedium,
                color = SteamTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columnas como la foto
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(gridItems) { item ->
                    ModeGridCard(item)
                }

                // Espacio extra abajo para que no se corte
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun SurvivalHeroCard(onClick: () -> Unit) {
    // Un fondo un poco más claro que el del app para destacar
    val cardBg = Color(0xFF1d1d27)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Icono Circular y Badge (simulado)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1d154a), CircleShape), // Fondo violeta oscuro
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite, // Corazón para Survival
                        contentDescription = null,
                        tint = Color(0xFF482bea), // Violeta brillante
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Badge opcional tipo "POPULAR"
                Surface(
                    color = Color(0xFF2E7D32).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "3 LIVES",
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Títulos
            Text(
                text = "Survival Mode",
                style = MaterialTheme.typography.titleLarge,
                color = SteamTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Test your knowledge with 3 lives. How far can you go?",
                style = MaterialTheme.typography.bodyMedium,
                color = SteamTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN GRADIENTE "Start Now"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF554BF9), // Violeta Start
                                Color(0xFF3B82F6)  // Azul End
                            )
                        )
                    )
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Now",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ModeGridCard(item: MenuModeItem) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1d1d27)),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f) // Cuadrado ligeramente rectangular
            .clickable { item.onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono con fondo circular de su color
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(item.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = SteamTextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp)) // Espacio pequeño entre título y descripción

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall, // Letra pequeña (12sp aprox)
                color = SteamTextSecondary, // Color gris secundario
                textAlign = TextAlign.Center, // Centrado
                maxLines = 2, // Limitamos a 2 líneas para que no rompa el diseño
                lineHeight = 14.sp, // Altura de línea compacta
                modifier = Modifier.padding(horizontal = 8.dp) // Margen para que no toque los bordes
            )
        }
    }
}


