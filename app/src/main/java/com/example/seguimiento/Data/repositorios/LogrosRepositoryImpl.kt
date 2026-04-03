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
        Logro("sys_1", "¡Bienvenido!", "Iniciaste sesión por primera vez.", iconResId = R.drawable.img1, categoria = "SISTEMA"),
        Logro("res_1", "Primer Rescate", "Publicaste tu primera mascota.", iconResId = R.drawable.img2, categoria = "RESCATE"),
        Logro("res_2", "Rescatista Activo", "Tienes 2 mascotas publicadas.", iconResId = R.drawable.img3, categoria = "RESCATE"),
        Logro("res_3", "Héroe Local", "Tienes 3 mascotas publicadas.", iconResId = R.drawable.img4, categoria = "RESCATE"),
        Logro("com_1", "Primer Like", "Diste tu primer voto.", iconResId = R.drawable.img5, categoria = "COMUNIDAD"),
        Logro("com_2", "Votante Frecuente", "Diste 5 votos.", iconResId = R.drawable.img6, categoria = "COMUNIDAD"),
        Logro("com_3", "Corazón de Oro", "Diste 10 votos.", iconResId = R.drawable.img7, categoria = "COMUNIDAD"),
        Logro("per_1", "Perfil Completo", "Completaste tus datos.", iconResId = R.drawable.img8, categoria = "PERFIL"),
        Logro("ado_1", "Interés en Adopción", "Completaste un formulario.", iconResId = R.drawable.img9, categoria = "ADOPCION"),
        Logro("ado_2", "Adoptante", "Tienes una mascota adoptada.", iconResId = R.drawable.img10, categoria = "ADOPCION"),
        Logro("sys_2", "Usuario Verificado", "Tu cuenta ha sido validada.", iconResId = R.drawable.img11, categoria = "SISTEMA"),
        Logro("res_4", "Protector", "Tienes 4 mascotas publicadas.", iconResId = R.drawable.img12, categoria = "RESCATE"),
        Logro("com_4", "Sin familiar", "Registro de adopción cancelado por el administrador.", iconResId = R.drawable.img13, categoria = "COMUNIDAD"),
        Logro("per_2", "Malos comentarios", "Publicación rechazada por el administrador.", iconResId = R.drawable.img14, categoria = "PERFIL"),
        Logro("ado_3", "Familia Multiespecie", "Adoptaste más de una mascota.", iconResId = R.drawable.img15, categoria = "ADOPCION"),
        Logro("sys_3", "Veterano", "Llevas un mes en la App.", iconResId = R.drawable.img16, categoria = "SISTEMA"),
        Logro("res_5", "Salvador", "Tienes 5 mascotas publicadas.", iconResId = R.drawable.img17, categoria = "RESCATE"),
        Logro("com_5", "Líder Comunitario", "Tus comentarios son muy valorados.", iconResId = R.drawable.img18, categoria = "COMUNIDAD"),
        Logro("per_3", "Estrella", "Lograste el rango máximo de perfil.", iconResId = R.drawable.img19, categoria = "PERFIL"),
        Logro("ado_4", "Adoptante de Leyenda", "Tienes 3 o más adopciones exitosas.", iconResId = R.drawable.img20, categoria = "ADOPCION")
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
