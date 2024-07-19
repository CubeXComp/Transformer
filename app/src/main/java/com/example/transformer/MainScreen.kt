package com.example.transformer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {

    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomBar(navController = navController) }) {
        BottomNavGraph(navController = navController)
    }
}
@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(BottomBarScreen.Home, BottomBarScreen.Scan, BottomBarScreen.Tools)

    val navStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->

            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }

    }
}

@Composable
fun RowScope.AddItem(screen: BottomBarScreen, currentDestination: NavDestination?, navController: NavHostController) {
    BottomNavigationItem(
        label = { Text(text = screen.tittle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        icon = { Icon(imageVector =
        if (currentDestination?.hierarchy?.any { it.route == screen.tittle } == true) {
                screen.selectedIcon
            }
        else {
                screen.unselectedIcon
            },

                contentDescription = "Navigation Icon")
        },
        selectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedContentColor = MaterialTheme.colorScheme.outline,
        selected = currentDestination?.hierarchy?.any { it.route == screen.tittle } == true,
        onClick = { navController.navigate(screen.tittle) }

    )
}