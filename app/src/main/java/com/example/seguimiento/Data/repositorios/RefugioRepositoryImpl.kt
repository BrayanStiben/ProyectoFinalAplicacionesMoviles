package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Refugio
import com.example.seguimiento.Dominio.modelos.RefugioEstado
import com.example.seguimiento.Dominio.modelos.RefugioTipo
import com.example.seguimiento.Dominio.repositorios.RefugioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefugioRepositoryImpl @Inject constructor() : RefugioRepository {

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
            Refugio("1", "ADA Quindío", "Carrera 19 #30-40, Armenia", "3104567890", "Asociación Defensora de Animales del Quindío. Más de 20 años rescatando.", "https://images.unsplash.com/photo-1548191265-cc70d3d45ba1", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("2", "Clínica Veterinaria del Café", "Calle 15 #14-25, Armenia", "3001112233", "Atención especializada 24/7 en el corazón de Armenia.", "https://images.unsplash.com/photo-1584132967334-10e028bd69f7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("3", "Refugio La Tebaida", "Vía Panamericana km 2, La Tebaida", "3209876543", "Hogar de paso para caninos y felinos rescatados en el sur del Quindío.", "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("4", "Veterinaria Mascotas Armenia", "Avenida Bolívar #10-15, Armenia", "3155556677", "Consulta general, cirugía y peluquería profesional.", "https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("5", "Refugio San Roque", "Vereda La Julia, Circasia", "3004445566", "Santuario dedicado al cuidado de mascotas ancianas abandonadas.", "https://images.unsplash.com/photo-1511497584788-8767fe770c52", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("6", "Hospital Veterinario Uniquindío", "Calle 12 Norte, Armenia", "3121234567", "Hospital docente de la Universidad del Quindío con servicios integrales.", "https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("7", "Veterinaria El Arca", "Carrera 24 #18-30, Calarcá", "3131234567", "Atención médica para mascotas en el municipio de Calarcá.", "https://images.unsplash.com/photo-1576201836106-db1758fd1c97", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("8", "Fundación Eco-Huellas", "Vía al Valle, Salento", "3141234567", "Protección animal y educación ambiental en Salento.", "https://images.unsplash.com/photo-1591768793355-74d7c836038c", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("9", "Clínica Vet Caninos y Felinos", "Carrera 14 #2-50, Armenia", "3113216547", "Medicina interna y laboratorio clínico especializado.", "https://images.unsplash.com/photo-1551076805-e1869033e561", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("10", "Huellas del Quindío", "Vía Montenegro km 1", "3187654321", "Refugio campestre con amplias zonas verdes para recreación.", "https://images.unsplash.com/photo-1503256207526-0d5d80fa2f47", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),

            // --- BOGOTÁ ---
            Refugio("11", "Fundación Voz Animal", "Chía, Cundinamarca", "3161234567", "Rescate masivo y esterilización en toda la sabana de Bogotá.", "https://images.unsplash.com/photo-1544191714-8011c64883b2", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("12", "Clínica Veterinaria Dover", "Calle 127 #15-36, Bogotá", "3171234567", "Pioneros en medicina veterinaria de alta complejidad en Colombia.", "https://images.unsplash.com/photo-1599443015574-be5fe8a05783", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("13", "Refugio Milagrinos", "Usaquén, Bogotá", "3191234567", "Especializados en el rescate y rehabilitación de gatos abandonados.", "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("14", "Agrocampo Vet Center", "Avenida Caracas #73-10, Bogotá", "3181234567", "El centro veterinario más grande de la ciudad con farmacia 24h.", "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("15", "Fundación Razas Únicas", "Suba, Bogotá", "3221234567", "Promoviendo la adopción de perros mestizos ('razas únicas').", "https://images.unsplash.com/photo-1561037404-61cd46aa615b", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),

            // --- MEDELLÍN ---
            Refugio("16", "Fundación Orca", "El Poblado, Medellín", "3241234567", "Defensores incansables de la vida animal en Antioquia.", "https://images.unsplash.com/photo-1583337130417-3346a1be7dee", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("17", "Centro Vet San Jerónimo", "Laureles, Medellín", "3251234567", "Expertos en comportamiento animal y medicina felina.", "https://images.unsplash.com/photo-1513360371669-4ada3ddbb9ad", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("18", "Refugio La Perla", "Corregimiento Altavista, Medellín", "3261234567", "Centro de bienestar animal oficial de la Alcaldía de Medellín.", "https://images.unsplash.com/photo-1548681528-6a5c45b66b42", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("19", "Clínica Vet El Establo", "Envigado, Antioquia", "3271234567", "Servicios médicos integrales para mascotas y animales de granja.", "https://images.unsplash.com/photo-1597673030062-0a0f1a801a21", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("20", "Fundación Animalnat", "Bello, Antioquia", "3281234567", "Rescate y educación sobre fauna urbana y doméstica.", "https://images.unsplash.com/photo-1571873717254-1472ae5f82cc", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),

            // --- CALI ---
            Refugio("21", "Salvando Huellas Cali", "Pance, Cali", "3291234567", "Asociación protectora dedicada a rescatar animales del maltrato.", "https://images.unsplash.com/photo-1552053831-71594a27632d", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("22", "Clínica Vet San Luis", "Avenida 6N, Cali", "3311234567", "Medicina de vanguardia para mascotas en el Valle del Cauca.", "https://images.unsplash.com/photo-1596202305412-d39fd7416ec7", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("23", "Refugio Villa Canina", "Vía Jamundí, Cali", "3321234567", "Santuario campestre con programas de rehabilitación física.", "https://images.unsplash.com/photo-1530281739423-0ed7ec5ccec1", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("24", "Veterinaria Pet Center Cali", "Barrio Granada, Cali", "3331234567", "Clínica, estética y hotel canino de cinco estrellas.", "https://images.unsplash.com/photo-1587559070757-f72a388edbba", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),

            // --- OTRAS CIUDADES ---
            Refugio("25", "Veterinaria Pereira Express", "Avenida Circunvalar, Pereira", "3341234567", "Atención médica rápida y confiable en la capital de Risaralda.", "https://images.unsplash.com/photo-1599141091150-009f5927902e", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("26", "Fundación Perros sin Nombre", "Bucaramanga, Santander", "3351234567", "Rescate en las calles de la ciudad bonita y Santander.", "https://images.unsplash.com/photo-1560743641-3914f2c45636", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("27", "Clínica Vet del Country", "Barrio El Prado, Barranquilla", "3361234567", "Líderes en salud animal en la Costa Caribe colombiana.", "https://images.unsplash.com/photo-1598133894008-61f7fdb8cc3a", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("28", "Refugio Paraíso Animal", "Manizales, Caldas", "3371234567", "Un paraíso entre las montañas para animales rescatados.", "https://images.unsplash.com/photo-1544568100-847a948585b9", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("29", "Clínica Vet Peludos", "Barrio Manga, Cartagena", "3381234567", "Atención integral para las mascotas de la ciudad amurallada.", "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.VETERINARIA),
            Refugio("30", "Fundación Huellitas Felices", "Ibagué, Tolima", "3391234567", "Protección y adopción responsable en el centro de Colombia.", "https://images.unsplash.com/photo-1552728089-57bdde30fc3b", estado = RefugioEstado.APROBADO, tipo = RefugioTipo.REFUGIO),
            Refugio("31", "Vet Plus Villavicencio", "Avenida 40, Villavicencio", "3401234567", "Servicios especializados en el llano colombiano.", "https://images.unsplash.com/photo-1591769225440-811ad7d62ca2", estado = RefugioEstado.PENDIENTE, tipo = RefugioTipo.VETERINARIA)
        )
    }
}
