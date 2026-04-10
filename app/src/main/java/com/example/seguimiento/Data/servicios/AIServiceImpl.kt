package com.example.seguimiento.Data.servicios

import com.example.seguimiento.Dominio.servicios.AIService
import com.example.seguimiento.R
import com.example.seguimiento.core.utils.ResourceProvider
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIServiceImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : AIService {
    
    // Lista simulada de palabras inapropiadas para la detección
    private val palabrasProhibidas = listOf("ofensivo", "insulto", "malo", "odio")

    override suspend fun analizarContenido(texto: String): String? {
        // Simulación de llamada a API de IA
        delay(500) 
        val lowerText = texto.lowercase()
        val containsInappropriate = palabrasProhibidas.any { lowerText.contains(it) }
        
        return if (containsInappropriate) {
            resourceProvider.getString(R.string.ai_inappropriate_content_warning)
        } else {
            null
        }
    }

    override suspend fun generarResumen(descripcion: String, categoria: String): String {
        delay(500)
        return resourceProvider.getString(
            R.string.ai_summary_template,
            categoria,
            descripcion.take(50),
            resourceProvider.getString(R.string.ai_relevance_medium),
            resourceProvider.getString(R.string.ai_action_verify)
        )
    }
}
