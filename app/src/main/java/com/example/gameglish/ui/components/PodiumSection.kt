package com.example.gameglish.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gameglish.R
import com.example.gameglish.data.model.EntityRanking
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun LeaderboardTabs() {
    var selectedIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Region", "National", "Global")
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color(0xFF2F3256),
        contentColor = Color.White
    ) {
        tabs.forEachIndexed { index, text ->
            Tab(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                text = {
                    Text(
                        text = text,
                        color = if (selectedIndex == index) Color.White else Color.LightGray,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
fun Top3Row(podiumList: List<EntityRanking>) {
    // Asumimos que podiumList.size >= 3
    // Color base: uno un poco más claro para #2 y #3
    val colorBase2y3 = Color(0xFF2D2F50).copy(alpha = 0.7f)
    // Color más oscuro para #1
    val colorBase1 = Color(0xFF2D2F50).copy(alpha = 0.9f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Box tras #2 y #3
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(280.dp)
                .height(100.dp)
                .clip(MaterialTheme.shapes.large)
                .background(colorBase2y3)
        )

        // Box tras #1, más oscuro y un poco más alto
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp)  // Sube un poco
                .width(160.dp)
                .height(120.dp)
                .clip(MaterialTheme.shapes.large)
                .background(colorBase1)
        )

        // Fila de los 3 avatares y su info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp), // Sube los avatares encima de los boxes
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Posición #2 (izquierda)
            Top3CardWithCrown(
                ranking = podiumList[1],
                position = 2,
                isCenter = false
            )
            // Posición #1 (centro)
            Top3CardWithCrown(
                ranking = podiumList[0],
                position = 1,
                isCenter = true
            )
            // Posición #3 (derecha)
            Top3CardWithCrown(
                ranking = podiumList[2],
                position = 3,
                isCenter = false
            )
        }
    }
}


/**
 * Card para uno de los top 3, con avatar grande o mediano,
 * sin trofeo, siguiendo el estilo de la referencia.
 */
@Composable
fun Top3CardWithCrown(
    ranking: EntityRanking,
    position: Int,
    isCenter: Boolean
) {
    // Avatar más grande si es el #1
    val avatarSize = if (isCenter) 90.dp else 70.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Contenedor principal del avatar y la corona (solo #1 la lleva)
        Box(contentAlignment = Alignment.TopCenter) {
            // Avatar (placeholder)
            Image(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            if (position == 1) {
                // Corona encima del avatar
                Image(
                    painter = painterResource(id = R.drawable.ic_crown),
                    colorFilter = ColorFilter.tint(Color(0xFFFFD700)),
                    contentDescription = "Crown",
                    modifier = Modifier
                        .size(32.dp)
                        .offset(y = (-24).dp) // Súbelo encima
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Nombre
        Text(
            text = ranking.nombre,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = if (isCenter) FontWeight.Bold else FontWeight.SemiBold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Puntos
        Text(
            text = "${ranking.puntos} pts",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFFFC107))
        )
    }
}

/**
 * Item de la lista para el resto de posiciones (4,5,6,...)
 */
@Composable
fun RankingListItem(ranking: EntityRanking, position: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F3256)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Posición
            Text(
                text = "$position.",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color(0xFFB0B0B0),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.width(30.dp)
            )
            // Avatar
            Image(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ranking.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Lvl: ${ranking.nivel}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }
            // Puntos a la derecha
            Text(
                text = "${ranking.puntos}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
