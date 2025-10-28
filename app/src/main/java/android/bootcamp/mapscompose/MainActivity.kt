package android.bootcamp.mapscompose

import android.bootcamp.mapscompose.presentation.navigation.AppNavigation
import android.bootcamp.mapscompose.ui.theme.MapsComposeTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapsComposeTheme {
                AppNavigation()
            }
        }
    }
}

