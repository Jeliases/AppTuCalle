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

    private fun buscarRolEnColecciones(
        uid: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("usuarios").document(uid).get().addOnSuccessListener { docU ->
            if (docU.exists()) {
                onSuccess("USUARIO")
            } else {
                db.collection("tiendas").document(uid).get().addOnSuccessListener { docT ->
                    if (docT.exists()) {
                        onSuccess("TIENDA")
                    } else {
                        db.collection("qualities").document(uid).get()
                            .addOnSuccessListener { docQ ->
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

    fun inyectarDatosDePrueba(onResult: (String) -> Unit) {
        // Listas de imágenes reales para variedad visual
        val fotosHuariques = listOf(
            "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4",
            "https://images.unsplash.com/photo-1552566626-52f8b828add9",
            "https://images.unsplash.com/photo-1514933651103-005eec06c04b",
            "https://images.unsplash.com/photo-1555396273-367ea4eb4db5",
            "https://images.unsplash.com/photo-1504674900247-0877df9cc836"
        )

        val fotosPlatos = listOf(
            "https://images.unsplash.com/photo-1513104890138-7c749659a591",
            "https://images.unsplash.com/photo-1598514982205-f36b96d1e8d4",
            "https://images.unsplash.com/photo-1563379091339-03b21bc4a4f8",
            "https://images.unsplash.com/photo-1550507992-06316ec53ca0"
        )

        val guariquesList = listOf(
            Triple("si_ronald", "El Ceviche de Ronald", Pair(-12.0883, -77.0312)),
            Triple("si_chinito", "Sanguchería El Chinito", Pair(-12.0872, -77.0321)),
            Triple("mf_preferida", "La Preferida", Pair(-12.1278, -77.0198)),
            Triple("ba_juanito", "Juanito de Barranco", Pair(-12.1496, -77.0207)),
            Triple("cl_cordano", "Restaurante Cordano", Pair(-12.0447, -77.0289)),
            Triple("li_ceci", "Chanfainita de la Tía Ceci", Pair(-12.0860, -77.0345)),
            Triple("su_tito", "Pollería Don Tito", Pair(-12.1102, -77.0003)),
            Triple("ma_siete", "Siete Sopas", Pair(-12.0911, -77.0540))
        )

        guariquesList.forEachIndexed { index, (id, nombre, coords) ->
            val distrito = when {
                id.startsWith("si") -> "San Isidro"
                id.startsWith("mf") -> "Miraflores"
                id.startsWith("ba") -> "Barranco"
                id.startsWith("cl") -> "Cercado"
                id.startsWith("li") -> "Lince"
                id.startsWith("su") -> "Surco"
                id.startsWith("ma") -> "Magdalena"
                else -> "Lima"
            }

            // Calificación aleatoria entre 3.5 y 5.0 para probar el ordenamiento (Sorting)
            val ratingAleatorio = (35..50).random().toDouble() / 10.0

            // 1. INYECTAR TIENDA
            val tiendaData = hashMapOf(
                "nombre" to nombre,
                "calificacion" to ratingAleatorio, // <-- Rating variado
                "portadaUrl" to fotosHuariques[index % fotosHuariques.size], // <-- Foto variada
                "horario" to "Abierto hasta las 11PM",
                "etiquetas" to listOf("Huarique", "Recomendado", distrito),
                "direccion" to mapOf(
                    "texto" to "$distrito, Lima",
                    "latitud" to coords.first,
                    "longitud" to coords.second
                )
            )
            db.collection("tiendas").document(id).set(tiendaData)

            // 2. INYECTAR PLATOS (CON PRECIOS CORREGIDOS)
            val pOriginal = (30..50).random().toDouble()
            val pDescuento = pOriginal * 0.8 // 20% de descuento automático

            val platoData = hashMapOf(
                "idTienda" to id,
                "nombre" to "Especial de $nombre",
                "precioOriginal" to pOriginal,
                "precioDescuento" to pDescuento, // <-- AHORA SÍ SE GUARDA
                "calificacion" to 4.8,
                "imagenUrl" to fotosPlatos[index % fotosPlatos.size]
            )
            db.collection("platos").add(platoData)
        }
        onResult("Datos inyectados con éxito")
    }
}