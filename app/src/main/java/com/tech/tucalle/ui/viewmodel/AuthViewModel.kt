package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // ==========================================
    // 1. REGISTRO DINÁMICO
    // ==========================================
    fun registerUserWithRole(
        email: String,
        pass: String,
        userData: Map<String, Any>,
        onSuccess: (rol: String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Limpiamos los espacios en blanco accidentales del emulador
        val emailLimpio = email.trim()
        val passLimpio = pass.trim()

        auth.createUserWithEmailAndPassword(emailLimpio, passLimpio)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val rol = userData["rol"] as? String ?: "USUARIO"

                    val collectionName = when (rol) {
                        "TIENDA"  -> "tiendas"
                        "QUALITY" -> "qualities"
                        "ADMIN"   -> "admins"
                        else      -> "usuarios"
                    }

                    // uid y antiguedad se añaden automáticamente
                    val dataConUid = userData.toMutableMap().apply {
                        put("uid", uid)
                        put("antiguedad", System.currentTimeMillis())
                    }

                    db.collection(collectionName).document(uid).set(dataConUid)
                        .addOnSuccessListener { onSuccess(rol) }
                        .addOnFailureListener { e -> onFailure("Error al guardar perfil: ${e.message}") }
                } else {
                    onFailure(task.exception?.message ?: "Error de registro")
                }
            }
    }

    // ==========================================
    // 2. LOGIN EN CASCADA
    // Orden: usuarios → tiendas → qualities → admins
    // ==========================================
    fun loginWithRole(
        email: String,
        pass: String,
        onSuccess: (rol: String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Limpiamos los espacios en blanco accidentales del emulador
        val emailLimpio = email.trim()
        val passLimpio = pass.trim()

        auth.signInWithEmailAndPassword(emailLimpio, passLimpio)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    buscarRolEnColecciones(uid, onSuccess, onFailure)
                } else {
                    onFailure(task.exception?.message ?: "Credenciales incorrectas")
                }
            }
    }

    private fun buscarRolEnColecciones(
        uid: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { docU ->
                if (docU.exists()) {
                    onSuccess("USUARIO")
                } else {
                    db.collection("tiendas").document(uid).get()
                        .addOnSuccessListener { docT ->
                            if (docT.exists()) {
                                onSuccess("TIENDA")
                            } else {
                                db.collection("qualities").document(uid).get()
                                    .addOnSuccessListener { docQ ->
                                        if (docQ.exists()) {
                                            onSuccess("QUALITY")
                                        } else {
                                            db.collection("admins").document(uid).get()
                                                .addOnSuccessListener { docA ->
                                                    if (docA.exists()) {
                                                        onSuccess("ADMIN")
                                                    } else {
                                                        onFailure("Perfil no encontrado en ninguna colección")
                                                    }
                                                }
                                                .addOnFailureListener { onFailure("Error al buscar admin: ${it.message}") }
                                        }
                                    }
                                    .addOnFailureListener { onFailure("Error al buscar quality: ${it.message}") }
                            }
                        }
                        .addOnFailureListener { onFailure("Error al buscar tienda: ${it.message}") }
                }
            }
            .addOnFailureListener { onFailure("Error de conexión: ${it.message}") }
    }

    // ==========================================
    // 3. LOGIN GOOGLE (crea perfil USUARIO si no existe)
    // ==========================================
    fun checkAndCreateGoogleUser(onSuccess: (rol: String) -> Unit) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    val defaultUserData = mapOf(
                        "uid"             to uid,
                        "nombre"          to (currentUser.displayName ?: "Usuario Google"),
                        "apellidos"       to "",
                        "email"           to (currentUser.email ?: ""),
                        "celular"         to "",
                        "fotoUrl"         to (currentUser.photoUrl?.toString() ?: ""),
                        "rol"             to "USUARIO",
                        "antiguedad"      to System.currentTimeMillis(),
                        "totalHuariques"  to 0,
                        "totalReseñas"    to 0,
                        "logros"          to emptyList<String>()
                    )
                    db.collection("usuarios").document(uid).set(defaultUserData)
                        .addOnSuccessListener { onSuccess("USUARIO") }
                        .addOnFailureListener { onSuccess("USUARIO") }
                } else {
                    onSuccess("USUARIO")
                }
            }
            .addOnFailureListener { onSuccess("USUARIO") }
    }
    fun uploadImageToStorage(uri: Uri, fileName: String, onSuccess: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("imagenes_tiendas/${System.currentTimeMillis()}_$fileName.jpg")

        viewModelScope.launch {
            try {
                // 1. Subir la imagen
                storageRef.putFile(uri).await()

                // 2. Obtener la URL de descarga
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // 3. Devolver la URL para que la guardes en Firestore
                onSuccess(downloadUrl)
            } catch (e: Exception) {
                // Manejar error de subida
            }
        }
    }

    suspend fun uploadImageSuspend(uri: Uri): String {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference
                .child("imagenes_tiendas/${System.currentTimeMillis()}.jpg")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            "" // Si falla, devuelve vacío
        }
    }
    // ==========================================
    // 4. UTILIDADES DE SESIÓN
    // ==========================================
    fun cerrarSesion() {
        auth.signOut()
    }

    fun obtenerUidActual(): String = auth.currentUser?.uid ?: ""

    fun obtenerEmailActual(): String = auth.currentUser?.email ?: ""

    fun estaLogueado(): Boolean = auth.currentUser != null
}