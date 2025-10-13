package android.bootcamp.mapscompose.data

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class LocationManager(context: Context) {

    // Cliente proporciona acceso a lso servicios de ubicación de Google Play Services
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): LatLng? {
        // Token para poder cancelar la solicitud
        val cancellationTokenSource = CancellationTokenSource()

        return try {
            // Solicitar ubicación actual
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            // Convierte al formato LatLng
            location?.let{
                LatLng(it.latitude, it.longitude)
            }
        } catch (e: Exception){
            // Cancela la solicitud
            cancellationTokenSource.cancel()

            // Maneja errores
            e.printStackTrace()
            null
        }
    }
}

