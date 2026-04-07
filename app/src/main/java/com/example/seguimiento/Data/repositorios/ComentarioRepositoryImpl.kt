package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Comentario
import com.example.seguimiento.Dominio.repositorios.ComentarioRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.HistoriaFelizRepository
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
class ComentarioRepositoryImpl @Inject constructor(
    private val notificacionRepository: NotificacionRepository,
    private val mascotaRepository: MascotaRepository,
    private val historiaRepository: HistoriaFelizRepository
) : ComentarioRepository {
    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    override val todosLosComentarios: StateFlow<List<Comentario>> = _comentarios.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun getComentariosPorTarget(targetId: String): StateFlow<List<Comentario>> {
        return _comentarios
            .map { lista -> lista.filter { it.targetId == targetId } }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    override suspend fun agregarComentario(comentario: Comentario) {
        _comentarios.update { it + comentario }
        
        // Notificar al dueño del target (Mascota o Historia)
        val mascota = mascotaRepository.getById(comentario.targetId)
        if (mascota != null) {
            if (mascota.autorId != comentario.autorId) {
                notificacionRepository.addNotificacion(
                    titulo = "Nuevo comentario",
                    mensaje = "${comentario.autorNombre} comentó en tu publicación de ${mascota.nombre}",
                    tipo = "INFO",
                    userId = mascota.autorId
                )
            }
        } else {
            val historia = historiaRepository.getById(comentario.targetId)
            if (historia != null && historia.autorId != comentario.autorId) {
                notificacionRepository.addNotificacion(
                    titulo = "Nuevo comentario en tu historia",
                    mensaje = "${comentario.autorNombre} comentó en tu historia de ${historia.mascotaNombre}",
                    tipo = "INFO",
                    userId = historia.autorId
                )
            }
        }

        // Si es una respuesta, notificar al autor del comentario padre
        if (comentario.parentId != null) {
            val padre = _comentarios.value.find { it.id == comentario.parentId }
            if (padre != null && padre.autorId != comentario.autorId) {
                notificacionRepository.addNotificacion(
                    titulo = "Nueva respuesta",
                    mensaje = "${comentario.autorNombre} respondió a tu comentario",
                    tipo = "INFO",
                    userId = padre.autorId
                )
            }
        }
    }

    override suspend fun eliminarComentario(comentarioId: String) {
        _comentarios.update { lista -> lista.filter { c -> c.id != comentarioId } }
    }

    override suspend fun censurarComentario(comentarioId: String, nuevoContenido: String) {
        val comentario = _comentarios.value.find { it.id == comentarioId }
        _comentarios.update { lista ->
            lista.map { if (it.id == comentarioId) it.copy(contenido = nuevoContenido) else it }
        }
        
        comentario?.let {
            notificacionRepository.addNotificacion(
                titulo = "Comentario Moderado",
                mensaje = "Tu comentario ha sido editado por un moderador.",
                tipo = "WARNING",
                userId = it.autorId
            )
        }
    }
}
