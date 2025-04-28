package com.example.darijadict.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.darijadict.data.CustomList
import com.example.darijadict.util.ApkgDownloader
import com.example.darijadict.viewmodel.EntryViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ListsScreen(
    viewModel: EntryViewModel,
    onCreateList: (Int) -> Unit,
    onListClick: (Int) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val allLists by viewModel.allLists.observeAsState(emptyList())
    var showCreateDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var listToDelete by remember { mutableStateOf<CustomList?>(null) }
    var listToEmpty by remember { mutableStateOf<CustomList?>(null) }

    // Permission handling
    var hasStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasStoragePermission = isGranted
        Log.d("ListsScreen", "Permission granted: $hasStoragePermission")
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ){
            Icon(
                Icons.AutoMirrored.Filled.FormatListBulleted,
                "Lists", modifier = Modifier.width(32.dp).height(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Lists",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Start
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showCreateDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(Modifier.width(8.dp))
                Text("Create New List")
            }

            Button(
                onClick = {
                    if (hasStoragePermission) {
                        val fileName = "darija_deck.apkg"
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val file = File(downloadsDir, fileName)

                        try {

                            val (success, message) = ApkgDownloader.downloadApkg(context) // Ensure this returns Pair<Boolean, String>

                            if (success) {
                                file.writeText(message) // Assuming message contains the content to write
                                Toast.makeText(context, "File saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
                                Log.d("ListsScreen", "File saved to ${file.absolutePath}");
                            } else {
                                Toast.makeText(context, "Failed to download file: $message", Toast.LENGTH_SHORT).show()
                                Log.e("ListsScreen", "Failed to download file: $message");
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to save file: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.e("ListsScreen", "Failed to save file: ${e.message}");
                        }
                    } else {
                        launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = "Download")
                Spacer(Modifier.width(8.dp))
                Text("Download .apkg")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Match the card elevation
        ) {
            Button(
                onClick = {
                    navController.navigate("saved") // Ensure this matches your navigation route
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant, // Match color with EntryCard
                    contentColor = Color.Black // Adjust text color accordingly
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) // Adjust padding to match the button style in EntryCard
            ) {

                Icon(Icons.Filled.Bookmarks, contentDescription = "Saved")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saved", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(1f))
            }
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
            Icon(Icons.Filled.EditNote, contentDescription = "Playlist")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = list.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),

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