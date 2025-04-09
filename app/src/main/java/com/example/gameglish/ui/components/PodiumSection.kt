package com.example.gameglish.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gameglish.data.model.EntityRanking
import androidx.compose.material.icons.filled.EmojiEvents

@Composable
fun PodiumSection(podium: List<EntityRanking>) {
    if (podium.size >= 3) {
        // Usamos un Box para posicionar los tres elementos en un estilo escalonado.
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // La posición 1 y 3 se desplazan hacia abajo para simular el escalón.
                PodiumRankingCard(
                    ranking = podium[0],
                    position = 1,
                    modifier = Modifier.offset(y = 16.dp)
                )
                PodiumRankingCard(
                    ranking = podium[1],
                    position = 2,
                    modifier = Modifier.offset(y = 0.dp)
                )
                PodiumRankingCard(
                    ranking = podium[2],
                    position = 3,
                    modifier = Modifier.offset(y = 16.dp)
                )
            }
        }
    }
}

@Composable
fun PodiumRankingCard(ranking: EntityRanking, position: Int, modifier: Modifier = Modifier) {
    // Asigna colores según la posición:
    val trophyColor = when (position) {
        1 -> Color(0xFFFFD700) // Oro
        2 -> Color(0xFFC0C0C0) // Plata
        3 -> Color(0xFFCD7F32) // Bronce
        else -> Color.Transparent
    }
    // Este composable muestra un ícono de copa encima y una Card con tamaño fijo.
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Ícono de copa.
        Icon(
            imageVector = Icons.Filled.EmojiEvents, // Asegúrate de tener este recurso
            contentDescription = "Trophy",
            tint = trophyColor,
            modifier = Modifier.size(32.dp)
        )
        Card(
            modifier = Modifier.width(120.dp),
            colors = CardDefaults.cardColors(containerColor = trophyColor.copy(alpha = 0.2f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = ranking.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Level: ${ranking.nivel}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Points: ${ranking.puntos}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
