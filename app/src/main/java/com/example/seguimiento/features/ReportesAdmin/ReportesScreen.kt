package com.example.seguimiento.features.ReportesAdmin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.core.navigation.AdminBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    viewModel: ReportesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToEstadisticas: () -> Unit = {},
    onNavigateToListaSolicitudes: () -> Unit = {},
    onNavigateToEncontrarMascotas: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val naranjaApp = Color(0xFFE67E22)
    val cafeApp = Color(0xFF5D2E17)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFDFBFA),
        bottomBar = {
            AdminBottomBar(
                currentRoute = "admin_reportes",
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
        ) {
            // --- HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 45.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            stringResource(R.string.admin_reports_title),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.admin_reports_subtitle),
                            color = Color.White.copy(0.9f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Button(
                        onClick = { viewModel.generarReportes() },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = naranjaApp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Analytics, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.admin_reports_btn_generate), fontWeight = FontWeight.Black)
                    }
                }

                if (uiState.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = naranjaApp)
                        }
                    }
                } else if (uiState.mascotasPorEspecie.isNotEmpty()) {
                    // --- SECCIÓN MASCOTAS ---
                    item { SectionHeader(stringResource(R.string.admin_reports_sec_pets)) }
                    item { ChartCard(stringResource(R.string.admin_reports_chart_species), Icons.Default.Pets, Color(0xFF2196F3)) { BarChartDiagram(uiState.mascotasPorEspecie) } }
                    item { ChartCard(stringResource(R.string.admin_reports_chart_success), Icons.Default.PieChart, Color(0xFF4CAF50)) { ProgressDistributionDiagram(uiState.adoptadasVsDisponibles) } }
                    item { ChartCard(stringResource(R.string.admin_reports_chart_requests), Icons.AutoMirrored.Filled.FactCheck, Color(0xFFFFC107)) { StatusStepDiagram(uiState.solicitudesPorEstado) } }

                    // --- SECCIÓN TIENDA (NUEVO) ---
                    item { SectionHeader(stringResource(R.string.admin_reports_sec_store)) }
                    item { ChartCard(stringResource(R.string.admin_reports_chart_cat_stock), Icons.Default.Inventory, Color(0xFFFF9800)) { BarChartDiagram(uiState.stockPorCategoria, color1 = Color(0xFFFF9800), color2 = Color(0xFFFFCC80)) } }
                    
                    item {
                        ChartCard(stringResource(R.string.admin_reports_chart_prod_stock), Icons.Default.ShoppingBag, Color(0xFF009688)) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.stockPorProducto.take(10).forEach { (nombre, stock) ->
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(nombre, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                                        Text(stringResource(R.string.admin_reports_stock_unit, stock), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if(stock < 3) Color.Red else Color.Black)
                                    }
                                }
                            }
                        }
                    }

                    // --- RANKING ---
                    item { SectionHeader(stringResource(R.string.admin_reports_sec_popularity)) }
                    item {
                        Text(stringResource(R.string.admin_reports_ranking_title), fontWeight = FontWeight.Black, fontSize = 16.sp, color = cafeApp)
                    }
                    items(uiState.mascotasMasPopulares) { (nombre, likes) ->
                        RankingCard(nombre, likes)
                    }
                } else {
                    item { EmptyStateMessage() }
                }
                
                item { Spacer(Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF5D2E17),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ChartCard(title: String, icon: ImageVector, color: Color, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).background(color.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF5D2E17))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
            content()
        }
    }
}

@Composable
fun BarChartDiagram(data: Map<String, Int>, color1: Color = Color(0xFF2196F3), color2: Color = Color(0xFF64B5F6)) {
    val maxVal = (data.values.maxOrNull() ?: 1).toFloat()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        data.forEach { (label, value) ->
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text(value.toString(), fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(Color(0xFFF5F5F5))) {
                    Box(
                        modifier = Modifier.fillMaxWidth(value / maxVal).fillMaxHeight().clip(CircleShape)
                            .background(brush = Brush.horizontalGradient(listOf(color1, color2)))
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressDistributionDiagram(data: Map<String, Int>) {
    val total = data.values.sum().toFloat().coerceAtLeast(1f)
    val adoptadas = (data["Adoptadas"] ?: 0).toFloat()
    val progreso = adoptadas / total
    
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progreso },
                modifier = Modifier.size(80.dp),
                color = Color(0xFF4CAF50),
                strokeWidth = 8.dp,
                trackColor = Color(0xFFE8F5E9),
                strokeCap = StrokeCap.Round
            )
            Text("${(progreso * 100).toInt()}%", fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DiagramLegend(Color(0xFF4CAF50), stringResource(R.string.admin_reports_legend_adopted, adoptadas.toInt()))
            DiagramLegend(Color(0xFFE8F5E9), stringResource(R.string.admin_reports_legend_available, data["Disponibles"] ?: 0))
        }
    }
}

@Composable
fun StatusStepDiagram(data: Map<String, Int>) {
    val approved = (data["APPROVED"] ?: 0).toFloat()
    val rejected = (data["REJECTED"] ?: 0).toFloat()
    val pending = (data["PENDING"] ?: 0).toFloat()
    val total = (approved + rejected + pending).coerceAtLeast(1f)
    
    Row(modifier = Modifier.fillMaxWidth().height(60.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (approved > 0) Box(Modifier.weight(approved / total).fillMaxHeight().clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)).background(Color(0xFF4CAF50)))
        if (pending > 0) Box(Modifier.weight(pending / total).fillMaxHeight().background(Color(0xFFFFC107)))
        if (rejected > 0) Box(Modifier.weight(rejected / total).fillMaxHeight().clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)).background(Color(0xFFF44336)))
    }
}

@Composable
fun RankingCard(nombre: String, likes: Int) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFF3E0), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Favorite, null, tint = Color.Red, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Text(nombre, fontWeight = FontWeight.Bold, color = Color(0xFF5D2E17), modifier = Modifier.weight(1f))
            Text(stringResource(R.string.admin_reports_ranking_votes, likes), fontWeight = FontWeight.Black, color = Color(0xFFE67E22), fontSize = 16.sp)
        }
    }
}

@Composable
fun DiagramLegend(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun EmptyStateMessage() {
    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.QueryStats, null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.admin_reports_empty_title), color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.admin_reports_empty_desc), color = Color.Gray, fontSize = 12.sp)
        }
    }
}
