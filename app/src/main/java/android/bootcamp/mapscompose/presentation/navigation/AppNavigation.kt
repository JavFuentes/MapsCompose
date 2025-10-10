package android.bootcamp.mapscompose.presentation.navigation

import android.bootcamp.mapscompose.presentation.screens.MapScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
object Map

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Map) {
        composable<Map>{
            MapScreen()
        }
    }
}