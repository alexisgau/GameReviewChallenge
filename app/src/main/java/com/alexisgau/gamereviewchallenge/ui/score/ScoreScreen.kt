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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexisgau.gamereviewchallenge.ui.theme.SteamAction
import com.alexisgau.gamereviewchallenge.ui.theme.SteamBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamPositive
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextPrimary
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextSecondary

@Composable
fun ScoreScreen(
    modifier: Modifier = Modifier,
    score: Int,
    correctAnswers: Int,
    isNewHighScore: Boolean = false,
    onRetryClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    // Definimos el gradiente basado en tu imagen (Azul oscuro -> Violeta brillante en el centro)
    val backgroundBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFF181236), // Centro más claro (SteamCardBackground)
            SteamBackground,   // Hacia afuera se oscurece (SteamBackground)
            Color.Black        // Bordes casi negros
        ),
        radius = 1200f // Ajusta el radio del brillo
    )

    Scaffold(
        containerColor = Color.Transparent // Importante para ver el gradiente del Box
    ) { innerPadding ->

        // Contenedor Principal con el Fondo Gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
        ) {

            // 1. CABECERA (Header)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Espaciador para centrar el texto "Session Complete"
                Spacer(modifier = Modifier.size(24.dp))

                Text(
                    text = "Session Complete",
                    style = MaterialTheme.typography.titleMedium,
                    color = SteamTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                // Botón de compartir (Icono)
                IconButton(onClick = { /* Acción compartir */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Score",
                        tint = SteamTextPrimary
                    )
                }
            }

            // 2. CONTENIDO CENTRAL (Puntaje)
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Badge de "NEW HIGH SCORE"
                if (isNewHighScore) {
                    HighScoreBadge()
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // El Número Gigante
                Text(
                    text = String.format("%,d", score), // Formato con comas (1,250)
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 80.sp, // Tamaño extra grande como la foto
                    fontWeight = FontWeight.Bold,
                    color = SteamTextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "TOTAL POINTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = SteamTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp // Espaciado entre letras tipo gamer
                )

                Spacer(modifier = Modifier.height(48.dp))

                CorrectAnswersBadge(count = correctAnswers)
            }

            // 3. BOTONES (Abajo)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón Principal: Retry
                Button(
                    onClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0XFF4a2aef) // Azul brillante
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Again", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Botón Secundario: Menu
                OutlinedButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SteamTextSecondary.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SteamTextPrimary
                    )
                ) {
                    Text("Return to Menu", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun HighScoreBadge() {
    // El color violeta oscuro de fondo del badge
    val BadgeBg = Color(0xFF311B92).copy(alpha = 0.6f)
    // El color violeta claro del texto/icono
    val BadgeContent = Color(0xFF7C4DFF)

    Surface(
        color = BadgeBg,
        shape = CircleShape, // Pill shape
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star, // O un trofeo si tienes la dependencia
                contentDescription = null,
                tint = BadgeContent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "NEW HIGH SCORE",
                color = BadgeContent,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun ResultStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = SteamTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SteamTextSecondary
        )
    }
}

@Composable
fun CorrectAnswersBadge(count: Int) {
    // Colores locales para este componente
    val containerColor = Color(0xFF171A21).copy(alpha = 0.5f) // Fondo oscuro sutil
    val borderColor = SteamTextSecondary.copy(alpha = 0.2f)   // Borde apenas visible
    val iconColor = SteamPositive // Verde (usamos tu color de tema)

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 1. Icono de Check
            Icon(
                imageVector = Icons.Default.CheckCircle, // Asegúrate de tener este icono
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Texto "Correct Answers"
            Text(
                text = "Correct Answers",
                style = MaterialTheme.typography.bodyMedium,
                color = SteamTextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 3. El Número (Resaltado)
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                color = SteamTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}