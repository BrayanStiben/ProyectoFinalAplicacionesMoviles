package com.example.seguimiento.Dominio.modelos

import com.example.seguimiento.R

enum class RefugioEstado {
    PENDIENTE, APROBADO, RECHAZADO
}

enum class RefugioTipo(val resId: Int) {
    REFUGIO(R.string.refugio_tipo_refugio),
    VETERINARIA(R.string.refugio_tipo_veterinaria)
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
    val autorId: String = "",
    val tipo: RefugioTipo = RefugioTipo.REFUGIO
)
