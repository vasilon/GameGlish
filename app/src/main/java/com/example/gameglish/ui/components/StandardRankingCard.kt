// File: app/src/main/java/com/example/gameglish/ui/components/StandardRankingCard.kt
package com.example.gameglish.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
fun StandardRankingCard(ranking: EntityRanking, position: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${position + 1}. ${ranking.nombre}",
                style = MaterialTheme.typography.titleMedium
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
