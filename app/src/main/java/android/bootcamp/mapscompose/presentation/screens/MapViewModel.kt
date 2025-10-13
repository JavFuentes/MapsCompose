package android.bootcamp.mapscompose.presentation.screens

import android.bootcamp.mapscompose.data.LocationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val locationManager: LocationManager? = null
) : ViewModel() {

    //Posición Inicial (Santiago de Chile)
    private val defaultLocation = LatLng(-33.4500, -70.6600)

    // Estado mutable para la posición de la cámara
    private val _cameraPosition = MutableStateFlow(
        CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    )

    // Estado público inmutable para observar los cambios en la posición de la cámara
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition

    // Estado para la ubicación del usuario
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    // Método para centrar la cámara en la ubicación GPS del usuario con animación
    @Suppress("MissingPermission")
    fun centerOnUserLocation(cameraPositionState: CameraPositionState, hasPermission: Boolean) {
        viewModelScope.launch {
            val targetLocation = if (hasPermission) {
                locationManager?.getCurrentLocation() ?: defaultLocation
            } else {
                defaultLocation
            }

            // Actualizar el estado de la ubicación del usuario para mostrar el marcador
            _userLocation.value = targetLocation

            // Animar la cámara a la ubicación
            cameraPositionState.animate(
                update = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    targetLocation,
                    15f
                ),
                durationMs = 1500 // 1.5 segundos de animación
            )
        }
    }

}