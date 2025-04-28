package com.example.darijadict.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.darijadict.ui.*
import com.example.darijadict.viewmodel.EntryViewModel

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
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "guide",
                    onClick = {
                        navController.navigate("guide") {
                            Log.d("Navigation", "Navigating to: guide")
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Info, contentDescription = "Guide") },
                    label = { Text("Guide") }
                )


                NavigationBarItem(
                    selected = currentRoute == "dictionary",
                    onClick = {
                        navController.navigate("dictionary") {
                            Log.d("Navigation", "Navigating to: dictionary")
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Dictionary") },
                    label = { Text("Dictionary") }
                )
                NavigationBarItem(
                    selected = currentRoute == "lists",
                    onClick = {
                        navController.navigate("lists") {
                            Log.d("Navigation", "Navigating to: lists")
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Lists") },
                    label = { Text("Lists") }
                )
                NavigationBarItem(
                    selected = currentRoute == "saved",
                    onClick = {
                        navController.navigate("saved") {
                            Log.d("Navigation", "Navigating to: saved")
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Bookmarks, contentDescription = "Saved") },
                    label = { Text("Saved") }
                )
                NavigationBarItem(
                    selected = currentRoute == "grammar",
                    onClick = {
                        navController.navigate("grammar") {
                            Log.d("Navigation", "Navigating to: grammar")
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Recommend, contentDescription = "Grammar") },
                    label = { Text("Grammar") }
                )
                NavigationBarItem(
                    selected = currentRoute == "recommendations",
                    onClick = {
                        navController.navigate("recommendations") {
                            Log.d("Navigation", "Navigating to: recommendations")
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Filled.Link,
                            contentDescription = "Recommendations"
                        )
                    },
                    alwaysShowLabel = true,
                    label = { Text("Links") }
                )
                // Remove the Custom List button
                /* NavigationBarItem(
                    selected = currentRoute == "customList",
                    onClick = {
                        navController.navigate("customList") {
                            Log.d("Navigation", "Navigating to: customList")
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Filled.List, contentDescription = "Custom List") },
                    label = { Text("Custom List") }
                ) */
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
                    onListClick = { listId -> navController.navigate("customList/$listId") }
                )
            }
            composable("saved") {
                SavedScreen(
                    viewModel = viewModel,
                    onEntryClick = { entry -> navController.navigate("detail/${entry.id}") }
                )
            }
            composable("grammar") {
                GrammarLessonScreen(navController = navController)
            }
            composable("recommendations") {
                RecommendationsScreen(navController = navController)
            }
            composable("guide") {
                GuideScreen(navController = navController)
            }
            composable("customList") { 
                CustomListScreen(
                    listId = -1, 
                    viewModel = viewModel,
                    onEntryClick = { /* handle entry click */ },
                    onNavigateBack = { /* handle navigate back */ },
                    context = LocalContext.current 
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
                        },
                        context = LocalContext.current
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
                WebViewScreen(navController = navController, url = url)
            }
        }
    }
}