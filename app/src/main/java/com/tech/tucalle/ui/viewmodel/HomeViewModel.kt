package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

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
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val rol = userData["rol"] as? String ?: "USUARIO"

                    val collectionName = when (rol) {
                        "TIENDA"   -> "tiendas"
                        "QUALITY"  -> "qualities"
                        "ADMIN"    -> "admins"
                        else       -> "usuarios"
                    }

                    db.collection(collectionName).document(uid).set(userData)
                        .addOnSuccessListener { onSuccess(rol) }
                        .addOnFailureListener { e -> onFailure("Error al guardar perfil: ${e.message}") }
                } else {
                    onFailure(task.exception?.message ?: "Error de registro")
                }
            }
    }

    // ==========================================
    // 2. LOGIN EN CASCADA (busca por colecciones)
    // ==========================================
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
                    onFailure(task.exception?.message ?: "Credenciales incorrectas")
                }
            }
    }

    private fun buscarRolEnColecciones(
        uid: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Orden: usuario → tienda → quality → admin
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
                                    db.collection("admins").document(uid).get()
                                        .addOnSuccessListener { docA ->
                                            if (docA.exists()) {
                                                onSuccess("ADMIN")
                                            } else {
                                                onFailure("Perfil no encontrado en ninguna colección")
                                            }
                                        }
                                }
                            }
                    }
                }
            }
        }
    }

    // ==========================================
    // 3. LOGIN GOOGLE (solo crea perfil USUARIO)
    // ==========================================
    fun checkAndCreateGoogleUser(onSuccess: (rol: String) -> Unit) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    val defaultUserData = mapOf(
                        "uid"      to uid,
                        "nombre"   to (currentUser.displayName ?: "Usuario Google"),
                        "email"    to (currentUser.email ?: ""),
                        "rol"      to "USUARIO",
                        "fotoUrl"  to (currentUser.photoUrl?.toString() ?: ""),
                        "antiguedad" to System.currentTimeMillis()
                    )
                    db.collection("usuarios").document(uid).set(defaultUserData)
                        .addOnSuccessListener { onSuccess("USUARIO") }
                } else {
                    onSuccess("USUARIO")
                }
            }
    }

    // ==========================================
    // 4. DATOS DE PRUEBA
    // Alineados con los nuevos DTOs:
    //   - "calificacionGeneral" (antes "calificacion")
    //   - "calificacionPlato"   (antes "calificacion" en platos)
    //   - "estado": "APROBADO"  (obligatorio para aparecer en los carruseles)
    //   - "nombreTienda" en cada plato (evita segunda consulta en cards)
    //   - "esPlatoBase": true   (son los 5 platos iniciales, sin pasar por aprobación)
    // ==========================================
    fun inyectarDatosDePrueba(onResult: (String) -> Unit) {

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

        // Cada Triple: (idDocumento, nombreTienda, Pair(lat, lng))
        val huariquesList = listOf(
            Triple("si_ronald",   "El Ceviche de Ronald",      Pair(-12.0883, -77.0312)),
            Triple("si_chinito",  "Sanguchería El Chinito",     Pair(-12.0872, -77.0321)),
            Triple("mf_preferida","La Preferida",               Pair(-12.1278, -77.0198)),
            Triple("ba_juanito",  "Juanito de Barranco",        Pair(-12.1496, -77.0207)),
            Triple("cl_cordano",  "Restaurante Cordano",        Pair(-12.0447, -77.0289)),
            Triple("li_ceci",     "Chanfainita de la Tía Ceci", Pair(-12.0860, -77.0345)),
            Triple("su_tito",     "Pollería Don Tito",          Pair(-12.1102, -77.0003)),
            Triple("ma_siete",    "Siete Sopas",                Pair(-12.0911, -77.0540))
        )

        // Platos base representativos por tipo de huarique
        val nombresPlatos = listOf(
            "Ceviche Mixto",
            "Sanguchón de Pollo",
            "Arroz con Leche",
            "Lomo Saltado",
            "Caldo de Gallina"
        )

        huariquesList.forEachIndexed { index, (id, nombre, coords) ->

            val distrito = when {
                id.startsWith("si") -> "San Isidro"
                id.startsWith("mf") -> "Miraflores"
                id.startsWith("ba") -> "Barranco"
                id.startsWith("cl") -> "Cercado de Lima"
                id.startsWith("li") -> "Lince"
                id.startsWith("su") -> "Surco"
                id.startsWith("ma") -> "Magdalena"
                else                -> "Lima"
            }

            // calificacionGeneral: entre 3.5 y 5.0
            val calificacionGeneral = (35..50).random().toDouble() / 10.0

            // ── TIENDA ──────────────────────────────────────────────────
            // CAMBIOS RESPECTO AL ORIGINAL:
            //   "calificacion"        →  "calificacionGeneral"   (nombre del campo en TiendaDTO)
            //   sin "estado"          →  "estado": "APROBADO"    (requerido por HomeViewModel)
            val tiendaData = hashMapOf(
                "nombre"              to nombre,
                "razonSocial"         to "$nombre SAC",
                "estado"              to "APROBADO",          // ← NUEVO: sin esto no aparece en Home
                "estadoLocal"         to "Abierto",
                "calificacionGeneral" to calificacionGeneral, // ← RENOMBRADO: era "calificacion"
                "portadaUrl"          to fotosHuariques[index % fotosHuariques.size],
                "horario"             to "10:00 AM – 11:00 PM",
                "diasApertura"        to listOf("L", "M", "X", "J", "V", "S"),
                "etiquetas"           to listOf("Huarique", "Recomendado", distrito),
                "seguidores"          to 0,
                "totalReseñas"        to 0,
                "plan"                to "Impulso",
                "direccion"           to mapOf(
                    "texto"     to "$distrito, Lima",
                    "latitud"   to coords.first,
                    "longitud"  to coords.second
                )
            )
            db.collection("tiendas").document(id).set(tiendaData)

            // ── PLATOS BASE (5 por tienda) ───────────────────────────────
            // CAMBIOS RESPECTO AL ORIGINAL:
            //   "calificacion"        →  "calificacionPlato"  (nombre del campo en PlatoDTO)
            //   sin "estado"          →  "estado": "APROBADO" (requerido por HomeViewModel)
            //   sin "nombreTienda"    →  "nombreTienda": nombre (evita segunda consulta en cards)
            //   sin "esPlatoBase"     →  "esPlatoBase": true   (marca platos iniciales)
            nombresPlatos.forEachIndexed { platoIndex, nombrePlato ->
                val precioOriginal  = (25..55).random().toDouble()
                val precioDescuento = precioOriginal * 0.80    // 20% de descuento fijo
                val calificacionPlato = (38..50).random().toDouble() / 10.0

                val platoData = hashMapOf(
                    "idTienda"            to id,
                    "nombreTienda"        to nombre,           // ← NUEVO: desnormalizado para cards
                    "nombre"              to nombrePlato,
                    "descripcion"         to "Plato especial de $nombre",
                    "precioOriginal"      to precioOriginal,
                    "precioDescuento"     to precioDescuento,
                    "calificacionPlato"   to calificacionPlato, // ← RENOMBRADO: era "calificacion"
                    "estado"              to "APROBADO",        // ← NUEVO: sin esto no aparece en Home
                    "esPlatoBase"         to true,              // ← NUEVO: marca los 5 iniciales
                    "totalRecomendaciones" to 0,
                    "imagenUrl"           to fotosPlatos[platoIndex % fotosPlatos.size],
                    "creadoEn"            to System.currentTimeMillis()
                )
                db.collection("platos").add(platoData)
            }
        }

        onResult("✓ ${huariquesList.size} huariques y ${huariquesList.size * 5} platos inyectados correctamente")
    }
}