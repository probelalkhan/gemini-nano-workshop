package dev.belalkhan.gemininanoworkshop.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.belalkhan.gemininanoworkshop.ui.home.HomeScreen
import dev.belalkhan.gemininanoworkshop.ui.home.imagedesc.ImageDescScreen
import dev.belalkhan.gemininanoworkshop.ui.home.summarization.SummarizationScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onNavigate = { feature -> navController.navigate(feature.route) })
        }

        composable(Feature.Summarization.route) {
            SummarizationScreen(onBack = { navController.navigateUp() })
        }

        composable(Feature.Proofreading.route) {
        }

        composable(Feature.Rewrite.route) {
        }

        composable(Feature.ImageDescription.route) {
            ImageDescScreen(onBack = { navController.navigateUp() })
        }
    }
}
