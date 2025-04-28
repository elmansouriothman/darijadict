package com.example.darijadict.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.IconButtonDefaults
import com.example.darijadict.data.Entry
import com.example.darijadict.viewmodel.EntryViewModel
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun EntryCard(
    entry: Entry,
    viewModel: EntryViewModel,
    onClick: () -> Unit,
    onAddToListClick: () -> Unit // New parameter for list addition
) {
    val savedEntries by viewModel.savedWords.observeAsState(emptyList())
    val isSaved = remember(savedEntries) { savedEntries.any { it.id == entry.id } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .fillMaxHeight(0.9f)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = entry.word,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(9.dp))
                Text(
                    text = entry.arabicScript,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(9.dp))
                Text(text = entry.meaning, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Column {
                // Bookmark Icon Button
                IconButton(
                    onClick = { viewModel.toggleSave(entry.id, isSaved) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    interactionSource = remember { MutableInteractionSource() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkAdd,
                        contentDescription = if (isSaved) "Unsave" else "Save",
                        tint = if (isSaved) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Add to list button
                IconButton(
                    onClick = { onAddToListClick() }, // Call the passed function
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Queue,
                        contentDescription = "Add to list",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}