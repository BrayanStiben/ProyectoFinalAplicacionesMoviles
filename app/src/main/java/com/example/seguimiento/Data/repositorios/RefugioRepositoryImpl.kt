package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Refugio
import com.example.seguimiento.Dominio.modelos.RefugioEstado
import com.example.seguimiento.Dominio.repositorios.RefugioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefugioRepositoryImpl @Inject constructor() : RefugioRepository {

    private val _refugios = MutableStateFlow<List<Refugio>>(fetchInitialRefugios())
    override val refugios: StateFlow<List<Refugio>> = _refugios.asStateFlow()

    override fun getAll(): List<Refugio> = _refugios.value

    override fun getById(id: String): Refugio? = _refugios.value.find { it.id == id }

    override fun save(refugio: Refugio) {
        _refugios.update { currentList ->
            val index = currentList.indexOfFirst { it.id == refugio.id }
            if (index != -1) {
                currentList.toMutableList().apply { set(index, refugio) }
            } else {
                currentList + refugio
            }
        }
    }

    override fun delete(id: String) {
        _refugios.update { it.filter { r -> r.id != id } }
    }

    override fun actualizarEstado(id: String, estado: RefugioEstado) {
        _refugios.update { list ->
            list.map { if (it.id == id) it.copy(estado = estado) else it }
        }
    }

    private fun fetchInitialRefugios(): List<Refugio> {
        return listOf(
            Refugio("1", "Huellitas de Amor", "Calle 10 #5-20", "3101234567", "Refugio dedicado al rescate de perros y gatos en situación de calle.", "https://images.unsplash.com/photo-1548191265-cc70d3d45ba1", estado = RefugioEstado.APROBADO),
            Refugio("2", "Segunda Oportunidad", "Av. Siempre Viva 123", "3209876543", "Buscamos hogares amorosos para nuestras mascotas rescatadas.", "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7", estado = RefugioEstado.APROBADO),
            Refugio("3", "Refugio San Roque", "Transversal 45 #12-80", "3004445566", "Comprometidos con el bienestar animal desde hace 10 años.", "https://images.unsplash.com/photo-1511497584788-8767fe770c52", estado = RefugioEstado.PENDIENTE)
        )
    }
}
