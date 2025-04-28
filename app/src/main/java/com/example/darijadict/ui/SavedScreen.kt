package com.example.darijadict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.darijadict.data.Entry
import com.example.darijadict.viewmodel.EntryViewModel

@Composable
fun SavedScreen(
    viewModel: EntryViewModel,
    onEntryClick: (Entry) -> Unit
) {
    val savedEntries by viewModel.savedWords.observeAsState(emptyList())
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
                Icons.Filled.Bookmarks,
                "Saved", modifier = Modifier.width(32.dp).height(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Saved",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Start
            )
        }
        EntryListScreen(
            title = "",
            entries = savedEntries,
            viewModel = viewModel,
            onEntryClick = onEntryClick
        )
    }
}
