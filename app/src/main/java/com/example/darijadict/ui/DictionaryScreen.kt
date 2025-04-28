package com.example.darijadict.ui

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.darijadict.viewmodel.EntryViewModel
import com.example.darijadict.viewmodel.SearchCategory
import com.example.darijadict.data.Entry
import androidx.compose.ui.text.style.TextAlign

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
                Icons.AutoMirrored.Filled.MenuBook,
                "Dictionary", modifier = Modifier.width(32.dp).height(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Dictionary",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(50)
                )
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                TextButton(
                    onClick = { expanded.value = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(selectedCategory.value.displayName)
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
