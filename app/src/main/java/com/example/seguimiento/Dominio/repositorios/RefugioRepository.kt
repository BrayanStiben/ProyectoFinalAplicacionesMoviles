package com.example.seguimiento.Dominio.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.Refugio
import com.example.seguimiento.Dominio.modelos.RefugioEstado
import kotlinx.coroutines.flow.StateFlow

interface RefugioRepository {
    val refugios: StateFlow<List<Refugio>>
    fun getAll(): List<Refugio>
    fun getById(id: String): Refugio?
    suspend fun save(refugio: Refugio, imageUri: Uri? = null)
    suspend fun delete(id: String)
    suspend fun actualizarEstado(id: String, estado: RefugioEstado)
}
