package android.bootcamp.mapscompose.presentation.screens

import android.bootcamp.mapscompose.data.LocationManager
import android.bootcamp.mapscompose.data.model.CustomMarker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

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

    // Estado para el tipo de mapa
    private val _mapType = MutableStateFlow(MapType.NORMAL)
    val mapType: StateFlow<MapType> = _mapType.asStateFlow()

    // Método para actualizar el tipo de mapa
    fun updateMapType(type: MapType) {
        _mapType.value = type
    }

    // Estado para los marcadores personalizados
    private val _customMarkers = MutableStateFlow<List<CustomMarker>>(emptyList())
    val customMarkers: StateFlow<List<CustomMarker>> = _customMarkers.asStateFlow()

    // Método para agregar marcador con long-press en el mapa
    fun addMarker(latLng: LatLng) {
        val markerNumber = _customMarkers.value.size + 1
        val newMarker = CustomMarker(
            position = latLng,
            title = "Lugar #$markerNumber",
            snippet = "Lat: ${"%.4f".format(Locale.US, latLng.latitude)}, " +
                    "Lng: ${"%.4f".format(Locale.US, latLng.longitude)}"
        )

        // Crear una nueva lista con el marcador agregado (inmutabilidad)
        _customMarkers.value = _customMarkers.value + newMarker
    }

    // Método para eliminar marcador por ID
    fun removeMarker(markerId: String) {
        _customMarkers.value = _customMarkers.value.filter { it.id != markerId }
    }

    // Método para limpiar todos los marcadores
    fun clearAllMarkers() {
        _customMarkers.value = emptyList()
    }

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