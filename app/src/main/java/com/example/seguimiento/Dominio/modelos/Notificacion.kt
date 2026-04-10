package com.example.seguimiento.Dominio.modelos

data class Notificacion(
    val id: String,
    val titulo: String = "",
    val tituloResId: Int? = null,
    val tituloArgs: List<String> = emptyList(),
    val mensaje: String = "",
    val mensajeResId: Int? = null,
    val mensajeArgs: List<String> = emptyList(),
    val userId: String = "", 
    val fecha: Long = System.currentTimeMillis(),
    val leida: Boolean = false,
    val tipo: String = "INFO"
)
