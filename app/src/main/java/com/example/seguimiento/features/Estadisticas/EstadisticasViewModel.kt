package com.example.seguimiento.features.Estadisticas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BarData(val label: String, val valueBlue: Int, val valueOrange: Int = 0)

data class EstadisticasUiState(
    val solicitudesNuevas: Int = 25,
    val mascotasTotales: Int = 0,
    val mascotasActivas: Int = 0,
    val usuariosTotales: Int = 0,
    val usuariosBaneados: Int = 0,
    val listaUsuarios: List<User> = emptyList(),
    val listaUsuariosBaneados: List<User> = emptyList(),
    val pantallaSeleccionada: Int = 3,
    val estadisticasSemanales: List<BarData> = listOf(
        BarData("Mon", 150), BarData("May", 260),
        BarData("Jun", 100, 120), BarData("Jun", 0, 170),
        BarData("Ven", 220, 230), BarData("San", 0, 330)
    )
)

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstadisticasUiState())
    val uiState: StateFlow<EstadisticasUiState> = combine(
        userRepository.users,
        mascotaRepository.mascotas,
        _uiState
    ) { users, mascotas, currentState ->
        val activos = users.filter { !it.isBanned }
        val baneados = users.filter { it.isBanned }
        currentState.copy(
            usuariosTotales = activos.size,
            usuariosBaneados = baneados.size,
            listaUsuarios = activos,
            listaUsuariosBaneados = baneados,
            mascotasTotales = mascotas.size,
            mascotasActivas = mascotas.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EstadisticasUiState()
    )

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(pantallaSeleccionada = index) }
    }

    fun banearUsuario(userId: String, motivo: String) {
        viewModelScope.launch {
            val user = userRepository.findById(userId)
            if (user != null) {
                val updatedUser = user.copy(isBanned = true, banReason = motivo)
                userRepository.save(updatedUser)
            }
        }
    }

    fun desbanearUsuario(userId: String) {
        viewModelScope.launch {
            val user = userRepository.findById(userId)
            if (user != null) {
                val updatedUser = user.copy(isBanned = false, banReason = "")
                userRepository.save(updatedUser)
            }
        }
    }
}
