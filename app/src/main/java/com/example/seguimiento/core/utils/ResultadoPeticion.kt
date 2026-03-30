package com.example.seguimiento.core.utils

sealed class ResultadoPeticion {
    data class Exito(val mensaje: String) : ResultadoPeticion()
    data class Error(val mensaje: String) : ResultadoPeticion()
}