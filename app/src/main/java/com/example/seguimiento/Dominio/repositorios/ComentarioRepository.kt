package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.Comentario
import kotlinx.coroutines.flow.StateFlow

interface ComentarioRepository {
    fun getComentariosPorMascota(mascotaId: String): StateFlow<List<Comentario>>
    suspend fun agregarComentario(comentario: Comentario)
}
