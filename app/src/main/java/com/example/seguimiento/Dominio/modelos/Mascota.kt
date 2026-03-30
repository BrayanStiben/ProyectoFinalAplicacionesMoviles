package com.example.seguimiento.Dominio.modelos

enum class PublicacionEstado {
    PENDIENTE, VERIFICADA, RECHAZADA, RESUELTA
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
    val votosImportante: Int = 0,
    val resumenIA: String = "" // Para el requisito de IA 4
)
