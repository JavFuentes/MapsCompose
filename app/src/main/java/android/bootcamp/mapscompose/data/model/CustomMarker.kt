package android.bootcamp.mapscompose.data.model

import com.google.android.gms.maps.model.LatLng
import java.util.UUID

/**
 * Representa un marcador personalizado en el mapa
 *
 * @param id Identificador único del marcador
 * @param position Coordenadas geográficas del marcador
 * @param title Título que se muestra en el marcador
 * @param snippet Información adicional que se muestra al tocar el marcador
 */

data class CustomMarker(
    val id: String = UUID.randomUUID().toString(),
    val position: LatLng,
    val title: String,
    val snippet: String? = null
)
