package com.example.seguimiento.core.utils

sealed class ResultadoPeticion {
    data class Exito(val mensaje: String) : ResultadoPeticion()
    data class ExitoResId(val resId: Int, val args: List<Any> = emptyList()) : ResultadoPeticion()
    data class Error(val mensaje: String) : ResultadoPeticion()
    data class ErrorResId(val resId: Int, val args: List<Any> = emptyList()) : ResultadoPeticion()
}
