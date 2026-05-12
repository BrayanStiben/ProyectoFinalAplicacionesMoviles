package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.*
import com.example.seguimiento.Dominio.repositorios.SaludRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SaludRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notificacionRepository: NotificacionRepository
) : SaludRepository {

    private val collection = firestore.collection("salud_mascotas")

    init {
        // Forzar creación de la categoría si está vacía
        collection.limit(1).get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                val seedCarnet = CarnetSalud(
                    mascotaId = "seed_pet",
                    petId = "000000",
                    pin = "1234",
                    vacunas = listOf(Vacuna(id = "seed", nombre = "Vacuna Inicial", fecha = "2024-01-01"))
                )
                collection.document("seed_pet").set(seedCarnet)
            }
        }
    }

    override fun getCarnetPorMascota(mascotaId: String): Flow<CarnetSalud> = callbackFlow {
        val subscription = collection.document(mascotaId)
            .addSnapshotListener { snapshot, _ ->
                val carnet = snapshot?.toObject(CarnetSalud::class.java) ?: CarnetSalud(
                    mascotaId = mascotaId,
                    petId = Random.nextInt(100000, 999999).toString(),
                    pin = Random.nextInt(1000, 9999).toString()
                )
                trySend(carnet)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun agregarVacuna(mascotaId: String, vacuna: Vacuna) {
        val doc = collection.document(mascotaId).get().await()
        val carnet = doc.toObject(CarnetSalud::class.java) ?: CarnetSalud(mascotaId = mascotaId)
        val nuevoCarnet = carnet.copy(vacunas = carnet.vacunas + vacuna)
        collection.document(mascotaId).set(nuevoCarnet)

        notificacionRepository.addNotificacion(
            tituloResId = com.example.seguimiento.R.string.health_notif_vaccine_title,
            mensajeResId = com.example.seguimiento.R.string.health_notif_vaccine_msg,
            mensajeArgs = listOf(vacuna.nombre, vacuna.proximaDosis),
            tipo = "SUCCESS"
        )
    }

    override suspend fun agregarDesparasitacion(mascotaId: String, despar: Desparasitacion) {
        val doc = collection.document(mascotaId).get().await()
        val carnet = doc.toObject(CarnetSalud::class.java) ?: CarnetSalud(mascotaId = mascotaId)
        collection.document(mascotaId).set(carnet.copy(desparasitaciones = carnet.desparasitaciones + despar))
    }

    override suspend fun agendarCita(mascotaId: String, cita: CitaVeterinaria) {
        val doc = collection.document(mascotaId).get().await()
        val carnet = doc.toObject(CarnetSalud::class.java) ?: CarnetSalud(mascotaId = mascotaId)
        collection.document(mascotaId).set(carnet.copy(citas = carnet.citas + cita))
        
        notificacionRepository.addNotificacion(
            tituloResId = com.example.seguimiento.R.string.health_notif_appointment_title,
            mensajeResId = com.example.seguimiento.R.string.health_notif_appointment_msg,
            mensajeArgs = listOf(cita.fecha, cita.hora, cita.clinica),
            tipo = "INFO"
        )
    }
}
