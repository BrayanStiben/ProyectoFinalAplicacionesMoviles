package com.example.seguimiento.core.utils

import android.util.Log
import com.example.seguimiento.Dominio.modelos.*
import com.example.seguimiento.Dominio.repositorios.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseSeeder @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val mascotaRepository: MascotaRepository,
    private val refugioRepository: RefugioRepository,
    private val tiendaRepository: TiendaRepository,
    private val historiaRepository: HistoriaFelizRepository
) {
    private val TAG = "FirebaseSeeder"

    fun startSeeding() {
        Log.d(TAG, "Iniciando proceso de sembrado forzado...")
        CoroutineScope(Dispatchers.IO).launch {
            
            // 1. Probar conexión básica
            firestore.collection("conexion_test").document("ping").set(mapOf("time" to System.currentTimeMillis()))
                .addOnSuccessListener { Log.d(TAG, "¡Conexión a Firebase EXITOSA!") }
                .addOnFailureListener { Log.e(TAG, "Error de conexión a Firebase: ${it.message}") }

            // 2. Forzar creación de categorías principales
            forceSeed("usuarios") {
                userRepository.save(User(id = "admin_id", name = "Administrador", email = "admin@gmail.com", role = UserRole.ADMIN))
            }
            
            forceSeed("mascotas") {
                mascotaRepository.save(Mascota(id = "pet_seed", nombre = "Mascota de Control", estado = PublicacionEstado.VERIFICADA))
            }

            forceSeed("refugios") {
                refugioRepository.save(Refugio(id = "ref_seed", nombre = "Refugio de Control", estado = RefugioEstado.APROBADO))
            }

            forceSeed("productos") {
                tiendaRepository.save(Producto(id = "prod_seed", nombre = "Producto de Control", precioPuntos = 100, categoria = "General"))
            }

            forceSeed("historias_felices") {
                historiaRepository.save(HistoriaFeliz(id = "story_seed", mascotaNombre = "Amigo", texto = "Activación", estado = HistoriaEstado.APROBADA))
            }
        }
    }

    private suspend fun forceSeed(collectionName: String, action: suspend () -> Unit) {
        try {
            val snapshot = firestore.collection(collectionName).limit(1).get().await()
            if (snapshot.isEmpty) {
                Log.d(TAG, "Categoría $collectionName vacía. Ejecutando escritura forzada...")
                action()
            } else {
                Log.d(TAG, "Categoría $collectionName ya contiene datos.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en forceSeed para $collectionName: ${e.message}")
        }
    }
}
