package com.example.seguimiento.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.seguimiento.features.home.HomeScreen
import com.example.seguimiento.features.home.MapaFeedScreen
import com.example.seguimiento.features.login.LoginScreen
import com.example.seguimiento.features.register.RegisterScreen
import com.example.seguimiento.features.EnvioDeCodigo.EnvioDeCodigoScreen
import com.example.seguimiento.features.FinalizarRegistro.FinalizarRegistroScreen
import com.example.seguimiento.features.OlvidoContrasena.OlvidoContrasenaScreen
import com.example.seguimiento.features.PantallaRecuperarContrasena.PantallaRecuperarContrasena
import com.example.seguimiento.features.MiPerfil.ProfileScreen
import com.example.seguimiento.features.EsperandoPorTi.EstaEsperandoPorTiScreen
import com.example.seguimiento.features.nutricionanimal.PantallaNutricion
import com.example.seguimiento.features.QueNesecitoParaAdoptar.QueNesecitoParaAdoptar
import com.example.seguimiento.features.Refugios.RefugiosScreen
import com.example.seguimiento.features.Filtros.PantallaFiltrosAvanzados
import com.example.seguimiento.features.HistoriaMascota.PantallaPetAdopta
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel
import com.example.seguimiento.features.FormularioDeAdopction.StepOneScreen.StepOneScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepTwoScreen.StepTwoScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepThreeScreen.StepThreeScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepFourScreen.StepFourScreen
import com.example.seguimiento.features.ConfirmacionMascotaFeliz.AdoptionConfirmationScreen
import com.example.seguimiento.features.IngresarMascota.PantallaRegistroMascota
import com.example.seguimiento.features.Estadisticas.EstadisticasScreen
import com.example.seguimiento.features.Estadisticas.GestionUsuariosScreen
import com.example.seguimiento.features.ListaDeSolicitudes.ListaSolicitudesScreen
import com.example.seguimiento.features.EncontrarMascotas.PantallaAdopcion
import com.example.seguimiento.features.Notificaciones.NotificacionesScreen
import com.example.seguimiento.features.Loading.LoadingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val adoptionViewModel: AdoptionViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Loading.route
    ) {
        composable(NavRoutes.Loading.route) {
            LoadingScreen(
                onFinished = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Loading.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    val destination = if (isAdmin) NavRoutes.Estadisticas.route else NavRoutes.Home.route
                    navController.navigate(destination) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(NavRoutes.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(NavRoutes.OlvidoContrasena.route) }
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
                onNavigateToHistorias = { navController.navigate(NavRoutes.HistoriasExito.route) },
                onNavigateToNotificaciones = { navController.navigate(NavRoutes.Notificaciones.route) },
                onNavigateToMapa = { navController.navigate(NavRoutes.MapaFeed.route) }
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

        composable(NavRoutes.Notificaciones.route) {
            NotificacionesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Profile.route) { 
            ProfileScreen(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { /* Ya estamos aquí */ }
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
                onNavigateToRequisitos = { navController.navigate(NavRoutes.RequisitosAdopcion.route) }
            ) 
        }

        composable(NavRoutes.Refugios.route) { 
            RefugiosScreen(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToRegistroMascota = { navController.navigate(NavRoutes.RegistroMascota.route) }
            ) 
        }

        composable(NavRoutes.RegistroMascota.route) {
            PantallaRegistroMascota(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.Estadisticas.route) {
            EstadisticasScreen(
                onNavigateToEstadisticas = { /* Ya estamos aqui */ },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
                onNavigateToGestionUsuarios = { navController.navigate(NavRoutes.GestionUsuarios.route) },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
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
        
        composable(NavRoutes.Nutricion.route) { PantallaNutricion() }
        composable(NavRoutes.RequisitosAdopcion.route) { QueNesecitoParaAdoptar() }
        composable(NavRoutes.FiltrosAvanzados.route) { PantallaFiltrosAvanzados() }
        composable(NavRoutes.HistoriasExito.route) { PantallaPetAdopta() }
        
        composable(NavRoutes.OlvidoContrasena.route) { EnvioDeCodigoScreen() }
        composable(NavRoutes.EnvioCodigo.route) { EnvioDeCodigoScreen() }
        composable(NavRoutes.RecuperarContrasena.route) { PantallaRecuperarContrasena() }

        composable(NavRoutes.StepOne.route) { StepOneScreen(vm = adoptionViewModel, onNext = { navController.navigate(NavRoutes.StepTwo.route) }) }
        composable(NavRoutes.StepTwo.route) { StepTwoScreen(vm = adoptionViewModel, onNext = { navController.navigate(NavRoutes.StepThree.route) }) }
        composable(NavRoutes.StepThree.route) { StepThreeScreen(vm = adoptionViewModel, onNext = { navController.navigate(NavRoutes.StepFour.route) }) }
        composable(NavRoutes.StepFour.route) { StepFourScreen(vm = adoptionViewModel, onFinish = { navController.navigate(NavRoutes.AdoptionConfirmation.route) }) }
        composable(NavRoutes.AdoptionConfirmation.route) { AdoptionConfirmationScreen(onNavigateToHome = { navController.navigate(NavRoutes.Home.route) }) }
    }
}
