package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.Mascota
import kotlinx.coroutines.flow.StateFlow

interface MascotaRepository {
    val mascotas: StateFlow<List<Mascota>>
    fun getAll(): List<Mascota>
    fun getById(id: String): Mascota?
    fun save(mascota: Mascota)
    fun delete(id: String)
    fun getDestacadas(): List<Mascota>
}
