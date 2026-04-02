package com.example.seguimiento.features.EncontrarMascotas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.R
import com.example.seguimiento.core.navigation.AdminBottomBar

val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFDFBFA))
        ) {
            // --- HEADER ESTILO HOME ---
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
                        "Gestión de Mascotas 🐾",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Administra todas las publicaciones",
                        color = Color.White.copy(0.9f),
                        fontSize = 15.sp
                    )
                }
            }

            if (mascotas.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1), // Cambiado a 1 para tarjetas más legibles con botones
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(mascotas) { mascota ->
                        TarjetaMascotaAdmin(
                            mascota = mascota,
                            onEliminar = { viewModel.eliminarMascota(mascota.id) },
                            onEditar = { onNavigateToEditarMascota(mascota.id) }
                        )
                    }
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay mascotas para gestionar", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TarjetaMascotaAdmin(
    mascota: Mascota,
    onEliminar: () -> Unit,
    onEditar: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("¿Eliminar mascota?") },
            text = { Text("Esta acción no se puede deshacer. Se borrará a ${mascota.nombre} del sistema.") },
            confirmButton = {
                TextButton(onClick = { 
                    onEliminar()
                    showConfirmDelete = false
                }) {
                    Text("ELIMINAR", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text("CANCELAR")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min)
        ) {
            // Imagen a la izquierda
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = mascota.imagenUrl,
                    contentDescription = mascota.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.petadopticono)
                )
                
                // Badge de estado
                val (colorEstado, textoEstado) = when(mascota.estado) {
                    PublicacionEstado.ADOPTADA -> Color(0xFF4CAF50) to "ADOPTADA"
                    PublicacionEstado.PENDIENTE -> Color(0xFFFFA000) to "PENDIENTE"
                    PublicacionEstado.VERIFICADA -> Color(0xFF2196F3) to "VERIFICADA"
                    else -> Color.Gray to mascota.estado.name
                }
                
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(4.dp),
                    color = colorEstado,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        textoEstado,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información y botones a la derecha
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = mascota.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafeApp,
                        maxLines = 1
                    )
                    Text(
                        text = "${mascota.tipo} • ${mascota.edad}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = NaranjaApp, modifier = Modifier.size(12.dp))
                        Text(text = mascota.ubicacion, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onEditar,
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFE3F2FD),
                            contentColor = Color(0xFF1976D2)
                        )
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Editar", fontSize = 12.sp)
                    }

                    FilledTonalButton(
                        onClick = { showConfirmDelete = true },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFFFEBEE),
                            contentColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Borrar", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
