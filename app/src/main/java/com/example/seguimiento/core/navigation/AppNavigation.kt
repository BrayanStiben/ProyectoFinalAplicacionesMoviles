package com.example.seguimiento.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.seguimiento.features.Loading.LoadingScreen
import com.example.seguimiento.features.CertificadoAdopcion.CertificadoAdopcionScreen
import com.example.seguimiento.features.CertificadoRechazo.CertificadoRechazoScreen
import com.example.seguimiento.features.ConfirmacionMascotaFeliz.AdoptionConfirmationScreen
import com.example.seguimiento.features.EncontrarMascotas.PantallaAdopcion
import com.example.seguimiento.features.EnvioDeCodigo.EnvioDeCodigoScreen
import com.example.seguimiento.features.Estadisticas.EstadisticasScreen
import com.example.seguimiento.features.Estadisticas.GestionHistoriasScreen
import com.example.seguimiento.features.Estadisticas.GestionUsuariosScreen
import com.example.seguimiento.features.Favoritos.FavoritosScreen
import com.example.seguimiento.features.Filtros.PantallaFiltrosAvanzado
import com.example.seguimiento.features.FinalizarRegistro.FinalizarRegistroScreen
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel
import com.example.seguimiento.features.FormularioDeAdopction.StepOneScreen.StepOneScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepTwoScreen.StepTwoScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepThreeScreen.StepThreeScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepFourScreen.StepFourScreen
import com.example.seguimiento.features.GestionAdopciones.GestionAdopcionesScreen
import com.example.seguimiento.features.GestionComentarios.GestionComentariosScreen
import com.example.seguimiento.features.HistoriaMascota.PantallaPetAdopta
import com.example.seguimiento.features.IngresarMascota.PantallaRegistroMascota
import com.example.seguimiento.features.ListaDeSolicitudes.ListaSolicitudesScreen
import com.example.seguimiento.features.Mercado.TiendaScreen
import com.example.seguimiento.features.MercadoAdmin.TiendaAdminScreen
import com.example.seguimiento.features.MiPerfil.ProfileScreen
import com.example.seguimiento.features.Notificaciones.NotificacionesScreen
import com.example.seguimiento.features.OlvidoContrasena.OlvidoContrasenaScreen
import com.example.seguimiento.features.PantallaRecuperarContrasena.PantallaRecuperarContrasena
import com.example.seguimiento.features.QueNesecitoParaAdoptar.QueNesecitoParaAdoptar
import com.example.seguimiento.features.Refugios.GestionRefugiosScreen
import com.example.seguimiento.features.Refugios.RegistroRefugioScreen
import com.example.seguimiento.features.ReportesAdmin.ReportesScreen
import com.example.seguimiento.features.home.HomeScreen
import com.example.seguimiento.features.home.MapaFeedScreen
import com.example.seguimiento.features.historialcompras.HistorialComprasScreen
import com.example.seguimiento.features.login.LoginScreen
import com.example.seguimiento.features.nutricionanimal.PantallaNutricion
import com.example.seguimiento.features.register.RegisterScreen
import com.example.seguimiento.features.EsperandoPorTi.EstaEsperandoPorTiScreen
import com.example.seguimiento.features.Logros.LogrosScreen
import com.example.seguimiento.features.MisAdopciones.MisAdopcionesScreen
import com.example.seguimiento.features.MisPublicaciones.MisPublicacionesScreen
import com.example.seguimiento.features.Refugios.RefugiosScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val adoptionViewModel: AdoptionViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = NavRoutes.Loading.route) {
        composable(NavRoutes.Loading.route) {
            LoadingScreen(onFinished = { isLoggedIn, isAdmin ->
                val destination = if (isLoggedIn) {
                    if (isAdmin) NavRoutes.Estadisticas.route else NavRoutes.Home.route
                } else {
                    NavRoutes.Login.route
                }
                navController.navigate(destination) {
                    popUpTo(NavRoutes.Loading.route) { inclusive = true }
                }
            })
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    val dest = if (isAdmin) NavRoutes.Estadisticas.route else NavRoutes.Home.route
                    navController.navigate(dest) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(NavRoutes.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(NavRoutes.OlvidoContrasena.route) }
            )
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onNavigateToFinalize = { navController.navigate(NavRoutes.FinalizarRegistro.route) },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.FinalizarRegistro.route) {
            FinalizarRegistroScreen(
                onRegistrationFinished = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Home.route) {
            HomeScreen(
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToMascotaDestacada = { id, nombre, edad, ubicacion, url ->
                    navController.navigate(NavRoutes.MascotaDestacada.createRoute(id, nombre, edad, ubicacion, url))
                },
                onNavigateToNutricion = { navController.navigate(NavRoutes.Nutricion.route) },
                onNavigateToRequisitos = { navController.navigate(NavRoutes.RequisitosAdopcion.route) },
                onNavigateToRefugios = { navController.navigate(NavRoutes.Refugios.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToHistorias = { navController.navigate(NavRoutes.HistoriasExito.route) },
                onNavigateToNotificaciones = { navController.navigate(NavRoutes.Notificaciones.route) },
                onNavigateToMapa = { navController.navigate(NavRoutes.MapaFeed.route) },
                onNavigateToRegistroMascota = { navController.navigate(NavRoutes.RegistroMascota.route) },
                onNavigateToLogros = { navController.navigate(NavRoutes.Logros.route) },
                onNavigateToTienda = { navController.navigate(NavRoutes.Tienda.route) }
            )
        }

        composable(NavRoutes.Tienda.route) {
            TiendaScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.Logros.route) {
            LogrosScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.MapaFeed.route) {
            MapaFeedScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id, nombre, edad, ubicacion, url ->
                    navController.navigate(NavRoutes.MascotaDestacada.createRoute(id, nombre, edad, ubicacion, url))
                }
            )
        }

        composable(NavRoutes.Notificaciones.route) {
            NotificacionesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.Profile.route) {
            ProfileScreen(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { /* Ya estamos aquí */ },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToMisAdopciones = { navController.navigate(NavRoutes.MisAdopciones.route) },
                onNavigateToMisPublicaciones = { navController.navigate(NavRoutes.MisPublicaciones.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.MisAdopciones.route) {
            MisAdopcionesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToDetail = { id, nombre, edad, ubicacion, url ->
                    navController.navigate(NavRoutes.MascotaDestacada.createRoute(id, nombre, edad, ubicacion, url))
                }
            )
        }

        composable(NavRoutes.MisPublicaciones.route) {
            MisPublicacionesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.Favoritos.route) {
            FavoritosScreen(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToDetail = { id, nombre, edad, ubicacion, url ->
                    navController.navigate(NavRoutes.MascotaDestacada.createRoute(id, nombre, edad, ubicacion, url))
                }
            )
        }

        composable(
            route = NavRoutes.MascotaDestacada.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("edad") { type = NavType.StringType },
                navArgument("ubicacion") { type = NavType.StringType },
                navArgument("url") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val edad = backStackEntry.arguments?.getString("edad") ?: ""
            val ubicacion = backStackEntry.arguments?.getString("ubicacion") ?: ""
            val url = backStackEntry.arguments?.getString("url") ?: ""

            EstaEsperandoPorTiScreen(
                id = id,
                nombre = nombre,
                edad = edad,
                ubicacion = ubicacion,
                url = url,
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToRequisitos = { navController.navigate(NavRoutes.RequisitosAdopcion.route) },
                onNavigateToCertificado = { reqId -> navController.navigate(NavRoutes.CertificadoAdopcion.createRoute(reqId)) },
                onNavigateToRechazo = { reqId -> navController.navigate(NavRoutes.CertificadoRechazo.createRoute(reqId)) },
                adoptionViewModel = adoptionViewModel
            )
        }

        composable(NavRoutes.Refugios.route) {
            RefugiosScreen(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToRegistroRefugio = { navController.navigate(NavRoutes.RegistroRefugio.route) }
            )
        }

        composable(NavRoutes.RegistroRefugio.route) {
            RegistroRefugioScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.RegistroMascota.route) {
            PantallaRegistroMascota(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToEstadisticas = { navController.navigate(NavRoutes.Estadisticas.route) },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavRoutes.EditarMascota.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            PantallaRegistroMascota(
                mascotaId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToEstadisticas = { navController.navigate(NavRoutes.Estadisticas.route) },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Estadisticas.route) {
            EstadisticasScreen(
                onNavigateToEstadisticas = { /* Ya estamos aqui */ },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onNavigateToGestionUsuarios = { navController.navigate(NavRoutes.GestionUsuarios.route) },
                onNavigateToGestionHistorias = { navController.navigate(NavRoutes.GestionHistorias.route) },
                onNavigateToGestionAdopciones = { navController.navigate(NavRoutes.GestionAdopciones.route) },
                onNavigateToGestionComentarios = { navController.navigate(NavRoutes.GestionComentarios.route) },
                onNavigateToGestionRefugios = { navController.navigate(NavRoutes.GestionRefugios.route) },
                onNavigateToReportesDetallados = { navController.navigate(NavRoutes.ReportesDetallados.route) },
                onNavigateToGestionTienda = { navController.navigate(NavRoutes.GestionTienda.route) },
                onNavigateToHistorialVentas = { navController.navigate(NavRoutes.HistorialVentas.route) },
                onNavigateToRegistroMascota = { navController.navigate(NavRoutes.RegistroMascota.route) },
                onNavigateToHistorias = { navController.navigate(NavRoutes.HistoriasExito.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HistorialVentas.route) {
            HistorialComprasScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEstadisticas = { navController.navigate(NavRoutes.Estadisticas.route) },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.GestionTienda.route) {
            TiendaAdminScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEstadisticas = { navController.navigate(NavRoutes.Estadisticas.route) },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.GestionRefugios.route) {
            GestionRefugiosScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEstadisticas = { navController.navigate(NavRoutes.Estadisticas.route) },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.ReportesDetallados.route) {
            ReportesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEstadisticas = { navController.navigate(NavRoutes.Estadisticas.route) },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.GestionAdopciones.route) {
            GestionAdopcionesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCertificado = { reqId -> navController.navigate(NavRoutes.CertificadoAdopcion.createRoute(reqId)) },
                onNavigateToRechazo = { reqId -> navController.navigate(NavRoutes.CertificadoRechazo.createRoute(reqId)) }
            )
        }

        composable(
            route = NavRoutes.CertificadoAdopcion.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            CertificadoAdopcionScreen(
                requestId = requestId,
                onFinish = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.CertificadoRechazo.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            CertificadoRechazoScreen(
                requestId = requestId,
                onFinish = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ListaSolicitudes.route) {
            ListaSolicitudesScreen(
                onNavigateToEstadisticas = { navController.navigate(NavRoutes.Estadisticas.route) },
                onNavigateToListaSolicitudes = { /* Ya estamos aqui */ },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.EncontrarMascotas.route) {
            PantallaAdopcion(
                onNavigateToEstadisticas = { navController.navigate(NavRoutes.Estadisticas.route) },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { /* Ya estamos aqui */ },
                onNavigateToEditarMascota = { id -> navController.navigate(NavRoutes.EditarMascota.createRoute(id)) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(NavRoutes.GestionUsuarios.route) {
            GestionUsuariosScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(NavRoutes.GestionHistorias.route) {
            GestionHistoriasScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(NavRoutes.GestionComentarios.route) {
            GestionComentariosScreen(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(NavRoutes.Nutricion.route) {
            PantallaNutricion(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }
        composable(NavRoutes.RequisitosAdopcion.route) {
            QueNesecitoParaAdoptar(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToStepOne = { navController.navigate(NavRoutes.StepOne.route) }
            )
        }

        composable(NavRoutes.HistoriasExito.route) {
            PantallaPetAdopta(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEstadisticas = { navController.navigate(NavRoutes.Estadisticas.route) },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.OlvidoContrasena.route) {
            OlvidoContrasenaScreen(
                onNavigateToCode = { email -> navController.navigate(NavRoutes.EnvioCodigo.route + "/$email") }
            )
        }

        composable(
            route = NavRoutes.EnvioCodigo.route + "/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EnvioDeCodigoScreen(
                email = email,
                onCodeVerified = { emailRec -> navController.navigate(NavRoutes.RecuperarContrasena.route + "/$emailRec") },
                onBackToLogin = { navController.navigate(NavRoutes.Login.route) }
            )
        }

        composable(
            route = NavRoutes.RecuperarContrasena.route + "/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            PantallaRecuperarContrasena(
                email = email,
                onPasswordUpdated = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.StepOne.route) { StepOneScreen(vm = adoptionViewModel, onNext = { navController.navigate(NavRoutes.StepTwo.route) }) }
        composable(NavRoutes.StepTwo.route) { StepTwoScreen(vm = adoptionViewModel, onNext = { navController.navigate(NavRoutes.StepThree.route) }) }
        composable(NavRoutes.StepThree.route) { StepThreeScreen(vm = adoptionViewModel, onNext = { navController.navigate(NavRoutes.StepFour.route) }) }
        composable(NavRoutes.StepFour.route) { StepFourScreen(vm = adoptionViewModel, onFinish = { requestId ->
            navController.navigate(NavRoutes.AdoptionConfirmation.createRoute(requestId)) {
                popUpTo(NavRoutes.StepOne.route) { inclusive = true }
            }
        }) }

        composable(
            route = NavRoutes.AdoptionConfirmation.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            AdoptionConfirmationScreen(
                requestId = requestId,
                onNavigateToHome = { 
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                    }
                },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.FiltrosAvanzados.route) {
            PantallaFiltrosAvanzado(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { /* ya aqui */ },
                onNavigateToFavoritos = { navController.navigate(NavRoutes.Favoritos.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }
    }
}
