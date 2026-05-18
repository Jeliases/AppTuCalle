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

    // ==========================================
    // 4. INYECTAR DATOS DE PRUEBA — VERSIÓN COMPLETA
    //
    // ✅ CAMPOS CRÍTICOS PARA QUE FUNCIONEN LOS FILTROS:
    //    tiendas → "estado": "APROBADO"   (HomeViewModel: whereEqualTo)
    //    platos  → "estado": "APROBADO"   (HomeViewModel: whereEqualTo)
    //
    // ✅ CAMPOS RENOMBRADOS (la versión anterior los tenía mal):
    //    "calificacion" → "calificacionGeneral"  en tiendas
    //    "calificacion" → "calificacionPlato"    en platos
    //
    // ✅ CAMPOS NUEVOS:
    //    platos → "nombreTienda"  (desnormalizado, evita segunda consulta en DishCard)
    //    platos → "esPlatoBase"   (true = plato inicial, no requiere aprobación)
    //    platos → "descripcion"
    //    tiendas → "estadoLocal", "razonSocial", "plan", "seguidores", "totalReseñas"
    // ==========================================
    fun inyectarDatosDePrueba(onResult: (String) -> Unit) {

        val fotosHuariques = listOf(
            "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800",
            "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800",
            "https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=800",
            "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800",
            "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800"
        )

        val fotosPlatos = listOf(
            "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600",
            "https://images.unsplash.com/photo-1598514982205-f36b96d1e8d4?w=600",
            "https://images.unsplash.com/photo-1563379091339-03b21bc4a4f8?w=600",
            "https://images.unsplash.com/photo-1550507992-06316ec53ca0?w=600",
            "https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=600"
        )

        val huariquesList = listOf(
            Triple("si_ronald",    "El Ceviche de Ronald",      Pair(-12.0883, -77.0312)),
            Triple("si_chinito",   "Sanguchería El Chinito",     Pair(-12.0872, -77.0321)),
            Triple("mf_preferida", "La Preferida",               Pair(-12.1278, -77.0198)),
            Triple("ba_juanito",   "Juanito de Barranco",        Pair(-12.1496, -77.0207)),
            Triple("cl_cordano",   "Restaurante Cordano",        Pair(-12.0447, -77.0289)),
            Triple("li_ceci",      "Chanfainita de la Tía Ceci", Pair(-12.0860, -77.0345)),
            Triple("su_tito",      "Pollería Don Tito",          Pair(-12.1102, -77.0003)),
            Triple("ma_siete",     "Siete Sopas",                Pair(-12.0911, -77.0540))
        )

        val nombresPlatos = listOf(
            "Ceviche Mixto",
            "Lomo Saltado",
            "Arroz con Leche",
            "Caldo de Gallina",
            "Anticuchos"
        )

        val descripciones = listOf(
            "Preparado al momento con los mejores ingredientes frescos del día.",
            "Receta de la casa con sazón de toda la vida.",
            "El favorito de nuestros clientes, no te lo pierdas.",
            "Hecho con amor y tradición limeña desde hace años.",
            "Un clásico de huarique que no falla nunca."
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

            val calificacionGeneral = (35..50).random().toDouble() / 10.0

            // ── TIENDA ──────────────────────────────────────────────
            val tiendaData = hashMapOf(
                "nombre"              to nombre,
                "razonSocial"         to "$nombre SAC",
                "email"               to "",
                "celular"             to "9${(10000000..99999999).random()}",
                "whatsapp"            to "9${(10000000..99999999).random()}",
                "portadaUrl"          to fotosHuariques[index % fotosHuariques.size],
                "logoUrl"             to fotosHuariques[(index + 1) % fotosHuariques.size],
                "plan"                to "Impulso",
                "estado"              to "APROBADO",       // ✅ CRÍTICO
                "estadoLocal"         to "Abierto",
                "calificacionGeneral" to calificacionGeneral, // ✅ nombre correcto
                "totalReseñas"        to 0,
                "seguidores"          to (0..120).random(),
                "horario"             to "10:00 AM – 11:00 PM",
                "diasApertura"        to listOf("L", "M", "X", "J", "V", "S"),
                "etiquetas"           to listOf("Huarique", "Recomendado", distrito),
                "direccion"           to mapOf(
                    "texto"     to "$distrito, Lima",
                    "latitud"   to coords.first,
                    "longitud"  to coords.second
                ),
                "encargadoNombre"     to "Encargado de $nombre",
                "encargadoContacto"   to "9${(10000000..99999999).random()}",
                "encargadoEmail"      to "contacto@$id.com"
            )

            db.collection("tiendas").document(id).set(tiendaData)

            // ── 5 PLATOS BASE POR TIENDA ────────────────────────────
            nombresPlatos.forEachIndexed { platoIndex, nombrePlato ->

                val precioOriginal    = (25..55).random().toDouble()
                val precioDescuento   = precioOriginal * 0.80
                val calificacionPlato = (38..50).random().toDouble() / 10.0

                val platoData = hashMapOf(
                    "idTienda"             to id,
                    "nombreTienda"         to nombre,       // ✅ desnormalizado para DishCard
                    "nombre"               to nombrePlato,
                    "descripcion"          to descripciones[platoIndex % descripciones.size],
                    "precioOriginal"       to precioOriginal,
                    "precioDescuento"      to precioDescuento,
                    "calificacionPlato"    to calificacionPlato, // ✅ nombre correcto
                    "totalRecomendaciones" to 0,
                    "estado"               to "APROBADO",   // ✅ CRÍTICO
                    "esPlatoBase"          to true,         // ✅ plato inicial
                    "imagenUrl"            to fotosPlatos[platoIndex % fotosPlatos.size],
                    "creadoEn"             to System.currentTimeMillis(),
                    "aprobadoPor"          to "sistema"
                )

                db.collection("platos").add(platoData)
            }
        }

        onResult("✅ ${huariquesList.size} huariques y ${huariquesList.size * nombresPlatos.size} platos inyectados correctamente")
    }

    // ==========================================
    // 5. UTILIDADES DE SESIÓN
    // ==========================================
    fun cerrarSesion() {
        auth.signOut()
    }

    fun obtenerUidActual(): String = auth.currentUser?.uid ?: ""

    fun obtenerEmailActual(): String = auth.currentUser?.email ?: ""

    fun estaLogueado(): Boolean = auth.currentUser != null
}