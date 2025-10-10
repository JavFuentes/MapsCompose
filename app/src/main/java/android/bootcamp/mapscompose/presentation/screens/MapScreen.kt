package android.bootcamp.mapscompose.presentation.screens

import android.bootcamp.mapscompose.presentation.screens.components.ZoomButtons
import android.widget.ZoomButtonsController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
) {
    //Observar la posición de la cámara desde el viewModel
    val cameraPosition by viewModel.cameraPosition.collectAsState()

    //Crear el estado de la cámara que se actualizacuando cambia la posición
    val cameraPositionState = remember(cameraPosition){
        CameraPositionState(position = cameraPosition)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            //Renderizar Google Maps
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapType = MapType.NORMAL),
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                onMapClick = { latLang -> {} }
                )

            // Controles de zoom
            ZoomButtons(
                cameraPositionState = cameraPositionState,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}