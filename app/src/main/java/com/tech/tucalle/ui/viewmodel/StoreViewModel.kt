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
    val razonSocial: String = "",
    val celular: String = "",
    val whatsapp: String = "",
    val direccion: String = "",
    val encargadoNombre: String = "",
    val encargadoContacto: String = "",
    val encargadoEmail: String = "",
    val estadoLocal: String = "Cerrado",
    val isLoading: Boolean = false
)

class StoreViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    init { cargarDatos() }

    // Estas funciones permiten que el teclado funcione y escriba
    fun onNombreChange(v: String) = _uiState.update { it.copy(nombreTienda = v) }
    fun onRazonSocialChange(v: String) = _uiState.update { it.copy(razonSocial = v) }
    fun onCelularChange(v: String) = _uiState.update { it.copy(celular = v) }
    fun onWhatsappChange(v: String) = _uiState.update { it.copy(whatsapp = v) }
    fun onDireccionChange(v: String) = _uiState.update { it.copy(direccion = v) }
    fun onEncargadoNombreChange(v: String) = _uiState.update { it.copy(encargadoNombre = v) }
    fun onEncargadoContactoChange(v: String) = _uiState.update { it.copy(encargadoContacto = v) }
    fun onEncargadoEmailChange(v: String) = _uiState.update { it.copy(encargadoEmail = v) }

    private fun cargarDatos() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("tiendas").document(uid).addSnapshotListener { snap, _ ->
            snap?.let { doc ->

                // BLINDAJE RECUPERADO: Extraemos la dirección sin que la app muera
                val direccionGuardada = try {
                    // Intenta leerlo como texto (por si es un registro antiguo)
                    doc.getString("direccion") ?: ""
                } catch (e: Exception) {
                    // Si falla porque es un Mapa (registro nuevo con GPS), extrae solo el texto
                    val mapaDireccion = doc.get("direccion") as? Map<*, *>
                    mapaDireccion?.get("texto")?.toString() ?: ""
                }

                _uiState.update { it.copy(
                    nombreTienda = doc.getString("nombre") ?: "",
                    razonSocial = doc.getString("razonSocial") ?: "",
                    celular = doc.getString("celular") ?: "",
                    whatsapp = doc.getString("whatsapp") ?: "",
                    direccion = direccionGuardada, // Usamos la variable blindada
                    encargadoNombre = doc.getString("encargadoNombre") ?: "",
                    encargadoContacto = doc.getString("encargadoContacto") ?: "",
                    encargadoEmail = doc.getString("encargadoEmail") ?: "",
                    estadoLocal = doc.getString("estadoLocal") ?: "Cerrado"
                )}
            }
        }
    }

    fun guardarCambios() {
        val uid = auth.currentUser?.uid ?: return
        val data = mapOf(
            "nombre" to _uiState.value.nombreTienda,
            "razonSocial" to _uiState.value.razonSocial,
            "celular" to _uiState.value.celular,
            "whatsapp" to _uiState.value.whatsapp,
            "direccion" to _uiState.value.direccion,
            "encargadoNombre" to _uiState.value.encargadoNombre,
            "encargadoContacto" to _uiState.value.encargadoContacto,
            "encargadoEmail" to _uiState.value.encargadoEmail
        )
        db.collection("tiendas").document(uid).update(data)
    }

    fun cambiarEstado(nuevoEstado: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("tiendas").document(uid).update("estadoLocal", nuevoEstado)
    }
}