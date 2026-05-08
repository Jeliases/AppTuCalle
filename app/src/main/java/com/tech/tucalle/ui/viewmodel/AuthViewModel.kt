package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // REGISTRO DE USUARIOS Y TIENDAS CON ROL
    fun registerUserWithRole(
        email: String,
        pass: String,
        userData: Map<String, Any>,
        onSuccess: (rol: String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    // Guarda el perfil completo con el ROL en Firestore
                    db.collection("usuarios").document(uid).set(userData)
                        .addOnSuccessListener {
                            val rol = userData["rol"] as? String ?: "USUARIO"
                            onSuccess(rol)
                        }
                        .addOnFailureListener { e ->
                            onFailure("Error al guardar perfil: ${e.message}")
                        }
                } else {
                    onFailure(task.exception?.message ?: "Error de registro")
                }
            }
    }

    // LOGIN CON VERIFICACIÓN DE ROL
    fun loginWithRole(
        email: String,
        pass: String,
        onSuccess: (rol: String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    // Obtiene el rol guardado para saber qué pantalla renderizar
                    db.collection("usuarios").document(uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val rol = document.getString("rol") ?: "USUARIO"
                                onSuccess(rol)
                            } else {
                                onFailure("Perfil no encontrado en la base de datos")
                            }
                        }
                        .addOnFailureListener { e ->
                            onFailure("Error de base de datos: ${e.message}")
                        }
                } else {
                    onFailure(task.exception?.message ?: "Error de credenciales")
                }
            }
    }

    // LOGIN CON GOOGLE (Por defecto asigna el rol USUARIO)
    fun checkAndCreateGoogleUser(onSuccess: (rol: String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        val defaultUserData = mapOf(
                            "uid" to uid,
                            "nombre" to (currentUser.displayName ?: "Usuario Google"),
                            "email" to (currentUser.email ?: ""),
                            "rol" to "USUARIO"
                        )
                        db.collection("usuarios").document(uid).set(defaultUserData)
                            .addOnSuccessListener { onSuccess("USUARIO") }
                    } else {
                        val rol = document.getString("rol") ?: "USUARIO"
                        onSuccess(rol)
                    }
                }
        }
    }
}