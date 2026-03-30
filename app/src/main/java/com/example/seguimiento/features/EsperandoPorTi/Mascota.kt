package com.example.seguimiento.features.EsperandoPorTi

data class Mascota(
    val nombre: String,
    val edad: String,
    val ubicacion: String,
    val imagenUrl: String,
    val descripcion: String = "",
    val id: String = ""
)
