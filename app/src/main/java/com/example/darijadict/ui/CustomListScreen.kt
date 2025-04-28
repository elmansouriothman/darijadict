package com.example.darijadict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.darijadict.data.Entry
import com.example.darijadict.viewmodel.EntryViewModel
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.compose.ui.platform.LocalContext
import java.io.File
import android.os.Environment

sealed interface DialogType {
    data object Delete : DialogType
    data object Empty : DialogType
}

@Composable
fun CustomListScreen(
    listId: Int,
    viewModel: EntryViewModel,
    onEntryClick: (Entry) -> Unit,
    onNavigateBack: () -> Unit,
    context: Context
) {
    // Check if permission is granted
    val context = LocalContext.current
    val permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(permissionGranted) {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(
                context as android.app.Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

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

        Button(
            onClick = { exportList(entries, listName, context) },
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
        ) {
            Text("Export List")
        }

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

private fun exportList(entries: List<Entry>, listName: String, context: Context) {
    val fileContent = buildString {
        append("#separator:semicolon\n")
        append("#html:true\n")
        entries.forEach { entry ->
            // Format each field, leaving empty if null or empty
            val word = entry.word ?: ""
            val pos = entry.pos ?: ""
            val plural = entry.plural ?: ""
            val present = entry.present ?: ""
            val fs = entry.fs ?: ""
            val mp = entry.mp ?: ""
            val fp = entry.fp ?: ""
            val arabicScript = entry.arabicScript ?: ""
            val meaning = entry.meaning ?: ""
            val uses = entry.uses ?: ""
            val example = entry.example ?: ""
            val pronunciationFormatted = entry.pronunciation?.let { "[sound:$it]" } ?: ""
            val usesPronunciationFormatted = entry.usesPronunciation?.let { "[sound:$it]" } ?: ""

            // Build the line without a trailing semicolon
            append("$word;$pos;$plural;$present;$fs;$mp;$fp;$arabicScript;$meaning;$uses;$example;$pronunciationFormatted;$usesPronunciationFormatted\n")
        }
    }

    // Save file logic here
    val fileName = "$listName.txt" // Use the list name as the filename
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, fileName)

    try {
        file.writeText(fileContent)
        Toast.makeText(context, "Exported to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to export: ${e.message}", Toast.LENGTH_SHORT).show()
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
