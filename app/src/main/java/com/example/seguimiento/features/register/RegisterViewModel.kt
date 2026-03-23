package com.example.seguimiento.features.register

import androidx.lifecycle.ViewModel
import com.example.seguimiento.core.utils.CampoValidado



import androidx.lifecycle.viewModelScope
import com.example.seguimiento.core.utils.ResultadoPeticion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

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
            _resultadoRegistro.value =
                ResultadoPeticion.Exito("Registro exitoso 🎉 Bienvenido a PetAdopta")
        }
    }
}