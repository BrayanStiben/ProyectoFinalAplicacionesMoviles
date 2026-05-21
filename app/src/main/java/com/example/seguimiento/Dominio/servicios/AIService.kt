package com.example.seguimiento.Dominio.servicios

import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado

data class ValidationResult(
    val isValid: Boolean,
    val isOffensive: Boolean,
    val feedback: String,
    val suggestedStatus: PublicacionEstado
)

interface AIService {
    /**
     * Analiza el texto para detectar lenguaje ofensivo o inapropiado.
     * @return null si el contenido es seguro, o una sugerencia de texto alternativo si es inapropiado.
     */
    suspend fun analizarContenido(texto: String): String?

    /**
     * Genera un resumen de la publicación para el moderador.
     */
    suspend fun generarResumen(descripcion: String, categoria: String): String

    /**
     * Realiza una validación completa de la publicación.
     */
    suspend fun validarPublicacion(mascota: Mascota): ValidationResult
}
