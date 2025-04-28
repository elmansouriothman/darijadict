package com.example.darijadict.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
inline fun <reified VM : ViewModel> viewModelScope(
    crossinline block: suspend () -> Unit
) {
    val viewModel = viewModel<VM>()
    LaunchedEffect(key1 = viewModel) {
        block()
    }
}