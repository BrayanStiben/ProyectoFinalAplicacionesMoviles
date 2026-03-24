package com.example.seguimiento.features.ListaDeSolicitudes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.core.navigation.AdminBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaSolicitudesScreen(
    viewModel: AdopcionViewModel = viewModel(),
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToListaSolicitudes: () -> Unit,
    onNavigateToEncontrarMascotas: () -> Unit,
    onLogout: () -> Unit
) {
    val solicitudes by viewModel.solicitudes.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text("SOLICITUDES DE ADOPCIÓN", 
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color(0xFFD37506)
                        ) 
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                )
            },
            bottomBar = {
                AdminBottomBar(
                    currentRoute = "lista_solicitudes",
                    onNavigateToEstadisticas = onNavigateToEstadisticas,
                    onNavigateToListaSolicitudes = onNavigateToListaSolicitudes,
                    onNavigateToEncontrarMascotas = onNavigateToEncontrarMascotas,
                    onLogout = onLogout
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(solicitudes) { solicitud ->
                    SolicitudItem(solicitud, onContactar = { viewModel.contactarMascota(solicitud.id) })
                }
            }
        }
    }
}

@Composable
fun SolicitudItem(solicitud: DatosMascota, onContactar: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = solicitud.imagenUrl,
                contentDescription = solicitud.nombre,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(solicitud.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(solicitud.estado, color = if (solicitud.esAprobado) Color(0xFF4CAF50) else Color.Gray)
                if (solicitud.subtexto != null) {
                    Text(solicitud.subtexto, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Button(
                onClick = onContactar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD37506)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Contactar", fontSize = 12.sp)
            }
        }
    }
}
