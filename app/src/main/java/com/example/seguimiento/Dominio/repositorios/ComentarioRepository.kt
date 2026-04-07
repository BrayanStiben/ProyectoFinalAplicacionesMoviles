package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.Comentario
import kotlinx.coroutines.flow.StateFlow

interface ComentarioRepository {
    val todosLosComentarios: StateFlow<List<Comentario>>
    fun getComentariosPorTarget(targetId: String): StateFlow<List<Comentario>>
    suspend fun agregarComentario(comentario: Comentario)
    suspend fun eliminarComentario(comentarioId: String)
    suspend fun censurarComentario(comentarioId: String, nuevoContenido: String)
}
