package com.example.seguimiento.features.ReportesAdmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.TiendaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

data class ReportesUiState(
    val mascotasPorEspecie: Map<String, Int> = emptyMap(),
    val adoptadasVsDisponibles: Map<String, Int> = emptyMap(),
    val solicitudesPorEstado: Map<String, Int> = emptyMap(),
    val adopcionesPorMes: Map<String, Int> = emptyMap(),
    val mascotasMasPopulares: List<Pair<String, Int>> = emptyList(),
    val stockPorCategoria: Map<String, Int> = emptyMap(),
    val stockPorProducto: List<Pair<String, Int>> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ReportesViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val adoptionRepository: AdoptionRepository,
    private val tiendaRepository: TiendaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportesUiState())
    val uiState: StateFlow<ReportesUiState> = _uiState.asStateFlow()

    fun generarReportes() {
        _uiState.update { it.copy(isLoading = true) }
        
        val mascotas = mascotaRepository.getAll()
        val solicitudes = adoptionRepository.requests.value
        val productos = tiendaRepository.getAll()

        // 1. Mascotas disponibles por especie
        val porEspecie = mascotas.filter { it.estado == PublicacionEstado.VERIFICADA }
            .groupBy { it.tipo }
            .mapValues { it.value.size }

        // 2. Adoptadas vs Disponibles
        val vs = mapOf(
            "Disponibles" to mascotas.count { it.estado == PublicacionEstado.VERIFICADA },
            "Adoptadas" to mascotas.count { it.estado == PublicacionEstado.ADOPTADA }
        )

        // 3. Solicitudes por estado
        val porEstado = solicitudes.groupBy { it.status.name }
            .mapValues { it.value.size }

        // 4. Adopciones por mes (Basado en solicitudes aprobadas)
        val sdf = SimpleDateFormat("MMM", Locale.getDefault())
        val porMes = solicitudes.filter { it.status == AdoptionRequestStatus.APPROVED }
            .groupBy { sdf.format(Date(it.timestamp)) }
            .mapValues { it.value.size }

        // 5. Mascotas con más likes (Votos)
        val masPopulares = mascotas.sortedByDescending { it.likerIds.size }
            .take(5)
            .map { it.nombre to it.likerIds.size }

        // 6. Existencia de productos por categoría (Stock total por categoría)
        val stockCat = productos.groupBy { it.categoria }
            .mapValues { entry -> entry.value.sumOf { it.stock } }

        // 7. Cantidad de cada producto (Individual)
        val stockProd = productos.sortedBy { it.stock }
            .map { it.nombre to it.stock }

        _uiState.update { 
            it.copy(
                mascotasPorEspecie = porEspecie,
                adoptadasVsDisponibles = vs,
                solicitudesPorEstado = porEstado,
                adopcionesPorMes = porMes,
                mascotasMasPopulares = masPopulares,
                stockPorCategoria = stockCat,
                stockPorProducto = stockProd,
                isLoading = false
            )
        }
    }
}
