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
    
    private val palabrasProhibidasLocal = listOf("estupido", "mierda", "puto", "puta", "idiota", "imbecil", "perra", "malparido", "gonorrea")

    override suspend fun analizarContenido(texto: String): String? = null
    override suspend fun generarResumen(descripcion: String, categoria: String): String = ""

    override suspend fun validarPublicacion(mascota: Mascota): ValidationResult {
        delay(1000)
        val reporte = StringBuilder()
        var hasInsults = false
        var hasEmptyFields = false
        
        val fullText = "${mascota.nombre} ${mascota.descripcion}".lowercase()

        // 1. CAMPOS VACÍOS
        val camposFaltantes = mutableListOf<String>()
        if (mascota.nombre.isBlank()) camposFaltantes.add(resourceProvider.getString(R.string.reg_pet_placeholder_name))
        if (mascota.descripcion.isBlank()) camposFaltantes.add(resourceProvider.getString(R.string.reg_pet_label_desc))

        if (camposFaltantes.isNotEmpty()) {
            hasEmptyFields = true
            reporte.append(resourceProvider.getString(R.string.ai_fields_incomplete, camposFaltantes.joinToString(", "))).append("\n")
        } else {
            reporte.append(resourceProvider.getString(R.string.ai_fields_complete)).append("\n")
        }

        // 2. INSULTOS
        val localEncontradas = palabrasProhibidasLocal.filter { fullText.contains(it) }
        if (localEncontradas.isNotEmpty()) {
            hasInsults = true
            reporte.append(resourceProvider.getString(R.string.ai_insult_detected, localEncontradas.joinToString(", "))).append("\n")
        } else {
            reporte.append(resourceProvider.getString(R.string.ai_language_clean)).append("\n")
        }

        val isValid = !hasInsults && !hasEmptyFields
        // ELIMINADO EL ENCABEZADO DE "ERROR" ROJO. SOLO EL REPORTE LIMPIO.
        return ValidationResult(
            isValid = isValid,
            isOffensive = hasInsults,
            feedback = reporte.toString(),
            suggestedStatus = if (hasInsults) PublicacionEstado.RECHAZADA else PublicacionEstado.PENDIENTE
        )
    }

    override suspend fun analizarEstadisticas(mascotasPorEspecie: Map<String, Int>, adoptadasVsDisponibles: Map<String, Int>, solicitudesPorEstado: Map<String, Int>): String {
        delay(2000)
        val total = adoptadasVsDisponibles.values.sum()
        val adoptadas = adoptadasVsDisponibles["Adoptadas"] ?: 0
        val tasaExito = if (total > 0) (adoptadas.toFloat() / total * 100).toInt() else 0

        return """
            🌟 ¡Hola, Administrador! Análisis listo:
            📈 La comunidad crece con un $tasaExito% de éxito en adopciones.
            🐾 Las mascotas se registran activamente por todo el país.
            💡 Sugerencia IA: Revisa las solicitudes pendientes para cerrar el mes con broche de oro.
            ¡Gran trabajo salvando vidas! ✨
        """.trimIndent()
    }
}
