package com.example.darijadict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.darijadict.data.CustomList
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
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (entries.isEmpty()) {
            Text("No entries to display.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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