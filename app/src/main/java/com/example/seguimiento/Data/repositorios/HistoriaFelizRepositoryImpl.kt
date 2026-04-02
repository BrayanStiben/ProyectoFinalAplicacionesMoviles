package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.HistoriaEstado
import com.example.seguimiento.Dominio.modelos.HistoriaFeliz
import com.example.seguimiento.Dominio.repositorios.HistoriaFelizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoriaFelizRepositoryImpl @Inject constructor() : HistoriaFelizRepository {
    private val _historias = MutableStateFlow<List<HistoriaFeliz>>(
        listOf(
            HistoriaFeliz(
                autorId = "1",
                autorNombre = "Admin",
                mascotaNombre = "Firulais",
                texto = "Fue el mejor día de mi vida cuando lo encontré.",
                imagenUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=500",
                estado = HistoriaEstado.APROBADA
            ),
            HistoriaFeliz(
                autorId = "2",
                autorNombre = "Maria",
                mascotaNombre = "Luna",
                texto = "Luna trajo alegría a nuestra casa.",
                imagenUrl = "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?q=80&w=500",
                estado = HistoriaEstado.APROBADA
            )
        )
    )
    override val historias: StateFlow<List<HistoriaFeliz>> = _historias.asStateFlow()

    override fun getAll(): List<HistoriaFeliz> = _historias.value

    override fun save(historia: HistoriaFeliz) {
        _historias.value = _historias.value + historia
    }

    override fun delete(id: String) {
        _historias.value = _historias.value.filter { it.id != id }
    }

    override fun actualizarEstado(id: String, estado: HistoriaEstado) {
        _historias.value = _historias.value.map {
            if (it.id == id) it.copy(estado = estado) else it
        }
    }

    override fun getAprobadas(): List<HistoriaFeliz> {
        return _historias.value.filter { it.estado == HistoriaEstado.APROBADA }
    }

    override fun getPendientes(): List<HistoriaFeliz> {
        return _historias.value.filter { it.estado == HistoriaEstado.PENDIENTE }
    }
}
