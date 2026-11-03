package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.FirebaseDemoScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RoomDemoScreen
import com.example.ui.screens.SharedPrefsDemoScreen
import com.example.viewmodel.FirebaseAuthViewModel
import com.example.viewmodel.SettingsViewModel
import com.example.viewmodel.TaskViewModel

object AppDestinations {
    const val HOME = "home"
    const val SHARED_PREFS = "shared_prefs_demo"
    const val ROOM = "room_demo"
    const val FIREBASE = "firebase_demo"
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    settingsViewModel: SettingsViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel(),
    firebaseAuthViewModel: FirebaseAuthViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME,
        modifier = modifier
    ) {
        composable(AppDestinations.HOME) {
            HomeScreen(
                onNavigateToSharedPrefs = {
                    navController.navigate(AppDestinations.SHARED_PREFS)
                },
                onNavigateToRoom = {
                    navController.navigate(AppDestinations.ROOM)
                },
                onNavigateToFirebase = {
                    navController.navigate(AppDestinations.FIREBASE)
                }
            )
        }

        composable(AppDestinations.SHARED_PREFS) {
            SharedPrefsDemoScreen(
                viewModel = settingsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.ROOM) {
            RoomDemoScreen(
                viewModel = taskViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.FIREBASE) {
            FirebaseDemoScreen(
                viewModel = firebaseAuthViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
