package com.example.seguimiento.features.register

import androidx.lifecycle.ViewModel
import com.example.seguimiento.core.utils.CampoValidado
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.User
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
        if (it.isBlank()) "El nombre es obligatorio" else null
    }

    val ciudad = CampoValidado("") {
        if (it.isBlank()) "La ciudad es obligatoria" else null
    }

    val direccion = CampoValidado("") {
        if (it.isBlank()) "La dirección es obligatoria" else null
    }

    val correo = CampoValidado("") {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches())
            "Correo electrónico inválido"
        else null
    }

    val contrasena = CampoValidado("") {
        if (it.length < 6)
            "La contraseña debe tener mínimo 6 caracteres"
        else null
    }

    val confirmarContrasena = CampoValidado("") {
        if (it != contrasena.value)
            "Las contraseñas no coinciden"
        else null
    }

    private val _resultadoRegistro = MutableStateFlow<ResultadoPeticion?>(null)
    val resultadoRegistro: StateFlow<ResultadoPeticion?> = _resultadoRegistro.asStateFlow()

    fun registrar() {
        if (
            !nombre.isValid ||
            !ciudad.isValid ||
            !direccion.isValid ||
            !correo.isValid ||
            !contrasena.isValid ||
            !confirmarContrasena.isValid
        ) {
            _resultadoRegistro.value =
                ResultadoPeticion.Error("Por favor complete correctamente el formulario")
            return
        }

        viewModelScope.launch {
            val newUser = User(
                id = java.util.UUID.randomUUID().toString(),
                name = nombre.value,
                city = ciudad.value,
                address = direccion.value,
                email = correo.value,
                password = contrasena.value,
                profilePictureUrl = ""
            )
            val result = authRepository.register(newUser)
            if (result.isSuccess) {
                _resultadoRegistro.value =
                    ResultadoPeticion.Exito("Registro exitoso 🎉 Bienvenido a PetAdopta")
            } else {
                _resultadoRegistro.value =
                    ResultadoPeticion.Error("Error al registrar: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}
