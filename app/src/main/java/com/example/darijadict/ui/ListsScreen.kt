package com.example.darijadict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.darijadict.data.CustomList
import com.example.darijadict.viewmodel.EntryViewModel
import kotlinx.coroutines.launch

@Composable
fun ListsScreen(
    viewModel: EntryViewModel,
    onCreateList: (Int) -> Unit,
    onListClick: (Int) -> Unit
) {
    val allLists by viewModel.allLists.observeAsState(emptyList())
    var showCreateDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var listToDelete by remember { mutableStateOf<CustomList?>(null) }
    var listToEmpty by remember { mutableStateOf<CustomList?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "📚 Your Lists",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Spacer(Modifier.width(8.dp))
            Text("Create New List")
        }

        Spacer(Modifier.height(16.dp))

        if (allLists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No lists yet. Create your first list!")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allLists) { list ->
                    ListItemCard(
                        list = list,
                        onListClick = onListClick,
                        onOpen = { onListClick(list.id) },
                        onEmpty = { listToEmpty = list },
                        onDelete = { listToDelete = list }
                    )
                }
            }
        }
    }

    // Confirm Delete Dialog
    listToDelete?.let { list ->
        AlertDialog(
            onDismissRequest = { listToDelete = null },
            title = { Text("Delete this list?") },
            text = { Text("This will permanently delete '${list.name}' and its associations.") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch { viewModel.deleteList(list.id) }
                    listToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { listToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirm Empty Dialog
    listToEmpty?.let { list ->
        AlertDialog(
            onDismissRequest = { listToEmpty = null },
            title = { Text("Empty this list?") },
            text = { Text("All entries in '${list.name}' will be removed, but the list will remain.") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch { viewModel.clearList(list.id) }
                    listToEmpty = null
                }) {
                    Text("Empty")
                }
            },
            dismissButton = {
                TextButton(onClick = { listToEmpty = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCreateDialog) {
        CreateListDialog(
            newListName = newListName,
            onNameChange = { newListName = it },
            onConfirm = {
                coroutineScope.launch {
                    viewModel.createList(newListName)
                    viewModel.allLists.value?.lastOrNull()?.id?.let { onCreateList(it) }
                    newListName = ""
                    showCreateDialog = false
                }
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
private fun ListItemCard(
    list: CustomList,
    onListClick: (Int) -> Unit,
    onOpen: () -> Unit,
    onEmpty: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        onClick = { onListClick(list.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Open") },
                        onClick = {
                            menuExpanded = false
                            onOpen()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Empty List") },
                        onClick = {
                            menuExpanded = false
                            onEmpty()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete List") },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateListDialog(
    newListName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New List Name") },
        text = {
            OutlinedTextField(
                value = newListName,
                onValueChange = onNameChange,
                label = { Text("Enter list name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = newListName.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
