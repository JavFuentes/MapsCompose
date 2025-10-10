package android.bootcamp.mapscompose.presentation.screens

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {

    //Posición Inicial (Santiago de Chile)
    private val defaultLocation = LatLng(-33.4500, -70.6600)

    // Estado mutable para la posición de la cámara
    private val _cameraPosition = MutableStateFlow(
        CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    )

    // Estado público inmutable para observar los cambios en la posición de la cámara
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition.asStateFlow()
}