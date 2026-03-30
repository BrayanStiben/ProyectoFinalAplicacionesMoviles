package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class MascotaRepositoryImpl @Inject constructor() : MascotaRepository {

    private val _mascotas = MutableStateFlow<List<Mascota>>(fetchInitialMascotas())
    override val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    override fun getAll(): List<Mascota> = _mascotas.value

    override fun getById(id: String): Mascota? = _mascotas.value.find { it.id == id }

    override fun save(mascota: Mascota) {
        val currentList = _mascotas.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == mascota.id }
        if (index != -1) {
            currentList[index] = mascota
        } else {
            currentList.add(mascota)
        }
        _mascotas.value = currentList
    }

    override fun delete(id: String) {
        _mascotas.value = _mascotas.value.filter { it.id != id }
    }

    override fun getDestacadas(): List<Mascota> = _mascotas.value.filter { it.esDestacada }

    override fun getVerificadas(categoria: String?, lat: Double?, lng: Double?, radioKm: Double?): List<Mascota> {
        return _mascotas.value.filter { mascota ->
            val matchesEstado = mascota.estado == PublicacionEstado.VERIFICADA || mascota.estado == PublicacionEstado.RESUELTA
            val matchesCategoria = categoria == null || mascota.tipo.equals(categoria, ignoreCase = true)
            
            val matchesUbicacion = if (lat != null && lng != null && radioKm != null) {
                calcularDistancia(lat, lng, mascota.lat, mascota.lng) <= radioKm
            } else {
                true
            }
            
            matchesEstado && matchesCategoria && matchesUbicacion
        }
    }

    override fun getPendientesModeracion(): List<Mascota> {
        return _mascotas.value.filter { it.estado == PublicacionEstado.PENDIENTE }
    }

    override fun actualizarEstado(id: String, estado: PublicacionEstado, motivo: String) {
        val currentList = _mascotas.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(estado = estado, motivoRechazo = motivo)
            _mascotas.value = currentList
        }
    }

    override fun votarImportante(id: String) {
        val currentList = _mascotas.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(votosImportante = currentList[index].votosImportante + 1)
            _mascotas.value = currentList
        }
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Radio de la Tierra en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun fetchInitialMascotas(): List<Mascota> {
        return listOf(
            Mascota(
                id = "1",
                nombre = "Rex",
                edad = "2 años",
                tipo = "Perro",
                raza = "Pastor Alemán",
                ubicacion = "Bogotá",
                descripcion = "Amigable y muy inteligente, ideal para familias.",
                imagenUrl = "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=500&auto=format&fit=crop",
                esDestacada = true,
                estado = PublicacionEstado.VERIFICADA,
                lat = 4.6097,
                lng = -74.0817
            ),
            Mascota(
                id = "2",
                nombre = "Luna",
                edad = "1 año",
                tipo = "Gato",
                raza = "Siamés",
                ubicacion = "Medellín",
                descripcion = "Tranquila, le encanta dormir en el sol.",
                imagenUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=500&auto=format&fit=crop",
                esDestacada = true,
                estado = PublicacionEstado.VERIFICADA,
                lat = 6.2442,
                lng = -75.5812
            ),
            Mascota(
                id = "3",
                nombre = "Toby",
                edad = "3 años",
                tipo = "Perro",
                raza = "Beagle",
                ubicacion = "Cali",
                descripcion = "Muy activo y juguetón.",
                imagenUrl = "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?q=80&w=500&auto=format&fit=crop",
                esDestacada = true,
                estado = PublicacionEstado.PENDIENTE,
                lat = 3.4516,
                lng = -76.5320
            )
        )
    }
}
