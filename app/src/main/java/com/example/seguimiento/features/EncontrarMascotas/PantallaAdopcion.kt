package com.example.seguimiento.features.EncontrarMascotas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.R
import com.example.seguimiento.core.navigation.AdminBottomBar

@Composable
fun PantallaAdopcion(
    viewModel: AdopcionViewModel = hiltViewModel(),
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToListaSolicitudes: () -> Unit,
    onNavigateToEncontrarMascotas: () -> Unit,
    onNavigateToEditarMascota: (String) -> Unit,
    onLogout: () -> Unit
) {
    val mascotas by viewModel.listaMascotas.collectAsState()
    val pagerState = rememberPagerState(pageCount = { mascotas.size })

    Scaffold(
        bottomBar = {
            AdminBottomBar(
                currentRoute = "encontrar_mascotas",
                onNavigateToEstadisticas = onNavigateToEstadisticas,
                onNavigateToListaSolicitudes = onNavigateToListaSolicitudes,
                onNavigateToEncontrarMascotas = onNavigateToEncontrarMascotas,
                onLogout = onLogout
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            if (mascotas.isNotEmpty()) {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    key = { mascotas[it].id }
                ) { index ->
                    ReelMascotaAdmin(
                        mascota = mascotas[index],
                        onEliminar = { viewModel.eliminarMascota(mascotas[index].id) },
                        onEditar = { onNavigateToEditarMascota(mascotas[index].id) }
                    )
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = R.string.pet_mgmt_no_pets), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReelMascotaAdmin(
    mascota: Mascota,
    onEliminar: () -> Unit,
    onEditar: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text(stringResource(id = R.string.pet_mgmt_delete_title)) },
            text = { Text(stringResource(id = R.string.pet_mgmt_delete_confirm, mascota.nombre)) },
            confirmButton = {
                TextButton(onClick = { 
                    onEliminar()
                    showConfirmDelete = false
                }) {
                    Text(stringResource(id = R.string.pet_mgmt_btn_delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text(stringResource(id = R.string.pet_mgmt_btn_cancel))
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo full screen
        AsyncImage(
            model = mascota.imagenUrl,
            contentDescription = mascota.nombre,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.petadopticono)
        )

        // Gradiente inferior para legibilidad del texto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 500f
                    )
                )
        )

        // Contenido superpuesto
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Información a la izquierda
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Badge de estado
                val colorEstado = when(mascota.estado) {
                    PublicacionEstado.ADOPTADA -> Color(0xFF4CAF50)
                    PublicacionEstado.PENDIENTE -> Color(0xFFFFA000)
                    PublicacionEstado.VERIFICADA -> Color(0xFF2196F3)
                    else -> Color.Gray
                }
                
                val labelEstado = when(mascota.estado) {
                    PublicacionEstado.ADOPTADA -> stringResource(R.string.status_adopted)
                    PublicacionEstado.PENDIENTE -> stringResource(R.string.status_pending)
                    PublicacionEstado.VERIFICADA -> stringResource(R.string.status_verified)
                    else -> mascota.estado.name
                }
                
                Surface(
                    color = colorEstado,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        labelEstado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = mascota.nombre,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Pets, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${mascota.tipo} • ${mascota.raza}",
                        color = Color.White.copy(0.9f),
                        fontSize = 16.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE67E22), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = mascota.ubicacion,
                        color = Color.White.copy(0.8f),
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = mascota.descripcion,
                    color = Color.White.copy(0.7f),
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)
                )
            }

            // Botones de acción a la derecha (estilo vertical)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                FloatingActionButton(
                    onClick = onEditar,
                    containerColor = Color.White,
                    contentColor = Color(0xFF2196F3),
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Edit, stringResource(id = R.string.pet_mgmt_edit_desc))
                }

                FloatingActionButton(
                    onClick = { showConfirmDelete = true },
                    containerColor = Color.White,
                    contentColor = Color(0xFFD32F2F),
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Delete, stringResource(id = R.string.pet_mgmt_delete_desc))
                }
                
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
