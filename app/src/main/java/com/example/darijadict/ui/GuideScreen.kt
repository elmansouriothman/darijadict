package com.example.darijadict.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun GuideScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // soft light background
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Info Icon",
                tint = Color(0xFF0D47A1), // calm dark grey-blue
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "How to Use the App",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1) // calm dark grey-blue
                ),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Guide sections
        GuideSection(
            title = "1. Main Screens Overview",
            content = listOf(
                "Guide",
                "Dictionary",
                "Lists",
                "Grammar"
            ),
            paragraph = "You are now in the Guide screen and you are about to learn how to use the many functions the app has to offer. This screen also has a button for Recommended Links that I encourage you to check."
        )

        GuideSection(
            title = "2. Dictionary Screen",
            content = listOf(
                "All categories",
                "Only in Darija",
                "Only in English",
                "Using Arabic script"
            ),
            paragraph = "On the Dictionary screen, you will find a search bar with a dropdown button. Below it, you will see cards showing Darija vocabulary (Latin script), Arabic script, and the English meaning. Each card has buttons to save vocabulary and add it to personal lists. Tapping a card opens a detailed screen with pronunciation, meaning, word forms, and usage example."
        )

        GuideSection(
            title = "3. Lists Screen",
            content = listOf(
                "New List button to create custom lists",
                "Download Anki Deck Note Template",
                "Open and export saved lists for Anki"
            ),
            paragraph = "Inside your lists, you can view saved words, export them, and manage your vocabulary easily."
        )

        GuideSection(
            title = "4. Grammar Screen",
            content = emptyList(),
            paragraph = "The Grammar screen contains all the lessons you should follow, from easiest to hardest, in the recommended order."
        )

        GuideSection(
            title = "5. How to Study Vocabulary on Anki",
            content = listOf(
                "Install Ankidroid (or Anki Desktop)",
                "Download the Anki Deck Template",
                "Export your list from the app",
                "Import into Anki (enable HTML, select Moroccan Darija Note Type)",
                "Map fields 1 to 13, leave Tags empty, then click Import"
            ),
            paragraph = ""
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Recommended Links Button
        Button(
            onClick = { navController.navigate("recommendations") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF37474F),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Recommended Links", fontSize = 20.sp)
        }
    }
}

@Composable
fun GuideSection(title: String, content: List<String>, paragraph: String) {
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
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFF0066CC), // calmer nice blue
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (paragraph.isNotEmpty()) {
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF546E7A)),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (content.isNotEmpty()) {
                content.forEach { item ->
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF37474F),
                            fontSize = 17.sp
                        ),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}
