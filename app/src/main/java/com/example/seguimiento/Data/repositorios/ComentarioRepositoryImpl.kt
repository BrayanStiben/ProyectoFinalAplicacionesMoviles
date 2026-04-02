package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Comentario
import com.example.seguimiento.Dominio.repositorios.ComentarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Singleton
class ComentarioRepositoryImpl @Inject constructor() : ComentarioRepository {
    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    override val todosLosComentarios: StateFlow<List<Comentario>> = _comentarios.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun getComentariosPorMascota(mascotaId: String): StateFlow<List<Comentario>> {
        return _comentarios
            .map { lista -> lista.filter { it.mascotaId == mascotaId } }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    override suspend fun agregarComentario(comentario: Comentario) {
        _comentarios.update { it + comentario }
    }

    override suspend fun eliminarComentario(comentarioId: String) {
        _comentarios.update { lista -> lista.filter { c -> c.id != comentarioId } }
    }

    override suspend fun censurarComentario(comentarioId: String, nuevoContenido: String) {
        _comentarios.update { lista ->
            lista.map { if (it.id == comentarioId) it.copy(contenido = nuevoContenido) else it }
        }
    }
}
