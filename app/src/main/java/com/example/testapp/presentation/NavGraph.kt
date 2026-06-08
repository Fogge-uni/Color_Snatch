package com.example.testapp.presentation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.testapp.AppContainer
import com.example.testapp.presentation.ui.screens.CameraScreen
import com.example.testapp.presentation.ui.screens.HomeScreen
import com.example.testapp.presentation.ui.screens.PickColorScreen
import com.example.testapp.presentation.ui.screens.PickPaletteScreen
import com.example.testapp.presentation.ui.screens.SettingsScreen
import com.example.testapp.presentation.viewmodel.HomeFactory
import com.example.testapp.presentation.viewmodel.HomeViewModel
import com.example.testapp.presentation.viewmodel.PickColorFactory
import com.example.testapp.presentation.viewmodel.PickColorViewModel
import com.example.testapp.presentation.viewmodel.PickPaletteFactory
import com.example.testapp.presentation.viewmodel.PickPaletteViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object Camera : Screen("camera?mode={mode}") {
        fun createRoute(mode: String): String = "camera?mode=$mode"
    }

    object PickColor : Screen("pick_color/{photoPath}") {
        fun passPath(path: String): String = "pick_color/${Uri.encode(path)}"
    }

    object PickPalette : Screen("pick_palette/{photoPath}") {
        fun passPath(path: String): String = "pick_palette/${Uri.encode(path)}"
    }
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(appContainer: AppContainer) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val factory = HomeFactory(
                appContainer.getAllPalettesUseCase,
                appContainer.getStandaloneColorsUseCase,
                appContainer.deleteColorUseCase,
                appContainer.deletePaletteUseCase
            )
            val viewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Camera.route,
            arguments = listOf(
                navArgument("mode") {
                    type = NavType.StringType
                    defaultValue = "color"
                }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "color"
            CameraScreen(navController = navController, mode = mode)
        }

        composable(
            route = Screen.PickColor.route,
            arguments = listOf(
                navArgument("photoPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val path = Uri.decode(backStackEntry.arguments?.getString("photoPath") ?: "")
            val factory = PickColorFactory(appContainer.addStandaloneColorUseCase)
            val viewModel: PickColorViewModel = viewModel(factory = factory)
            PickColorScreen(photoPath = path, navController = navController, viewModel = viewModel)
        }

        composable(
            route = Screen.PickPalette.route,
            arguments = listOf(
                navArgument("photoPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val path = Uri.decode(backStackEntry.arguments?.getString("photoPath") ?: "")
            val factory = PickPaletteFactory(appContainer.addPaletteUseCase)
            val viewModel: PickPaletteViewModel = viewModel(factory = factory)
            PickPaletteScreen(photoPath = path, navController = navController, viewModel = viewModel)
        }
    }
}