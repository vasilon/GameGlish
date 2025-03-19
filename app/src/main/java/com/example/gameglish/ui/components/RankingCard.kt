// File: app/src/main/java/com/example/gameglish/ui/components/RankingCard.kt
package com.example.gameglish.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gameglish.data.model.EntityRanking

@Composable
fun RankingCard(ranking: EntityRanking, position: Int) {
    val containerColor = when (position) {
        0 -> Color(0xFFFFD700) // Gold
        1 -> Color(0xFFC0C0C0) // Silver
        2 -> Color(0xFFCD7F32) // Bronze
        else -> Color(0xFFE0E0E0) // Gray
    }

    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Position: ${position + 1}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Name: ${ranking.nombre}",
                style = MaterialTheme.typography.bodyLarge
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