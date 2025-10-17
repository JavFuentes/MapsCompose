package android.bootcamp.mapscompose.data.local

import android.bootcamp.mapscompose.data.local.dao.MarkerDao
import android.bootcamp.mapscompose.data.local.entity.MarkerEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MarkerEntity::class], //Lista de entidades que conforman la bbdd
    version = 1,
    exportSchema = false // No exportar el esquema JSON
)
abstract class AppDatabase : RoomDatabase() {

    // Proporciona acceso al de marcadores
    abstract fun markerDao(): MarkerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null //Instancia Singleton

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maps_database" // Nombre del archivo de la bbdd
                )
                    .setJournalMode(JournalMode.TRUNCATE) //Descativar el WAL
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance //Guardar la instancia para reutilizar
                instance
            }
        }
    }
}