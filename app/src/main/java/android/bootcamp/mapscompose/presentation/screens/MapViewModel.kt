package android.bootcamp.mapscompose.presentation.screens

import android.bootcamp.mapscompose.data.LocationManager
import android.bootcamp.mapscompose.data.SoundManager
import android.bootcamp.mapscompose.data.model.CustomMarker
import android.bootcamp.mapscompose.data.repository.MarkerRepository
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// Constante para el radio máximo de distancia permitido (1km en metros)
private const val MAX_DISTANCE_METERS = 1000f

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val markerRepository: MarkerRepository,
    private val soundManager: SoundManager,
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

    // Estado para posición pendiente de marcador (activa el diálogo cuando no es null)
    private val _pendingMarkerPosition = MutableStateFlow<LatLng?>(null)
    val pendingMarkerPosition: StateFlow<LatLng?> = _pendingMarkerPosition.asStateFlow()

    // Estado para los marcadores personalizados - se obtinen del Repository
    val customMarkers: StateFlow<List<CustomMarker>> = markerRepository?.getAllMarkers()
        ?.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        ) ?: MutableStateFlow(emptyList())


    // Estado para mensajes de validación (se muestran en Snackbar)
    private val _validationMessage = MutableStateFlow<String?>(null)
    val validationMessage: StateFlow<String?> = _validationMessage.asStateFlow()

    // Método para iniciar el proceso de agregar marcador (con validaciones)
    fun addMarker(latLng: LatLng) {
        // Validación 1: Verificar que la ubicación del usuario esté disponible
        val currentUserLocation = _userLocation.value
        if (currentUserLocation == null) {
            _validationMessage.value = "Primero activa tu ubicación usando el botón GPS"
            return
        }

        // Validación 2: Calcular distancia y verificar que esté dentro del radio permitido
        val distance = calculateDistance(currentUserLocation, latLng)
        if (distance > MAX_DISTANCE_METERS) {
            _validationMessage.value =
                "El marcador debe estar dentro de 1km de tu ubicación (distancia: ${distance.toInt()}m)"
            return
        }

        // Si pasa las validaciones, guardar la posición pendiente (activa el diálogo)
        _pendingMarkerPosition.value = latLng
    }

    // Método para confirmar la creación del marcador con datos del usuario
    fun confirmAddMarker(title: String, snippet: String?) {
        viewModelScope.launch {
            // Obtener la posición pendiente
            val position = _pendingMarkerPosition.value ?: return@launch

            //Crear el marcador con los datos ingresados por el usuario
            val newMarker = CustomMarker(
                position = position,
                title = title,
                snippet = snippet
            )

            // Guardar el marcador en la bbdd
            markerRepository.addMarker(newMarker)

            // Limpiar la posición pendiente (cierra el diálogo)
            _pendingMarkerPosition.value = null
        }
    }

    // Método para cancelar la creación del marcador
    fun cancelAddMarker() {
        // Limpiar la posición pendiente (cierra el diálogo sin guardar)
        _pendingMarkerPosition.value = null
    }


    // Método para eliminar marcador por ID
    fun removeMarker(markerId: String) {
        // Reproducir sonido de borrado
        soundManager.playDeleteSound()

        viewModelScope.launch {
            markerRepository.removeMarker(markerId)
        }
    }

    // Método para limpiar mensajes de validación (cierra el Snackbar)
    fun clearValidationMessage() {
        _validationMessage.value = null
    }

    // Método para limpiar todos los marcadores
    fun clearAllMarkers() {
        viewModelScope.launch {
            markerRepository.removeAllMarkers()
        }
    }

    // Estado para la ubicación del usuario
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    // Método para centrar la cámara en la ubicación GPS del usuario con animación
    @Suppress("MissingPermission")
    fun centerOnUserLocation(cameraPositionState: CameraPositionState, hasPermission: Boolean) {
        viewModelScope.launch {
            val targetLocation = if (hasPermission) {
                locationManager.getCurrentLocation() ?: defaultLocation
            } else {
                defaultLocation
            }

            // Actualizar el estado de la ubicación del usuario para mostrar el marcador
            _userLocation.value = targetLocation

            //Reproducir el sonido de inicio

            if (hasPermission && targetLocation != defaultLocation) {
                soundManager.playStartSound()
            }


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

    // Método privado para calcular distancia entre dos coordenadas (en metros)
    private fun calculateDistance(from: LatLng, to: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            from.latitude, from.longitude,
            to.latitude, to.longitude,
            results
        )
        return results[0]
    }
}
