package com.example.seguimiento.Dominio.modelos

data class Mascota(
    val id: String,
    val nombre: String,
    val edad: String,
    val tipo: String,
    val raza: String,
    val ubicacion: String,
    val descripcion: String,
    val imagenUrl: String,
    val esDestacada: Boolean = false
)
