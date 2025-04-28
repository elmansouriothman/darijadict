package com.example.darijadict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.darijadict.data.Entry
import com.example.darijadict.viewmodel.EntryViewModel

sealed interface DialogType {
    data object Delete : DialogType
    data object Empty : DialogType
}

@Composable
fun CustomListScreen(
    listId: Int,
    viewModel: EntryViewModel,
    onEntryClick: (Entry) -> Unit,
    onNavigateBack: () -> Unit
) {
    // Validate listId early
    if (listId == -1) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    // States
    val entries by viewModel.getEntriesInList(listId).observeAsState(emptyList())
    val listName by viewModel.getListName(listId).observeAsState("Custom List")
    var showDialog by remember { mutableStateOf<DialogType?>(null) }

    // Guard: List not found (deleted)
    if (listName.isEmpty()) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top title & action buttons
        ListHeader(
            listName = listName,
            onEmptyClick = { showDialog = DialogType.Empty },
            onDeleteClick = { showDialog = DialogType.Delete }
        )

        Spacer(Modifier.height(8.dp))

        // List entries
        EntryListScreen(
            title = "",
            entries = entries,
            viewModel = viewModel,
            onEntryClick = onEntryClick
        )

        // Dialogs
        when (showDialog) {
            DialogType.Delete -> DeleteListDialog(
                onConfirm = {
                    viewModel.deleteList(listId)
                    showDialog = null
                    onNavigateBack()
                },
                onDismiss = { showDialog = null }
            )
            DialogType.Empty -> EmptyListDialog(
                onConfirm = {
                    viewModel.clearList(listId)
                    showDialog = null
                },
                onDismiss = { showDialog = null }
            )
            null -> Unit
        }
    }
}

@Composable
private fun ListHeader(
    listName: String,
    onEmptyClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column {
        Text(
            text = listName,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onEmptyClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Empty List")
            }
            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete List")
            }
        }
    }
}

@Composable
private fun DeleteListDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete this list?") },
        text = { Text("This will permanently delete the list and all saved links to it.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EmptyListDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Empty this list?") },
        text = { Text("This will remove all words from this list. The list itself will remain.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Empty")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
