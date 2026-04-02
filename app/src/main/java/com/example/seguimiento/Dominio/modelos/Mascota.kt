package com.example.seguimiento.Dominio.modelos

enum class PublicacionEstado {
    PENDIENTE, VERIFICADA, RECHAZADA, RESUELTA, ADOPTADA
}

data class Mascota(
    val id: String,
    val nombre: String, // Actúa como título
    val edad: String,
    val tipo: String, // Actúa como categoría
    val raza: String,
    val ubicacion: String,
    val descripcion: String,
    val imagenUrl: String,
    val esDestacada: Boolean = false,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val autorId: String = "",
    val estado: PublicacionEstado = PublicacionEstado.PENDIENTE,
    val motivoRechazo: String = "",
    val likerIds: List<String> = emptyList(), // Usuarios que dieron like
    val resumenIA: String = "" // Para el requisito de IA 4
) {
    val totalLikes: Int get() = likerIds.size
}
