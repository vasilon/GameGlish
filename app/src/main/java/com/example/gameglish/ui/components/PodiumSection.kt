package com.example.gameglish.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gameglish.R
import com.example.gameglish.data.model.EntityRanking

/** -------------------------------------------------------------------
 *  LeaderboardTabs – ahora con colores del nuevo Theme
 * -------------------------------------------------------------------- */
@Composable
fun LeaderboardTabs() {
    var selectedIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Region", "National", "Global")
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        indicator = {}
    ) {
        tabs.forEachIndexed { index, text ->
            Tab(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                text = {
                    Text(
                        text = text,
                        color = if (selectedIndex == index)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

/** -------------------------------------------------------------------
 *  Top-3 podio – usa primaryContainer vs. surfaceVariant
 * -------------------------------------------------------------------- */
@Composable
fun Top3Row(podiumList: List<EntityRanking>) {
    // Asumimos podiumList.size >= 3
    val colorBoxTop   = MaterialTheme.colorScheme.primaryContainer      // #1
    val colorBoxOther = MaterialTheme.colorScheme.surfaceVariant        // #2 y #3

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Caja para #2 y #3
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(340.dp)
                .height(100.dp)
                .clip(MaterialTheme.shapes.large)
                .background(colorBoxOther)
        )

        // Caja para #1
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp)
                .width(160.dp)
                .height(120.dp)
                .clip(MaterialTheme.shapes.large)
                .background(colorBoxTop)
        )

        // Fila de los 3 avatares
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            Top3CardWithCrown(ranking = podiumList[1], position = 2, isCenter = false)
            Top3CardWithCrown(ranking = podiumList[0], position = 1, isCenter = true )
            Top3CardWithCrown(ranking = podiumList[2], position = 3, isCenter = false)
        }
    }
}

/** -------------------------------------------------------------------
 *  Card de un usuario del podio
 * -------------------------------------------------------------------- */
@Composable
fun Top3CardWithCrown(
    ranking: EntityRanking,
    position: Int,
    isCenter: Boolean
) {
    val avatarSize = if (isCenter) 90.dp else 70.dp
    val bgColor    = if (position == 1) MaterialTheme.colorScheme.primaryContainer
    else                MaterialTheme.colorScheme.surfaceVariant
    val textColor  = if (position == 1) MaterialTheme.colorScheme.onPrimaryContainer
    else                MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = if (isCenter) Modifier else Modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar + corona
        Box(contentAlignment = Alignment.TopCenter) {
            Image(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .background(bgColor),
                contentScale = ContentScale.Crop
            )
            if (position == 1) {
                Image(
                    painter = painterResource(id = R.drawable.ic_crown),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .offset(y = (-24).dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = ranking.nombre,
            style = MaterialTheme.typography.titleMedium.copy(
                color = textColor,
                fontWeight = if (isCenter) FontWeight.Bold else FontWeight.SemiBold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${ranking.puntos} pts",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}

/** -------------------------------------------------------------------
 *  Item del ranking a partir del 4º puesto
 * -------------------------------------------------------------------- */
@Composable
fun RankingListItem(ranking: EntityRanking, position: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$position.",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.width(30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ranking.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Lvl: ${ranking.nivel}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                )
            }
            Text(
                text = "${ranking.puntos}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}