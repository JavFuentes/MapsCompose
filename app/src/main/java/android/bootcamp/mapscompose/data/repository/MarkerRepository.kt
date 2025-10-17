package android.bootcamp.mapscompose.data.repository

import android.bootcamp.mapscompose.data.local.dao.MarkerDao
import android.bootcamp.mapscompose.data.local.entity.toCustomMarker
import android.bootcamp.mapscompose.data.local.entity.toEntity
import android.bootcamp.mapscompose.data.model.CustomMarker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MarkerRepository(
    private val markerDao: MarkerDao
) {
    // Obtiene todos los marcadores como un flow reactivo
    fun getAllMarkers(): Flow<List<CustomMarker>> {
        return markerDao.getAllMarkers().map { entities ->
            entities.map { it.toCustomMarker() }
        }
    }

    //Agrega un nuevo marcador a la bbdd
    suspend fun addMarker(marker: CustomMarker){
        markerDao.insertMarker(marker.toEntity())
    }

    //Eliminar un marcador por su ID
    suspend fun removeMarker(markerId: String){
        markerDao.deleteMarkerById(markerId)
    }

    //Eliminar todos los marcadores
    suspend fun removeAllMarkers(){
        markerDao.deleteAllMarkers()
    }
}