package com.example.seguimiento.features.Refugios

data class Refugio(
    val id: Int,
    val nombre: String,
    val estado: String,
    val esNuevo: Boolean = false,
    val verificado: Boolean = false // <--- Esto soluciona "Unresolved reference 'verificado'"
)