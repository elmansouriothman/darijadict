package com.example.darijadict.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ){
            Icon(
                Icons.Filled.Info,
                "Info", modifier = Modifier.width(32.dp).height(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Guide",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Start
            )
        }
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
