package com.example.seguimiento.Dominio.modelos

data class Notificacion(
    val id: String = "",
    val titulo: String = "",
    val mensaje: String = "",
    val tipo: String = "INFO", // INFO, SUCCESS, WARNING, ERROR
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = "",
    val leida: Boolean = false
)
