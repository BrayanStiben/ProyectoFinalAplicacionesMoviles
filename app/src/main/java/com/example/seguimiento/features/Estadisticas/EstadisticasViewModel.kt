package com.example.seguimiento.features.Estadisticas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BarData(val label: String, val valueBlue: Int, val valueOrange: Int = 0)

data class EstadisticasUiState(
    val adoptionRequestsCount: Int = 0,
    val petModerationCount: Int = 0,
    val mascotasTotales: Int = 0,
    val mascotasVerificadas: Int = 0,
    val usuariosTotales: Int = 0,
    val usuariosBaneados: Int = 0,
    val listaUsuarios: List<User> = emptyList(),
    val listaUsuariosBaneados: List<User> = emptyList(),
    val estadisticasSemanales: List<BarData> = listOf(
        BarData("Lun", 150, 80), BarData("Mar", 260, 120),
        BarData("Mie", 100, 150), BarData("Jue", 180, 170),
        BarData("Vie", 220, 230), BarData("Sab", 300, 330),
        BarData("Dom", 250, 200)
    )
)

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mascotaRepository: MascotaRepository,
    private val adoptionRepository: AdoptionRepository
) : ViewModel() {

    val uiState: StateFlow<EstadisticasUiState> = combine(
        userRepository.users,
        mascotaRepository.mascotas,
        adoptionRepository.requests
    ) { users, mascotas, requests ->
        val activos = users.filter { !it.isBanned }
        val baneados = users.filter { it.isBanned }
        val verificadas = mascotas.filter { it.estado == PublicacionEstado.VERIFICADA || it.estado == PublicacionEstado.RESUELTA }
        val pendientesMascotas = mascotas.filter { it.estado == PublicacionEstado.PENDIENTE }
        val pendientesAdopcion = requests.filter { it.status == AdoptionRequestStatus.PENDING }

        EstadisticasUiState(
            adoptionRequestsCount = pendientesAdopcion.size,
            petModerationCount = pendientesMascotas.size,
            mascotasTotales = mascotas.size,
            mascotasVerificadas = verificadas.size,
            usuariosTotales = activos.size,
            usuariosBaneados = baneados.size,
            listaUsuarios = activos,
            listaUsuariosBaneados = baneados
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EstadisticasUiState()
    )

    fun banearUsuario(userId: String, motivo: String) {
        viewModelScope.launch {
            val user = userRepository.findById(userId)
            if (user != null) {
                userRepository.save(user.copy(isBanned = true, banReason = motivo))
            }
        }
    }

    fun desbanearUsuario(userId: String) {
        viewModelScope.launch {
            val user = userRepository.findById(userId)
            if (user != null) {
                userRepository.save(user.copy(isBanned = false, banReason = ""))
            }
        }
    }
}
