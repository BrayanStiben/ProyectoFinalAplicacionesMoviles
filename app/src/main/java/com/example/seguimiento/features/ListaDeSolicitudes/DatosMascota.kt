package com.example.seguimiento.features.ListaDeSolicitudes

data class DatosMascota(
    val id: Int,
    val nombre: String,
    val estado: String,
    val imagenUrl: String,
    val subtexto: String? = null,
    val progreso: Float? = null,
    val esAprobado: Boolean = false
)