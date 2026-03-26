package com.example.seguimiento.Dominio.modelos

data class Refugio(
    val id: String,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val descripcion: String,
    val imagenUrl: String,
    val latitud: Double,
    val longitud: Double
)
