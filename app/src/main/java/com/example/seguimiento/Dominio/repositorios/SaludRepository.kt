package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.*
import kotlinx.coroutines.flow.StateFlow

interface SaludRepository {
    fun getCarnetPorMascota(mascotaId: String): StateFlow<CarnetSalud>
    suspend fun agregarVacuna(mascotaId: String, vacuna: Vacuna)
    suspend fun agregarDesparasitacion(mascotaId: String, despar: Desparasitacion)
    suspend fun agendarCita(mascotaId: String, cita: CitaVeterinaria)
}
