package com.example.darijadict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.outlined.Queue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.darijadict.data.Entry
import com.example.darijadict.util.playAssetAudio
import com.example.darijadict.viewmodel.EntryViewModel

@Composable
fun EntryDetailScreen(
    entry: Entry,
    viewModel: EntryViewModel
) {
    val context = LocalContext.current
    val savedEntries by viewModel.savedWords.observeAsState(emptyList())
    val isSaved = savedEntries.any { it.id == entry.id }

    var showListDialog by remember { mutableStateOf(false) }

    var iconModifier = Modifier.size(28.dp)

    val darkGray = Color(0xFF444444)
    val darkGreen = Color(0xFF006400)
    val brightPink = Color(0xFFFF69B4)
    val blue = Color(0xFF0000FF)
    val black = Color(0xFF000000)
    val brightRed = Color(0xFFFF0000)
    val darkBlue = Color(0xFF00008B)

    val iconColors = mapOf(
        "meaning" to darkGray,
        "arabic" to darkGreen,
        "brightPink" to brightPink,
        "blue" to blue,
        "black" to black,
        "brightRed" to brightRed,
        "darkBlue" to darkBlue
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Word and Pronunciation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            entry.pronunciation?.takeIf { it.isNotBlank() }?.let {

                IconButton(onClick = { playAssetAudio(context, it) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Pronunciation",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
            Text(
                text = entry.word,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        // Meaning
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Meaning", tint = iconColors["meaning"]!!, modifier = iconModifier)
            Spacer(Modifier.width(8.dp))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Meaning: ") }
                    append(entry.meaning)
                },
                fontSize = 20.sp
            )
        }

        // Part of Speech
        entry.pos?.takeIf { it.isNotBlank() }?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = "Part of Speech", tint = MaterialTheme.colorScheme.secondary, modifier = iconModifier)
                Spacer(Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Part of Speech: ") }
                        append(it)
                    },
                    fontSize = 20.sp
                )
            }
        }

        // Arabic Script
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Translate, contentDescription = "Arabic Script", tint = iconColors["arabic"]!!, modifier = iconModifier)
            Spacer(Modifier.width(8.dp))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Arabic Script: ") }
                    append(entry.arabicScript)
                },
                fontSize = 20.sp
            )
        }

        // Plural
        entry.plural?.takeIf { it.isNotBlank() }?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Group, contentDescription = "Plural", tint = black, modifier = iconModifier)
                Spacer(Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Plural: ") }
                        append(it)
                    },
                    fontSize = 20.sp
                )
            }
        }

        // Present
        entry.present?.takeIf { it.isNotBlank() }?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = "Present", tint = iconColors["black"]!!, modifier = iconModifier)
                Spacer(Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Present: ") }
                        append(it)
                    },
                    fontSize = 20.sp
                )
            }
        }

        // fs
        entry.fs?.takeIf { it.isNotBlank() }?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Female, contentDescription = "Feminine Singular", tint = iconColors["brightPink"]!!, modifier = iconModifier)
                Spacer(Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Feminine Singular: ") }
                        append(it)
                    },
                    fontSize = 20.sp
                )
            }
        }

        // mp
        entry.mp?.takeIf { it.isNotBlank() }?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Male, contentDescription = "Masculine Plural", tint = iconColors["blue"]!!, modifier = iconModifier)
                Icon(Icons.Default.Male, contentDescription = null, tint = iconColors["blue"]!!, modifier = iconModifier)
                Spacer(Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Masculine Plural: ") }
                        append(it)
                    },
                    fontSize = 20.sp
                )
            }
        }

        // fp
        entry.fp?.takeIf { it.isNotBlank() }?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Female, contentDescription = "Feminine Plural", tint = iconColors["brightPink"]!!, modifier = iconModifier)
                Icon(Icons.Default.Female, contentDescription = null, tint = iconColors["brightPink"]!!, modifier = iconModifier)
                Spacer(Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Feminine Plural: ") }
                        append(it)
                    },
                    fontSize = 20.sp
                )
            }
        }

        // Usage (Darija)
        entry.uses?.takeIf { it.isNotBlank() }?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Flag, contentDescription = "Darija Usage", tint = iconColors["brightRed"]!!, modifier = iconModifier)
                Spacer(Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Usage (Darija): ") }
                        append(it)
                    },
                    fontSize = 20.sp
                )
            }
        }

        // Usage Pronunciation
        entry.usesPronunciation?.takeIf { it.isNotBlank() }?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { playAssetAudio(context, it) }) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = "Play usage pronunciation",
                        tint = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = iconModifier

                    )
                }
                Text(
                    text = "Play usage audio",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Usage (English)
        entry.example?.takeIf { it.isNotBlank() }?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Flag, contentDescription = "Usage (English)", tint = iconColors["darkBlue"]!!, modifier = iconModifier)
                Spacer(Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Usage (English): ") }
                        append(it)
                    },
                    fontSize = 20.sp
                )
            }
        }

        // Spacer between content and buttons
        Spacer(Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.toggleSave(entry.id, isSaved) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkAdd,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isSaved) "Saved" else "Save")
            }

            Button(
                onClick = { showListDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Outlined.Queue, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add to List")
            }
        }

        if (showListDialog) {
            AddToListDialog(
                viewModel = viewModel,
                entryId = entry.id,
                onDismiss = { showListDialog = false }
            )
        }
    }
}
