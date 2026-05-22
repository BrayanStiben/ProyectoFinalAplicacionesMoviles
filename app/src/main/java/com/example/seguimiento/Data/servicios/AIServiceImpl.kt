package com.example.seguimiento.Data.servicios

import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.servicios.AIService
import com.example.seguimiento.Dominio.servicios.ValidationResult
import com.example.seguimiento.core.utils.ResourceProvider
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIServiceImpl @Inject constructor(
    private val nvidiaApi: NvidiaApi,
    private val resourceProvider: ResourceProvider
) : AIService {

    private val gson = Gson()
    private val apiKey = "Bearer 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"
    private val model = "meta/llama-3.1-8b-instruct"

    override suspend fun analizarContenido(texto: String): String? {
        if (texto.isBlank()) return null
        
        val prompt = """
            Actúa como moderador de PetAdopta. Analiza si este texto es ofensivo o inapropiado: "$texto". 
            Si es seguro, responde ÚNICAMENTE con la palabra "SAFE". 
            Si no lo es, devuelve una versión corregida y amable en español.
        """.trimIndent()

        return try {
            val request = NvidiaRequest(model = model, messages = listOf(NvidiaMessage("user", prompt)))
            val response = nvidiaApi.complete(apiKey, request = request)
            val result = response.choices.firstOrNull()?.message?.content?.trim()
            if (result?.contains("SAFE", ignoreCase = true) == true) null else result
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun generarResumen(descripcion: String, categoria: String): String {
        if (descripcion.isBlank()) return "Publicación sin descripción disponible."
        
        val prompt = "Resume en una frase corta y amigable esta publicación de un $categoria: $descripcion"
        return try {
            val request = NvidiaRequest(model = model, messages = listOf(NvidiaMessage("user", prompt)))
            val response = nvidiaApi.complete(apiKey, request = request)
            response.choices.firstOrNull()?.message?.content ?: descripcion
        } catch (e: Exception) {
            descripcion
        }
    }

    override suspend fun validarPublicacion(mascota: Mascota): ValidationResult {
        // 1. VALIDACIÓN MANUAL EN KOTLIN (Prioridad absoluta para evitar el error de 'campo vacío' por la IA)
        val nombre = mascota.nombre.trim()
        val descripcion = mascota.descripcion.trim()
        val especie = mascota.tipo.trim()
        
        val faltantes = mutableListOf<String>()
        if (nombre.isEmpty()) faltantes.add("Nombre")
        if (descripcion.isEmpty()) faltantes.add("Descripción")
        if (especie.isEmpty()) faltantes.add("Especie")

        if (faltantes.isNotEmpty()) {
            return ValidationResult(
                isValid = false,
                isOffensive = false,
                feedback = "El campo '${faltantes.first()}' está vacío. Por favor, complete todos los campos obligatorios para la adopción.",
                suggestedStatus = PublicacionEstado.RECHAZADA
            )
        }

        // 2. VALIDACIÓN CON IA (GROSERÍAS Y CALIDAD)
        val prompt = """
            Eres un moderador de seguridad experto para la app PetAdopta. 
            Analiza rigurosamente este contenido para detectar groserías, insultos o lenguaje inapropiado.
            
            CONTENIDO A EVALUAR:
            - Nombre: "$nombre"
            - Descripción: "$descripcion"
            - Especie: "$especie"
            
            TAREAS:
            1. Busca groserías o insultos (ej: 'malparido', 'mierda', 'pendejo', 'gonorrea', 'hp', etc.).
            2. Si detectas CUALQUIER lenguaje ofensivo o vulgar, marca esOfensiva: true.
            3. Los campos YA fueron verificados como NO vacíos, así que concéntrate en moderar el lenguaje.
            
            RESPONDE ESTRICTAMENTE EN FORMATO JSON PLANO:
            {
              "esOfensiva": boolean,
              "feedback": "mensaje explicativo en español indicando si se detectaron groserías o si el texto es correcto",
              "estadoSugerido": "PENDIENTE" o "RECHAZADA"
            }
        """.trimIndent()

        return try {
            val request = NvidiaRequest(model = model, messages = listOf(NvidiaMessage("user", prompt)))
            val response = nvidiaApi.complete(apiKey, request = request)
            val content = response.choices.firstOrNull()?.message?.content ?: ""
            
            // Extracción segura del JSON
            val jsonStart = content.indexOf("{")
            val jsonEnd = content.lastIndexOf("}")
            val cleanJson = if (jsonStart != -1 && jsonEnd != -1) content.substring(jsonStart, jsonEnd + 1) else "{}"
            
            val data = gson.fromJson(cleanJson, Map::class.java)
            
            val esOfensiva = data["esOfensiva"] as? Boolean ?: false
            val feedback = data["feedback"] as? String ?: (data["feedbackAdmin"] as? String ?: "Análisis de IA completado.")
            val estadoSugerido = data["estadoSugerido"] as? String ?: "PENDIENTE"

            ValidationResult(
                isValid = !esOfensiva,
                isOffensive = esOfensiva,
                feedback = feedback,
                suggestedStatus = if (esOfensiva || estadoSugerido == "RECHAZADA") 
                    PublicacionEstado.RECHAZADA else PublicacionEstado.PENDIENTE
            )
        } catch (e: Exception) {
            // Si falla la IA pero sabemos que tiene texto (por el paso 1), dejamos pasar como pendiente de moderación manual
            ValidationResult(
                isValid = true,
                isOffensive = false,
                feedback = "Validación técnica superada. Pendiente de revisión por un moderador.",
                suggestedStatus = PublicacionEstado.PENDIENTE
            )
        }
    }

    override suspend fun analizarEstadisticas(
        mascotasPorEspecie: Map<String, Int>,
        adoptadasVsDisponibles: Map<String, Int>,
        solicitudesPorEstado: Map<String, Int>
    ): String {
        val prompt = """
            Genera un reporte motivador de 3 líneas con emojis para el administrador basado en:
            Especies: $mascotasPorEspecie
            Éxito: $adoptadasVsDisponibles
            Solicitudes: $solicitudesPorEstado
        """.trimIndent()

        return try {
            val request = NvidiaRequest(model = model, messages = listOf(NvidiaMessage("user", prompt)))
            val response = nvidiaApi.complete(apiKey, request = request)
            response.choices.firstOrNull()?.message?.content ?: "Estadísticas procesadas."
        } catch (e: Exception) {
            "Análisis no disponible actualmente."
        }
    }
}
