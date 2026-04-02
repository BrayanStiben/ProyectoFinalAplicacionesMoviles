package com.example.seguimiento.features.PantallaRecuperarContrasena

import androidx.lifecycle.ViewModel
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RecuperarContrasenaViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _esExitoso = MutableStateFlow(false)
    val esExitoso: StateFlow<Boolean> = _esExitoso.asStateFlow()

    fun setEmail(nuevoEmail: String) {
        _email.value = nuevoEmail
    }

    fun onPasswordChanged(nuevaPass: String) {
        _password.value = nuevaPass
    }

    fun onConfirmPasswordChanged(nuevaPass: String) {
        _confirmPassword.value = nuevaPass
    }

    fun actualizarContrasena(): Boolean {
        if (_password.value.isNotEmpty() && _password.value == _confirmPassword.value) {
            val resultado = userRepository.updatePassword(_email.value, _password.value)
            if (resultado) {
                _esExitoso.value = true
                return true
            }
        }
        return false
    }
}
