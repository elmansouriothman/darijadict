package com.example.darijadict.ui

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.darijadict.data.CsvLoader
import kotlinx.coroutines.launch
import androidx.navigation.NavController

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
                .background(Color(0xFFF5F7FA)) // Soft background
                .padding(16.dp)
        ) {
            // Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Icon(
                    Icons.Filled.Link,
                    contentDescription = "Recommended Links",
                    tint = Color(0xFF0D47A1),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Recommended Links",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                )
            }

            // List of Recommendations
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 8.dp)
            ) {
                items(recommendations) { recommendation ->
                    RecommendationCard(
                        recommendation = recommendation,
                        navController = navController,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(
    recommendation: com.example.darijadict.data.Recommendation,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
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
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    color = Color(0xFF0066CC)
                )
            )
            if (recommendation.link.isBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }
        }
    }
}
