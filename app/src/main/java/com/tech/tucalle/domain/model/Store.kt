package com.tech.tucalle.domain.model

data class Store(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val celular: String = "",
    val horario: String = "",
    val horarioApertura: String = "", // Útil para editar en el perfil
    val horarioCierre: String = "",   // Útil para editar en el perfil
    val diasApertura: List<String> = emptyList(),
    val direccion: Direccion = Direccion(),
    val logoUrl: String = "",
    val portadaUrl: String = "",
    val rol: String = "TIENDA",
    val estado: String = "APROBADO",
    val antiguedad: Long = 0L,

    // 🔥 Campos para el Home y Consultas
    val calificacionGeneral: Double = 0.0,
    val totalResenas: Int = 0,
    val etiquetas: List<String> = emptyList(),

    // 🔥 Campos exclusivos del Perfil
    val seguidores: Int = 0,
    val estadoLocal: String = "Cerrado",
    val plan: String = "Impulso",
    val razonSocial: String = "",
    val encargadoNombre: String = "",
    val encargadoContacto: String = "",
    val encargadoEmail: String = ""
)

data class Direccion(
    val texto: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0
)