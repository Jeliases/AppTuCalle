package com.tech.tucalle.domain.model

data class Store(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val celular: String = "",
    val horario: String = "",

    // Horario Fijo
    val horarioApertura: String = "",
    val horarioCierre: String = "",
    val diasApertura: List<String> = emptyList(),

    // Horario Variable
    val tipoHorario: String = "FIJO",
    val horariosVariables: Map<String, Map<String, String>> = emptyMap(),

    val direccion: Direccion = Direccion(),
    val logoUrl: String = "",
    val portadaUrl: String = "",
    val rol: String = "TIENDA",
    val estado: String = "APROBADO",
    val antiguedad: Long = 0L,

    // Etiquetas validadas
    val calificacionGeneral: Double = 5.0,
    val totalResenas: Int = 0,
    val etiquetas: List<String> = emptyList(),

    // Campos exclusivos del Perfil
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

// 🔥 LISTA OFICIAL DE ETIQUETAS EN PRODUCCIÓN
object EtiquetasTienda {
    val lista = listOf(
        "Broaster", "Caldos", "Parrilla", "Ceviche", "Chifa",
        "Sanguchería", "Menú del día", "Bebidas", "Postres",
        "Hamburguesas", "Pollo a la brasa", "Mariscos", "Criollo"
    )
}