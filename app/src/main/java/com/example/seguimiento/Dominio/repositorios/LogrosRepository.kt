package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.Logro
import kotlinx.coroutines.flow.StateFlow

interface LogrosRepository {
    val todosLosLogros: List<Logro>
    fun getLogrosUsuario(userId: String): StateFlow<List<String>> // Retorna IDs de logros obtenidos
    suspend fun ganarLogro(userId: String, logroId: String)
}
