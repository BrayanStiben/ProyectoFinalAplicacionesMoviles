package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.R
import com.example.seguimiento.core.utils.ResourceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MascotaRepositoryImpl @Inject constructor(
    private val notificacionRepository: NotificacionRepository,
    private val resourceProvider: ResourceProvider
) : MascotaRepository {

    private val _mascotas = MutableStateFlow<List<Mascota>>(getMockData())
    override val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    private fun getMockData(): List<Mascota> {
        return listOf(
            Mascota(
                id = "mock1",
                nombre = resourceProvider.getString(R.string.mock_pet_1_name),
                edad = resourceProvider.getString(R.string.mock_pet_1_age),
                tipo = "Gato",
                raza = "Mix",
                ubicacion = resourceProvider.getString(R.string.mock_pet_1_location),
                descripcion = "Linda mascota buscando hogar.",
                imagenUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=500",
                estado = PublicacionEstado.VERIFICADA,
                autorId = "admin"
            ),
            Mascota(
                id = "mock2",
                nombre = resourceProvider.getString(R.string.mock_pet_2_name),
                edad = resourceProvider.getString(R.string.mock_pet_2_age),
                tipo = "Perro",
                raza = "Mix",
                ubicacion = resourceProvider.getString(R.string.mock_pet_2_location),
                descripcion = "Compañero fiel.",
                imagenUrl = "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?q=80&w=500",
                estado = PublicacionEstado.VERIFICADA,
                autorId = "admin"
            ),
            Mascota(
                id = "mock3",
                nombre = resourceProvider.getString(R.string.mock_pet_3_name),
                edad = resourceProvider.getString(R.string.mock_pet_3_age),
                tipo = "Perro",
                raza = "Mix",
                ubicacion = resourceProvider.getString(R.string.mock_pet_3_location),
                descripcion = "Pequeño Toby.",
                imagenUrl = "https://images.unsplash.com/photo-1517849845537-4d257902454a?q=80&w=500",
                estado = PublicacionEstado.VERIFICADA,
                autorId = "admin"
            ),

        )
    }

    override fun getAll(): List<Mascota> = _mascotas.value
    override fun getById(id: String): Mascota? = _mascotas.value.find { it.id == id }

    override fun save(mascota: Mascota) {
        _mascotas.update { currentList ->
            val index = currentList.indexOfFirst { it.id == mascota.id }
            if (index != -1) {
                currentList.toMutableList().apply { set(index, mascota) }
            } else {
                currentList + mascota
            }
        }
    }

    override fun delete(id: String) {
        _mascotas.update { it.filter { m -> m.id != id } }
    }

    override fun getDestacadas(): List<Mascota> = _mascotas.value.filter { it.esDestacada }

    override fun getVerificadas(categoria: String?, lat: Double?, lng: Double?, radioKm: Double?): List<Mascota> {
        return _mascotas.value.filter { mascota ->
            val matchesEstado = mascota.estado == PublicacionEstado.VERIFICADA || mascota.estado == PublicacionEstado.RESUELTA || mascota.estado == PublicacionEstado.ADOPTADA
            val matchesCategoria = categoria == null || mascota.tipo.equals(categoria, ignoreCase = true)
            matchesEstado && matchesCategoria
        }
    }

    override fun getPendientesModeracion(): List<Mascota> = _mascotas.value.filter { it.estado == PublicacionEstado.PENDIENTE }

    override fun actualizarEstado(id: String, estado: PublicacionEstado, motivo: String) {
        _mascotas.update { currentList ->
            currentList.map { if (it.id == id) it.copy(estado = estado, motivoRechazo = motivo) else it }
        }
    }

    override fun toggleLike(mascotaId: String, userId: String) {
        _mascotas.update { currentList ->
            currentList.map { mascota ->
                if (mascota.id == mascotaId) {
                    val currentLikers = mascota.likerIds.toMutableList()
                    if (currentLikers.contains(userId)) {
                        currentLikers.remove(userId)
                    } else {
                        currentLikers.add(userId)
                    }
                    mascota.copy(likerIds = currentLikers)
                } else mascota
            }
        }
    }
}
