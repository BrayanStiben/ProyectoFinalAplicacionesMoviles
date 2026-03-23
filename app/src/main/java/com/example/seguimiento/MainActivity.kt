package com.example.seguimiento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.seguimiento.core.theme.SeguimientoTheme
import com.example.seguimiento.features.ConfirmacionMascotaFeliz.AdoptionConfirmationScreen
import com.example.seguimiento.features.EsperandoPorTi.EstaEsperandoPorTiScreen
import com.example.seguimiento.features.EsperandoPorTi.EstaEsperandoViewModel
import com.example.seguimiento.features.FinalizarRegistro.FinalizarRegistroScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepFourScreen.StepFourScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepOneScreen.StepOneScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepThreeScreen.StepThreeScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepTwoScreen.StepTwoScreen
import com.example.seguimiento.features.HistoriaMascota.PantallaPetAdopta
import com.example.seguimiento.features.IngresarMascota.PantallaRegistroMascota
 import com.example.seguimiento.features.PantallaRecuperarContrasena.PantallaRecuperarContrasena
import com.example.seguimiento.features.QueNesecitoParaAdoptar.QueNesecitoParaAdoptar
import com.example.seguimiento.features.Refugios.RefugiosScreen
import com.example.seguimiento.features.nutricionanimal.PantallaNutricion
import com.example.seguimiento.features.register.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeguimientoTheme {
                // Pantalla que estamos editando
                StepFourScreen()
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SeguimientoTheme {
        Greeting("Android")
    }
}