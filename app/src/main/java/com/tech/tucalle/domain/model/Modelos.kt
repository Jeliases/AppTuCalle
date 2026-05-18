package com.tech.tucalle.data

// ==========================================
// 1. PERFILES (Trinidad de Usuarios)
// ==========================================

data class UsuarioDTO(
    val uid: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val celular: String = "",
    val rol: String = "USUARIO",
    val fotoUrl: String = "",
    val descripcion: String = "",
    val fechaNacimiento: String = "",       // Formato: "30/04/1998"
    val dni: String = "",
    val antiguedad: Long = 0L,              // Timestamp de registro
    val totalHuariques: Int = 0,            // Cuántos huariques sigue
    val totalReseñas: Int = 0,
    val recibirPromociones: Boolean = false,
    val logros: List<String> = emptyList()  // IDs de LogroDTO obtenidos
)

data class QualityDTO(
    val uid: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val celular: String = "",
    val rol: String = "QUALITY",
    val fotoUrl: String = "",
    val descripcion: String = "",           // Bio del Quality
    val fechaNacimiento: String = "",
    val dni: String = "",
    val antiguedad: Long = 0L,
    val nivel: String = "Explorador",       // Gamificación futura
    val seguidores: Int = 0,
    val totalReseñas: Int = 0,
    val misHuariques: List<String> = emptyList(), // IDs de tiendas que sigue
    // Disponibilidad horaria (para mostrar en perfil)
    val diasDisponibles: List<String> = emptyList(), // ["L","M","X","J","V"]
    val horaDisponibleDesde: String = "",   // Formato 12h: "08:00 AM"
    val horaDisponibleHasta: String = "",   // Formato 12h: "09:30 PM"
    val logros: List<String> = emptyList()
)

data class TiendaDTO(
    val id: String = "",
    val nombre: String = "",
    val razonSocial: String = "",
    val email: String = "",
    val celular: String = "",
    val whatsapp: String = "",
    val logoUrl: String = "",
    val portadaUrl: String = "",
    val plan: String = "Impulso",           // "Impulso" | "Premium"
    // IMPORTANTE: estado controla visibilidad en Home
    // Solo aparecen en carruseles las tiendas con estado == "APROBADO"
    val estado: String = "PENDIENTE",       // "PENDIENTE" | "APROBADO" | "RECHAZADO"
    val estadoLocal: String = "Cerrado",    // "Abierto" | "Cerrado" (el dueño lo cambia)
    val horario: String = "",               // Texto libre: "10PM – 12AM"
    val diasApertura: List<String> = emptyList(),
    // calificacionGeneral = promedio CHAS de todas las recomendaciones recibidas
    // Se actualiza cada vez que un Quality deja una recomendación
    val calificacionGeneral: Double = 0.0,
    val totalReseñas: Int = 0,
    val seguidores: Int = 0,
    val etiquetas: List<String> = emptyList(),
    val direccion: Map<String, Any>? = null,
    // Información del encargado
    val encargadoNombre: String = "",
    val encargadoContacto: String = "",
    val encargadoEmail: String = ""
) {
    fun obtenerDistrito(): String {
        return direccion?.get("texto")?.toString() ?: "Ubicación desconocida"
    }

    fun obtenerLatitud(): Double {
        return (direccion?.get("latitud") as? Double) ?: 0.0
    }

    fun obtenerLongitud(): Double {
        return (direccion?.get("longitud") as? Double) ?: 0.0
    }
}

// ==========================================
// 2. SISTEMA CORE (Platos y Recomendaciones)
// ==========================================

data class PlatoDTO(
    val id: String = "",
    val idTienda: String = "",
    // nombreTienda se guarda desnormalizado para no necesitar una segunda
    // consulta cada vez que se muestra una card en el carrusel "Populares"
    val nombreTienda: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precioOriginal: Double = 0.0,
    val precioDescuento: Double = 0.0,
    // calificacionPlato = promedio de las valoraciones de plato de Qualities
    // Es independiente de calificacionGeneral de la tienda
    val calificacionPlato: Double = 0.0,
    val totalRecomendaciones: Int = 0,
    // estado controla visibilidad: solo "APROBADO" aparece en el carrusel
    // Los primeros 5 platos de una tienda se crean como "APROBADO" directamente
    // Los adicionales empiezan en "PENDIENTE" hasta que Admin los aprueba
    val estado: String = "PENDIENTE",       // "PENDIENTE" | "APROBADO" | "RECHAZADO"
    val esPlatoBase: Boolean = false,       // true = uno de los 5 platos iniciales
    val imagenUrl: String = "",
    val creadoEn: Long = System.currentTimeMillis(),
    val aprobadoPor: String = ""            // UID del admin que lo aprobó
)

data class RecomendacionQualityDTO(
    val id: String = "",
    val idQuality: String = "",
    val nombreQuality: String = "",         // Desnormalizado para cards de reseña
    val fotoQuality: String = "",           // Desnormalizado para cards de reseña
    val idTienda: String = "",
    // CHAS = Confort, Higiene, Atención, Sabrosura (cada uno de 1.0 a 5.0)
    // La calificacionGeneral de la tienda se calcula como promedio de estos 4
    val chas: Map<String, Double> = mapOf(
        "confort" to 0.0,
        "higiene" to 0.0,
        "atencion" to 0.0,
        "sabrosura" to 0.0
    ),
    // platosRecomendados: IDs de PlatoDTO seleccionados en el dropdown
    // Puede ser 1 o varios platos del mismo huarique
    val platosRecomendados: List<String> = emptyList(),
    // estrellaPlato: valoración específica del/los plato(s) seleccionados
    // Es independiente del promedio CHAS del huarique
    val estrellaPlato: Double = 0.0,
    val comentario: String = "",
    val etiquetas: List<String> = emptyList(),
    val fecha: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val dislikes: Int = 0
)

// ==========================================
// 3. ECOSISTEMA SOCIAL (Likes, Follows, Favs)
// ==========================================

data class FollowDTO(
    val idSeguidor: String = "",
    val idSeguido: String = "",
    // tipo describe la dirección exacta de la relación
    val tipo: String = "USUARIO_TIENDA",    // "USUARIO_TIENDA" | "USUARIO_QUALITY" | "QUALITY_TIENDA"
    val fecha: Long = System.currentTimeMillis()
)

data class LikeDTO(
    val idUsuario: String = "",
    val referenciaId: String = "",          // ID del plato, tienda o recomendación
    val tipoReferencia: String = "PLATO",   // "PLATO" | "TIENDA" | "RECOMENDACION"
    val fecha: Long = System.currentTimeMillis()
)

// FavoritoDTO usa targetId + tipoTarget en lugar de campos separados
// Así un favorito puede ser una tienda O un plato con la misma estructura
data class FavoritoDTO(
    val idUsuario: String = "",
    val targetId: String = "",              // ID del plato o tienda
    val tipoTarget: String = "TIENDA",      // "TIENDA" | "PLATO"
    val fecha: Long = System.currentTimeMillis()
)

data class LogroDTO(
    val id: String = "",
    val nombre: String = "",                // "Fundador", "Recomendador", "Explorador"…
    val descripcion: String = "",
    val iconoUrl: String = "",              // Placeholder hasta que exista el asset real
    val condicion: String = ""              // Regla futura para asignación automática
)

// ==========================================
// 4. FLUJO ADMINISTRATIVO (Aprobaciones)
// ==========================================

data class AprobacionDTO(
    val id: String = "",
    val tipo: String = "PLATO",             // "PLATO" | "TIENDA"
    val referenciaId: String = "",          // ID del plato o tienda a evaluar
    val solicitadoPor: String = "",         // UID del dueño de la tienda
    val estado: String = "PENDIENTE",       // "PENDIENTE" | "APROBADO" | "RECHAZADO"
    val motivoRechazo: String = "",         // Solo se rellena si estado == "RECHAZADO"
    val revisadoPor: String = "",           // UID del admin que resolvió
    val fechaSolicitud: Long = System.currentTimeMillis(),
    val fechaResolucion: Long? = null
)

// ==========================================
// 5. BANNERS HOME
// ==========================================

// imageUrl: nombre exacto del campo en Firestore (case-sensitive)
data class Banner(
    val imageUrl: String = "",
    val activo: Boolean = false,
    val orden: Int = 0,
    val titulo: String = "",
    val linkTiendaId: String = ""           // Opcional: abre el mural de esa tienda
)