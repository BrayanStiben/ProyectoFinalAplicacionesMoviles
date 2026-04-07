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
    object MisAdopciones : NavRoutes("mis_adopciones")
    object MisPublicaciones : NavRoutes("mis_publicaciones")
    object MascotaDestacada : NavRoutes("mascota_destacada/{id}/{nombre}/{edad}/{ubicacion}/{url}") {
        fun createRoute(id: String, nombre: String, edad: String, ubicacion: String, url: String): String {
            val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
            return "mascota_destacada/$id/$nombre/$edad/$ubicacion/$encodedUrl"
        }
    }
    object Nutricion : NavRoutes("nutricion")
    object RequisitosAdopcion : NavRoutes("requisitos_adopcion")
    object Refugios : NavRoutes("refugios")
    object RegistroRefugio : NavRoutes("registro_refugio")
    object Tienda : NavRoutes("tienda")
    object FiltrosAvanzados : NavRoutes("filtros_avanzados")
    object HistoriasExito : NavRoutes("historias_exito")
    object Notificaciones : NavRoutes("notificaciones")
    object MapaFeed : NavRoutes("mapa_feed")
    object Favoritos : NavRoutes("favoritos")
    object Logros : NavRoutes("logros")
    object SaludMascota : NavRoutes("salud_mascota/{id}") {
        fun createRoute(id: String) = "salud_mascota/$id"
    }

    // Formulario de Adopción
    object StepOne : NavRoutes("step_one")
    object StepTwo : NavRoutes("step_two")
    object StepThree : NavRoutes("step_three")
    object StepFour : NavRoutes("step_four")
    object AdoptionConfirmation : NavRoutes("adoption_confirmation/{requestId}") {
        fun createRoute(requestId: String) = "adoption_confirmation/$requestId"
    }

    // Gestión
    object RegistroMascota : NavRoutes("register_mascota")
    object EditarMascota : NavRoutes("editar_mascota/{id}") {
        fun createRoute(id: String) = "editar_mascota/$id"
    }

    // Admin Features
    object Estadisticas : NavRoutes("estadisticas")
    object ReportesDetallados : NavRoutes("admin_reportes")
    object GestionTienda : NavRoutes("admin_tienda")
    object HistorialVentas : NavRoutes("admin_ventas")
    object ListaSolicitudes : NavRoutes("lista_solicitudes")
    object EncontrarMascotas : NavRoutes("encontrar_mascotas")
    object GestionUsuarios : NavRoutes("gestion_usuarios")
    object GestionHistorias : NavRoutes("gestion_historias")
    object GestionComentarios : NavRoutes("gestion_comentarios")
    object GestionAdopciones : NavRoutes("gestion_adopciones")
    object GestionRefugios : NavRoutes("gestion_refugios")
    object CertificadoAdopcion : NavRoutes("certificado_adopcion/{requestId}") {
        fun createRoute(requestId: String) = "certificado_adopcion/$requestId"
    }
    object CertificadoRechazo : NavRoutes("certificado_rechazo/{requestId}") {
        fun createRoute(requestId: String) = "certificado_rechazo/$requestId"
    }
}
