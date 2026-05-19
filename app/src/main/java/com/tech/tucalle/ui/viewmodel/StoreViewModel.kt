package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StoreUiState(
    // Identidad visual
    val nombreTienda: String = "",
    val logoUrl: String = "",
    val portadaUrl: String = "",
    // Info negocio
    val razonSocial: String = "",
    val celular: String = "",
    val whatsapp: String = "",
    val direccion: String = "",
    // Encargado
    val encargadoNombre: String = "",
    val encargadoContacto: String = "",
    val encargadoEmail: String = "",
    // Estado y horario
    val estadoLocal: String = "Cerrado",
    val horarioApertura: String = "",
    val horarioCierre: String = "",
    // Métricas (solo lectura)
    val plan: String = "Impulso",
    val seguidores: Int = 0,
    val totalResenas: Int = 0,
    // UI
    val isLoading: Boolean = false,
    val mensajeGuardado: String = ""
)

class StoreViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    init { cargarDatos() }

    // ── CAMBIOS DE CAMPO ──────────────────────────────────────────
    fun onNombreChange(v: String)           = _uiState.update { it.copy(nombreTienda = v) }
    fun onRazonSocialChange(v: String)      = _uiState.update { it.copy(razonSocial = v) }
    fun onCelularChange(v: String)          = _uiState.update { it.copy(celular = v) }
    fun onWhatsappChange(v: String)         = _uiState.update { it.copy(whatsapp = v) }
    fun onDireccionChange(v: String)        = _uiState.update { it.copy(direccion = v) }
    fun onEncargadoNombreChange(v: String)  = _uiState.update { it.copy(encargadoNombre = v) }
    fun onEncargadoContactoChange(v: String)= _uiState.update { it.copy(encargadoContacto = v) }
    fun onEncargadoEmailChange(v: String)   = _uiState.update { it.copy(encargadoEmail = v) }
    fun onHorarioAperturaChange(v: String)  = _uiState.update { it.copy(horarioApertura = v) }
    fun onHorarioCierreChange(v: String)    = _uiState.update { it.copy(horarioCierre = v) }
    fun onLogoUrlChange(v: String)          = _uiState.update { it.copy(logoUrl = v) }

    // ── CARGA DE DATOS ────────────────────────────────────────────
    private fun cargarDatos() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("tiendas").document(uid).addSnapshotListener { snap, _ ->
            snap?.let { doc ->
                // Blindaje: direccion puede ser String o Map (compatibilidad)
                val direccionTexto = try {
                    doc.getString("direccion") ?: ""
                } catch (e: Exception) {
                    val mapa = doc.get("direccion") as? Map<*, *>
                    mapa?.get("texto")?.toString() ?: ""
                }

                // Horario: puede venir como "10:00 AM – 11:00 PM" o separado
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

    // ── GUARDAR ───────────────────────────────────────────────────
    fun guardarCambios() {
        val uid = auth.currentUser?.uid ?: return
        val s = _uiState.value
        val horarioTexto = if (s.horarioApertura.isNotBlank() && s.horarioCierre.isNotBlank())
            "${s.horarioApertura} – ${s.horarioCierre}" else ""

        val data = mapOf(
            "nombre"            to s.nombreTienda,
            "razonSocial"       to s.razonSocial,
            "celular"           to s.celular,
            "whatsapp"          to s.whatsapp,
            "direccion"         to s.direccion,
            "encargadoNombre"   to s.encargadoNombre,
            "encargadoContacto" to s.encargadoContacto,
            "encargadoEmail"    to s.encargadoEmail,
            "horario"           to horarioTexto,
            "logoUrl"           to s.logoUrl
        )
        _uiState.update { it.copy(isLoading = true) }
        db.collection("tiendas").document(uid).update(data)
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false, mensajeGuardado = "✅ Cambios guardados") }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(isLoading = false, mensajeGuardado = "❌ Error: ${e.message}") }
            }
    }

    fun cambiarEstado(nuevoEstado: String) {
        val uid = auth.currentUser?.uid ?: return
        _uiState.update { it.copy(estadoLocal = nuevoEstado) }
        db.collection("tiendas").document(uid).update("estadoLocal", nuevoEstado)
    }
}