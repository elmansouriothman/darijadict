package com.example.darijadict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.darijadict.viewmodel.EntryViewModel

@Composable
fun AddToListDialog(
    viewModel: EntryViewModel,
    entryId: Int,
    onDismiss: () -> Unit
) {
    val allLists by viewModel.allLists.observeAsState(emptyList())
    val listsForEntry by viewModel.getListsForEntry(entryId).observeAsState(emptyList())
    var showCreateDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to List") },
        text = {
            Column {
                if (allLists.isEmpty()) {
                    Text(
                        "No lists available. Create one first!",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn {
                        items(allLists) { list ->
                            val isInList = listsForEntry.any { it.id == list.id }
                            ListItem(
                                headlineContent = { Text(list.name) },
                                trailingContent = {
                                    Checkbox(
                                        checked = isInList,
                                        onCheckedChange = { _ ->
                                            viewModel.toggleEntryInList(entryId, list.id)
                                        }
                                    )
                                }
                            )
                            HorizontalDivider(thickness = 1.dp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            Button(onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
            ) {
                Text("New List")
            }
        }
    )

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
                        viewModel.createList(newListName)
                        newListName = ""
                        showCreateDialog = false
                    },
                    enabled = newListName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCreateDialog = false
                              }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
                    )
                { Text("Cancel") }
            }
        )
    }
}

