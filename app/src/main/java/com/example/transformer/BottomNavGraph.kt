package com.example.transformer

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.transformer.screen.HomeScreen
import com.example.transformer.screen.ScanPage
import com.example.transformer.screen.ToolsPage


@Composable
fun BottomNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.tittle
    ) {

        composable(route = BottomBarScreen.Home.tittle) {
            HomeScreen(navController)
        }
        composable(route = BottomBarScreen.Scan.tittle) {
            ScanPage()
        }
        composable(route = BottomBarScreen.Tools.tittle) {
            ToolsPage()
        }
    }
}