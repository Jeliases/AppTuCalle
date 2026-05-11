package com.tech.tucalle.data

data class Banner(
    val imageUrl: String = "",
    val activo: Boolean = false
)

data class Tienda(
    val id: String = "",
    val nombre: String = "",
    val calificacion: Double = 0.0, // <-- Listo para ser dinámico con tu algoritmo
    val portadaUrl: String = "",
    val horario: String = "",
    val etiquetas: List<String> = emptyList(),
    val direccion: Map<String, Any>? = null
) {
    // Función auxiliar para sacar solo el texto del distrito
    fun obtenerDistrito(): String {
        return direccion?.get("texto")?.toString() ?: "Ubicación desconocida"
    }
}

data class Plato(
    val idTienda: String = "",
    val nombre: String = "",
    val precioDescuento: Double = 0.0,
    val precioOriginal: Double = 0.0,
    val calificacion: Double = 0.0,
    val imagenUrl: String = ""
)