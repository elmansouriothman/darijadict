package com.example.darijadict

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.darijadict.navigation.AppNavHost
import com.example.darijadict.viewmodel.EntryViewModel
import com.example.darijadict.ui.theme.DarijaTheme

class MainActivity : ComponentActivity() {

    private val viewModel: EntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DarijaTheme {
                AppNavHost(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}