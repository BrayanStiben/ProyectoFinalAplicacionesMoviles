package com.example.seguimiento

import android.app.Application
import com.example.seguimiento.core.utils.FirebaseSeeder
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import javax.inject.Inject

@HiltAndroidApp
class SeguimientoApp : Application() {

    @Inject
    lateinit var firebaseSeeder: FirebaseSeeder

    override fun onCreate() {
        super.onCreate()
        
        // Configuración necesaria para OpenStreetMap (Osmdroid)
        Configuration.getInstance().userAgentValue = packageName

        // Forzar la creación de colecciones y carga de datos iniciales en Firebase
        firebaseSeeder.startSeeding()
    }
}
