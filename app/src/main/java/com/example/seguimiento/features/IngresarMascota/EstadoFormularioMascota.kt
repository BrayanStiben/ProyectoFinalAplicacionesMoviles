package com.example.seguimiento.features.IngresarMascota

import android.net.Uri

data class EstadoFormularioMascota(
    val nombre: String = "",
    val tipo: String = "",
    val raza: String = "",
    val edad: String = "",
    val unidadEdad: String = "años",
    val sexo: String = "Hembra",
    val ciudad: String = "",
    val descripcion: String = "",
    val fotoUri: Uri? = null,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val isLoading: Boolean = false,
    val aiWarning: String? = null
)
