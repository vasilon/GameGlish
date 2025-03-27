package com.example.gameglish.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LivesRow(lives: Int) {
    Row {
        repeat(3) { index ->
            val filled = index < lives
            Icon(
                imageVector = if (filled) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Vida ${index+1}",
                tint = if (filled) Color.Red else Color.Gray,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}
