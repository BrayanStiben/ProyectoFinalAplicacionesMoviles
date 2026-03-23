package com.example.seguimiento.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.seguimiento.core.utils.CampoValidado


class LoginViewModel : ViewModel() {

    val email = CampoValidado("") { value ->
        when {
            value.isEmpty() -> "El usuario es obligatorio"
            // Si quieres forzar formato email, descomenta la siguiente línea:
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

    fun resetForm() {
        email.reset()
        password.reset()
    }
}

