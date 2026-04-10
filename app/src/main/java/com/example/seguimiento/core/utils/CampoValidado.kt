package com.example.seguimiento.core.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

    class CampoValidado<T>(
        private val initialValue: T,
        private val validate: (T) -> Int?
    ) {
        // Estado del valor del campo
        var value by mutableStateOf(initialValue)
            private set

        // Estado para controlar cuándo mostrar el error
        var showError by mutableStateOf(false)
            private set

        // ID del mensaje de error. get() para que sea de solo lectura desde el exterior
        val errorResId: Int?
            get() = if (showError) validate(value) else null

        // Indica si el campo es válido, es de solo lectura desde el exterior
        val isValid: Boolean
            get() = validate(value) == null

        // Función para actualizar el valor del campo
        fun onChange(newValue: T) {
            value = newValue
            showError = true
        }

        // Función para resetear el campo a su valor inicial
        fun reset() {
            value = initialValue
            showError = false
        }
}