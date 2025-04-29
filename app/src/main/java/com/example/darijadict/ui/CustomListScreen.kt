package com.example.darijadict.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.darijadict.data.Entry
import com.example.darijadict.viewmodel.EntryViewModel
import java.io.File
import android.Manifest
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.ui.unit.sp

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
    val context = LocalContext.current
    val permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
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

    if (listId == -1) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    val entries by viewModel.getEntriesInList(listId).observeAsState(emptyList())
    val listName by viewModel.getListName(listId).observeAsState("Custom List")
    var showDialog by remember { mutableStateOf<DialogType?>(null) }

    if (listName.isEmpty()) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Soft background
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Icon(
                Icons.Default.EditNote,
                contentDescription = "Custom List",
                tint = Color(0xFF0D47A1),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = listName,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1),
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showDialog = DialogType.Empty },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
            ) {
                Text("Empty List")
            }
            Button(
                onClick = { showDialog = DialogType.Delete },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
            ) {
                Text("Delete List")
            }
        }

        Button(
            onClick = { exportList(entries, listName, context) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
        ) {
            Text("Export List")
        }

        Spacer(modifier = Modifier.height(16.dp))

        EntryListScreen(
            title = "",
            entries = entries,
            viewModel = viewModel,
            onEntryClick = onEntryClick
        )
    }

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

private fun exportList(entries: List<Entry>, listName: String, context: Context) {
    val fileContent = buildString {
        append("#separator:semicolon\n")
        append("#html:true\n")
        entries.forEach { entry ->
            val word = entry.word
            val pos = entry.pos ?: ""
            val plural = entry.plural ?: ""
            val present = entry.present ?: ""
            val fs = entry.fs ?: ""
            val mp = entry.mp ?: ""
            val fp = entry.fp ?: ""
            val arabicScript = entry.arabicScript
            val meaning = entry.meaning
            val uses = entry.uses ?: ""
            val example = entry.example ?: ""
            val pronunciationFormatted = entry.pronunciation?.let { "[sound:$it]" } ?: ""
            val usesPronunciationFormatted = entry.usesPronunciation?.let { "[sound:$it]" } ?: ""

            append("$word;$pos;$plural;$present;$fs;$mp;$fp;$arabicScript;$meaning;$uses;$example;$pronunciationFormatted;$usesPronunciationFormatted\n")
        }
    }

    val fileName = "$listName.txt"
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
private fun DeleteListDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete this list?") },
        text = { Text("This will permanently delete the list and all saved links to it.") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
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
            TextButton(onClick = onConfirm) { Text("Empty") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
