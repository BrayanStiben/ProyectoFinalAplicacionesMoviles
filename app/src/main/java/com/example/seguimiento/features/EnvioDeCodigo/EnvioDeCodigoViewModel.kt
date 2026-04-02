package com.example.seguimiento.features.EnvioDeCodigo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnvioDeCodigoViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var codigo by mutableStateOf(listOf("", "", "", ""))
        private set

    var segundosRestantes by mutableStateOf(50)
        private set

    var mostrarDialogo by mutableStateOf(false)
    var errorCodigo by mutableStateOf(false)

    init {
        iniciarTemporizador()
    }

    fun onDigitChange(index: Int, valor: String) {
        if (valor.length <= 1 && (valor.isEmpty() || valor.all { it.isDigit() })) {
            val nuevaLista = codigo.toMutableList()
            nuevaLista[index] = valor
            codigo = nuevaLista
            errorCodigo = false
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
        val codigoCompleto = codigo.joinToString("")
        val codigoCorrecto = authRepository.getVerificationCode()

        if (codigoCompleto == codigoCorrecto && codigoCorrecto != null) {
            mostrarDialogo = true
            errorCodigo = false
        } else {
            errorCodigo = true
        }
    }
}
