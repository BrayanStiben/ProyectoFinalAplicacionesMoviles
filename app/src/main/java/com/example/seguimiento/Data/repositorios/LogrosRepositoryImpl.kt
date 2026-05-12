package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Logro
import com.example.seguimiento.Dominio.repositorios.LogrosRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogrosRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository
) : LogrosRepository {

    private val collection = firestore.collection("logros_usuarios")

    override val todosLosLogros = listOf(
        Logro("sys_1", R.string.logro_sys_1_title, R.string.logro_sys_1_desc, iconResId = R.drawable.img1, categoria = "SISTEMA"),
        Logro("res_1", R.string.logro_res_1_title, R.string.logro_res_1_desc, iconResId = R.drawable.img2, categoria = "RESCATE"),
        Logro("res_2", R.string.logro_res_2_title, R.string.logro_res_2_desc, iconResId = R.drawable.img3, categoria = "RESCATE"),
        Logro("com_1", R.string.logro_com_1_title, R.string.logro_com_1_desc, iconResId = R.drawable.img5, categoria = "COMUNIDAD"),
        Logro("ado_1", R.string.logro_ado_1_title, R.string.logro_ado_1_desc, iconResId = R.drawable.img9, categoria = "ADOPCION")
    )

    private val _logrosUsuariosMap = MutableStateFlow<Map<String, List<String>>>(emptyMap())

    init {
        // Escucha en tiempo real todos los logros de usuarios
        collection.addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                val newMap = mutableMapOf<String, List<String>>()
                for (doc in it.documents) {
                    val achievements = doc.get("achievements") as? List<String> ?: emptyList()
                    newMap[doc.id] = achievements
                }
                _logrosUsuariosMap.value = newMap
                
                if (newMap.isEmpty()) seedInitialLogros()
            }
        }
    }

    private fun seedInitialLogros() {
        // Otorgar un logro inicial al admin para crear la colección
        CoroutineScope(Dispatchers.IO).launch {
            ganarLogro("admin_id", "sys_1")
        }
    }

    override fun getLogrosUsuario(userId: String): StateFlow<List<String>> {
        return _logrosUsuariosMap.map { it[userId] ?: emptyList() }
            .stateIn(
                scope = CoroutineScope(Dispatchers.Main), 
                started = SharingStarted.WhileSubscribed(5000), 
                initialValue = emptyList()
            )
    }

    override suspend fun ganarLogro(userId: String, logroId: String) {
        val currentLogros = _logrosUsuariosMap.value[userId] ?: emptyList()
        if (!currentLogros.contains(logroId)) {
            val newList = currentLogros + logroId
            // Guardar en Firestore
            collection.document(userId).set(mapOf("achievements" to newList))
            // Premiar al usuario
            userRepository.addPoints(userId, 100)
            userRepository.addBadge(userId, logroId)
        }
    }
}
