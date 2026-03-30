package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.Refugio
import kotlinx.coroutines.flow.StateFlow

interface RefugioRepository {
    val refugios: StateFlow<List<Refugio>>
    fun getAll(): List<Refugio>
    fun getById(id: String): Refugio?
    fun save(refugio: Refugio)
    fun delete(id: String)
}
