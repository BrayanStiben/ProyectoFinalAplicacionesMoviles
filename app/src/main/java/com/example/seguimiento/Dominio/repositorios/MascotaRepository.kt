package com.example.seguimiento.Dominio.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import kotlinx.coroutines.flow.StateFlow

interface MascotaRepository {
    val mascotas: StateFlow<List<Mascota>>
    fun getAll(): List<Mascota>
    fun getById(id: String): Mascota?
    suspend fun save(mascota: Mascota, imageUri: Uri? = null)
    suspend fun delete(id: String)
    fun getDestacadas(): List<Mascota>
    
    // Métodos para requerimientos
    fun getVerificadas(categoria: String?, lat: Double?, lng: Double?, radioKm: Double?): List<Mascota>
    fun getPendientesModeracion(): List<Mascota>
    suspend fun actualizarEstado(id: String, estado: PublicacionEstado, motivo: String = "")
    suspend fun toggleLike(mascotaId: String, userId: String)
}
