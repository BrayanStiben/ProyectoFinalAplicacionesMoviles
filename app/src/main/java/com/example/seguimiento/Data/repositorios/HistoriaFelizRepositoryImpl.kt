package com.example.seguimiento.Data.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.HistoriaEstado
import com.example.seguimiento.Dominio.modelos.HistoriaFeliz
import com.example.seguimiento.Dominio.repositorios.HistoriaFelizRepository
import com.example.seguimiento.Dominio.repositorios.ImageStorageRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoriaFelizRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val imageStorageRepository: ImageStorageRepository
) : HistoriaFelizRepository {

    private val collection = firestore.collection("historias_felices")
    private val _historias = MutableStateFlow<List<HistoriaFeliz>>(emptyList())
    override val historias: StateFlow<List<HistoriaFeliz>> = _historias.asStateFlow()

    init {
        collection.orderBy("fechaPublicacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(HistoriaFeliz::class.java)
                    _historias.value = list
                    
                    if (list.isEmpty()) {
                        seedInitialStories()
                    }
                }
            }
    }

    private fun seedInitialStories() {
        val initialStories = listOf(
            HistoriaFeliz(
                id = "story_001",
                autorId = "admin_id",
                autorNombre = "Admin",
                mascotaNombre = "Firulais",
                texto = "Una historia de superación y amor.",
                imagenUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=500",
                estado = HistoriaEstado.APROBADA
            )
        )
        val batch = firestore.batch()
        initialStories.forEach { story ->
            batch.set(collection.document(story.id), story)
        }
        batch.commit()
    }

    override fun getAll(): List<HistoriaFeliz> = _historias.value

    override fun getById(id: String): HistoriaFeliz? = _historias.value.find { it.id == id }

    override suspend fun save(historia: HistoriaFeliz, imageUri: Uri?) {
        val id = if (historia.id.isEmpty()) collection.document().id else historia.id
        
        var finalUrl = historia.imagenUrl
        imageUri?.let { uri ->
            val imageUrl = imageStorageRepository.uploadImage(uri, "historias", "${id}_${historia.mascotaNombre}.jpg")
            if (imageUrl != null) {
                finalUrl = imageUrl
            }
        }

        collection.document(id).set(historia.copy(id = id, imagenUrl = finalUrl)).await()
    }

    override suspend fun delete(id: String) {
        collection.document(id).delete().await()
    }

    override suspend fun actualizarEstado(id: String, estado: HistoriaEstado) {
        collection.document(id).update("estado", estado).await()
    }

    override fun getAprobadas(): List<HistoriaFeliz> = 
        _historias.value.filter { it.estado == HistoriaEstado.APROBADA }

    override fun getPendientes(): List<HistoriaFeliz> = 
        _historias.value.filter { it.estado == HistoriaEstado.PENDIENTE }

    override suspend fun toggleFollow(historiaId: String, userId: String) {
        val historia = getById(historiaId) ?: return
        val currentFollowers = historia.followersIds.toMutableList()
        if (currentFollowers.contains(userId)) currentFollowers.remove(userId) 
        else currentFollowers.add(userId)
        collection.document(historiaId).update("followersIds", currentFollowers).await()
    }

    override suspend fun toggleLike(historiaId: String, userId: String) {
        val historia = getById(historiaId) ?: return
        val currentLikers = historia.likerIds.toMutableList()
        if (currentLikers.contains(userId)) currentLikers.remove(userId) 
        else currentLikers.add(userId)
        collection.document(historiaId).update("likerIds", currentLikers).await()
    }
}
