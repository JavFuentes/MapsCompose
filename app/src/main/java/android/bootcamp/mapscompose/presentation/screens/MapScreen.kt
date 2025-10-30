package android.bootcamp.mapscompose.presentation.screens

import android.Manifest
import android.bootcamp.mapscompose.presentation.screens.components.AddMarkerDialog
import android.bootcamp.mapscompose.presentation.screens.components.CustomMarkerInfoWindow
import android.bootcamp.mapscompose.presentation.screens.components.LocationButton
import android.bootcamp.mapscompose.presentation.screens.components.MapTypeSelector
import android.bootcamp.mapscompose.presentation.screens.components.ZoomButtons
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
) {

    // Solicitar permiso de ubicación
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    //Observar la posición de la cámara desde el viewModel
    val cameraPosition by viewModel.cameraPosition.collectAsState()

    // Solicita el permiso de ubicación al usuario cuando se carga la pantalla por primera vez
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Observar la ubicación del usuario desde el ViewModel
    val userLocation by viewModel.userLocation.collectAsState()

    // Observar el tipo de mapa desde el ViewModel
    val mapType by viewModel.mapType.collectAsState()

    // Observar los marcadores personalizados desde el ViewModel
    val customMarkers by viewModel.customMarkers.collectAsState()

    // Crear el estado de la cámara que persistirá durante recomposiciones
    val cameraPositionState = remember {
        CameraPositionState(
            position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                com.google.android.gms.maps.model.LatLng(-33.4500, -70.6600),
                10f
            )
        )
    }

    // Observar la posición pendiente para mostrar el diálogo de nuevo marcador
    val pendingMarkerPosition by viewModel.pendingMarkerPosition.collectAsState()

    // Observar mensajes de validación para mostrar en Snackbar
    val validationMessage by viewModel.validationMessage.collectAsState()

    // Estado del Snackbar y coroutine scope para mostrarlo
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Mostrar Snackbar cuando hay un mensaje de validación
    LaunchedEffect(validationMessage) {
        validationMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                // Limpiar el mensaje después de mostrarlo
                viewModel.clearValidationMessage()
            }
        }
    }

    // Mostrar diálogo cuando hay una posición pendiente
    AddMarkerDialog(
        showDialog = pendingMarkerPosition != null,
        onDismiss = {
            // Usuario canceló la creación del marcador
            viewModel.cancelAddMarker()
        },
        onConfirm = { title, snippet ->
            // Usuario confirmó con título y snippet
            viewModel.confirmAddMarker(title, snippet)
        }
    )

    Scaffold(
        snackbarHost = {
            // Host para mostrar Snackbars de validación
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            //Renderizar Google Maps
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapType = mapType),
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                onMapClick = { latLang -> {} },
                onMapLongClick = { latLng ->
                    // Agregar marcador con long-press
                    viewModel.addMarker(latLng)
                }

            ) {

                // Mostrar marcador en la ubicación del usuario si está disponible
                userLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = "Mi ubicación",
                        snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }

                // Renderizar marcadores personalizados
                customMarkers.forEach { marker ->
                    //  Estado del marcador personalizado
                    val markerState = remember(marker.id) {
                        MarkerState(position = marker.position)
                    }

                    // MarkerInfoWindow permite personalizar completamente el info window
                    MarkerInfoWindow(
                        state = markerState,
                        onInfoWindowLongClick = {
                            viewModel.removeMarker(marker.id)
                        }
                    ){
                        CustomMarkerInfoWindow(
                            title = marker.title,
                            snippet = marker.snippet
                        )

                    }
                }
            }

            // Selector de tipo de mapa
            MapTypeSelector(
                currentMapType = mapType,
                onMapTypeSelected = { newType ->
                    viewModel.updateMapType(newType)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {

                // Centrar en la ubicación del usuario
                LocationButton(
                    onClick = {
                        viewModel.centerOnUserLocation(
                            cameraPositionState,
                            hasPermission = locationPermissionState.status.isGranted
                        )
                    }
                )

                Spacer(Modifier.height(32.dp))

                // Controles de zoom
                ZoomButtons(
                    cameraPositionState = cameraPositionState
                )
            }
        }
    }
}

