package com.example.seguimiento.features.Estadisticas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.Dominio.modelos.HistoriaEstado
import com.example.seguimiento.Dominio.modelos.HistoriaFeliz
import com.example.seguimiento.features.HistoriaMascota.HistoriaMascotaViewModel
import com.example.seguimiento.features.HistoriaMascota.SocialHistoryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionHistoriasScreen(
    viewModel: HistoriaMascotaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val historias by viewModel.todasLasHistorias.collectAsState()
    val currentUser by viewModel.authRepository.currentUser.collectAsState()
    
    var tabSelected by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_stories_title), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF5D2E17)
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5))) {
            TabRow(
                selectedTabIndex = tabSelected,
                containerColor = Color.White,
                contentColor = Color(0xFFE67E22)
            ) {
                Tab(selected = tabSelected == 0, onClick = { tabSelected = 0 }, text = { Text(stringResource(R.string.admin_panel_metric_moderation)) })
                Tab(selected = tabSelected == 1, onClick = { tabSelected = 1 }, text = { Text(stringResource(R.string.home_category_all)) })
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.fondo3),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.3f
                )

                val filteredList = if (tabSelected == 0) {
                    historias.filter { it.estado == HistoriaEstado.PENDIENTE }
                } else {
                    historias
                }

                if (filteredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.admin_stories_empty), color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredList) { historia ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column {
                                    // Header de moderación
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        StatusBadge(historia.estado)
                                        Spacer(Modifier.weight(1f))
                                        IconButton(onClick = { viewModel.eliminarHistoria(historia.id) }) {
                                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                                        }
                                    }

                                    // Componente Social (permite dar like, follow y comentar)
                                    SocialHistoryCard(
                                        historia = historia,
                                        currentUserId = currentUser?.id ?: "",
                                        onLike = { viewModel.toggleLike(historia.id) },
                                        onFollow = { viewModel.toggleFollow(historia.id) },
                                        onDelete = { viewModel.eliminarHistoria(historia.id) },
                                        isAdmin = true,
                                        viewModel = viewModel
                                    )

                                    // Botones de acción Moderación (solo si está pendiente)
                                    if (historia.estado == HistoriaEstado.PENDIENTE) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.aprobarHistoria(historia.id) },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(stringResource(R.string.admin_stories_btn_approve), fontWeight = FontWeight.Black)
                                            }
                                            Button(
                                                onClick = { viewModel.rechazarHistoria(historia.id) },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(stringResource(R.string.admin_stories_btn_reject), fontWeight = FontWeight.Black)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(estado: HistoriaEstado) {
    val color = when(estado) {
        HistoriaEstado.PENDIENTE -> Color(0xFFFFA000)
        HistoriaEstado.APROBADA -> Color(0xFF4CAF50)
        HistoriaEstado.RECHAZADA -> Color(0xFFF44336)
    }
    val texto = when(estado) {
        HistoriaEstado.PENDIENTE -> stringResource(R.string.profile_stats_pending)
        HistoriaEstado.APROBADA -> "APROBADA"
        HistoriaEstado.RECHAZADA -> stringResource(R.string.status_rejected)
    }
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
        Text(texto, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
