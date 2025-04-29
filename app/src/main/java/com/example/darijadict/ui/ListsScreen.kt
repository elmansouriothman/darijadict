package com.example.darijadict.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.darijadict.data.CustomList
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.ui.unit.sp
import com.example.darijadict.util.ApkgDownloader
import com.example.darijadict.viewmodel.EntryViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CreateListDialog(
    newListName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New List") },
        text = {
            OutlinedTextField(
                value = newListName,
                onValueChange = onNameChange,
                singleLine = true,
                label = { Text("List name") }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
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

@Composable
fun ListItemCard(
    list: CustomList,
    onListClick: (Int) -> Unit,
    onOpen: () -> Unit,
    onEmpty: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable { onOpen() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = "Custom List",
                    tint = Color(0xFF0066CC)
                )
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF0066CC)
                    )

                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEmpty) {
                    Icon(
                        imageVector = Icons.Default.SpaceBar,
                        contentDescription = "Empty",
                        tint = Color(0xFF81C784)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFF81C784)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    viewModel: EntryViewModel,
    onCreateList: (Int) -> Unit,
    onListClick: (Int) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val allLists by viewModel.allLists.observeAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    var listToDelete by remember { mutableStateOf<CustomList?>(null) }
    var listToEmpty by remember { mutableStateOf<CustomList?>(null) }

    var hasStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasStoragePermission = isGranted }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
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
                Icons.AutoMirrored.Filled.FormatListBulleted,
                contentDescription = "Lists",
                tint = Color(0xFF0D47A1),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Lists",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                ),
                textAlign = TextAlign.Start
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showCreateDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(Modifier.width(8.dp))
                Text("New List")
            }

            Button(
                onClick = {
                    if (hasStoragePermission) {
                        coroutineScope.launch {
                            try {
                                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                val file = File(downloadsDir, "darija_deck.zip")
                                val (success, message) = ApkgDownloader.downloadApkg(context)

                                if (success) {
                                    file.writeText(message)
                                    Toast.makeText(context, "Saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
                                    Log.d("ListsScreen", "Saved to ${file.absolutePath}")
                                } else {
                                    Toast.makeText(context, "Download failed: $message", Toast.LENGTH_SHORT).show()
                                    Log.e("ListsScreen", "Download failed: $message")
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("ListsScreen", "Error: ${e.message}")
                            }
                        }
                    } else {
                        launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = "Download")
                Spacer(Modifier.width(8.dp))
                Text(".apkg")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp)
                .clickable { navController.navigate("saved") },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmarks,
                    contentDescription = "Saved", tint = Color(0xFF0066CC)


                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Saved",
                    style = MaterialTheme.typography.titleMedium.copy(
                        Color(0xFF0066CC)
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "", modifier = Modifier.padding(end = 10.dp))


            }
        }



        Spacer(modifier = Modifier.height(20.dp))

        if (allLists.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No lists yet. Create your first one!",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.DarkGray)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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

    listToDelete?.let { list ->
        ConfirmDialog(
            title = "Delete this list?",
            text = "This will permanently delete '${list.name}'.",
            onConfirm = {
                coroutineScope.launch { viewModel.deleteList(list.id) }
                listToDelete = null
            },
            onDismiss = { listToDelete = null }
        )
    }

    listToEmpty?.let { list ->
        ConfirmDialog(
            title = "Empty this list?",
            text = "All entries in '${list.name}' will be removed.",
            onConfirm = {
                coroutineScope.launch { viewModel.clearList(list.id) }
                listToEmpty = null
            },
            onDismiss = { listToEmpty = null }
        )
    }

    if (showCreateDialog) {
        CreateListDialog(
            newListName = newListName,
            onNameChange = { newListName = it },
            onConfirm = {
                coroutineScope.launch {
                    viewModel.createList(newListName)
                    val lastId = viewModel.allLists.value?.lastOrNull()?.id
                    lastId?.let { onCreateList(it) }
                    newListName = ""
                    showCreateDialog = false
                }
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}


@Composable
private fun ConfirmDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
