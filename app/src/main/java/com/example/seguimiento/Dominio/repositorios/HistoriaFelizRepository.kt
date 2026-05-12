package com.example.seguimiento.Dominio.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.HistoriaFeliz
import com.example.seguimiento.Dominio.modelos.HistoriaEstado
import kotlinx.coroutines.flow.StateFlow

interface HistoriaFelizRepository {
    val historias: StateFlow<List<HistoriaFeliz>>
    fun getAll(): List<HistoriaFeliz>
    fun getById(id: String): HistoriaFeliz?
    suspend fun save(historia: HistoriaFeliz, imageUri: Uri? = null)
    suspend fun delete(id: String)
    suspend fun actualizarEstado(id: String, estado: HistoriaEstado)
    fun getAprobadas(): List<HistoriaFeliz>
    fun getPendientes(): List<HistoriaFeliz>
    suspend fun toggleFollow(historiaId: String, userId: String)
    suspend fun toggleLike(historiaId: String, userId: String)
}
