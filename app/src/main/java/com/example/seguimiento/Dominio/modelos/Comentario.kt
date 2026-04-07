package com.example.seguimiento.Dominio.modelos

data class Comentario(
    val id: String,
    val targetId: String, // Puede ser el ID de una mascota o de una historia
    val autorId: String,
    val autorNombre: String,
    val contenido: String,
    val fecha: Long = System.currentTimeMillis(),
    val parentId: String? = null // Si es null es un comentario principal, si tiene ID es una respuesta
)
