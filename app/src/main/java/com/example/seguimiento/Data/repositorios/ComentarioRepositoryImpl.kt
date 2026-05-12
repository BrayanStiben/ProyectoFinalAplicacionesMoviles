package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Comentario
import com.example.seguimiento.Dominio.repositorios.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComentarioRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notificacionRepository: NotificacionRepository,
    private val mascotaRepository: MascotaRepository,
    private val historiaRepository: HistoriaFelizRepository
) : ComentarioRepository {

    private val collection = firestore.collection("comentarios")

    private val _todosLosComentarios = MutableStateFlow<List<Comentario>>(emptyList())
    override val todosLosComentarios: StateFlow<List<Comentario>> = _todosLosComentarios.asStateFlow()

    init {
        collection.orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    _todosLosComentarios.value = it.toObjects(Comentario::class.java)
                }
            }
    }

    override fun getComentariosPorTarget(targetId: String): Flow<List<Comentario>> {
        return _todosLosComentarios.map { lista -> 
            lista.filter { it.targetId == targetId } 
        }
    }

    override suspend fun agregarComentario(comentario: Comentario) {
        val id = collection.document().id
        val finalComentario = comentario.copy(id = id)
        collection.document(id).set(finalComentario).await()
        
        // Lógica de notificaciones (se mantiene igual, enviando a los autores correspondientes)
        enviarNotificacionesPorComentario(finalComentario)
    }

    private suspend fun enviarNotificacionesPorComentario(comentario: Comentario) {
        val mascota = mascotaRepository.getById(comentario.targetId)
        if (mascota != null && mascota.autorId != comentario.autorId) {
            notificacionRepository.addNotificacion(
                tituloResId = com.example.seguimiento.R.string.notif_comment_new_title,
                mensajeResId = com.example.seguimiento.R.string.notif_comment_new_msg,
                mensajeArgs = listOf(comentario.autorNombre, mascota.nombre),
                tipo = "INFO",
                userId = mascota.autorId
            )
        }
    }

    override suspend fun eliminarComentario(comentarioId: String) {
        collection.document(comentarioId).delete()
    }

    override suspend fun censurarComentario(comentarioId: String, nuevoContenido: String) {
        collection.document(comentarioId).update("contenido", nuevoContenido)
    }
}
