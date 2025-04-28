// In a new file, e.g., RecommendationsScreen.kt
package com.example.darijadict.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.darijadict.data.CsvLoader
import kotlinx.coroutines.launch

@Composable
fun RecommendationsScreen(navController: NavController) {
    val context = LocalContext.current
    val recommendations = remember { CsvLoader.loadRecommendationsFromAssets(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(recommendations) { recommendation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = {
                        if (recommendation.link.isNotBlank()) {
                            val encodedUrl = Uri.encode(recommendation.link)
                            navController.navigate("webView/$encodedUrl")
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "This recommendation has no valid link",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = recommendation.title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (recommendation.link.isBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "⚠️ Link not available",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}