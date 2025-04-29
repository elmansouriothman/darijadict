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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.darijadict.data.Entry
import com.example.darijadict.viewmodel.EntryViewModel

@Composable
fun EntryCard(
    entry: Entry,
    viewModel: EntryViewModel,
    onClick: () -> Unit,
    onAddToListClick: () -> Unit
) {
    val savedEntries by viewModel.savedWords.observeAsState(emptyList())
    val isSaved = remember(savedEntries) { savedEntries.any { it.id == entry.id } }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = entry.word,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF37474F)
                    )
                )

                if (entry.arabicScript.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.arabicScript,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            color = Color(0xFF0066CC)
                        )
                    )
                }

                if (entry.meaning.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = entry.meaning,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF546E7A)
                        )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { viewModel.toggleSave(entry.id, isSaved) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.DarkGray
                    ),
                    interactionSource = remember { MutableInteractionSource() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkAdd,
                        contentDescription = if (isSaved) "Unsave" else "Save",
                        tint = if (isSaved) Color(0xFF81C784) else Color(0xFFC8E6C9)

                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                IconButton(
                    onClick = onAddToListClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    interactionSource = remember { MutableInteractionSource() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Queue,
                        contentDescription = "Add to List",
                        tint = Color(0xFF81C784)
                    )
                }
            }
        }
    }
}
