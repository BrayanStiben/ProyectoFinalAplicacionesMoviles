package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Refugio
import com.example.seguimiento.Dominio.modelos.RefugioEstado
import com.example.seguimiento.Dominio.modelos.RefugioTipo
import com.example.seguimiento.Dominio.repositorios.RefugioRepository
import com.example.seguimiento.R
import com.example.seguimiento.core.utils.ResourceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefugioRepositoryImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : RefugioRepository {

    private val _refugios = MutableStateFlow<List<Refugio>>(fetchInitialRefugios())
    override val refugios: StateFlow<List<Refugio>> = _refugios.asStateFlow()

    override fun getAll(): List<Refugio> = _refugios.value

    override fun getById(id: String): Refugio? = _refugios.value.find { it.id == id }

    override fun save(refugio: Refugio) {
        _refugios.update { currentList ->
            val index = currentList.indexOfFirst { it.id == refugio.id }
            if (index != -1) {
                currentList.toMutableList().apply { set(index, refugio) }
            } else {
                currentList + refugio
            }
        }
    }

    override fun delete(id: String) {
        _refugios.update { it.filter { r -> r.id != id } }
    }

    override fun actualizarEstado(id: String, estado: RefugioEstado) {
        _refugios.update { list ->
            list.map { if (it.id == id) it.copy(estado = estado) else it }
        }
    }

    private fun fetchInitialRefugios(): List<Refugio> {
        return listOf(
            // --- QUINDÍO (ARMENIA Y ALREDEDORES) ---
            Refugio("1", resourceProvider.getString(R.string.mock_shelter_ada_name), resourceProvider.getString(R.string.mock_shelter_ada_addr), "3104567890", resourceProvider.getString(R.string.mock_shelter_ada_desc), "https://images.unsplash.com/photo-1548191265-cc70d3d45ba1", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("2", resourceProvider.getString(R.string.mock_shelter_cafe_name), resourceProvider.getString(R.string.mock_shelter_cafe_addr), "3001112233", resourceProvider.getString(R.string.mock_shelter_cafe_desc), "https://images.unsplash.com/photo-1584132967334-10e028bd69f7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("3", resourceProvider.getString(R.string.mock_shelter_tebaida_name), resourceProvider.getString(R.string.mock_shelter_tebaida_addr), "3209876543", resourceProvider.getString(R.string.mock_shelter_tebaida_desc), "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("4", resourceProvider.getString(R.string.mock_shelter_armenia_name), resourceProvider.getString(R.string.mock_shelter_armenia_addr), "3155556677", resourceProvider.getString(R.string.mock_shelter_armenia_desc), "https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("5", resourceProvider.getString(R.string.mock_shelter_roque_name), resourceProvider.getString(R.string.mock_shelter_roque_addr), "3004445566", resourceProvider.getString(R.string.mock_shelter_roque_desc), "https://images.unsplash.com/photo-1511497584788-8767fe770c52", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("6", resourceProvider.getString(R.string.mock_shelter_uniquindio_name), resourceProvider.getString(R.string.mock_shelter_uniquindio_addr), "3121234567", resourceProvider.getString(R.string.mock_shelter_uniquindio_desc), "https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("7", resourceProvider.getString(R.string.mock_shelter_arca_name), resourceProvider.getString(R.string.mock_shelter_arca_addr), "3131234567", resourceProvider.getString(R.string.mock_shelter_arca_desc), "https://images.unsplash.com/photo-1576201836106-db1758fd1c97", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("8", resourceProvider.getString(R.string.mock_shelter_eco_name), resourceProvider.getString(R.string.mock_shelter_eco_addr), "3141234567", resourceProvider.getString(R.string.mock_shelter_eco_desc), "https://images.unsplash.com/photo-1591768793355-74d7c836038c", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("9", resourceProvider.getString(R.string.mock_shelter_caninos_name), resourceProvider.getString(R.string.mock_shelter_caninos_addr), "3113216547", resourceProvider.getString(R.string.mock_shelter_caninos_desc), "https://images.unsplash.com/photo-1551076805-e1869033e561", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("10", resourceProvider.getString(R.string.mock_shelter_huellas_name), resourceProvider.getString(R.string.mock_shelter_huellitas_addr_ibague), "3187654321", resourceProvider.getString(R.string.mock_shelter_huellitas_desc_ibague), "https://images.unsplash.com/photo-1503256207526-0d5d80fa2f47", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),

            // --- BOGOTÁ ---
            Refugio("11", resourceProvider.getString(R.string.mock_shelter_voz_name), resourceProvider.getString(R.string.mock_shelter_voz_addr), "3161234567", resourceProvider.getString(R.string.mock_shelter_voz_desc), "https://images.unsplash.com/photo-1544191714-8011c64883b2", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("12", resourceProvider.getString(R.string.mock_shelter_dover_name), resourceProvider.getString(R.string.mock_shelter_dover_addr), "3171234567", resourceProvider.getString(R.string.mock_shelter_dover_desc), "https://images.unsplash.com/photo-1599443015574-be5fe8a05783", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("13", resourceProvider.getString(R.string.mock_shelter_milagrinos_name), resourceProvider.getString(R.string.mock_shelter_milagrinos_addr), "3191234567", resourceProvider.getString(R.string.mock_shelter_milagrinos_desc), "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("14", resourceProvider.getString(R.string.mock_shelter_agrocampo_name), resourceProvider.getString(R.string.mock_shelter_agrocampo_addr), "3181234567", resourceProvider.getString(R.string.mock_shelter_agrocampo_desc), "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("15", resourceProvider.getString(R.string.mock_shelter_razas_name), resourceProvider.getString(R.string.mock_shelter_razas_addr), "3221234567", resourceProvider.getString(R.string.mock_shelter_razas_desc), "https://images.unsplash.com/photo-1561037404-61cd46aa615b", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),

            // --- MEDELLÍN ---
            Refugio("16", resourceProvider.getString(R.string.mock_shelter_orca_name), resourceProvider.getString(R.string.mock_shelter_orca_addr), "3241234567", resourceProvider.getString(R.string.mock_shelter_orca_desc), "https://images.unsplash.com/photo-1583337130417-3346a1be7dee", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("17", resourceProvider.getString(R.string.mock_shelter_jeronimo_name), resourceProvider.getString(R.string.mock_shelter_jeronimo_addr), "3251234567", resourceProvider.getString(R.string.mock_shelter_jeronimo_desc), "https://images.unsplash.com/photo-1513360371669-4ada3ddbb9ad", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("18", resourceProvider.getString(R.string.mock_shelter_perla_name), resourceProvider.getString(R.string.mock_shelter_perla_addr), "3261234567", resourceProvider.getString(R.string.mock_shelter_perla_desc), "https://images.unsplash.com/photo-1548681528-6a5c45b66b42", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("19", resourceProvider.getString(R.string.mock_shelter_establo_name), resourceProvider.getString(R.string.mock_shelter_establo_addr), "3271234567", resourceProvider.getString(R.string.mock_shelter_establo_desc), "https://images.unsplash.com/photo-1597673030062-0a0f1a801a21", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("20", resourceProvider.getString(R.string.mock_shelter_animalnat_name), resourceProvider.getString(R.string.mock_shelter_animalnat_addr), "3281234567", resourceProvider.getString(R.string.mock_shelter_animalnat_desc), "https://images.unsplash.com/photo-1571873717254-1472ae5f82cc", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),

            // --- CALI ---
            Refugio("21", resourceProvider.getString(R.string.mock_shelter_salvando_name), resourceProvider.getString(R.string.mock_shelter_salvando_addr), "3291234567", resourceProvider.getString(R.string.mock_shelter_salvando_desc), "https://images.unsplash.com/photo-1552053831-71594a27632d", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("22", resourceProvider.getString(R.string.mock_shelter_luis_name), resourceProvider.getString(R.string.mock_shelter_luis_addr), "3311234567", resourceProvider.getString(R.string.mock_shelter_luis_desc), "https://images.unsplash.com/photo-1596202305412-d39fd7416ec7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("23", resourceProvider.getString(R.string.mock_shelter_villa_name), resourceProvider.getString(R.string.mock_shelter_villa_addr), "3321234567", resourceProvider.getString(R.string.mock_shelter_villa_desc), "https://images.unsplash.com/photo-1530281739423-0ed7ec5ccec1", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("24", resourceProvider.getString(R.string.mock_shelter_petcenter_name), resourceProvider.getString(R.string.mock_shelter_petcenter_addr), "3331234567", resourceProvider.getString(R.string.mock_shelter_petcenter_desc), "https://images.unsplash.com/photo-1587559070757-f72a388edbba", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),

            // --- OTRAS CIUDADES ---
            Refugio("25", resourceProvider.getString(R.string.mock_shelter_pereira_name), resourceProvider.getString(R.string.mock_shelter_pereira_addr), "3341234567", resourceProvider.getString(R.string.mock_shelter_pereira_desc), "https://images.unsplash.com/photo-1599141091150-009f5927902e", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("26", resourceProvider.getString(R.string.mock_shelter_nombre_name), resourceProvider.getString(R.string.mock_shelter_nombre_addr), "3351234567", resourceProvider.getString(R.string.mock_shelter_nombre_desc), "https://images.unsplash.com/photo-1560743641-3914f2c45636", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("27", resourceProvider.getString(R.string.mock_shelter_country_name), resourceProvider.getString(R.string.mock_shelter_country_addr), "3361234567", resourceProvider.getString(R.string.mock_shelter_country_desc), "https://images.unsplash.com/photo-1598133894008-61f7fdb8cc3a", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("28", resourceProvider.getString(R.string.mock_shelter_paraiso_name), resourceProvider.getString(R.string.mock_shelter_paraiso_addr), "3371234567", resourceProvider.getString(R.string.mock_shelter_paraiso_desc), "https://images.unsplash.com/photo-1544568100-847a948585b9", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("29", resourceProvider.getString(R.string.mock_shelter_peludos_name), resourceProvider.getString(R.string.mock_shelter_peludos_addr), "3381234567", resourceProvider.getString(R.string.mock_shelter_peludos_desc), "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("30", resourceProvider.getString(R.string.mock_shelter_huellitas_name), resourceProvider.getString(R.string.mock_shelter_huellitas_addr), "3391234567", resourceProvider.getString(R.string.mock_shelter_huellitas_desc), "https://images.unsplash.com/photo-1552728089-57bdde30fc3b", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("31", resourceProvider.getString(R.string.mock_shelter_plus_name), resourceProvider.getString(R.string.mock_shelter_plus_addr), "3401234567", resourceProvider.getString(R.string.mock_shelter_plus_desc), "https://images.unsplash.com/photo-1591769225440-811ad7d62ca2", estado = RefugioEstado.PENDIENTE, tipo = RefugioTipo.VETERINARIA)
        )
    }
}
