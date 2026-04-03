package com.example.seguimiento.Dominio.modelos

enum class RefugioEstado {
    PENDIENTE, APROBADO, RECHAZADO
}

data class Refugio(
    val id: String,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val descripcion: String,
    val imagenUrl: String,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val estado: RefugioEstado = RefugioEstado.PENDIENTE,
    val autorId: String = ""
)
