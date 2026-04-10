package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Logro
import com.example.seguimiento.Dominio.repositorios.LogrosRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogrosRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository
) : LogrosRepository {

    override val todosLosLogros = listOf(
        Logro("sys_1", R.string.logro_sys_1_title, R.string.logro_sys_1_desc, iconResId = R.drawable.img1, categoria = "SISTEMA"),
        Logro("res_1", R.string.logro_res_1_title, R.string.logro_res_1_desc, iconResId = R.drawable.img2, categoria = "RESCATE"),
        Logro("res_2", R.string.logro_res_2_title, R.string.logro_res_2_desc, iconResId = R.drawable.img3, categoria = "RESCATE"),
        Logro("res_3", R.string.logro_res_3_title, R.string.logro_res_3_desc, iconResId = R.drawable.img4, categoria = "RESCATE"),
        Logro("com_1", R.string.logro_com_1_title, R.string.logro_com_1_desc, iconResId = R.drawable.img5, categoria = "COMUNIDAD"),
        Logro("com_2", R.string.logro_com_2_title, R.string.logro_com_2_desc, iconResId = R.drawable.img6, categoria = "COMUNIDAD"),
        Logro("com_3", R.string.logro_com_3_title, R.string.logro_com_3_desc, iconResId = R.drawable.img7, categoria = "COMUNIDAD"),
        Logro("per_1", R.string.logro_per_1_title, R.string.logro_per_1_desc, iconResId = R.drawable.img8, categoria = "PERFIL"),
        Logro("ado_1", R.string.logro_ado_1_title, R.string.logro_ado_1_desc, iconResId = R.drawable.img9, categoria = "ADOPCION"),
        Logro("ado_2", R.string.logro_ado_2_title, R.string.logro_ado_2_desc, iconResId = R.drawable.img10, categoria = "ADOPCION"),
        Logro("sys_2", R.string.logro_sys_2_title, R.string.logro_sys_2_desc, iconResId = R.drawable.img11, categoria = "SISTEMA"),
        Logro("res_4", R.string.logro_res_4_title, R.string.logro_res_4_desc, iconResId = R.drawable.img12, categoria = "RESCATE"),
        Logro("com_4", R.string.logro_com_4_title, R.string.logro_com_4_desc, iconResId = R.drawable.img13, categoria = "COMUNIDAD"),
        Logro("per_2", R.string.logro_per_2_title, R.string.logro_per_2_desc, iconResId = R.drawable.img14, categoria = "PERFIL"),
        Logro("ado_3", R.string.logro_ado_3_title, R.string.logro_ado_3_desc, iconResId = R.drawable.img15, categoria = "ADOPCION"),
        Logro("sys_3", R.string.logro_sys_3_title, R.string.logro_sys_3_desc, iconResId = R.drawable.img16, categoria = "SISTEMA"),
        Logro("res_5", R.string.logro_res_5_title, R.string.logro_res_5_desc, iconResId = R.drawable.img17, categoria = "RESCATE"),
        Logro("com_5", R.string.logro_com_5_title, R.string.logro_com_5_desc, iconResId = R.drawable.img18, categoria = "COMUNIDAD"),
        Logro("per_3", R.string.logro_per_3_title, R.string.logro_per_3_desc, iconResId = R.drawable.img19, categoria = "PERFIL"),
        Logro("ado_4", R.string.logro_ado_4_title, R.string.logro_ado_4_desc, iconResId = R.drawable.img20, categoria = "ADOPCION")
    )

    // Sincronizamos logros con los puntos de los usuarios quemados (100 pts por logro + 50 base)
    private val _logrosUsuarios = MutableStateFlow<Map<String, List<String>>>(mapOf(
        "user_colaborador" to todosLosLogros.take(4).map { it.id }, // 4 logros = 400 pts + 50 = 450
        "user_protector" to todosLosLogros.take(8).map { it.id },   // 8 logros = 800 pts + 50 = 850
        "user_heroe" to todosLosLogros.take(12).map { it.id },     // 12 logros = 1200 pts
        "user_leyenda" to todosLosLogros.take(20).map { it.id },    // 20 logros = 2000 pts
        "admin_id" to todosLosLogros.map { it.id }                 // Todo desbloqueado
    ))

    @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
    override fun getLogrosUsuario(userId: String): StateFlow<List<String>> {
        return _logrosUsuarios.map { it[userId] ?: emptyList() }
            .stateIn(
                scope = GlobalScope, 
                started = SharingStarted.WhileSubscribed(), 
                initialValue = emptyList()
            )
    }

    override suspend fun ganarLogro(userId: String, logroId: String) {
        _logrosUsuarios.update { currentMap ->
            val userLogros = currentMap[userId] ?: emptyList()
            if (!userLogros.contains(logroId)) {
                userRepository.addPoints(userId, 100)
                userRepository.addBadge(userId, logroId)
                currentMap + (userId to (userLogros + logroId))
            } else {
                currentMap
            }
        }
    }
}
