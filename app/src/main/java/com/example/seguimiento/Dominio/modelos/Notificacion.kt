package com.example.seguimiento.Dominio.modelos

data class Notificacion(
    val id: String,
    val titulo: String,
    val mensaje: String,
    val userId: String = "", // Agregado para filtrar notificaciones por usuario
    val fecha: Long = System.currentTimeMillis(),
    val leida: Boolean = false,
    val tipo: String = "INFO" // "INFO", "POST_VOTADO", "COMENTARIO_NUEVO", "ZONA_NUEVA"
)
