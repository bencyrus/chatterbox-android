package io.glovee.chatterbox.UI.Views

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.layout.padding
import io.glovee.chatterbox.Core.Security.TokenManager

sealed class TabDestination(val route: String) {
    data object Home: TabDestination("home")
    data object Settings: TabDestination("settings")
}

@Composable
fun RootTabView(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val ctx = LocalContext.current
    val items = listOf(TabDestination.Home, TabDestination.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    val label = when (item) {
                        TabDestination.Home -> Strings.Tabs.home(ctx)
                        TabDestination.Settings -> Strings.Tabs.settings(ctx)
                    }
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        icon = {
                            when (item) {
                                TabDestination.Home -> Icon(Icons.Default.Home, contentDescription = label)
                                TabDestination.Settings -> Icon(Icons.Default.Settings, contentDescription = label)
                            }
                        },
                        label = { Text(label) },
                        selected = selected,
                        onClick = {
                            if (!selected) navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = TabDestination.Home.route, modifier = Modifier.padding(padding)) {
            composable(TabDestination.Home.route) {
                HomeView(tokenManager)
            }
            composable(TabDestination.Settings.route) {
                SettingsView(tokenManager)
            }
        }
    }
}
