package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import kotlinx.coroutines.flow.StateFlow

interface MascotaRepository {
    val mascotas: StateFlow<List<Mascota>>
    fun getAll(): List<Mascota>
    fun getById(id: String): Mascota?
    fun save(mascota: Mascota)
    fun delete(id: String)
    fun getDestacadas(): List<Mascota>
    
    // Métodos para requerimientos
    fun getVerificadas(categoria: String?, lat: Double?, lng: Double?, radioKm: Double?): List<Mascota>
    fun getPendientesModeracion(): List<Mascota>
    fun actualizarEstado(id: String, estado: PublicacionEstado, motivo: String = "")
    fun toggleLike(mascotaId: String, userId: String)
}
