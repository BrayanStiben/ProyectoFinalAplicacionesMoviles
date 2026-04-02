package com.example.seguimiento.features.OlvidoContrasena

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OlvidoContrasenaViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _esEmailValido = MutableStateFlow(true)
    val esEmailValido: StateFlow<Boolean> = _esEmailValido.asStateFlow()

    private val _codigoGenerado = MutableStateFlow<String?>(null)
    val codigoGenerado: StateFlow<String?> = _codigoGenerado.asStateFlow()

    fun onEmailChanged(nuevoEmail: String) {
        _email.value = nuevoEmail
        _esEmailValido.value = if (nuevoEmail.isEmpty()) true
        else Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()
    }

    fun generarCodigo() {
        _codigoGenerado.value = authRepository.generateVerificationCode()
    }

    fun limpiarCodigo() {
        _codigoGenerado.value = null
    }

    fun ejecutarValidacionFinal(): Boolean {
        val esValido = Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()
        _esEmailValido.value = esValido
        return esValido
    }
}
