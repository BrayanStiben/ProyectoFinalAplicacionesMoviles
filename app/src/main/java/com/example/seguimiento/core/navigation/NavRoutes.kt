package com.example.seguimiento.core.navigation

sealed class NavRoutes(val route: String) {
    object Loading : NavRoutes("loading")
    object Login : NavRoutes("login")
    object Home : NavRoutes("home")
    object Register : NavRoutes("register")
    object OlvidoContrasena : NavRoutes("olvido_contrasena")
    object EnvioCodigo : NavRoutes("envio_codigo")
    object RecuperarContrasena : NavRoutes("recuperar_contrasena")
    object FinalizarRegistro : NavRoutes("finalizar_registro")
    
    // Exploración y Educación
    object Profile : NavRoutes("profile")
    object MascotaDestacada : NavRoutes("mascota_destacada/{nombre}/{edad}/{ubicacion}/{url}") {
        fun createRoute(nombre: String, edad: String, ubicacion: String, url: String): String {
            val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
            return "mascota_destacada/$nombre/$edad/$ubicacion/$encodedUrl"
        }
    }
    object Nutricion : NavRoutes("nutricion")
    object RequisitosAdopcion : NavRoutes("requisitos_adopcion")
    object Refugios : NavRoutes("refugios")
    object FiltrosAvanzados : NavRoutes("filtros_avanzados")
    object HistoriasExito : NavRoutes("historias_exito")

    // Formulario de Adopción
    object StepOne : NavRoutes("step_one")
    object StepTwo : NavRoutes("step_two")
    object StepThree : NavRoutes("step_three")
    object StepFour : NavRoutes("step_four")
    object AdoptionConfirmation : NavRoutes("adoption_confirmation")

    // Gestión
    object RegistroMascota : NavRoutes("registro_mascota")

    // Admin Features
    object Estadisticas : NavRoutes("estadisticas")
    object ListaSolicitudes : NavRoutes("lista_solicitudes")
    object EncontrarMascotas : NavRoutes("encontrar_mascotas")
}
