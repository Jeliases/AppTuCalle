package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.Date

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // 1. REGISTRO DINÁMICO
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
                    val rol = userData["rol"] as? String ?: "USUARIO"

                    val collectionName = when (rol) {
                        "TIENDA" -> "tiendas"
                        "QUALITY" -> "qualities"
                        else -> "usuarios"
                    }

                    db.collection(collectionName).document(uid).set(userData)
                        .addOnSuccessListener { onSuccess(rol) }
                        .addOnFailureListener { e -> onFailure("Error al guardar perfil: ${e.message}") }
                } else {
                    onFailure(task.exception?.message ?: "Error de registro")
                }
            }
    }

    // 2. LOGIN EN CASCADA
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
                    buscarRolEnColecciones(uid, onSuccess, onFailure)
                } else {
                    onFailure(task.exception?.message ?: "Error de credenciales")
                }
            }
    }

    private fun buscarRolEnColecciones(uid: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        db.collection("usuarios").document(uid).get().addOnSuccessListener { docU ->
            if (docU.exists()) {
                onSuccess("USUARIO")
            } else {
                db.collection("tiendas").document(uid).get().addOnSuccessListener { docT ->
                    if (docT.exists()) {
                        onSuccess("TIENDA")
                    } else {
                        db.collection("qualities").document(uid).get().addOnSuccessListener { docQ ->
                            if (docQ.exists()) {
                                onSuccess("QUALITY")
                            } else {
                                onFailure("Perfil no encontrado")
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. LOGIN GOOGLE
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
                        onSuccess("USUARIO")
                    }
                }
        }
    }

    // 4. INYECCIÓN COMPLETA (Ahora incluye los Banners del Carrusel)
    fun inyectarDatosDePrueba(onResult: (String) -> Unit) {
        // Quality
        val qualityData = hashMapOf(
            "nombre" to "Sofia Perez", "rol" to "QUALITY",
            "fotoPerfil" to "https://images.unsplash.com/photo-1438761681033-6461ffad8d80"
        )
        db.collection("qualities").document("mock_user_sofia").set(qualityData)

        // Tiendas
        val luisData = hashMapOf(
            "nombre" to "Donde Luis - Broaster", "rol" to "TIENDA", "calificacion" to 4.8,
            "portadaUrl" to "https://images.unsplash.com/photo-1552566626-52f8b828add9"
        )
        db.collection("tiendas").document("mock_tienda_luis").set(luisData)

        val flashData = hashMapOf(
            "nombre" to "Salchichones Flash", "rol" to "TIENDA", "calificacion" to 4.5,
            "portadaUrl" to "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4"
        )
        db.collection("tiendas").document("mock_tienda_flash").set(flashData)

        // Platos y Mural
        db.collection("platos").add(hashMapOf("idTienda" to "mock_tienda_luis", "nombre" to "Mostrito Poderoso", "precioDescuento" to 12.0))
        db.collection("mural_comentarios").add(hashMapOf("idTienda" to "mock_tienda_luis", "texto" to "¡Excelente!", "fecha" to Date()))

        // --- NUEVO: BANNERS PARA EL CARRUSEL ---
        val banner1 = hashMapOf("imageUrl" to "https://images.unsplash.com/photo-1504674900247-0877df9cc836", "activo" to true)
        val banner2 = hashMapOf("imageUrl" to "https://images.unsplash.com/photo-1555939594-58d7cb561ad1", "activo" to true)
        val banner3 = hashMapOf("imageUrl" to "https://images.unsplash.com/photo-1493770348161-369560ae357d", "activo" to true)

        db.collection("banners_home").document("banner_promo_1").set(banner1)
        db.collection("banners_home").document("banner_promo_2").set(banner2)
        db.collection("banners_home").document("banner_promo_3").set(banner3)
            .addOnSuccessListener { onResult("¡Datos y Banners inyectados!") }
            .addOnFailureListener { e -> onResult("Error: ${e.message}") }
    }
}