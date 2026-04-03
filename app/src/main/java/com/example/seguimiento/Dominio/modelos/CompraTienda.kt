package com.example.seguimiento.Dominio.modelos

import java.util.UUID

data class CompraTienda(
    val id: String = UUID.randomUUID().toString(),
    val productoId: String,
    val productoNombre: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val puntosGastados: Int,
    val fecha: Long = System.currentTimeMillis()
)
