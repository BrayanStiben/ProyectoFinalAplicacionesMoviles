package com.example.seguimiento.features.Estadisticas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontFamily
import com.example.seguimiento.core.navigation.AdminBottomBar

// --- PALETA DE COLORES ---
val ColorVivoNaranja = Color(0xFFFF6D00)
val ColorVivoAzul = Color(0xFF0091EA)
val ColorVivoVerde = Color(0xFF00C853)
val ColorVivoAmarillo = Color(0xFFFFD600)
val ColorVivoRojo = Color(0xFFD32F2F)
val ColorTextoFuerte = Color(0xFF1A1A1A)
val ColorCafeApp = Color(0xFF5D2E17)

@Composable
fun EstadisticasScreen(
    viewModel: EstadisticasViewModel = hiltViewModel(),
    onNavigateToEstadisticas: () -> Unit = {},
    onNavigateToListaSolicitudes: () -> Unit = {},
    onNavigateToEncontrarMascotas: () -> Unit = {},
    onNavigateToGestionUsuarios: () -> Unit = {},
    onNavigateToGestionHistorias: () -> Unit = {},
    onNavigateToGestionComentarios: () -> Unit = {},
    onNavigateToGestionAdopciones: () -> Unit = {},
    onNavigateToRegistroMascota: () -> Unit = {},
    onNavigateToHistorias: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
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

                // --- TÍTULO ---
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
                        Text("PANEL DE CONTROL", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = ColorTextoFuerte)
                        Text(" • ", fontWeight = FontWeight.Black, fontSize = 18.sp, color = ColorVivoNaranja)
                        Text("ADMINISTRACIÓN", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = ColorVivoNaranja)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // --- FILA DE CONTADORES PRINCIPALES ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Adopciones Pendientes
                    Card(
                        modifier = Modifier.weight(1f).height(110.dp).clickable { onNavigateToGestionAdopciones() },
                        colors = CardDefaults.cardColors(containerColor = ColorVivoVerde),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.VolunteerActivism, null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Text("${state.adoptionRequestsCount}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("SOLICITUDES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                    // Mascotas por Moderar
                    Card(
                        modifier = Modifier.weight(1f).height(110.dp).clickable { onNavigateToListaSolicitudes() },
                        colors = CardDefaults.cardColors(containerColor = ColorVivoNaranja),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.NewReleases, null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Text("${state.petModerationCount}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("MODERACIÓN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- SECCIÓN DE GESTIÓN RÁPIDA ---
                Text("GESTIÓN DE CONTENIDO", fontWeight = FontWeight.Black, fontSize = 14.sp, color = ColorCafeApp, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))

                // Moderar Chat
                Card(
                    modifier = Modifier.fillMaxWidth().height(70.dp).padding(bottom = 10.dp).clickable { onNavigateToGestionComentarios() },
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).background(ColorVivoAzul.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.Comment, null, tint = ColorVivoAzul, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Text("MODERAR CHAT COMUNITARIO", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
                    }
                }

                // Historias Felices
                Card(
                    modifier = Modifier.fillMaxWidth().height(70.dp).padding(bottom = 10.dp).clickable { onNavigateToGestionHistorias() },
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).background(ColorCafeApp.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.HistoryEdu, null, tint = ColorCafeApp, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Text("HISTORIAS FELICES PENDIENTES", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- ACCIONES DE CREACIÓN ---
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onNavigateToRegistroMascota,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorCafeApp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("NUEVA MASCOTA", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = onNavigateToHistorias,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorVivoNaranja),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("NUEVA HISTORIA", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- ESTADÍSTICAS DE USUARIOS ---
                Text("CONTROL DE USUARIOS", fontWeight = FontWeight.Black, fontSize = 14.sp, color = ColorCafeApp, modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToGestionUsuarios() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(45.dp).background(ColorVivoAmarillo.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Group, null, tint = ColorVivoAmarillo)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text("USUARIOS REGISTRADOS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${state.usuariosTotales + state.usuariosBaneados} en total", color = Color.Gray, fontSize = 11.sp)
                            }
                            Text("${state.usuariosTotales}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = ColorVivoVerde)
                        }
                        
                        HorizontalDivider(Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(45.dp).background(ColorVivoRojo.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Block, null, tint = ColorVivoRojo)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text("USUARIOS BANEADOS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Sin acceso a la plataforma", color = Color.Gray, fontSize = 11.sp)
                            }
                            Text("${state.usuariosBaneados}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = ColorVivoRojo)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- GRÁFICA SEMANAL ---
                Card(
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("ACTIVIDAD SEMANAL", fontWeight = FontWeight.Black, fontSize = 14.sp, color = ColorTextoFuerte)
                        Spacer(Modifier.height(20.dp))
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
fun BarChartViva(data: BarData) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Box(Modifier.width(18.dp).height(120.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp)))
            Column {
                if (data.valueOrange > 0) Box(Modifier.width(18.dp).height((data.valueOrange / 6).dp).background(ColorVivoNaranja, RoundedCornerShape(10.dp)))
                if (data.valueBlue > 0) Box(Modifier.width(18.dp).height((data.valueBlue / 6).dp).background(ColorVivoAzul, RoundedCornerShape(10.dp)))
            }
        }
        Text(data.label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
    }
}
