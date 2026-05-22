package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StoreUiState(
    val nombreTienda: String = "",
    val logoUrl: String = "",
    val portadaUrl: String = "",
    val razonSocial: String = "",
    val celular: String = "",
    val whatsapp: String = "",
    val direccion: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val encargadoNombre: String = "",
    val encargadoContacto: String = "",
    val encargadoEmail: String = "",
    val estadoLocal: String = "Cerrado",
    val horarioApertura: String = "",
    val horarioCierre: String = "",
    val diasApertura: List<String> = emptyList(),
    val plan: String = "Impulso",
    val seguidores: Int = 0,
    val totalResenas: Int = 0,
    val isLoading: Boolean = false,
    val mensajeGuardado: String = ""
)

class StoreViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    init { cargarDatos() }

    fun onNombreChange(v: String)           = _uiState.update { it.copy(nombreTienda = v) }
    fun onRazonSocialChange(v: String)      = _uiState.update { it.copy(razonSocial = v) }
    fun onCelularChange(v: String)          = _uiState.update { it.copy(celular = v) }
    fun onWhatsappChange(v: String)         = _uiState.update { it.copy(whatsapp = v) }
    fun onUbicacionChange(dir: String, lat: Double, lng: Double) = _uiState.update { it.copy(direccion = dir, latitud = lat, longitud = lng) }
    fun onEncargadoNombreChange(v: String)  = _uiState.update { it.copy(encargadoNombre = v) }
    fun onEncargadoContactoChange(v: String)= _uiState.update { it.copy(encargadoContacto = v) }
    fun onEncargadoEmailChange(v: String)   = _uiState.update { it.copy(encargadoEmail = v) }
    fun onHorarioAperturaChange(v: String)  = _uiState.update { it.copy(horarioApertura = v) }
    fun onHorarioCierreChange(v: String)    = _uiState.update { it.copy(horarioCierre = v) }
    fun onLogoUrlChange(v: String)          = _uiState.update { it.copy(logoUrl = v) }
    fun onDiasAperturaChange(v: List<String>) = _uiState.update { it.copy(diasApertura = v) }

    private fun cargarDatos() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("tiendas").document(uid).addSnapshotListener { snap, _ ->
            snap?.let { doc ->
                val mapa = doc.get("direccion") as? Map<*, *>
                val direccionTexto = mapa?.get("texto")?.toString() ?: doc.getString("direccion") ?: ""
                val lat = (mapa?.get("latitud") as? Number)?.toDouble() ?: 0.0
                val lng = (mapa?.get("longitud") as? Number)?.toDouble() ?: 0.0
                val diasList = (doc.get("diasApertura") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                val horarioCompleto = doc.getString("horario") ?: ""
                val partes = horarioCompleto.split("–", "-").map { it.trim() }
                val apertura = partes.getOrNull(0) ?: ""
                val cierre   = partes.getOrNull(1) ?: ""

                _uiState.update {
                    it.copy(
                        nombreTienda      = doc.getString("nombre") ?: "",
                        razonSocial       = doc.getString("razonSocial") ?: "",
                        celular           = doc.getString("celular") ?: "",
                        whatsapp          = doc.getString("whatsapp") ?: "",
                        direccion         = direccionTexto,
                        latitud           = lat,
                        longitud          = lng,
                        diasApertura      = diasList,
                        encargadoNombre   = doc.getString("encargadoNombre") ?: "",
                        encargadoContacto = doc.getString("encargadoContacto") ?: "",
                        encargadoEmail    = doc.getString("encargadoEmail") ?: "",
                        estadoLocal       = doc.getString("estadoLocal") ?: "Cerrado",
                        horarioApertura   = apertura,
                        horarioCierre     = cierre,
                        logoUrl           = doc.getString("logoUrl") ?: doc.getString("portadaUrl") ?: "",
                        portadaUrl        = doc.getString("portadaUrl") ?: "",
                        plan              = doc.getString("plan") ?: "Impulso",
                        seguidores        = doc.getLong("seguidores")?.toInt() ?: 0,
                        totalResenas      = doc.getLong("totalReseñas")?.toInt() ?: 0
                    )
                }
            }
        }
    }

    fun guardarCambios() {
        val uid = auth.currentUser?.uid ?: return
        val s = _uiState.value
        val horarioTexto = if (s.horarioApertura.isNotBlank() && s.horarioCierre.isNotBlank()) "${s.horarioApertura} – ${s.horarioCierre}" else ""

        val data = mapOf(
            "nombre"            to s.nombreTienda,
            "razonSocial"       to s.razonSocial,
            "celular"           to s.celular,
            "whatsapp"          to s.whatsapp,
            "direccion"         to mapOf("texto" to s.direccion, "latitud" to s.latitud, "longitud" to s.longitud),
            "diasApertura"      to s.diasApertura,
            "encargadoNombre"   to s.encargadoNombre,
            "encargadoContacto" to s.encargadoContacto,
            "encargadoEmail"    to s.encargadoEmail,
            "horario"           to horarioTexto,
            "logoUrl"           to s.logoUrl
        )
        _uiState.update { it.copy(isLoading = true) }
        db.collection("tiendas").document(uid).update(data)
            .addOnSuccessListener { _uiState.update { it.copy(isLoading = false, mensajeGuardado = "✅ Cambios guardados") } }
            .addOnFailureListener { e -> _uiState.update { it.copy(isLoading = false, mensajeGuardado = "❌ Error: ${e.message}") } }
    }

    fun cambiarEstado(nuevoEstado: String) {
        val uid = auth.currentUser?.uid ?: return
        _uiState.update { it.copy(estadoLocal = nuevoEstado) }
        db.collection("tiendas").document(uid).update("estadoLocal", nuevoEstado)
    }
    fun cerrarSesion(onLogoutSuccess: () -> Unit) {
        auth.signOut()
        onLogoutSuccess()
    }
}