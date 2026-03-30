package com.example.seguimiento.Dominio.modelos

data class Comentario(
    val id: String,
    val mascotaId: String,
    val autorId: String,
    val autorNombre: String,
    val contenido: String,
    val fecha: Long = System.currentTimeMillis()
)
