package com.alexisgau.gamereviewchallenge.ui.components

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexisgau.gamereviewchallenge.R
import com.alexisgau.gamereviewchallenge.ui.theme.SteamContentBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamNegative
import com.alexisgau.gamereviewchallenge.ui.theme.SteamPositive
import com.alexisgau.gamereviewchallenge.ui.theme.SteamReviewCardBackground
import com.alexisgau.gamereviewchallenge.ui.theme.SteamTextSecondary

@Composable
fun ReviewCard(
    modifier: Modifier = Modifier,
    userName: String = "Usuario anonimo",
    hoursPlayed: Double,
    reviewText: String,
    isPositive: Boolean,
) {
    val SteamAvatarBlue = Color(0xFF4B64F2)
    val SteamTextSecondary = Color(0xFF7c889c)

    Card(
        modifier = modifier.fillMaxWidth(),
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
                        Text(
                            text = "$hoursPlayed horas jugadas",
                            color = SteamTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Badge Positivo/Negativo
                val (statusColor, iconPainter, text) = if (isPositive) {
                    Triple(
                        SteamPositive,
                        rememberVectorPainter(Icons.Default.ThumbUp), // Convertimos Vector a Painter
                        "POSITIVE"
                    )
                } else {
                    Triple(
                        SteamNegative,
                        painterResource(id = R.drawable.thumb_down_icon), // Cargamos el Drawable como Painter
                        "NEGATIVE"
                    )
                }

                Row(
                    modifier = Modifier
                        .background(
                            statusColor.copy(alpha = 0.2f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = iconPainter,
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
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .error(R.drawable.error_game) // Si falla o no existe, muestra esto
//                  .placeholder(R.drawable.loading_game) // Mientras carga, muestra esto
                    .build(),
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


