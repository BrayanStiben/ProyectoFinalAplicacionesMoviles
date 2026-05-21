package com.example.seguimiento.Data.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.Refugio
import com.example.seguimiento.Dominio.modelos.RefugioEstado
import com.example.seguimiento.Dominio.repositorios.RefugioRepository
import com.example.seguimiento.Dominio.repositorios.ImageStorageRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefugioRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val imageStorageRepository: ImageStorageRepository
) : RefugioRepository {

    private val collection = firestore.collection("refugios")
    private val _refugios = MutableStateFlow<List<Refugio>>(emptyList())
    override val refugios: StateFlow<List<Refugio>> = _refugios.asStateFlow()

    init {
        collection.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.toObjects(Refugio::class.java)
                _refugios.value = list
            }
        }
    }

    override fun getAll(): List<Refugio> = _refugios.value

    override fun getById(id: String): Refugio? = _refugios.value.find { it.id == id }

    override suspend fun save(refugio: Refugio, imageUri: Uri?) {
        val id = if (refugio.id.isEmpty()) collection.document().id else refugio.id
        
        var finalUrl = refugio.imagenUrl
        imageUri?.let { uri ->
            val imageUrl = imageStorageRepository.uploadImage(uri, "refugios", "${id}_${refugio.nombre}.jpg")
            if (imageUrl != null) {
                finalUrl = imageUrl
            }
        }

        collection.document(id).set(refugio.copy(id = id, imagenUrl = finalUrl)).await()
    }

    override suspend fun delete(id: String) {
        collection.document(id).delete().await()
    }

    override suspend fun actualizarEstado(id: String, estado: RefugioEstado) {
        collection.document(id).update("estado", estado).await()
    }
}
