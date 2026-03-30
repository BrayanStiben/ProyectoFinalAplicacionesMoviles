package com.example.seguimiento.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.Dominio.modelos.Mascota
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaFeedScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String, String, String, String, String) -> Unit
) {
    val mascotas by viewModel.mascotasFeed.collectAsState()
    val bogota = LatLng(4.6097, -74.0817)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 10f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mascotas en el Mapa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(padding),
            cameraPositionState = cameraPositionState
        ) {
            mascotas.forEach { mascota ->
                Marker(
                    state = MarkerState(position = LatLng(mascota.lat, mascota.lng)),
                    title = mascota.nombre,
                    snippet = "${mascota.tipo} - ${mascota.edad}",
                    onInfoWindowClick = {
                        onNavigateToDetail(mascota.id, mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl)
                    }
                )
            }
        }
    }
}
