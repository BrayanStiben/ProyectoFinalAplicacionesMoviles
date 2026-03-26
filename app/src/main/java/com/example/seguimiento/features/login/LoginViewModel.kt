package com.example.seguimiento.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.seguimiento.core.utils.CampoValidado
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val isFormValid: Boolean
        get() = email.isValid && password.isValid

    fun login() {
        if (isFormValid) {
            val user = userRepository.login(email.value, password.value)
            // Manejar resultado del login
        }
    }

    fun resetForm() {
        email.reset()
        password.reset()
    }
}
