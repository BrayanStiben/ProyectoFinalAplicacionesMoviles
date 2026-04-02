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
    val imagenUrl: String, // Podríamos extenderlo a una lista si se desea más de una
    val estado: HistoriaEstado = HistoriaEstado.PENDIENTE,
    val fechaPublicacion: Long = System.currentTimeMillis()
)
