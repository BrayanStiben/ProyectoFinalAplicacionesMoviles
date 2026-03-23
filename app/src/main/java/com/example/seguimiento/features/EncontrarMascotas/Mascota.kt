package com.example.seguimiento.features.EncontrarMascotas

data class Mascota(
    val nombre: String,
    val edad: String,
    val ubicacion: String,
    val descripcion: String,
    val fotoUrl: String,
    val esAdoptada: Boolean = false
)