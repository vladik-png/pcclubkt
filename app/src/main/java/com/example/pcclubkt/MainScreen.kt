package com.example.pcclubkt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(db: AppDatabase) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen()
            }
            composable("statistics") {
                StatisticsScreen()
            }

            composable("clients") {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Клієнти")
                }
            }

            composable("settings") {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Налаштування")
                }
            }
            composable("tickets") {
                Box(Modifier.fillMaxSize()) {
                    Text("Тікети")
                }
            }
            composable("events")
            {
                PcGridScreen(pcDao = db.pcDao())
            }
//            composable("events") {
//                Box(Modifier.fillMaxSize()) {
//                    Text("Івенти")
//                }
//            }

//            composable("pc_grid")
//            {
//                PcGridScreen(pcDao = db.pcDao())
//            }
        }
    }
}