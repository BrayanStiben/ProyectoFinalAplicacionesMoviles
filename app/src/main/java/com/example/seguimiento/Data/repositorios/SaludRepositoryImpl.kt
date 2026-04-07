package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.*
import com.example.seguimiento.Dominio.repositorios.SaludRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SaludRepositoryImpl @Inject constructor(
    private val notificacionRepository: NotificacionRepository
) : SaludRepository {

    private val _carnets = MutableStateFlow<Map<String, CarnetSalud>>(emptyMap())

    override fun getCarnetPorMascota(mascotaId: String): StateFlow<CarnetSalud> {
        if (!_carnets.value.containsKey(mascotaId)) {
            val nuevoCarnet = CarnetSalud(
                mascotaId = mascotaId,
                petId = Random.nextInt(100000, 999999).toString(),
                pin = Random.nextInt(1000, 9999).toString()
            )
            _carnets.update { it + (mascotaId to nuevoCarnet) }
        }
        // Retornamos un StateFlow que observe los cambios específicos de este carnet
        return MutableStateFlow(_carnets.value[mascotaId] ?: CarnetSalud(mascotaId)).asStateFlow()
    }

    override suspend fun agregarVacuna(mascotaId: String, vacuna: Vacuna) {
        actualizarCarnet(mascotaId) { carnet ->
            carnet.copy(vacunas = carnet.vacunas + vacuna)
        }
        notificacionRepository.addNotificacion(
            titulo = "Vacuna Registrada 💉",
            mensaje = "Se ha registrado la vacuna ${vacuna.nombre}. Próxima dosis: ${vacuna.proximaDosis}.",
            tipo = "SUCCESS"
        )
    }

    override suspend fun agregarDesparasitacion(mascotaId: String, despar: Desparasitacion) {
        actualizarCarnet(mascotaId) { carnet ->
            carnet.copy(desparasitaciones = carnet.desparasitaciones + despar)
        }
    }

    override suspend fun agendarCita(mascotaId: String, cita: CitaVeterinaria) {
        actualizarCarnet(mascotaId) { carnet ->
            carnet.copy(citas = carnet.citas + cita)
        }
        notificacionRepository.addNotificacion(
            titulo = "Cita Agendada 📅",
            mensaje = "Tienes una cita el ${cita.fecha} a las ${cita.hora} en ${cita.clinica}.",
            tipo = "INFO"
        )
    }

    private fun actualizarCarnet(mascotaId: String, transform: (CarnetSalud) -> CarnetSalud) {
        _carnets.update { mapa ->
            val carnetActual = mapa[mascotaId] ?: CarnetSalud(
                mascotaId = mascotaId,
                petId = Random.nextInt(100000, 999999).toString(),
                pin = Random.nextInt(1000, 9999).toString()
            )
            mapa + (mascotaId to transform(carnetActual))
        }
    }
}
