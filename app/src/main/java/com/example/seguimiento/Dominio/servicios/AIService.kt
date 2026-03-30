package com.example.seguimiento.Dominio.servicios

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
}
