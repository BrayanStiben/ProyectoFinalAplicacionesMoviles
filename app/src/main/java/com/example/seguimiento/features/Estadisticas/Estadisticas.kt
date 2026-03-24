package com.example.seguimiento.features.Estadisticas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontFamily

// --- 1. PALETA DE COLORES VIVOS Y VIBRANTES ---
val ColorVivoNaranja = Color(0xFFFF6D00)
val ColorVivoAzul = Color(0xFF0091EA)
val ColorVivoVerde = Color(0xFF00C853)
val ColorVivoAmarillo = Color(0xFFFFD600)
val ColorTextoFuerte = Color(0xFF1A1A1A)
val ColorBarraBase = Color(0xFFFDF7F2) 

data class NavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun EstadisticasScreen(
    viewModel: EstadisticasViewModel = viewModel(),
    onNavigateToEstadisticas: () -> Unit = {},
    onNavigateToListaSolicitudes: () -> Unit = {},
    onNavigateToEncontrarMascotas: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // FONDO DE IMAGEN
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                AdminBottomBar(
                    currentRoute = "estadisticas",
                    onNavigateToEstadisticas = onNavigateToEstadisticas,
                    onNavigateToListaSolicitudes = onNavigateToListaSolicitudes,
                    onNavigateToEncontrarMascotas = onNavigateToEncontrarMascotas,
                    onLogout = onLogout
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(25.dp))

                // --- TÍTULO HORIZONTAL RESALTADO ---
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(15.dp),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "PANEL DE CONTROL",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = ColorTextoFuerte,
                            letterSpacing = 1.sp
                        )
                        Text(
                            " • ",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ColorVivoNaranja
                        )
                        Text(
                            "RESUMEN",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = ColorVivoNaranja,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // --- TARJETAS VIBRANTES ---
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        modifier = Modifier
                            .weight(0.45f)
                            .height(190.dp)
                            .shadow(8.dp, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("SOLICITUDES", fontWeight = FontWeight.Black, fontSize = 12.sp, color = ColorVivoNaranja)
                            Text("${state.solicitudesNuevas}", fontSize = 44.sp, fontWeight = FontWeight.Black, color = ColorTextoFuerte)
                            Box(
                                Modifier.size(70.dp).background(ColorVivoNaranja.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(painter = painterResource(id = R.drawable.perro), null, modifier = Modifier.size(50.dp))
                            }
                        }
                    }

                    Column(Modifier.weight(0.55f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(85.dp),
                            colors = CardDefaults.cardColors(containerColor = ColorVivoNaranja),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("NUEVAS:", Modifier.weight(1f), fontWeight = FontWeight.Black, color = Color.White, fontSize = 14.sp)
                                Text("${state.solicitudesNuevas}", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MiniCardViva("Mascotas", "${state.mascotasTotales}", ColorVivoVerde, Modifier.weight(1f), painterResource(id = R.drawable.gato))
                            MiniCardViva("Activas", "${state.mascotasActivas}", ColorVivoAzul, Modifier.weight(1f), painterResource(id = R.drawable.perro))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Fila Usuarios Totales (Saturado)
                Card(
                    modifier = Modifier.fillMaxWidth().height(75.dp).shadow(4.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(45.dp).background(ColorVivoAmarillo, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Group, "", tint = Color.White, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.width(15.dp))
                        Text("USUARIOS TOTALES", Modifier.weight(1f), fontWeight = FontWeight.ExtraBold, color = ColorTextoFuerte, fontSize = 13.sp)
                        Text("${state.usuariosTotales}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = ColorVivoAmarillo)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Gráfica
                Card(
                    modifier = Modifier.fillMaxWidth().height(260.dp).shadow(6.dp, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("ESTADÍSTICAS SEMANALES", fontWeight = FontWeight.Black, fontSize = 15.sp, color = ColorTextoFuerte)
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceEvenly) {
                            state.estadisticasSemanales.forEach { data -> BarChartViva(data) }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun AdminBottomBar(
    currentRoute: String,
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToListaSolicitudes: () -> Unit,
    onNavigateToEncontrarMascotas: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBarraBase)
            .shadow(12.dp)
    ) {
        NavigationBar(
            containerColor = ColorBarraBase,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.height(65.dp)
        ) {
            val navItems = listOf(
                NavItem("Estadísticas", Icons.Default.BarChart, "estadisticas"),
                NavItem("Solicitudes", Icons.Default.ListAlt, "lista_solicitudes"),
                NavItem("Mascotas", Icons.Default.Pets, "encontrar_mascotas"),
                NavItem("Salir", Icons.Default.ExitToApp, "logout")
            )
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        when (item.route) {
                            "estadisticas" -> onNavigateToEstadisticas()
                            "lista_solicitudes" -> onNavigateToListaSolicitudes()
                            "encontrar_mascotas" -> onNavigateToEncontrarMascotas()
                            "logout" -> onLogout()
                        }
                    },
                    icon = {
                        Icon(
                            item.icon,
                            null,
                            tint = if (isSelected) ColorVivoNaranja else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) ColorVivoNaranja else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            }
        }
        Spacer(
            Modifier
                .navigationBarsPadding()
                .height(10.dp)
                .fillMaxWidth()
                .background(ColorBarraBase)
        )
    }
}

@Composable
fun MiniCardViva(title: String, value: String, color: Color, modifier: Modifier, imagen: Painter) {
    Card(
        modifier = modifier.height(93.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = imagen, contentDescription = null, modifier = Modifier.size(22.dp))
            Text(title.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
fun BarChartViva(data: BarData) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Box(Modifier.width(22.dp).height(140.dp).background(Color(0xFFEEEEEE), RoundedCornerShape(11.dp)))
            Column {
                if (data.valueOrange > 0) Box(Modifier.width(22.dp).height((data.valueOrange / 6).dp).background(ColorVivoNaranja, RoundedCornerShape(11.dp)))
                if (data.valueBlue > 0) Box(Modifier.width(22.dp).height((data.valueBlue / 6).dp).background(ColorVivoAzul, RoundedCornerShape(11.dp)))
            }
        }
        Text(data.label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
    }
}
