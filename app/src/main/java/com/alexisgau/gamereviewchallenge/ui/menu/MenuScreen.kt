package com.alexisgau.gamereviewchallenge.ui.menu

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexisgau.gamereviewchallenge.R
import com.alexisgau.gamereviewchallenge.ui.theme.SteamBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamNegative
import com.alexisgau.gamereviewchallenge.ui.theme.SteamNegativeAccent
import com.alexisgau.gamereviewchallenge.ui.theme.SteamPositive
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextPrimary
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextSecondary

// Data class para configurar cada botoncito del grid
data class MenuModeItem(
    val title: String,
    val description: String,
    val icon: ImageVector? = null,
    val color: Color,
    val onClick: () -> Unit,
    val drawable: Int? = null
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
    //  items del Grid
    val gridItems = listOf(
        MenuModeItem(
            "Hardcore",
            description = "Sin vidas",
            drawable = R.drawable.skull_icon,
            color = SteamNegative,
            onClick = onPlayHardcore
        ),
        MenuModeItem(
            "Free Mode",
            description = "Vidas ilimitadas",
            drawable = R.drawable.infinite_icon,
            color = Color(0xFF85cbe1),
            onClick = onPlayFree
        ),
        MenuModeItem(
            "Bad Reviews",
            description = "Solo malas reviews",
            drawable = R.drawable.thumb_down_icon,
            color = SteamNegativeAccent,
            onClick = onPlayBadReviews
        ),
        MenuModeItem(
            "Good Reviews",
            description = "Solo buenas reviews",
            Icons.Default.ThumbUp,
            SteamPositive,
            onPlayGoodReviews
        )
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

            //  HEADER
            Text(
                text = "Listo para adivinar?",
                style = MaterialTheme.typography.headlineMedium,
                color = SteamTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // HERO CARD (Survival - El Principal)
            SurvivalHeroCard(onClick = onPlaySurvival)

            Spacer(modifier = Modifier.height(32.dp))

            // SECCIÓN GRID
            Text(
                text = "Seleccionar modo",
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2C1A20), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite, // Corazón para Survival
                        contentDescription = null,
                        tint = Color(0xFFF43F5E),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    color = Color(0xFF2E7D32).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "3 VIDAS",
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
                text = "Pon a prueba tus conocimientos con 3 vidas. ¿Hasta dónde puedes llegar?",
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
                                Color(0xFF554BF9),
                                Color(0xFF3B82F6)
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
            .aspectRatio(1.1f)
            .clickable { item.onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(item.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {

                if (item.icon != null) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.color,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(item.drawable!!),
                        contentDescription = null,
                        tint = item.color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = SteamTextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = SteamTextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}


