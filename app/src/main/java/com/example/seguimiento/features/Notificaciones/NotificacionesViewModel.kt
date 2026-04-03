package com.example.seguimiento.features.Notificaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    private val notificacionRepository: NotificacionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val notificaciones: StateFlow<List<Notificacion>> = combine(
        notificacionRepository.notificaciones,
        authRepository.currentUser
    ) { lista, user ->
        lista.filter { notif ->
            notif.userId == "" || notif.userId == user?.id
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun marcarComoLeida(id: String) {
        notificacionRepository.marcarComoLeida(id)
    }

    fun limpiarTodo() {
        notificacionRepository.clearAll()
    }
}
