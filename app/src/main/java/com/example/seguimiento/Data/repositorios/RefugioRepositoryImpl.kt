package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Refugio
import com.example.seguimiento.Dominio.repositorios.RefugioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefugioRepositoryImpl @Inject constructor() : RefugioRepository {

    private val _refugios = MutableStateFlow<List<Refugio>>(fetchInitialRefugios())
    override val refugios: StateFlow<List<Refugio>> = _refugios.asStateFlow()

    override fun getAll(): List<Refugio> = _refugios.value

    override fun getById(id: String): Refugio? = _refugios.value.find { it.id == id }

    override fun save(refugio: Refugio) {
        val currentList = _refugios.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == refugio.id }
        if (index != -1) {
            currentList[index] = refugio
        } else {
            currentList.add(refugio)
        }
        _refugios.value = currentList
    }

    override fun delete(id: String) {
        _refugios.value = _refugios.value.filter { it.id != id }
    }

    private fun fetchInitialRefugios(): List<Refugio> {
        return listOf(
            Refugio("1", "Huellitas de Amor", "Calle 10 #5-20", "3101234567", "Refugio dedicado al rescate de perros y gatos en situación de calle.", "https://example.com/refugio1.jpg", 4.6097, -74.0817),
            Refugio("2", "Segunda Oportunidad", "Av. Siempre Viva 123", "3209876543", "Buscamos hogares amorosos para nuestras mascotas rescatadas.", "https://example.com/refugio2.jpg", 4.6534, -74.0658),
            Refugio("3", "Refugio San Roque", "Transversal 45 #12-80", "3004445566", "Comprometidos con el bienestar animal desde hace 10 años.", "https://example.com/refugio3.jpg", 4.7110, -74.1102)
        )
    }
}
