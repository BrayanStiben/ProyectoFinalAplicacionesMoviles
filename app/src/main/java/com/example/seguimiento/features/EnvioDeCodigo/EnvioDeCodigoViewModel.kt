package com.example.seguimiento.features.EnvioDeCodigo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EnvioDeCodigoViewModel : ViewModel() {
    // Lista de 4 strings, cada uno será un solo número
    var codigo by mutableStateOf(listOf("", "", "", ""))
        private set

    var segundosRestantes by mutableStateOf(50)
        private set

    var mostrarDialogo by mutableStateOf(false)

    init {
        iniciarTemporizador()
    }

    fun onDigitChange(index: Int, valor: String) {
        // Validación estricta: Solo permite 1 dígito y que sea número
        if (valor.length <= 1 && (valor.isEmpty() || valor.all { it.isDigit() })) {
            val nuevaLista = codigo.toMutableList()
            nuevaLista[index] = valor
            codigo = nuevaLista
        }
    }

    private fun iniciarTemporizador() {
        viewModelScope.launch {
            while (segundosRestantes > 0) {
                delay(1000)
                segundosRestantes--
            }
        }
    }

    fun confirmarIdentidad() {
        // Unimos los dígitos para formar el código final
        val codigoCompleto = codigo.joinToString("")

        // Verificación exacta: "1234"
        if (codigoCompleto == "1234") {
            mostrarDialogo = true
        } else {
            // Log para debug si no es 1234
            println("Código incorrecto o incompleto: $codigoCompleto")
        }
    }
}