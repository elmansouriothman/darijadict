package com.example.darijadict.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.darijadict.data.Entry
import com.example.darijadict.viewmodel.EntryViewModel

@Composable
fun EntryListScreen(
    title: String,
    entries: List<Entry>,
    viewModel: EntryViewModel,
    onEntryClick: (Entry) -> Unit
) {
    var showAddToListDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<Entry?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Calm light background
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No entries to display.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.DarkGray
                    )
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(entries) { entry ->
                    EntryCard(
                        entry = entry,
                        viewModel = viewModel,
                        onClick = { onEntryClick(entry) },
                        onAddToListClick = {
                            selectedEntry = entry
                            showAddToListDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showAddToListDialog && selectedEntry != null) {
        AddToListDialog(
            viewModel = viewModel,
            entryId = selectedEntry!!.id,
            onDismiss = { showAddToListDialog = false }
        )
    }
}
