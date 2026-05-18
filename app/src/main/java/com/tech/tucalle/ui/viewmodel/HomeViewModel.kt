package com.tech.tucalle.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.tech.tucalle.data.Banner
import com.tech.tucalle.data.PlatoDTO
import com.tech.tucalle.data.TiendaDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val db = Firebase.firestore

    // Estado para la ubicación real del GPS
    private val _direccionActual = MutableStateFlow("Detectando ubicación...")
    val direccionActual: StateFlow<String> = _direccionActual.asStateFlow()

    // Estados reactivos mapeados a los nuevos DTOs
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _tiendasCercanas = MutableStateFlow<List<TiendaDTO>>(emptyList())
    val tiendasCercanas: StateFlow<List<TiendaDTO>> = _tiendasCercanas.asStateFlow()

    private val _platosPopulares = MutableStateFlow<List<PlatoDTO>>(emptyList())
    val platosPopulares: StateFlow<List<PlatoDTO>> = _platosPopulares.asStateFlow()

    init {
        cargarBanners()
        cargarTiendasCercanas()
        cargarPlatosPopulares()
    }

    private fun cargarBanners() {
        db.collection("banners_home")
            .whereEqualTo("activo", true)
            .get()
            .addOnSuccessListener { result ->
                val listaBanners = result.documents.mapNotNull { it.toObject(Banner::class.java) }
                _banners.value = listaBanners
            }
            .addOnFailureListener { Log.e("HomeViewModel", "Error al cargar banners", it) }
    }

    private fun cargarTiendasCercanas() {
        // Filtramos solo las tiendas que el Administrador/TI ya aprobó
        db.collection("tiendas")
            .whereEqualTo("estado", "APROBADO")
            .orderBy("calificacionGeneral", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val listaTiendas = result.documents.mapNotNull { doc ->
                    doc.toObject(TiendaDTO::class.java)?.copy(id = doc.id)
                }
                _tiendasCercanas.value = listaTiendas
            }
            .addOnFailureListener { Log.e("HomeViewModel", "Error al cargar tiendas", it) }
    }

    private fun cargarPlatosPopulares() {
        // NUEVA LÓGICA: Platos más recomendados ordenados por su propia nota (calificacionPlato)
        // Además, solo se muestran si pasaron la moderación de la administración (estado == APROBADO)
        db.collection("platos")
            .whereEqualTo("estado", "APROBADO")
            .orderBy("calificacionPlato", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val listaPlatos = result.documents.mapNotNull { doc ->
                    doc.toObject(PlatoDTO::class.java)?.copy(id = doc.id)
                }
                _platosPopulares.value = listaPlatos
            }
            .addOnFailureListener { Log.e("HomeViewModel", "Error al cargar platos populares", it) }
    }

    // --- LÓGICA DE GEOLOCALIZACIÓN DEL DISPOSITIVO ---
    fun obtenerUbicacionReal(context: android.content.Context) {
        val fusedLocationClient =
            com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    resolverDireccion(context, location.latitude, location.longitude)
                } else {
                    pedirUbicacionFresca(context, fusedLocationClient)
                }
            }.addOnFailureListener {
                _direccionActual.value = "Ubicación no disponible"
            }
        } catch (e: SecurityException) {
            _direccionActual.value = "Permiso denegado"
        }
    }

    private fun resolverDireccion(context: android.content.Context, lat: Double, lng: Double) {
        try {
            val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    val addr = addresses.firstOrNull()
                    _direccionActual.value = formatearDireccion(addr)
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val addr = addresses?.firstOrNull()
                _direccionActual.value = formatearDireccion(addr)
            }
        } catch (e: Exception) {
            _direccionActual.value = "Ubicación desconocida"
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun pedirUbicacionFresca(
        context: android.content.Context,
        fusedClient: com.google.android.gms.location.FusedLocationProviderClient
    ) {
        val request = com.google.android.gms.location.CurrentLocationRequest.Builder()
            .setPriority(com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(30_000L)
            .build()

        try {
            fusedClient.getCurrentLocation(request, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        resolverDireccion(context, it.latitude, it.longitude)
                    } ?: run {
                        _direccionActual.value = "Activa el GPS"
                    }
                }
                .addOnFailureListener {
                    _direccionActual.value = "GPS no disponible"
                }
        } catch (e: SecurityException) {
            _direccionActual.value = "Permiso denegado"
        }
    }

    private fun formatearDireccion(addr: android.location.Address?): String {
        if (addr == null) return "Lima"
        val numero = addr.subThoroughfare
        val calle = if (addr.thoroughfare != null && numero != null) {
            "${addr.thoroughfare} $numero"
        } else {
            addr.thoroughfare
        }
        val distrito = addr.locality ?: addr.subAdminArea
        val provincia = addr.adminArea ?: "Lima"

        return when {
            calle != null && distrito != null -> "$calle, $distrito, $provincia"
            calle != null                     -> "$calle, $provincia"
            distrito != null                  -> "$distrito, $provincia"
            else                              -> provincia
        }
    }
}