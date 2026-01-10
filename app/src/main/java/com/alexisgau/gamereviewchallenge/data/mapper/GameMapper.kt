package com.alexisgau.gamereviewchallenge.data.mapper

import com.alexisgau.gamereviewchallenge.data.remote.dto.GameDto
import com.alexisgau.gamereviewchallenge.domain.model.Game
import com.alexisgau.gamereviewchallenge.domain.model.Review
import java.util.Locale

private const val BASE_IMAGE_URL =
    "https://raw.githubusercontent.com/alexisgau/GameReviewChallenge/master/imagenes/"

fun GameDto.toDomain(): Game {
    val imageName = generateImageSlug(this.name)
    return Game(
        id = name.hashCode().toLong(),
        title = name,
        genres = genres,
        imageUrl = "$BASE_IMAGE_URL$imageName.webp",
        reviews = reviews.map { dto ->
            Review(
                content = dto.text,
                isPositive = dto.positive,
                hours = dto.hours ?: 0.0
            )
        }
    )
}

// Función auxiliar para limpiar el nombre
// Convierte "No Man's Sky!" en "no_mans_sky"
fun generateImageSlug(originalName: String): String {
    return originalName
        .lowercase(Locale.ROOT)       // 1. A minúsculas
        .replace(" ", "_")            // 2. Espacios a guiones bajos
        .replace(":", "")             // 3. Borrar dos puntos (Red Dead: Redemption)
        .replace("'", "")             // 4. Borrar comillas simples (No Man's Sky)
        .replace("-", "_")            // 5. Guiones medios a bajos (Half-Life)
        .replace("!", "")             // 6. Borrar exclamaciones
        .replace("?", "")             // 7. Borrar interrogaciones
        .replace(".", "")             // 8. Borrar puntos (S.T.A.L.K.E.R.)
        .replace(
            Regex("[^a-z0-9_]"),
            ""
        ) // 9. Limpieza final: borrar cualquier cosa que no sea letra, numero o guion bajo
}