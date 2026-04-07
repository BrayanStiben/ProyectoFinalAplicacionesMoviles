package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Refugio
import com.example.seguimiento.Dominio.modelos.RefugioEstado
import com.example.seguimiento.Dominio.modelos.RefugioTipo
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
            Refugio("1", "Huellitas de Amor", "Calle 10 #5-20", "3101234567", "Refugio dedicado al rescate de perros y gatos en situación de calle.", "https://images.unsplash.com/photo-1548191265-cc70d3d45ba1", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("2", "Segunda Oportunidad", "Av. Siempre Viva 123", "3209876543", "Buscamos hogares amorosos para nuestras mascotas rescatadas.", "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("3", "Vet Express", "Carrera 15 #45-10", "3001112233", "Atención veterinaria de alta calidad 24/7.", "https://images.unsplash.com/photo-1584132967334-10e028bd69f7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("4", "Clínica Animal San José", "Calle 80 #12-45", "3155556677", "Especialistas en cirugía y medicina preventiva.", "https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("5", "Refugio San Roque", "Transversal 45 #12-80", "3004445566", "Comprometidos con el bienestar animal desde hace 10 años.", "https://images.unsplash.com/photo-1511497584788-8767fe770c52", estado = RefugioEstado.PENDIENTE, tipo = RefugioTipo.REFUGIO)
        )
    }
}
