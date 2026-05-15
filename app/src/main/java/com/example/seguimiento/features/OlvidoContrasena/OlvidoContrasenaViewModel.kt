package com.example.seguimiento.features.OlvidoContrasena

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.core.utils.ResultadoPeticion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OlvidoContrasenaViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _esEmailValido = MutableStateFlow(true)
    val esEmailValido: StateFlow<Boolean> = _esEmailValido.asStateFlow()

    private val _resultado = MutableStateFlow<ResultadoPeticion?>(null)
    val resultado: StateFlow<ResultadoPeticion?> = _resultado.asStateFlow()

    fun onEmailChanged(nuevoEmail: String) {
        _email.value = nuevoEmail
        _esEmailValido.value = if (nuevoEmail.isEmpty()) true
        else Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()
    }

    fun enviarCorreoRecuperacion() {
        if (!ejecutarValidacionFinal()) return

        viewModelScope.launch {
            val res = authRepository.recoverPassword(_email.value)
            if (res.isSuccess) {
                _resultado.value = ResultadoPeticion.ExitoResId(com.example.seguimiento.R.string.forgot_password_success_sent)
            } else {
                _resultado.value = ResultadoPeticion.ErrorResId(
                    com.example.seguimiento.R.string.error_recovery_failed,
                    listOf(res.exceptionOrNull()?.message ?: "Unknown error")
                )
            }
        }
    }

    fun limpiarResultado() {
        _resultado.value = null
    }

    fun ejecutarValidacionFinal(): Boolean {
        val esValido = Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()
        _esEmailValido.value = esValido
        return esValido
    }
}
