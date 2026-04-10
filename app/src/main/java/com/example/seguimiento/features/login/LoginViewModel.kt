package com.example.seguimiento.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.core.utils.CampoValidado
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.LogrosRepository
import com.example.seguimiento.Dominio.modelos.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val notificacionRepository: NotificacionRepository,
    private val logrosRepository: LogrosRepository
) : ViewModel() {

    val email = CampoValidado("") { value ->
        when {
            value.isEmpty() -> com.example.seguimiento.R.string.error_user_required
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> com.example.seguimiento.R.string.error_email_invalid
            else -> null
        }
    }

    val password = CampoValidado("") { value ->
        when {
            value.isEmpty() -> com.example.seguimiento.R.string.error_password_required
            value.length < 3 -> com.example.seguimiento.R.string.error_password_too_short
            else -> null
        }
    }

    private val _loginState = MutableStateFlow<LoginResult?>(null)
    val loginState = _loginState.asStateFlow()

    val isFormValid: Boolean
        get() = email.isValid && password.isValid

    fun login() {
        if (isFormValid) {
            viewModelScope.launch {
                val result = authRepository.login(email.value, password.value)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    
                    // --- REQUISITO: NOTIFICACIÓN Y LOGRO SOLO AL LOGUEARSE ---
                    user?.let {
                        logrosRepository.ganarLogro(it.id, "sys_1")
                        notificacionRepository.addNotificacion(
                            tituloResId = com.example.seguimiento.R.string.login_welcome_back_title,
                            tituloArgs = listOf(it.name),
                            mensajeResId = com.example.seguimiento.R.string.login_welcome_back_message,
                            tipo = "INFO",
                            userId = it.id
                        )
                    }

                    _loginState.value = LoginResult.Success(isAdmin = user?.role == UserRole.ADMIN)
                } else {
                    _loginState.value = LoginResult.Error(com.example.seguimiento.R.string.error_invalid_credentials)
                }
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
    data class Error(val messageResId: Int) : LoginResult()
}
