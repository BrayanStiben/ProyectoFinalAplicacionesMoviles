package com.example.seguimiento.Data.servicios

import com.example.seguimiento.Dominio.servicios.AIService
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIServiceImpl @Inject constructor() : AIService {
    
    // Lista simulada de palabras inapropiadas para la detección
    private val palabrasProhibidas = listOf("ofensivo", "insulto", "malo", "odio")

    override suspend fun analizarContenido(texto: String): String? {
        // Simulación de llamada a API de IA
        delay(500) 
        val lowerText = texto.lowercase()
        val containsInappropriate = palabrasProhibidas.any { lowerText.contains(it) }
        
        return if (containsInappropriate) {
            "El contenido parece inapropiado. Sugerencia: Por favor, utiliza un lenguaje más respetuoso y positivo."
        } else {
            null
        }
    }

    override suspend fun generarResumen(descripcion: String, categoria: String): String {
        delay(500)
        return "Resumen IA: Publicación sobre un $categoria. Puntos clave: ${descripcion.take(50)}... Relevancia estimada: Media. Acción recomendada: Verificar ubicación."
    }
}
