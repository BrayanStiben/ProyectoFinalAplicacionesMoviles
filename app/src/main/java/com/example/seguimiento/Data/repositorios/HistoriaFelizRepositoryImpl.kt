package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.HistoriaEstado
import com.example.seguimiento.Dominio.modelos.HistoriaFeliz
import com.example.seguimiento.Dominio.repositorios.HistoriaFelizRepository
import com.example.seguimiento.R
import com.example.seguimiento.core.utils.ResourceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoriaFelizRepositoryImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : HistoriaFelizRepository {
    private val _historias = MutableStateFlow<List<HistoriaFeliz>>(
        listOf(
            HistoriaFeliz(
                autorId = "1",
                autorNombre = "Admin",
                mascotaNombre = "Firulais",
                texto = resourceProvider.getString(R.string.mock_story_1_text),
                imagenUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=500",
                estado = HistoriaEstado.APROBADA
            ),
            HistoriaFeliz(
                autorId = "2",
                autorNombre = "Maria",
                mascotaNombre = "Luna",
                texto = resourceProvider.getString(R.string.mock_story_2_text),
                imagenUrl = "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?q=80&w=500",
                estado = HistoriaEstado.APROBADA
            )
        )
    )
    override val historias: StateFlow<List<HistoriaFeliz>> = _historias.asStateFlow()

    override fun getAll(): List<HistoriaFeliz> = _historias.value

    override fun getById(id: String): HistoriaFeliz? = _historias.value.find { it.id == id }

    override fun save(historia: HistoriaFeliz) {
        _historias.update { list ->
            val index = list.indexOfFirst { it.id == historia.id }
            if (index != -1) {
                list.toMutableList().apply { set(index, historia) }
            } else {
                list + historia
            }
        }
    }

    override fun delete(id: String) {
        _historias.update { it.filter { it.id != id } }
    }

    override fun actualizarEstado(id: String, estado: HistoriaEstado) {
        _historias.update { list ->
            list.map { if (it.id == id) it.copy(estado = estado) else it }
        }
    }

    override fun getAprobadas(): List<HistoriaFeliz> {
        return _historias.value.filter { it.estado == HistoriaEstado.APROBADA }
    }

    override fun getPendientes(): List<HistoriaFeliz> {
        return _historias.value.filter { it.estado == HistoriaEstado.PENDIENTE }
    }

    override fun toggleFollow(historiaId: String, userId: String) {
        _historias.update { list ->
            list.map { historia ->
                if (historia.id == historiaId) {
                    val currentFollowers = historia.followersIds.toMutableList()
                    if (currentFollowers.contains(userId)) {
                        currentFollowers.remove(userId)
                    } else {
                        currentFollowers.add(userId)
                    }
                    historia.copy(followersIds = currentFollowers)
                } else historia
            }
        }
    }

    override fun toggleLike(historiaId: String, userId: String) {
        _historias.update { list ->
            list.map { historia ->
                if (historia.id == historiaId) {
                    val currentLikers = historia.likerIds.toMutableList()
                    if (currentLikers.contains(userId)) {
                        currentLikers.remove(userId)
                    } else {
                        currentLikers.add(userId)
                    }
                    historia.copy(likerIds = currentLikers)
                } else historia
            }
        }
    }
}
