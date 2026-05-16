package com.example.seguimiento.features.register

import androidx.lifecycle.ViewModel
import com.example.seguimiento.core.utils.CampoValidado
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.core.utils.ResultadoPeticion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val nombre = CampoValidado("") {
        if (it.isBlank()) com.example.seguimiento.R.string.error_name_required else null
    }

    val correo = CampoValidado("") {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches())
            com.example.seguimiento.R.string.error_email_invalid
        else null
    }

    val contrasena = CampoValidado("") {
        if (it.length < 6)
            com.example.seguimiento.R.string.error_password_too_short_register
        else null
    }

    val confirmarContrasena = CampoValidado("") {
        if (it != contrasena.value)
            com.example.seguimiento.R.string.error_passwords_dont_match
        else null
    }

    private val _resultadoRegistro = MutableStateFlow<ResultadoPeticion?>(null)
    val resultadoRegistro: StateFlow<ResultadoPeticion?> = _resultadoRegistro.asStateFlow()

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando = _estaCargando.asStateFlow()

    fun registrar() {
        if (!nombre.isValid || !correo.isValid || !contrasena.isValid || !confirmarContrasena.isValid) {
            _resultadoRegistro.value = ResultadoPeticion.ErrorResId(com.example.seguimiento.R.string.error_form_incomplete)
            return
        }

        viewModelScope.launch {
            _estaCargando.value = true
            try {
                val newUser = User(
                    id = "", 
                    name = nombre.value.trim(),
                    email = correo.value.trim().lowercase(),
                    role = UserRole.USER
                )
                
                // Registro sin imagen aquí, ya que se hará en "Finalizar Registro"
                val result = authRepository.register(newUser, contrasena.value, null)
                
                if (result.isSuccess) {
                    _resultadoRegistro.value = ResultadoPeticion.ExitoResId(com.example.seguimiento.R.string.register_success)
                } else {
                    _resultadoRegistro.value = ResultadoPeticion.Error(result.exceptionOrNull()?.message ?: "Error")
                }
            } catch (e: Exception) {
                _resultadoRegistro.value = ResultadoPeticion.Error(e.message ?: "Error")
            } finally {
                _estaCargando.value = false
            }
        }
    }
}
