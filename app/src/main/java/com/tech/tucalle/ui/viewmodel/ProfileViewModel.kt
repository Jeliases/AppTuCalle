package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

data class ProfileUiState(
    val uid: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val fotoUrl: String = "",
    val rol: String = "",
    val correo: String = "",
    val celular: String = "",
    val fechaNacimiento: String = "",
    val tipoDocumento: String = "DNI",
    val dni: String = "",
    val descripcion: String = "",
    // Stats
    val seguidores: Int = 0,
    val totalResenas: Int = 0,
    val totalHuariques: Int = 0,
    val antiguedad: Long = 0L,
    // Tienda
    val plan: String = "Impulso",
    // Quality
    val diasDisponibles: List<String> = emptyList(),
    val horaDesde: String = "",
    val horaHasta: String = "",
    val logros: List<String> = emptyList(),
    // UI
    val isLoading: Boolean = false,
    val mensajeGuardado: String = ""
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { cargarPerfilGlobal() }

    private fun cargarPerfilGlobal() {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        db.collection("qualities").document(uid).get().addOnSuccessListener { docQ ->
            if (docQ.exists()) {
                _uiState.update {
                    it.copy(
                        uid           = uid,
                        nombre        = docQ.getString("nombre") ?: "",
                        apellidos     = docQ.getString("apellidos") ?: "",
                        correo        = docQ.getString("email") ?: currentUser.email ?: "",
                        celular       = docQ.getString("celular") ?: "",
                        fotoUrl       = docQ.getString("fotoUrl") ?: "",
                        descripcion   = docQ.getString("descripcion") ?: "",
                        fechaNacimiento = docQ.getString("fechaNacimiento") ?: "",
                        tipoDocumento = docQ.getString("tipoDocumento") ?: "DNI",
                        dni           = docQ.getString("dni") ?: "",
                        rol           = "QUALITY",
                        seguidores    = docQ.getLong("seguidores")?.toInt() ?: 0,
                        totalResenas  = docQ.getLong("totalReseñas")?.toInt() ?: 0,
                        antiguedad    = docQ.getLong("antiguedad") ?: 0L,
                        diasDisponibles = (docQ.get("diasDisponibles") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        horaDesde     = docQ.getString("horaDisponibleDesde") ?: "",
                        horaHasta     = docQ.getString("horaDisponibleHasta") ?: "",
                        logros        = (docQ.get("logros") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        isLoading     = false
                    )
                }
                return@addOnSuccessListener
            }

            db.collection("usuarios").document(uid).get().addOnSuccessListener { docU ->
                if (docU.exists()) {
                    _uiState.update {
                        it.copy(
                            uid             = uid,
                            nombre          = docU.getString("nombre") ?: "",
                            apellidos       = docU.getString("apellidos") ?: "",
                            correo          = docU.getString("email") ?: currentUser.email ?: "",
                            celular         = docU.getString("celular") ?: "",
                            fotoUrl         = docU.getString("fotoUrl") ?: "",
                            fechaNacimiento = docU.getString("fechaNacimiento") ?: "",
                            tipoDocumento   = docU.getString("tipoDocumento") ?: "DNI",
                            dni             = docU.getString("dni") ?: "",
                            rol             = "USUARIO",
                            totalResenas    = docU.getLong("totalReseñas")?.toInt() ?: 0,
                            totalHuariques  = docU.getLong("totalHuariques")?.toInt() ?: 0,
                            antiguedad      = docU.getLong("antiguedad") ?: 0L,
                            logros          = (docU.get("logros") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                            isLoading       = false
                        )
                    }
                    return@addOnSuccessListener
                }

                db.collection("tiendas").document(uid).get().addOnSuccessListener { docT ->
                    if (docT.exists()) {
                        _uiState.update {
                            it.copy(
                                uid           = uid,
                                nombre        = docT.getString("nombre") ?: "",
                                correo        = docT.getString("email") ?: currentUser.email ?: "",
                                celular       = docT.getString("celular") ?: "",
                                fotoUrl       = docT.getString("logoUrl") ?: docT.getString("portadaUrl") ?: "",
                                rol           = "TIENDA",
                                seguidores    = docT.getLong("seguidores")?.toInt() ?: 0,
                                totalResenas  = docT.getLong("totalReseñas")?.toInt() ?: 0,
                                plan          = docT.getString("plan") ?: "Impulso",
                                antiguedad    = docT.getLong("antiguedad") ?: 0L,
                                isLoading     = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun onNombreChange(v: String)          = _uiState.update { it.copy(nombre = v) }
    fun onApellidosChange(v: String)       = _uiState.update { it.copy(apellidos = v) }
    fun onCelularChange(v: String)         = _uiState.update { it.copy(celular = v) }
    fun onFechaNacimientoChange(v: String) = _uiState.update { it.copy(fechaNacimiento = v) }
    fun onTipoDocumentoChange(v: String)   = _uiState.update { it.copy(tipoDocumento = v) }
    fun onDniChange(v: String)             = _uiState.update { it.copy(dni = v) }
    fun onDescripcionChange(v: String)     = _uiState.update { it.copy(descripcion = v) }
    fun onFotoUrlChange(v: String)         = _uiState.update { it.copy(fotoUrl = v) }
    fun onHoraDesdeChange(v: String)       = _uiState.update { it.copy(horaDesde = v) }
    fun onHoraHastaChange(v: String)       = _uiState.update { it.copy(horaHasta = v) }
    fun onDiasChange(v: List<String>)      = _uiState.update { it.copy(diasDisponibles = v) }

    fun guardarCambios() {
        val uid = auth.currentUser?.uid ?: return
        val s = _uiState.value
        _uiState.update { it.copy(isLoading = true, mensajeGuardado = "") }

        val coleccion = when (s.rol) {
            "QUALITY" -> "qualities"
            "TIENDA"  -> "tiendas"
            else      -> "usuarios"
        }

        val data: Map<String, Any> = when (s.rol) {
            "QUALITY" -> mapOf(
                "nombre"               to s.nombre,
                "apellidos"            to s.apellidos,
                "celular"              to s.celular,
                "fotoUrl"              to s.fotoUrl,
                "descripcion"          to s.descripcion,
                "diasDisponibles"      to s.diasDisponibles,
                "horaDisponibleDesde"  to s.horaDesde,
                "horaDisponibleHasta"  to s.horaHasta,
                "fechaNacimiento"      to s.fechaNacimiento,
                "tipoDocumento"        to s.tipoDocumento,
                "dni"                  to s.dni
            )
            "TIENDA" -> mapOf(
                "nombre"  to s.nombre,
                "celular" to s.celular,
                "logoUrl" to s.fotoUrl
            )
            else -> mapOf(
                "nombre"          to s.nombre,
                "apellidos"       to s.apellidos,
                "celular"         to s.celular,
                "fotoUrl"         to s.fotoUrl,
                "fechaNacimiento" to s.fechaNacimiento,
                "tipoDocumento"   to s.tipoDocumento,
                "dni"             to s.dni
            )
        }

        db.collection(coleccion).document(uid).update(data)
            .addOnSuccessListener { _uiState.update { it.copy(isLoading = false, mensajeGuardado = "✅ Cambios guardados") } }
            .addOnFailureListener { e -> _uiState.update { it.copy(isLoading = false, mensajeGuardado = "❌ Error: ${e.message}") } }
    }

    fun cerrarSesion(onLogoutSuccess: () -> Unit) {
        auth.signOut()
        onLogoutSuccess()
    }

    fun calcularAntiguedad(): String {
        val ms = System.currentTimeMillis() - _uiState.value.antiguedad
        val dias = ms / (1000 * 60 * 60 * 24)
        return when {
            dias < 30  -> "$dias días"
            dias < 365 -> "${dias / 30} meses"
            else       -> "${dias / 365} año${if (dias / 365 > 1) "s" else ""}"
        }
    }
}