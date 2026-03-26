package com.example.seguimiento.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.seguimiento.core.utils.CampoValidado
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.Dominio.modelos.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val email = CampoValidado("") { value ->
        when {
            value.isEmpty() -> "El usuario es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Email inválido"
            else -> null
        }
    }

    val password = CampoValidado("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }
    }

    private val _loginState = MutableStateFlow<LoginResult?>(null)
    val loginState = _loginState.asStateFlow()

    val isFormValid: Boolean
        get() = email.isValid && password.isValid

    fun login() {
        if (isFormValid) {
            val user = userRepository.login(email.value, password.value)
            if (user != null) {
                _loginState.value = LoginResult.Success(isAdmin = user.role == UserRole.ADMIN)
            } else {
                _loginState.value = LoginResult.Error("Credenciales incorrectas")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }

    fun resetForm() {
        email.reset()
        password.reset()
    }
}

sealed class LoginResult {
    data class Success(val isAdmin: Boolean) : LoginResult()
    data class Error(val message: String) : LoginResult()
}
