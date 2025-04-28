// In a new file, e.g., RecommendationsScreen.kt
package com.example.darijadict.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ){
                Icon(
                    Icons.Filled.Link,
                    "Recommended Links", modifier = Modifier.width(32.dp).height(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recommended Links",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start
                )
            }
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
}