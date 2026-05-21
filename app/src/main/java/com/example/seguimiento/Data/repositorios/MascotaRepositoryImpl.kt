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
                    
                    // Asegurar que existan datos globales para el mapa
                    val seedIds = listOf("p001", "p002", "p003")
                    val existingSeeds = list.count { it.id in seedIds }
                    if (existingSeeds < 2) {
                        seedInitialMascotas()
                    }
                }
            }
    }

    private fun seedInitialMascotas() {
        val initialPets = listOf(
            Mascota(id = "p001", nombre = "Max", tipo = "Perro", raza = "Labrador", edad = "2 años", ubicacion = "Bogotá, Cund.", descripcion = "Max es muy energético y amigable.", imagenUrl = "https://images.unsplash.com/photo-1552053831-71594a27632d", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 4.6097, lng = -74.0817),
            Mascota(id = "p002", nombre = "Luna", tipo = "Gato", raza = "Siamés", edad = "1 año", ubicacion = "Medellín, Ant.", descripcion = "Luna es dulce y tranquila.", imagenUrl = "https://images.unsplash.com/photo-1513245543132-31f507417b26", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 6.2442, lng = -75.5812),
            Mascota(id = "p003", nombre = "Rocky", tipo = "Perro", raza = "Pastor Alemán", edad = "3 años", ubicacion = "Cali, Valle", descripcion = "Rocky es leal y guardián.", imagenUrl = "https://images.unsplash.com/photo-1589944171255-67fb1619aa24", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 3.4516, lng = -76.5320),
            Mascota(id = "p004", nombre = "Mimi", tipo = "Gato", raza = "Persa", edad = "4 años", ubicacion = "Barranquilla, Atl.", descripcion = "Mimi es hogareña y elegante.", imagenUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 10.9685, lng = -74.7813),
            Mascota(id = "p005", nombre = "Bella", tipo = "Perro", raza = "Golden Retriever", edad = "2 años", ubicacion = "Bucaramanga, Sant.", descripcion = "Bella ama a los niños.", imagenUrl = "https://images.unsplash.com/photo-1552728089-57bdde30fc3e", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 7.1193, lng = -73.1227),
            Mascota(id = "p006", nombre = "Coco", tipo = "Perro", raza = "Poodle", edad = "5 meses", ubicacion = "Pereira, Ris.", descripcion = "Coco es un cachorro juguetón.", imagenUrl = "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 4.8133, lng = -75.6961),
            Mascota(id = "p007", nombre = "Simba", tipo = "Gato", raza = "Común", edad = "2 años", ubicacion = "Cartagena, Bol.", descripcion = "Simba es curioso e independiente.", imagenUrl = "https://images.unsplash.com/photo-1573865526739-10659fec78a5", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 10.3910, lng = -75.4794),
            Mascota(id = "p008", nombre = "Toby", tipo = "Perro", raza = "Beagle", edad = "4 años", ubicacion = "Manizales, Cal.", descripcion = "Toby tiene un gran olfato.", imagenUrl = "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 5.0689, lng = -75.5174),
            Mascota(id = "p009", nombre = "Nala", tipo = "Gato", raza = "Bengala", edad = "3 años", ubicacion = "Ibagué, Tol.", descripcion = "Nala es atlética y activa.", imagenUrl = "https://images.unsplash.com/photo-1533738363-b7f9aef128ce", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 4.4389, lng = -75.2322),
            Mascota(id = "p010", nombre = "Bruno", tipo = "Perro", raza = "Bulldog", edad = "2 años", ubicacion = "Santa Marta, Mag.", descripcion = "Bruno es tranquilo y perezoso.", imagenUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9", estado = PublicacionEstado.VERIFICADA, autorId = "admin_id", autorNombre = "Admin", iaEsValida = true, lat = 11.2408, lng = -74.1990)
        )
        val batch = firestore.batch()
        val col = firestore.collection("mascotas")
        initialPets.forEach { batch.set(col.document(it.id), it) }
        batch.commit()
    }

    override fun getAll(): List<Mascota> = _mascotas.value

    override fun getById(id: String): Mascota? = _mascotas.value.find { it.id == id }

    override suspend fun save(mascota: Mascota, imageUri: Uri?) {
        val id = if (mascota.id.isEmpty()) firestore.collection("mascotas").document().id else mascota.id
        var finalUrl = mascota.imagenUrl
        imageUri?.let { uri ->
            val imageUrl = imageStorageRepository.uploadImage(uri, "mascotas", "${id}_${mascota.nombre}.jpg")
            if (imageUrl != null) finalUrl = imageUrl
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
