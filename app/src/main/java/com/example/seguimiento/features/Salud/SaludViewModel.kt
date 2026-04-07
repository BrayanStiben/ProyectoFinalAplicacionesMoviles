package com.example.seguimiento.features.Salud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.*
import com.example.seguimiento.Dominio.repositorios.SaludRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.RefugioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SaludViewModel @Inject constructor(
    private val saludRepository: SaludRepository,
    private val mascotaRepository: MascotaRepository,
    private val adoptionRepository: AdoptionRepository,
    private val refugioRepository: RefugioRepository,
    val authRepository: AuthRepository
) : ViewModel() {

    private val _mascotaId = MutableStateFlow<String?>(null)
    
    val mascota = _mascotaId.map { id ->
        id?.let { mascotaRepository.getById(it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val carnet = _mascotaId.flatMapLatest { id ->
        if (id == null) flowOf(CarnetSalud(""))
        else saludRepository.getCarnetPorMascota(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CarnetSalud(""))

    val adoptionRequest = _mascotaId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else adoptionRepository.requests.map { requests ->
            requests.find { it.mascotaId == id && it.status == AdoptionRequestStatus.APPROVED }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val aliadosAprobados: StateFlow<List<Refugio>> = refugioRepository.refugios
        .map { list -> list.filter { it.estado == RefugioEstado.APROBADO } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Valores aleatorios estables para la sesión
    val randomPetId = rememberRandomValue(100000, 999999)
    val randomPin = rememberRandomValue(1000, 9999)

    private fun rememberRandomValue(min: Int, max: Int): Int {
        return (min..max).random()
    }

    fun setMascotaId(id: String) {
        _mascotaId.value = id
    }

    fun registrarVacuna(nombre: String, fecha: String, proxima: String) {
        val id = _mascotaId.value ?: return
        viewModelScope.launch {
            saludRepository.agregarVacuna(id, Vacuna(UUID.randomUUID().toString(), nombre, fecha, proxima))
        }
    }

    fun registrarDesparasitacion(producto: String, fecha: String) {
        val id = _mascotaId.value ?: return
        viewModelScope.launch {
            saludRepository.agregarDesparasitacion(id, Desparasitacion(UUID.randomUUID().toString(), producto, fecha))
        }
    }

    fun agendarCita(motivo: String, fecha: String, hora: String, clinica: String) {
        val id = _mascotaId.value ?: return
        viewModelScope.launch {
            saludRepository.agendarCita(id, CitaVeterinaria(UUID.randomUUID().toString(), motivo, fecha, hora, clinica))
        }
    }
}
