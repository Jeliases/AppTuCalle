package com.tech.tucalle.domain.model

data class Plato(
    val id: String = "",
    val idTienda: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagenUrl: String = "",
    val categoria: String = "" // Ej: Entrada, Plato de fondo, Bebida
)