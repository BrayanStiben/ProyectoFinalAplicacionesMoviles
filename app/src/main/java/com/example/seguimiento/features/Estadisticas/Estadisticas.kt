package com.example.seguimiento.features.Estadisticas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import com.example.seguimiento.core.navigation.AdminBottomBar

// --- PALETA DE COLORES OFICIAL ---
val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)
val GrisSuave = Color(0xFFF5F5F5)

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
    onNavigateToGestionRefugios: () -> Unit = {},
    onNavigateToReportesDetallados: () -> Unit = {},
    onNavigateToGestionTienda: () -> Unit = {},
    onNavigateToHistorialVentas: () -> Unit = {},
    onNavigateToRegistroMascota: () -> Unit = {},
    onNavigateToHistorias: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFDFBFA),
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
        ) {
            // --- HEADER ESTILO UNIFICADO ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 45.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Text(
                        stringResource(R.string.admin_panel_title),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.admin_panel_subtitle),
                        color = Color.White.copy(0.9f),
                        fontSize = 15.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(Modifier.height(24.dp))

                // --- SECCIÓN: MÉTRICAS CLAVE ---
                Text(stringResource(R.string.admin_panel_resumen_operativo), fontWeight = FontWeight.Black, fontSize = 14.sp, color = CafeApp, modifier = Modifier.padding(bottom = 12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricMiniCard(
                        label = stringResource(R.string.admin_panel_metric_requests),
                        value = state.adoptionRequestsCount.toString(),
                        icon = Icons.Default.VolunteerActivism,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToGestionAdopciones() }
                    
                    MetricMiniCard(
                        label = stringResource(R.string.admin_panel_metric_moderation),
                        value = state.petModerationCount.toString(),
                        icon = Icons.Default.NewReleases,
                        color = NaranjaApp,
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToListaSolicitudes() }

                    MetricMiniCard(
                        label = stringResource(R.string.admin_panel_metric_users),
                        value = state.usuariosTotales.toString(),
                        icon = Icons.Default.Group,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToGestionUsuarios() }
                }

                Spacer(Modifier.height(24.dp))

                // --- SECCIÓN: HERRAMIENTAS DE GESTIÓN (BOTONES CON COLOR) ---
                Text(stringResource(R.string.admin_panel_tools_title), fontWeight = FontWeight.Black, fontSize = 14.sp, color = CafeApp, modifier = Modifier.padding(bottom = 12.dp))

                // Fila 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ColorfulAdminButton(
                        title = stringResource(R.string.admin_panel_tool_store),
                        icon = Icons.Default.Storefront,
                        containerColor = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToGestionTienda() }

                    ColorfulAdminButton(
                        title = stringResource(R.string.admin_panel_tool_reports),
                        icon = Icons.Default.BarChart,
                        containerColor = Color(0xFF673AB7),
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToReportesDetallados() }
                }

                Spacer(Modifier.height(10.dp))

                // Fila 2
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ColorfulAdminButton(
                        title = stringResource(R.string.admin_panel_tool_sales),
                        icon = Icons.Default.ReceiptLong,
                        containerColor = Color(0xFF0091EA),
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToHistorialVentas() }

                    ColorfulAdminButton(
                        title = stringResource(R.string.admin_panel_tool_shelters),
                        icon = Icons.Default.Apartment,
                        containerColor = Color(0xFF00ACC1),
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToGestionRefugios() }
                }

                Spacer(Modifier.height(10.dp))

                // Fila 3
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ColorfulAdminButton(
                        title = stringResource(R.string.admin_panel_tool_chat),
                        icon = Icons.AutoMirrored.Filled.Comment,
                        containerColor = Color(0xFF009688),
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToGestionComentarios() }

                    ColorfulAdminButton(
                        title = stringResource(R.string.admin_panel_tool_add_pet),
                        icon = Icons.Default.AddCircle,
                        containerColor = NaranjaApp,
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToRegistroMascota() }
                }

                Spacer(Modifier.height(10.dp))

                // Fila 4
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ColorfulAdminButton(
                        title = stringResource(R.string.admin_panel_tool_stories),
                        icon = Icons.Default.AutoAwesome,
                        containerColor = Color(0xFFFFC107),
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToGestionHistorias() }
                    
                    ColorfulAdminButton(
                        title = stringResource(R.string.admin_panel_tool_view_stories),
                        icon = Icons.Default.Collections,
                        containerColor = Color(0xFFE91E63),
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToHistorias() }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ColorfulAdminButton(title: String, icon: ImageVector, containerColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(75.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun MetricMiniCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(90.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = CafeApp)
            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}
