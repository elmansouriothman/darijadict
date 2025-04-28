package com.example.darijadict.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import kotlinx.coroutines.delay

import android.content.Context
import android.widget.Toast

// Function to open a URL in a web browser
fun openWebPage(context: Context, url: String) {
    // Validate URL before opening
    val urlString = if (!url.startsWith("http://") && !url.startsWith("https://")) {
        "https://$url"
    } else {
        url
    }

    try {
        val webpage = Uri.parse(urlString)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // No activity to handle the URL
            Toast.makeText(
                context,
                "No app available to open the link.",
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        // Handle invalid URL or other issues
        Toast.makeText(
            context,
            "Invalid URL or error opening the link.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun GrammarDetailScreen(
    navController: NavController,
    link: String
) {
    val context = LocalContext.current
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        openWebPage(context, link)
        navController.popBackStack()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showError) {
            AlertDialog(
                onDismissRequest = { showError = false },
                title = { Text("Error Opening Link") },
                text = { Text(errorMessage) },
                confirmButton = {
                    Button(onClick = { showError = false }) {
                        Text("OK")
                    }
                }
            )
        } else {
            CircularProgressIndicator()
        }
    }
}