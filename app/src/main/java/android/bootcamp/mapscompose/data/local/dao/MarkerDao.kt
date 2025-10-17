package android.bootcamp.mapscompose.data.local.dao

import android.bootcamp.mapscompose.data.local.entity.MarkerEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object) para operaciones de CRUD
@Dao
interface MarkerDao{

    //Obtiene todos los marcadores de  las bbdd
    @Query("SELECT * FROM markers")
    fun getAllMarkers(): Flow<List<MarkerEntity>>

    //Insertar un nuevo marcador en la bbdd
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarker(marker: MarkerEntity)

    //Eliminar un marcador por su ID
    @Query("DELETE FROM markers WHERE id = :markerId")
    suspend fun deleteMarkerById(markerId: String)

    //Eliminar todos los marcadores
    @Query("DELETE FROM markers")
    suspend fun deleteAllMarkers()
}