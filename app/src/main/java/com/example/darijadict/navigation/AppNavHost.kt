package com.example.darijadict.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.darijadict.ui.*
import com.example.darijadict.viewmodel.EntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    viewModel: EntryViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = currentRoute == "guide",
                    onClick = {
                        navController.navigate("guide") {
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Guide",
                            tint = if (currentRoute == "guide") Color(0xFF37474F) else Color(0xFF37474F)
                        )
                    },
                    label = { Text("Guide") }
                )
                NavigationBarItem(
                    selected = currentRoute == "dictionary",
                    onClick = {
                        navController.navigate("dictionary") {
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Dictionary",
                            tint = if (currentRoute == "dictionary") Color(0xFF37474F) else Color(0xFF37474F)
                        )
                    },
                    label = { Text("Dictionary") }
                )
                NavigationBarItem(
                    selected = currentRoute == "lists",
                    onClick = {
                        navController.navigate("lists") {
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Lists",
                            tint = if (currentRoute == "lists") Color(0xFF37474F) else Color(0xFF37474F)
                        )
                    },
                    label = { Text("Lists") }
                )
                NavigationBarItem(
                    selected = currentRoute == "grammar",
                    onClick = {
                        navController.navigate("grammar") {
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Construction,
                            contentDescription = "Grammar",
                            tint = if (currentRoute == "grammar") Color(0xFF37474F) else Color(0xFF37474F)
                        )
                    },
                    label = { Text("Grammar") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dictionary",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dictionary") {
                DictionaryScreen(
                    viewModel = viewModel,
                    onEntryClick = { entry -> navController.navigate("detail/${entry.id}") }
                )
            }
            composable("lists") {
                ListsScreen(
                    viewModel = viewModel,
                    onCreateList = { navController.navigate("lists") { popUpTo("lists") { inclusive = true }; launchSingleTop = true } },
                    onListClick = { listId -> navController.navigate("customList/$listId") },
                    navController = navController
                )
            }
            composable("grammar") {
                GrammarLessonScreen(navController = navController)
            }
            composable("guide") {
                GuideScreen(navController = navController)
            }
            composable("customList") {
                CustomListScreen(
                    listId = -1,
                    viewModel = viewModel,
                    onEntryClick = {},
                    onNavigateBack = {}
                )
            }
            composable(
                "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                val entry by viewModel.getEntryById(id).observeAsState()
                EntryDetailScreen(entry = entry ?: return@composable, viewModel = viewModel)
            }
            composable(
                "customList/{listId}",
                arguments = listOf(navArgument("listId") { type = NavType.IntType })
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getInt("listId") ?: -1
                if (listId != -1) {
                    CustomListScreen(
                        listId = listId,
                        viewModel = viewModel,
                        onEntryClick = { entry -> navController.navigate("detail/${entry.id}") },
                        onNavigateBack = {
                            navController.popBackStack()
                            navController.navigate("lists")
                        }
                    )
                } else {
                    navController.popBackStack()
                    navController.navigate("lists")
                }
            }
            composable(
                "webView/{url}",
                arguments = listOf(navArgument("url") { type = NavType.StringType })
            ) { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url") ?: ""
                WebViewScreen(url = url)
            }
            composable("recommendations") {
                RecommendationsScreen(navController = navController)
            }
            composable("saved") {
                SavedScreen(
                    viewModel = viewModel,
                    onEntryClick = { entry -> navController.navigate("detail/${entry.id}") }
                )
            }
        }
    }
}
