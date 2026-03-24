package com.example.seguimiento.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seguimiento.features.home.HomeScreen
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
import com.example.seguimiento.features.Filtros.PantallaFiltrosAvanzado
import com.example.seguimiento.features.HistoriaMascota.PantallaPetAdopta
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel
import com.example.seguimiento.features.FormularioDeAdopction.StepOneScreen.StepOneScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepTwoScreen.StepTwoScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepThreeScreen.StepThreeScreen
import com.example.seguimiento.features.FormularioDeAdopction.StepFourScreen.StepFourScreen
import com.example.seguimiento.features.ConfirmacionMascotaFeliz.AdoptionConfirmationScreen
import com.example.seguimiento.features.IngresarMascota.PantallaRegistroMascota
import com.example.seguimiento.features.Estadisticas.EstadisticasScreen
import com.example.seguimiento.features.ListaDeSolicitudes.ListaSolicitudesScreen
import com.example.seguimiento.features.EncontrarMascotas.PantallaAdopcion

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val adoptionViewModel: AdoptionViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login.route
    ) {
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
                onNavigateToMascotaDestacada = { nombre, edad, ubicacion, url ->
                    navController.navigate(NavRoutes.MascotaDestacada.createRoute(nombre, edad, ubicacion, url))
                },
                onNavigateToNutricion = { navController.navigate(NavRoutes.Nutricion.route) },
                onNavigateToRequisitos = { navController.navigate(NavRoutes.RequisitosAdopcion.route) },
                onNavigateToRefugios = { navController.navigate(NavRoutes.Refugios.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToHistorias = { navController.navigate(NavRoutes.HistoriasExito.route) }
            )
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onNavigateToFinalize = { navController.navigate(NavRoutes.FinalizarRegistro.route) },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.OlvidoContrasena.route) {
            OlvidoContrasenaScreen(
                onNavigateToCode = { navController.navigate(NavRoutes.EnvioCodigo.route) }
            )
        }

        composable(NavRoutes.EnvioCodigo.route) {
            EnvioDeCodigoScreen(
                onCodeVerified = { navController.navigate(NavRoutes.RecuperarContrasena.route) },
                onBackToLogin = { 
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    } 
                },
                onNavigateToHome = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.RecuperarContrasena.route) {
            PantallaRecuperarContrasena() 
        }

        composable(NavRoutes.FinalizarRegistro.route) {
            FinalizarRegistroScreen(
                onRegistrationFinished = { 
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    } 
                }
            )
        }

        composable(NavRoutes.Profile.route) { 
            ProfileScreen(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { /* Ya estamos aquí */ }
            ) 
        }

        composable(NavRoutes.MascotaDestacada.route) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val edad = backStackEntry.arguments?.getString("edad") ?: ""
            val ubicacion = backStackEntry.arguments?.getString("ubicacion") ?: ""
            val url = backStackEntry.arguments?.getString("url") ?: ""

            EstaEsperandoPorTiScreen(
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

        composable(NavRoutes.Nutricion.route) { 
            PantallaNutricion(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
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

        composable(NavRoutes.Refugios.route) { 
            RefugiosScreen(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) },
                onNavigateToRegistroMascota = { navController.navigate(NavRoutes.RegistroMascota.route) }
            ) 
        }

        composable(NavRoutes.FiltrosAvanzados.route) { 
            PantallaFiltrosAvanzado(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { /* Ya estamos aquí */ },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            ) 
        }

        composable(NavRoutes.HistoriasExito.route) { 
            PantallaPetAdopta(
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            ) 
        }

        composable(NavRoutes.StepOne.route) {
            StepOneScreen(
                vm = adoptionViewModel,
                onNext = { navController.navigate(NavRoutes.StepTwo.route) },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.StepTwo.route) {
            StepTwoScreen(
                vm = adoptionViewModel,
                onNext = { navController.navigate(NavRoutes.StepThree.route) },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.StepThree.route) {
            StepThreeScreen(
                vm = adoptionViewModel,
                onNext = { navController.navigate(NavRoutes.StepFour.route) },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.StepFour.route) {
            StepFourScreen(
                vm = adoptionViewModel,
                onFinish = { 
                    navController.navigate(NavRoutes.AdoptionConfirmation.route) {
                        popUpTo(NavRoutes.StepOne.route) { inclusive = true }
                    }
                },
                onNavigateToHome = { navController.navigate(NavRoutes.Home.route) },
                onNavigateToFiltros = { navController.navigate(NavRoutes.FiltrosAvanzados.route) },
                onNavigateToProfile = { navController.navigate(NavRoutes.Profile.route) }
            )
        }

        composable(NavRoutes.AdoptionConfirmation.route) {
            AdoptionConfirmationScreen(
                onNavigateToHome = { 
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                    }
                }
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

        // Admin Features
        composable(NavRoutes.Estadisticas.route) {
            EstadisticasScreen(
                onNavigateToEstadisticas = { /* Ya estamos aqui */ },
                onNavigateToListaSolicitudes = { navController.navigate(NavRoutes.ListaSolicitudes.route) },
                onNavigateToEncontrarMascotas = { navController.navigate(NavRoutes.EncontrarMascotas.route) },
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
    }
}
