package com.example.seguimiento.features.EncontrarMascotas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
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
    var indiceActual by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
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
            if (mascotas.isNotEmpty()) {
                val mascotaActual = mascotas[indiceActual.coerceIn(0, mascotas.size - 1)]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // TÍTULO SECCIÓN
                    Surface(
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "GESTIÓN DE MASCOTAS PUBLICADAS",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFFD37506)
                        )
                    }

                    // TARJETA CENTRAL
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        TarjetaMascotaAdmin(
                            mascota = mascotaActual,
                            onEliminar = { 
                                viewModel.eliminarMascota(mascotaActual.id)
                                if (indiceActual >= mascotas.size - 1) indiceActual = 0
                            },
                            onEditar = { onNavigateToEditarMascota(mascotaActual.id) }
                        )
                    }

                    // CONTROLES DE NAVEGACIÓN (Siguiente Mascota)
                    Row(
                        modifier = Modifier
                            .padding(bottom = 30.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { indiceActual = if (indiceActual > 0) indiceActual - 1 else mascotas.size - 1 },
                            modifier = Modifier.size(50.dp).background(Color.White.copy(alpha = 0.8f), CircleShape)
                        ) {
                            Icon(Icons.Default.ChevronLeft, "Anterior")
                        }
                        
                        Spacer(Modifier.width(20.dp))
                        
                        Text(
                            text = "${indiceActual + 1} / ${mascotas.size}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                        )

                        Spacer(Modifier.width(20.dp))

                        IconButton(
                            onClick = { indiceActual = (indiceActual + 1) % mascotas.size },
                            modifier = Modifier.size(50.dp).background(Color.White.copy(alpha = 0.8f), CircleShape)
                        ) {
                            Icon(Icons.Default.ChevronRight, "Siguiente")
                        }
                    }
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay mascotas para gestionar", color = Color.White, fontWeight = FontWeight.Bold)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box {
                AsyncImage(
                    model = mascota.imagenUrl,
                    contentDescription = mascota.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                
                if (mascota.estado == PublicacionEstado.ADOPTADA) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("ADOPTADA", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mascota.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color(0xFF5D2E17)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Pets, null, tint = Color(0xFFF28B31), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = "${mascota.tipo} • ${mascota.edad}", color = Color.Gray, fontSize = 14.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = Color(0xFFF28B31), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = mascota.ubicacion, color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onEditar,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0091EA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("EDITAR", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("ELIMINAR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
