package com.example.seguimiento.features.Notificaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    private val notificacionRepository: NotificacionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Se especifican tipos explícitos para lista y user para ayudar al compilador
    val notificaciones: StateFlow<List<Notificacion>> = combine(
        notificacionRepository.notificaciones,
        authRepository.currentUser
    ) { lista: List<Notificacion>, user: User? ->
        lista.filter { notif ->
            notif.userId == "" || notif.userId == user?.id
        }.sortedByDescending { it.timestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun marcarComoLeida(id: String) {
        viewModelScope.launch {
            notificacionRepository.marcarComoLeida(id)
        }
    }

    fun limpiarTodo() {
        viewModelScope.launch {
            val user = authRepository.currentUser.value
            user?.id?.let { userId ->
                notificacionRepository.clearAll(userId)
            }
        }
    }
}
