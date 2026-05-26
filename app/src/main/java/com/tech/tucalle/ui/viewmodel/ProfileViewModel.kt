package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    val seguidores: Int = 0,
    val totalResenas: Int = 0,
    val totalHuariques: Int = 0,
    val antiguedad: Long = 0L,
    val plan: String = "Impulso",
    val diasDisponibles: List<String> = emptyList(),
    val horaDesde: String = "",
    val horaHasta: String = "",
    val logros: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val mensajeGuardado: String = ""
)

class ProfileViewModel : ViewModel() {
    private val auth    = FirebaseAuth.getInstance()
    private val db      = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // ── AUTH STATE LISTENER ───────────────────────────────────────
    // Escucha cuando Firebase Auth confirma la sesión activa.
    // Esto resuelve el problema de perfil en blanco al registrarse:
    // el listener espera a que currentUser != null antes de cargar.
    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null && _uiState.value.uid.isEmpty()) {
            cargarPerfilGlobal(user.uid, user.email ?: "")
        }
    }

    init {
        // Registrar listener — también carga si ya hay sesión activa
        auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    // ── CARGA EN CASCADA: qualities → usuarios → tiendas ─────────
    private fun cargarPerfilGlobal(uid: String, email: String) {
        _uiState.update { it.copy(isLoading = true) }

        db.collection("qualities").document(uid).get()
            .addOnSuccessListener { docQ ->
                if (docQ.exists()) {
                    _uiState.update { it.copy(
                        uid             = uid,
                        nombre          = docQ.getString("nombre") ?: "",
                        apellidos       = docQ.getString("apellidos") ?: "",
                        correo          = docQ.getString("email") ?: email,
                        celular         = docQ.getString("celular") ?: "",
                        fotoUrl         = docQ.getString("fotoUrl") ?: "",
                        descripcion     = docQ.getString("descripcion") ?: "",
                        fechaNacimiento = docQ.getString("fechaNacimiento") ?: "",
                        tipoDocumento   = docQ.getString("tipoDocumento") ?: "DNI",
                        dni             = docQ.getString("dni") ?: "",
                        rol             = "QUALITY",
                        seguidores      = docQ.getLong("seguidores")?.toInt() ?: 0,
                        totalResenas    = docQ.getLong("totalReseñas")?.toInt() ?: 0,
                        antiguedad      = docQ.getLong("antiguedad") ?: 0L,
                        diasDisponibles = (docQ.get("diasDisponibles") as? List<*>)
                            ?.filterIsInstance<String>() ?: emptyList(),
                        horaDesde       = docQ.getString("horaDisponibleDesde") ?: "",
                        horaHasta       = docQ.getString("horaDisponibleHasta") ?: "",
                        logros          = (docQ.get("logros") as? List<*>)
                            ?.filterIsInstance<String>() ?: emptyList(),
                        isLoading       = false
                    ) }
                    return@addOnSuccessListener
                }

                db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener { docU ->
                        if (docU.exists()) {
                            _uiState.update { it.copy(
                                uid             = uid,
                                nombre          = docU.getString("nombre") ?: "",
                                apellidos       = docU.getString("apellidos") ?: "",
                                correo          = docU.getString("email") ?: email,
                                celular         = docU.getString("celular") ?: "",
                                fotoUrl         = docU.getString("fotoUrl") ?: "",
                                fechaNacimiento = docU.getString("fechaNacimiento") ?: "",
                                tipoDocumento   = docU.getString("tipoDocumento") ?: "DNI",
                                dni             = docU.getString("dni") ?: "",
                                rol             = "USUARIO",
                                totalResenas    = docU.getLong("totalReseñas")?.toInt() ?: 0,
                                totalHuariques  = docU.getLong("totalHuariques")?.toInt() ?: 0,
                                antiguedad      = docU.getLong("antiguedad") ?: 0L,
                                logros          = (docU.get("logros") as? List<*>)
                                    ?.filterIsInstance<String>() ?: emptyList(),
                                isLoading       = false
                            ) }
                            return@addOnSuccessListener
                        }

                        db.collection("tiendas").document(uid).get()
                            .addOnSuccessListener { docT ->
                                if (docT.exists()) {
                                    _uiState.update { it.copy(
                                        uid          = uid,
                                        nombre       = docT.getString("nombre") ?: "",
                                        correo       = docT.getString("email") ?: email,
                                        celular      = docT.getString("celular") ?: "",
                                        fotoUrl      = docT.getString("logoUrl")
                                            ?: docT.getString("portadaUrl") ?: "",
                                        rol          = "TIENDA",
                                        seguidores   = docT.getLong("seguidores")?.toInt() ?: 0,
                                        totalResenas = docT.getLong("totalReseñas")?.toInt() ?: 0,
                                        plan         = docT.getString("plan") ?: "Impulso",
                                        antiguedad   = docT.getLong("antiguedad") ?: 0L,
                                        isLoading    = false
                                    ) }
                                } else {
                                    // No encontrado en ninguna colección
                                    _uiState.update { it.copy(isLoading = false) }
                                }
                            }
                            .addOnFailureListener {
                                _uiState.update { it.copy(isLoading = false) }
                            }
                    }
                    .addOnFailureListener {
                        _uiState.update { it.copy(isLoading = false) }
                    }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false) }
            }
    }

    // ── CAMBIOS DE CAMPO ──────────────────────────────────────────
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
    fun onDiaToggle(dia: String) {
        val actuales = _uiState.value.diasDisponibles.toMutableList()
        if (actuales.contains(dia)) actuales.remove(dia) else actuales.add(dia)
        _uiState.update { it.copy(diasDisponibles = actuales) }
    }


    //   match /fotos_perfil/{uid}/{allPaths=**} {
    //     allow read: if true;
    //     allow write: if request.auth != null && request.auth.uid == uid;
    //   }
    fun subirFotoPerfil(uri: Uri, onResult: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val ref = storage.reference.child("fotos_perfil/$uid/perfil.jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    val url = downloadUri.toString()
                    onFotoUrlChange(url)
                    // Guardar URL en Firestore también
                    val coleccion = when (_uiState.value.rol) {
                        "QUALITY" -> "qualities"
                        "TIENDA"  -> "tiendas"
                        else      -> "usuarios"
                    }
                    val campo = if (_uiState.value.rol == "TIENDA") "logoUrl" else "fotoUrl"
                    db.collection(coleccion).document(uid).update(campo, url)
                    onResult(url)
                }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(mensajeGuardado = " Error al subir foto: ${e.message}") }
            }
    }

    // ── GUARDAR CAMBIOS EN FIRESTORE ──────────────────────────────
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
                "nombre"       to s.nombre,
                "celular"      to s.celular,
                "logoUrl"      to s.fotoUrl,
                "fechaNacimiento" to s.fechaNacimiento,
                "tipoDocumento"   to s.tipoDocumento,
                "dni"             to s.dni
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
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false, mensajeGuardado = "✅ Cambios guardados") }
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(isLoading = false, mensajeGuardado = " Error: ${e.message}") }
            }
    }

    // ── CERRAR SESIÓN ─────────────────────────────────────────────
    fun cerrarSesion(onLogoutSuccess: () -> Unit) {
        auth.signOut()
        // Limpiar estado local
        _uiState.value = ProfileUiState()
        onLogoutSuccess()
    }

    // ── UTILIDADES ────────────────────────────────────────────────
    fun calcularAntiguedad(): String {
        val ms = System.currentTimeMillis() - _uiState.value.antiguedad
        if (ms <= 0) return "Nuevo"
        val dias = ms / (1000 * 60 * 60 * 24)
        return when {
            dias < 1   -> "Hoy"
            dias < 30  -> "$dias días"
            dias < 365 -> "${dias / 30} meses"
            else       -> "${dias / 365} año${if (dias / 365 > 1L) "s" else ""}"
        }
    }
}