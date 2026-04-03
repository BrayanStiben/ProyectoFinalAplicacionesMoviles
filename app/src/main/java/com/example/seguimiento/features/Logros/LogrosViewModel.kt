package com.example.seguimiento.features.Logros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Logro
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.LogrosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class LogrosUiState(
    val todosLosLogros: List<Logro> = emptyList(),
    val obtenidosIds: List<String> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LogrosViewModel @Inject constructor(
    private val logrosRepository: LogrosRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<LogrosUiState> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(LogrosUiState())
            else {
                logrosRepository.getLogrosUsuario(user.id).map { obtenidos ->
                    LogrosUiState(
                        todosLosLogros = logrosRepository.todosLosLogros,
                        obtenidosIds = obtenidos
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LogrosUiState(isLoading = true))
}
