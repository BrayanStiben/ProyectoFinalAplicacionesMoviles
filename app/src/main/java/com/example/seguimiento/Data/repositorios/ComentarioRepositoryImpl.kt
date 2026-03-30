package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Comentario
import com.example.seguimiento.Dominio.repositorios.ComentarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComentarioRepositoryImpl @Inject constructor() : ComentarioRepository {
    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())

    override fun getComentariosPorMascota(mascotaId: String): StateFlow<List<Comentario>> {
        // En una implementación real, filtraríamos desde la fuente de datos.
        // Aquí devolvemos el StateFlow global y el UI filtrará o usaremos un Map de StateFlows.
        return _comentarios.asStateFlow()
    }

    override suspend fun agregarComentario(comentario: Comentario) {
        _comentarios.update { it + comentario }
    }
}
