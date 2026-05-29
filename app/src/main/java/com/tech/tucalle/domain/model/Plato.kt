package com.tech.tucalle.domain.model

data class Plato(
    val uid: String = "", // ID del documento (opcional si usas el ID automático de Firebase)
    val idTienda: String = "",
    val nombreTienda: String = "", // Muy útil para no tener que buscar la tienda después
    val nombre: String = "",
    val descripcion: String = "",

    // Precios
    val precioOriginal: Double = 0.0,
    val precioDescuento: Double = 0.0,

    val imagenUrl: String = "",

    // Control y Auditoría
    val estado: String = "APROBADO", // O "PENDIENTE" si los admins deben revisarlo
    val aprobadoPor: String = "sistema",
    val esPlatoBase: Boolean = true,

    // Métricas (Inician en 0)
    val calificacionPlato: Double = 0.0,
    val totalRecomendaciones: Int = 0,

    // Timestamp
    val creadoEn: Long = System.currentTimeMillis()
)