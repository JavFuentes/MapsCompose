package android.bootcamp.mapscompose.data.local.entity

import android.bootcamp.mapscompose.data.model.CustomMarker
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "markers")
data class MarkerEntity(
    @PrimaryKey
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val snippet: String?,
)

//Mappers

// Convertir de entidad a modelo de dominio
fun MarkerEntity.toCustomMarker(): CustomMarker {
    return CustomMarker(
        id = this.id,
        position = LatLng(this.latitude, this.longitude),
        title = this.title,
        snippet = this.snippet
    )
}

// Convertir de modelo a entidad
fun CustomMarker.toEntity(): MarkerEntity {
    return MarkerEntity(
        id = this.id,
        latitude = this.position.latitude,
        longitude = this.position.longitude,
        title = this.title,
        snippet = this.snippet
    )
}

