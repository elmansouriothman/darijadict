package com.example.darijadict.ui

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.darijadict.viewmodel.EntryViewModel
import com.example.darijadict.viewmodel.SearchCategory
import com.example.darijadict.data.Entry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DictionaryScreen(
    viewModel: EntryViewModel = viewModel(),
    onEntryClick: (Entry) -> Unit = {}
) {
    val allEntries by viewModel.getAll().observeAsState(emptyList())
    val searchQuery = remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val selectedCategory = remember { mutableStateOf(SearchCategory.ALL) }

    val filtered = remember(allEntries, searchQuery.value, selectedCategory.value) {
        val query = searchQuery.value.lowercase()
        allEntries.filter { entry ->
            when (selectedCategory.value) {
                SearchCategory.ALL -> listOf(
                    entry.word, entry.meaning, entry.example, entry.arabicScript,
                    entry.plural, entry.present, entry.fs, entry.mp, entry.fp, entry.uses
                ).any { it?.contains(query, ignoreCase = true) == true }

                SearchCategory.DARIJA -> listOf(
                    entry.word, entry.plural, entry.present, entry.fs, entry.mp, entry.fp, entry.uses
                ).any { it?.contains(query, ignoreCase = true) == true }

                SearchCategory.ENGLISH -> listOf(entry.meaning, entry.example)
                    .any { it?.contains(query, ignoreCase = true) == true }

                SearchCategory.ARABIC -> entry.arabicScript.contains(query, ignoreCase = true)
            }
        }.sortedBy { it.word.lowercase() }
    }

    val permissionState = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Light soft background
            .padding(16.dp)
    ) {
        // Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Dictionary",
                tint = Color(0xFF0D47A1),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Dictionary",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                ),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search and Dropdown
        Row(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                TextButton(
                    onClick = { expanded.value = true },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        selectedCategory.value.displayName,
                        color = Color(0xFF0066CC)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    SearchCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.displayName) },
                            onClick = {
                                selectedCategory.value = category
                                expanded.value = false
                                viewModel.updateCategory(category)
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = {
                    searchQuery.value = it
                    viewModel.updateQuery(it)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.surface
                ),
                placeholder = { Text("Search...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List of entries
        Column(modifier = Modifier.fillMaxSize()) {
            EntryListScreen(
                title = "",
                entries = filtered,
                viewModel = viewModel,
                onEntryClick = onEntryClick
            )
        }
    }
}
