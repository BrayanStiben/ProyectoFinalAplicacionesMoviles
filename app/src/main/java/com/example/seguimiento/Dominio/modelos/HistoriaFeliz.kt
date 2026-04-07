package com.example.seguimiento.Dominio.modelos

import java.util.UUID

enum class HistoriaEstado {
    PENDIENTE, APROBADA, RECHAZADA
}

data class HistoriaFeliz(
    val id: String = UUID.randomUUID().toString(),
    val autorId: String,
    val autorNombre: String,
    val mascotaNombre: String,
    val texto: String,
    val imagenUrl: String,
    val estado: HistoriaEstado = HistoriaEstado.PENDIENTE,
    val fechaPublicacion: Long = System.currentTimeMillis(),
    val followersIds: List<String> = emptyList(), // Lista de IDs de usuarios que siguen esta historia
    val likerIds: List<String> = emptyList() // Lista de IDs de usuarios que dieron like
)
