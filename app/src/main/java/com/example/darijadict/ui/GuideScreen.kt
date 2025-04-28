package com.example.darijadict.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun GuideScreen(navController: NavController) {
    val context = LocalContext.current
    var guideContent by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        guideContent = loadGuideText(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = guideContent, style = MaterialTheme.typography.bodyLarge)

        Button(
            onClick = {
                navController.navigate("recommendations") // Ensure this matches your navigation route
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Recommended Links")
        }
    }
}

private fun loadGuideText(context: Context): String {
    return try {
        val inputStream = context.assets.open("guide.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.use { it.readText() }
    } catch (e: Exception) {
        "Error loading guide: ${e.message}"
    }
}
