package com.tech.tucalle.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.tech.tucalle.data.Banner
import com.tech.tucalle.data.Plato
import com.tech.tucalle.data.Tienda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val db = Firebase.firestore

    // Estado para la ubicación real
    private val _direccionActual = MutableStateFlow("Detectando ubicación...")
    val direccionActual: StateFlow<String> = _direccionActual.asStateFlow()

    // Estados reactivos para los datos de Firebase
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _tiendasCercanas = MutableStateFlow<List<Tienda>>(emptyList())
    val tiendasCercanas: StateFlow<List<Tienda>> = _tiendasCercanas.asStateFlow()

    private val _platosPopulares = MutableStateFlow<List<Plato>>(emptyList())
    val platosPopulares: StateFlow<List<Plato>> = _platosPopulares.asStateFlow()

    init {
        // Al iniciar, cargamos los datos de las colecciones
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
        db.collection("tiendas")
            .orderBy("calificacion", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val listaTiendas = result.documents.mapNotNull { doc ->
                    doc.toObject(Tienda::class.java)?.copy(id = doc.id)
                }
                _tiendasCercanas.value = listaTiendas
            }
            .addOnFailureListener { Log.e("HomeViewModel", "Error al cargar tiendas", it) }
    }

    private fun cargarPlatosPopulares() {
        db.collection("platos")
            .orderBy("calificacion", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val listaPlatos = result.documents.mapNotNull { it.toObject(Plato::class.java) }
                _platosPopulares.value = listaPlatos
            }
            .addOnFailureListener { Log.e("HomeViewModel", "Error al cargar platos", it) }
    }

    // Función mágica que detectará tu distrito en el celular
    fun obtenerUbicacionReal(context: android.content.Context) {
        val fusedLocationClient =
            com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)

        try {
            // Primero intentamos lastLocation (rápido, puede ser null)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    resolverDireccion(context, location.latitude, location.longitude)
                } else {
                    // Si lastLocation es null, pedimos una ubicación fresca
                    pedirUbicacionFresca(context, fusedLocationClient)
                }
            }.addOnFailureListener {
                _direccionActual.value = "Ubicación no disponible"
            }
        } catch (e: SecurityException) {
            _direccionActual.value = "Permiso denegado"
        }
    }

    // Función auxiliar: convierte coordenadas → nombre de distrito
    private fun resolverDireccion(context: android.content.Context, lat: Double, lng: Double) {
        try {
            val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                // API 33+: versión asíncrona
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    val addr = addresses.firstOrNull()
                    _direccionActual.value = formatearDireccion(addr)
                }
            } else {
                // API < 33: versión síncrona
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val addr = addresses?.firstOrNull()
                _direccionActual.value = formatearDireccion(addr)
            }
        } catch (e: Exception) {
            _direccionActual.value = "Ubicación desconocida"
        }
    }
    // Función auxiliar: solicita coordenadas frescas cuando lastLocation == null
    @SuppressWarnings("MissingPermission")
    private fun pedirUbicacionFresca(
        context: android.content.Context,
        fusedClient: com.google.android.gms.location.FusedLocationProviderClient
    ) {
        val request = com.google.android.gms.location.CurrentLocationRequest.Builder()
            .setPriority(com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(30_000L) // Acepta datos de hasta 30 seg atrás
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

        // Número de puerta/lote (ej: "245", "1302")
        val numero = addr.subThoroughfare

        // Calle o avenida (ej: "Av. Benavides", "Jr. Huallaga")
        val calle = if (addr.thoroughfare != null && numero != null) {
            "${addr.thoroughfare} $numero"   // → "Av. Benavides 245"
        } else {
            addr.thoroughfare                // → "Av. Benavides" (sin número si no hay)
        }

        // Distrito real
        val distrito = addr.locality ?: addr.subAdminArea

        // Provincia
        val provincia = addr.adminArea ?: "Lima"

        return when {
            calle != null && distrito != null -> "$calle, $distrito, $provincia"
            calle != null                     -> "$calle, $provincia"
            distrito != null                  -> "$distrito, $provincia"
            else                              -> provincia
        }
    }
}