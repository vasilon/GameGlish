// File: app/src/main/java/com/example/gameglish/ui/view/GlobalRankingScreen.kt
package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.gameglish.R
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityRanking
import com.example.gameglish.data.repository.RepositoryEstadistica
import com.example.gameglish.ui.components.RankingCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalRankingScreen(navController: NavController) {
    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryEstadistica = RepositoryEstadistica(db)
    var rankingList by remember { mutableStateOf<List<EntityRanking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            rankingList = repositoryEstadistica.obtenerRankingGlobal()
        } catch (e: Exception) {
            Log.e("GlobalRankingScreen", "Error loading ranking", e)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Ranking") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(rankingList) { index, ranking ->
                        RankingCard(ranking = ranking, position = index)
                    }
                }
            }
        }
    )
}