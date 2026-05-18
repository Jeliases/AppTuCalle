package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    val seguidores: Int = 0,
    val totalResenas: Int = 0,
    val antiguedad: Long = 0L,
    val plan: String = "Impulso", // Exclusivo para tiendas
    val isLoading: Boolean = true
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        cargarPerfilGlobal()
    }

    private fun cargarPerfilGlobal() {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        // 1. Buscamos en Quality primero (ya que estamos enfocados en ese flujo)
        db.collection("qualities").document(uid).addSnapshotListener { snap, _ ->
            if (snap != null && snap.exists()) {
                _uiState.update { it.copy(
                    uid = uid,
                    nombre = snap.getString("nombre") ?: "",
                    apellidos = snap.getString("apellidos") ?: "",
                    correo = snap.getString("email") ?: currentUser.email ?: "",
                    celular = snap.getString("celular") ?: "",
                    fotoUrl = snap.getString("fotoUrl") ?: "",
                    rol = "QUALITY",
                    seguidores = snap.getLong("seguidores")?.toInt() ?: 0,
                    totalResenas = snap.getLong("totalReseñas")?.toInt() ?: 0,
                    isLoading = false
                )}
                return@addSnapshotListener
            }
        }

        // 2. Si no es Quality, buscamos en Usuario
        db.collection("usuarios").document(uid).addSnapshotListener { snap, _ ->
            if (snap != null && snap.exists()) {
                _uiState.update { it.copy(
                    uid = uid,
                    nombre = snap.getString("nombre") ?: "",
                    apellidos = snap.getString("apellidos") ?: "",
                    correo = snap.getString("email") ?: currentUser.email ?: "",
                    celular = snap.getString("celular") ?: "",
                    fotoUrl = snap.getString("fotoUrl") ?: "",
                    rol = "USUARIO",
                    totalResenas = snap.getLong("totalReseñas")?.toInt() ?: 0,
                    isLoading = false
                )}
                return@addSnapshotListener
            }
        }

        // 3. Y finalmente en Tiendas
        db.collection("tiendas").document(uid).addSnapshotListener { snap, _ ->
            if (snap != null && snap.exists()) {
                _uiState.update { it.copy(
                    uid = uid,
                    nombre = snap.getString("nombre") ?: "",
                    correo = snap.getString("email") ?: currentUser.email ?: "",
                    celular = snap.getString("celular") ?: "",
                    fotoUrl = snap.getString("logoUrl") ?: snap.getString("portadaUrl") ?: "",
                    rol = "TIENDA",
                    seguidores = snap.getLong("seguidores")?.toInt() ?: 0,
                    totalResenas = snap.getLong("totalReseñas")?.toInt() ?: 0,
                    plan = snap.getString("plan") ?: "Impulso",
                    isLoading = false
                )}
            }
        }
    }

    fun cerrarSesion(onLogoutSuccess: () -> Unit) {
        auth.signOut()
        onLogoutSuccess()
    }
}