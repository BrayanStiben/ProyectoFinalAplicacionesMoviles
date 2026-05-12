package com.example.seguimiento.Data.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.ImageStorageRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MascotaRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val imageStorageRepository: ImageStorageRepository
) : MascotaRepository {

    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    override val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    init {
        firestore.collection("mascotas")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(Mascota::class.java)
                    _mascotas.value = list
                    if (list.isEmpty()) {
                        seedInitialMascotas()
                    }
                }
            }
    }

    private fun seedInitialMascotas() {
        val initialData = listOf(
            Mascota(
                id = "pet_001",
                nombre = "Firulais",
                tipo = "Perro",
                raza = "Labrador",
                ubicacion = "Bogotá, DC",
                estado = PublicacionEstado.VERIFICADA,
                lat = 4.6097, lng = -74.0817,
                imagenUrl = "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?q=80&w=500"
            ),
            Mascota(
                id = "pet_002",
                nombre = "Misi",
                tipo = "Gato",
                raza = "Común",
                ubicacion = "Medellín, ANT",
                estado = PublicacionEstado.VERIFICADA,
                lat = 6.2442, lng = -75.5812,
                imagenUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=500"
            )
        )
        
        val batch = firestore.batch()
        initialData.forEach { mascota ->
            val docRef = firestore.collection("mascotas").document(mascota.id)
            batch.set(docRef, mascota)
        }
        batch.commit().addOnFailureListener { it.printStackTrace() }
    }

    override fun getAll(): List<Mascota> = _mascotas.value

    override fun getById(id: String): Mascota? = _mascotas.value.find { it.id == id }

    override suspend fun save(mascota: Mascota, imageUri: Uri?) {
        val id = if (mascota.id.isEmpty()) firestore.collection("mascotas").document().id else mascota.id
        
        var finalUrl = mascota.imagenUrl
        android.util.Log.d("MascotaRepository", "Guardando mascota $id. Uri: $imageUri")
        
        imageUri?.let { uri ->
            val imageUrl = imageStorageRepository.uploadImage(uri, "mascotas", "${id}_${mascota.nombre}.jpg")
            if (imageUrl != null) {
                finalUrl = imageUrl
                android.util.Log.d("MascotaRepository", "URL Imagen: $finalUrl")
            }
        }

        val mascotaToSave = mascota.copy(id = id, imagenUrl = finalUrl)
        firestore.collection("mascotas").document(id).set(mascotaToSave).await()
    }

    override suspend fun delete(id: String) {
        firestore.collection("mascotas").document(id).delete().await()
    }

    override fun getDestacadas(): List<Mascota> = _mascotas.value.filter { it.esDestacada }

    override fun getVerificadas(categoria: String?, lat: Double?, lng: Double?, radioKm: Double?): List<Mascota> {
        return _mascotas.value.filter { mascota ->
            val matchesEstado = mascota.estado == PublicacionEstado.VERIFICADA || 
                               mascota.estado == PublicacionEstado.RESUELTA || 
                               mascota.estado == PublicacionEstado.ADOPTADA
            val matchesCategoria = categoria == null || mascota.tipo.equals(categoria, ignoreCase = true)
            matchesEstado && matchesCategoria
        }
    }

    override fun getPendientesModeracion(): List<Mascota> = 
        _mascotas.value.filter { it.estado == PublicacionEstado.PENDIENTE }

    override suspend fun actualizarEstado(id: String, estado: PublicacionEstado, motivo: String) {
        firestore.collection("mascotas").document(id).update("estado", estado, "motivoRechazo", motivo).await()
    }

    override suspend fun toggleLike(mascotaId: String, userId: String) {
        val mascota = getById(mascotaId) ?: return
        val currentLikers = mascota.likerIds.toMutableList()
        if (currentLikers.contains(userId)) currentLikers.remove(userId) else currentLikers.add(userId)
        firestore.collection("mascotas").document(mascotaId).update("likerIds", currentLikers).await()
    }
}
