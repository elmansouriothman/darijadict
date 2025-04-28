package com.example.darijadict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.darijadict.data.Entry
import com.example.darijadict.viewmodel.EntryViewModel

@Composable
fun SavedScreen(
    viewModel: EntryViewModel,
    onEntryClick: (Entry) -> Unit,
    navController: NavController
) {
    val savedEntries by viewModel.savedWords.observeAsState(emptyList())
    EntryListScreen(
        title = "⭐ Saved Words",
        entries = savedEntries,
        viewModel = viewModel,
        onEntryClick = onEntryClick
    )
}
