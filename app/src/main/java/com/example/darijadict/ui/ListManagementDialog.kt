package com.example.darijadict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.darijadict.data.CustomList
import com.example.darijadict.viewmodel.EntryViewModel
import kotlinx.coroutines.launch

@Composable
fun ListManagementDialog(
    viewModel: EntryViewModel,
    entryId: Int,
    onDismiss: () -> Unit
) {
    // State for the entry's current lists
    val listsForEntry by viewModel.getListsForEntry(entryId).observeAsState(emptyList())

    // State for all available lists
    val allLists by viewModel.allLists.observeAsState(emptyList())

    // State for creating a new list
    var newListName by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Lists") },
        text = {
            Column {
                // Button to create new list
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("➕ Create New List")
                }

                Spacer(Modifier.height(16.dp))

                // List of all available lists with checkboxes
                LazyColumn {
                    items(allLists) { list ->
                        val isInList = listsForEntry.any { it.id == list.id }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Checkbox(
                                checked = isInList,
                                onCheckedChange = { checked ->
                                    coroutineScope.launch {
                                        viewModel.toggleEntryInList(
                                            entryId,
                                            list.id
                                        )
                                    }
                                }
                            )
                            Text(
                                text = list.name,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )

    // Dialog for creating new lists
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New List Name") },
            text = {
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("Enter list name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.createList(newListName)
                            newListName = ""
                            showCreateDialog = false
                        }
                    },
                    enabled = newListName.isNotBlank()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}