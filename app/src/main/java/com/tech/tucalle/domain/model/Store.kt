package com.tech.tucalle.domain.model

data class Store(
    val uid: String = "",
    val nombreLocal: String = "",
    val email: String = "",
    val celular: String = "",
    val direccion: String = "", // Campo clave solicitado
    val horarioAtencion: String = "",
    val rol: String = "TIENDA",
    val recibirPromociones: Boolean = false
)