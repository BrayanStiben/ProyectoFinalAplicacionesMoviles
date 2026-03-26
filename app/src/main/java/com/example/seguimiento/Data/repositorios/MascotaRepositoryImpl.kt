package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MascotaRepositoryImpl @Inject constructor() : MascotaRepository {

    private val _mascotas = MutableStateFlow<List<Mascota>>(fetchInitialMascotas())
    override val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    override fun getAll(): List<Mascota> = _mascotas.value

    override fun getById(id: String): Mascota? = _mascotas.value.find { it.id == id }

    override fun save(mascota: Mascota) {
        val currentList = _mascotas.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == mascota.id }
        if (index != -1) {
            currentList[index] = mascota
        } else {
            currentList.add(mascota)
        }
        _mascotas.value = currentList
    }

    override fun delete(id: String) {
        _mascotas.value = _mascotas.value.filter { it.id != id }
    }

    override fun getDestacadas(): List<Mascota> = _mascotas.value.filter { it.esDestacada }

    private fun fetchInitialMascotas(): List<Mascota> {
        return listOf(
            Mascota("1", "Rex", "2 años", "Perro", "Pastor Alemán", "Bogotá", "Amigable y juguetón", "https://images.dog.ceo/breeds/germanshepherd/n02106662_13912.jpg", true),
            Mascota("2", "Luna", "1 año", "Gato", "Siamés", "Medellín", "Tranquila y cariñosa", "https://upload.wikimedia.org/wikipedia/commons/2/25/Siam_cat_pink_nose.jpg", true),
            Mascota("3", "Toby", "3 años", "Perro", "Beagle", "Cali", "Le gusta correr en el parque", "https://images.dog.ceo/breeds/beagle/n02088364_10121.jpg", false)
        )
    }
}
