package com.example.seguimiento.features.Loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estado del progreso (0.0f a 1.0f)
    private val _progress = MutableStateFlow(0.0f)
    val progress = _progress.asStateFlow()

    // Estado para indicar cuándo terminar la carga
    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        startLoading()
    }

    private fun startLoading() {
        viewModelScope.launch {
            // Simulamos una carga progresiva
            for (i in 0..100) {
                delay(20) 
                _progress.value = i / 100.0f
            }
            
            // Verificamos si hay sesión activa
            val user = authRepository.currentUser.value
            if (user != null) {
                _isLoggedIn.value = true
                _isAdmin.value = user.role == UserRole.ADMIN
            } else {
                _isLoggedIn.value = false
            }

            // Marcamos como finalizado
            _isFinished.value = true
        }
    }
}
