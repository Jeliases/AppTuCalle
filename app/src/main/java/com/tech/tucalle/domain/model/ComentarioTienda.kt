package com.tech.tucalle.domain.model

data class ComentarioTienda(
    val id: String = "",
    val idTienda: String = "",
    val nombreTienda: String = "",
    val idUsuario: String = "",
    val nombreUsuario: String = "",
    val fotoUsuario: String = "",
    val rolUsuario: String = "USUARIO",
    val texto: String = "",
    val calificacion: Double = 5.0,
    val fecha: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val likedBy: List<String> = emptyList(),
    // 🔥 NUEVO: Para guardar los platos que sugiere el Quality
    val platosSugeridos: List<String> = emptyList()
)