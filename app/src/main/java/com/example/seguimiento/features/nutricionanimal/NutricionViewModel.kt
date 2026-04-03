package com.example.seguimiento.features.nutricionanimal

import androidx.lifecycle.ViewModel
import com.example.seguimiento.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NutricionViewModel @Inject constructor() : ViewModel() {

    private val datosNutricion = listOf(
        Regulacion(1, "Dieta Barf", "Guía para alimentación natural", R.drawable.proteccion, "https://www.petys.com/blog/perros/que-es-la-dieta-barf-para-perros/"),
        Regulacion(2, "Alimentos Prohibidos", "Lo que nunca debes darle", R.drawable.leyes, "https://www.clinicaveterinariabarranquilla.com/alimentos-prohibidos-para-perros-y-gatos/"),
        Regulacion(3, "Hidratación Importante", "Consejos para verano", R.drawable.normasyregulaciones, "https://www.purina.lat/articulos/perros/nutricion/agua-e-hidratacion")
    )

    private val datosNecesidades = listOf(
        Regulacion(4, "Cachorros", "Nutrición en crecimiento", R.drawable.petadopticono, "https://www.hillspet.es/dog-care/nutrition-feeding/puppy-nutrition"),
        Regulacion(5, "Adultos Mayores", "Cuidados senior", R.drawable.img1, "https://www.royalcanin.com/es/dogs/health-and-well-being/senior-dog-nutrition"),
        Regulacion(6, "Control de Peso", "Manejo de obesidad", R.drawable.img2, "https://www.affinity-petcare.com/vets/blog/obesidad-en-perros-y-gatos/")
    )

    private val datosAsiFunciona = listOf(
        Regulacion(7, "Digestión Animal", "Proceso biológico", R.drawable.img3, "https://www.expertoanimal.com/sistema-digestivo-del-perro-anatomia-y-caracteristicas-25445.html"),
        Regulacion(8, "Metabolismo", "Energía y vitalidad", R.drawable.img4, "https://www.purina.es/articulos/perros/nutricion/metabolismo-del-perro")
    )

    private val datosAcerca = listOf(
        Regulacion(9, "PetAdopta Nutri", "Sobre nuestra misión", R.drawable.petadopticono, "https://petadopta.com/nosotros")
    )

    private val _regulaciones = MutableStateFlow(datosNutricion)
    val regulaciones: StateFlow<List<Regulacion>> = _regulaciones.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow("nutricion")
    val categoriaSeleccionada = _categoriaSeleccionada.asStateFlow()

    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
        _regulaciones.value = when (categoria) {
            "nutricion" -> datosNutricion
            "necesidades" -> datosNecesidades
            "asifunciona" -> datosAsiFunciona
            "acerca" -> datosAcerca
            else -> datosNutricion
        }
    }
}
