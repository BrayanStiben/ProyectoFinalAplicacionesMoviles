package com.example.seguimiento.features.Refugios
import androidx.compose.runtime.getValue 
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RefugioViewModel @Inject constructor() : ViewModel() {
    // Estado para que la barra inferior cambie de color
    var tabSeleccionada by mutableStateOf(3)

    private val _refugios = MutableStateFlow(
        listOf(
            Refugio(1, "Refugio Patitas Felices", "Verificado", verificado = true),
            Refugio(2, "Albergue del Sol", "Pendiente de Verificación", verificado = false),
            Refugio(3, "Refugio Esperanza", "Pendiente de Verificación", esNuevo = true, verificado = false)
        )
    )
    val refugios: StateFlow<List<Refugio>> = _refugios.asStateFlow()

    fun aprobarRefugio(id: Int) {
        _refugios.value = _refugios.value.map {
            if (it.id == id) it.copy(estado = "Verificado", verificado = true) else it
        }
    }
    fun contactarRefugio(id: Int) { /* Lógica de Intent aquí */ }
}
