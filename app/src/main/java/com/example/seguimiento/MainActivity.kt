package com.example.seguimiento

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.seguimiento.core.navigation.AppNavigation
import com.example.seguimiento.core.theme.SeguimientoTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Cargar y aplicar el idioma guardado
        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = sharedPref.getString("language", "es") ?: "es"
        applyLocale(this, lang)
        
        enableEdgeToEdge()
        setContent {
            SeguimientoTheme {
                AppNavigation()
            }
        }
    }

    private fun applyLocale(context: Context, lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
