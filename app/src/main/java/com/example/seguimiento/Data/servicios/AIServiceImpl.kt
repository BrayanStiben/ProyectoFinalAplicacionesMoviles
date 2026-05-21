package com.example.seguimiento.Data.servicios

import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.servicios.AIService
import com.example.seguimiento.Dominio.servicios.ValidationResult
import com.example.seguimiento.R
import com.example.seguimiento.core.utils.ResourceProvider
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- API DE DETECCIÓN DE PROFANIDAD ---
interface ProfanityApiService {
    @GET("service/containsprofanity")
    suspend fun containsProfanity(@Query("text") text: String): Boolean
}

@Singleton
class AIServiceImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : AIService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.purgomalum.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val profanityApi = retrofit.create(ProfanityApiService::class.java)
    
    // Lista local de respaldo para términos específicos de la región
    private val palabrasProhibidasLocal = listOf(
        "estupido", "mierda", "puto", "puta", "idiota", "imbecil", "perra", "malparido", "gonorrea"
    )

    override suspend fun analizarContenido(texto: String): String? {
        val lowerText = texto.lowercase()
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
        
        val apiDetected = try { profanityApi.containsProfanity(texto) } catch (e: Exception) { false }
        val localDetected = palabrasProhibidasLocal.any { lowerText.contains(it) }
        
        return if (apiDetected || localDetected) {
            resourceProvider.getString(R.string.ai_inappropriate_content_warning)
        } else {
            null
        }
    }

    override suspend fun generarResumen(descripcion: String, categoria: String): String {
        return resourceProvider.getString(R.string.ai_resumen_auto)
    }

    override suspend fun validarPublicacion(mascota: Mascota): ValidationResult {
        delay(1200) // Simular procesamiento profundo de IA
        
        val reporte = StringBuilder()
        var hasInsults = false
        var hasEmptyFields = false
        
        val fullText = "${mascota.nombre} ${mascota.descripcion} ${mascota.raza} ${mascota.ubicacion}".lowercase()
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")

        reporte.append(resourceProvider.getString(R.string.ai_report_title)).append("\n\n")

        // 1. VALIDACIÓN DE CAMPOS VACÍOS
        val camposFaltantes = mutableListOf<String>()
        if (mascota.nombre.isBlank()) camposFaltantes.add(resourceProvider.getString(R.string.reg_pet_placeholder_name))
        if (mascota.descripcion.isBlank()) camposFaltantes.add(resourceProvider.getString(R.string.reg_pet_label_desc))
        if (mascota.tipo.isBlank()) camposFaltantes.add(resourceProvider.getString(R.string.reg_pet_placeholder_type))
        if (mascota.raza.isBlank()) camposFaltantes.add(resourceProvider.getString(R.string.reg_pet_placeholder_breed))
        if (mascota.ubicacion.isBlank()) camposFaltantes.add(resourceProvider.getString(R.string.reg_pet_placeholder_city))

        if (camposFaltantes.isNotEmpty()) {
            hasEmptyFields = true
            reporte.append(resourceProvider.getString(R.string.ai_fields_incomplete, camposFaltantes.joinToString(", "))).append("\n")
        } else {
            reporte.append(resourceProvider.getString(R.string.ai_fields_complete)).append("\n")
        }

        // 2. VALIDACIÓN DE INSULTOS
        val apiDetected = try { profanityApi.containsProfanity("${mascota.nombre} ${mascota.descripcion}") } catch (e: Exception) { false }
        val localEncontradas = palabrasProhibidasLocal.filter { fullText.contains(it) }
        
        if (apiDetected || localEncontradas.isNotEmpty()) {
            hasInsults = true
            val detalle = if (localEncontradas.isNotEmpty()) localEncontradas.joinToString(", ") else resourceProvider.getString(R.string.ai_insult_global)
            reporte.append(resourceProvider.getString(R.string.ai_insult_detected, detalle)).append("\n")
        } else {
            reporte.append(resourceProvider.getString(R.string.ai_language_clean)).append("\n")
        }

        // 3. CONSISTENCIA
        if (mascota.tipo.equals("Perro", true) && (mascota.raza.contains("Siamés", true) || mascota.raza.contains("Persa", true))) {
            hasEmptyFields = true // Usamos esto como flag general de error
            reporte.append(resourceProvider.getString(R.string.ai_consistency_error)).append("\n")
        }

        val isValid = !hasInsults && !hasEmptyFields
        val feedbackFinal = if (isValid) {
            resourceProvider.getString(R.string.ai_status_apta)
        } else {
            resourceProvider.getString(R.string.ai_status_error) + "\n\n" + reporte.toString()
        }

        return ValidationResult(
            isValid = isValid,
            isOffensive = hasInsults,
            feedback = feedbackFinal,
            suggestedStatus = if (hasInsults) PublicacionEstado.RECHAZADA else PublicacionEstado.PENDIENTE
        )
    }
}
