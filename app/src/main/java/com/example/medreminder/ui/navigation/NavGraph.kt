package com.example.medreminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medreminder.ui.addmedication.AddMedicationScreen
import com.example.medreminder.ui.home.HomeScreen
import com.example.medreminder.ui.medications.MedicationsScreen
import com.example.medreminder.ui.settings.SettingsScreen
import com.example.medreminder.ui.weekly.WeeklyScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddMedication : Screen("add_medication?medicationId={medicationId}") {
        fun createRoute(medicationId: Long = -1) = "add_medication?medicationId=$medicationId"
    }
    object Medications : Screen("medications")
    object Settings : Screen("settings")
    object Weekly : Screen("weekly")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddMedication = { navController.navigate(Screen.AddMedication.createRoute()) },
                onEditMedication = { id -> navController.navigate(Screen.AddMedication.createRoute(id)) },
                onViewMedications = { navController.navigate(Screen.Medications.route) },
                onViewSettings = { navController.navigate(Screen.Settings.route) },
                onViewWeekly = { navController.navigate(Screen.Weekly.route) }
            )
        }
        composable(
            route = Screen.AddMedication.route,
            arguments = listOf(navArgument("medicationId") {
                type = NavType.LongType; defaultValue = -1L
            })
        ) {
            AddMedicationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Medications.route) {
            MedicationsScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddMedication = { navController.navigate(Screen.AddMedication.createRoute()) },
                onEditMedication = { id -> navController.navigate(Screen.AddMedication.createRoute(id)) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Weekly.route) {
            WeeklyScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
