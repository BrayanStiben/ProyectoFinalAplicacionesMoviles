package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.HistoriaFeliz
import com.example.seguimiento.Dominio.modelos.HistoriaEstado
import kotlinx.coroutines.flow.StateFlow

interface HistoriaFelizRepository {
    val historias: StateFlow<List<HistoriaFeliz>>
    fun getAll(): List<HistoriaFeliz>
    fun save(historia: HistoriaFeliz)
    fun delete(id: String)
    fun actualizarEstado(id: String, estado: HistoriaEstado)
    fun getAprobadas(): List<HistoriaFeliz>
    fun getPendientes(): List<HistoriaFeliz>
}
