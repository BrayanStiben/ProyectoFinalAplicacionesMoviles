package com.example.seguimiento

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class SeguimientoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Configuración necesaria para OpenStreetMap (Osmdroid)
        Configuration.getInstance().userAgentValue = packageName
    }
}
