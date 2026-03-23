package com.example.seguimiento.features.OlvidoContrasena

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch
    import android.util.Patterns
    import com.example.seguimiento.core.utils.CampoValidado
    import com.example.seguimiento.core.utils.ResultadoPeticion


import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class RecuperarContrasenaViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _esEmailValido = MutableStateFlow(true)
    val esEmailValido: StateFlow<Boolean> = _esEmailValido.asStateFlow()

    fun onEmailChanged(nuevoEmail: String) {
        _email.value = nuevoEmail
        // No mostramos error si está vacío, solo si el formato es incorrecto
        _esEmailValido.value = if (nuevoEmail.isEmpty()) true
        else Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()
    }

    fun ejecutarValidacionFinal(): Boolean {
        val esValido = Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()
        _esEmailValido.value = esValido
        return esValido
    }
}