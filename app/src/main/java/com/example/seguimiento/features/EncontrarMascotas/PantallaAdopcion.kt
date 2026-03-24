package com.example.seguimiento.features.EncontrarMascotas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.core.navigation.AdminBottomBar

@Composable
fun PantallaAdopcion(
    viewModel: AdopcionViewModel = viewModel(),
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToListaSolicitudes: () -> Unit,
    onNavigateToEncontrarMascotas: () -> Unit,
    onLogout: () -> Unit
) {
    val mascotas by viewModel.listaMascotas.collectAsState()
    var indiceActual by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. FONDO
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
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
                    // LOGO
                    Image(
                        painter = painterResource(id = R.drawable.petadopticono),
                        contentDescription = null,
                        modifier = Modifier.size(300.dp)
                    )

                    // TARJETA (Muy pegada al logo)
                    Box(
                        modifier = Modifier
                            .offset(y = (-85).dp) // Sube la tarjeta
                            .padding(horizontal = 24.dp)
                            .weight(1f, fill = false)
                    ) {
                        TarjetaMascota(
                            mascota = mascotaActual,
                            onAdoptar = { viewModel.adoptar(mascotaActual.nombre) }
                        )
                    }

                    // 2. ICONO NEGRO SUBIDO
                    Box(
                        modifier = Modifier
                            .offset(y = (-65).dp) 
                            .size(60.dp)
                            .background(Color.Black, CircleShape)
                            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            .clickable {
                                indiceActual = (indiceActual + 1) % mascotas.size
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Siguiente",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaMascota(mascota: Mascota, onAdoptar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            AsyncImage(
                model = mascota.fotoUrl,
                contentDescription = mascota.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mascota.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${mascota.edad} • ${mascota.ubicacion}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = mascota.descripcion,
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = onAdoptar,
                enabled = !mascota.esAdoptada,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mascota.esAdoptada) Color.Gray else Color(0xFFF28B31)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (mascota.esAdoptada) "¡ADOPTADO!" else "Adoptar",
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
