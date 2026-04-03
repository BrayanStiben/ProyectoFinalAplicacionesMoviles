package com.example.seguimiento.features.Notificaciones

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.DeleteSweep
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.R
import com.example.seguimiento.features.home.BottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    viewModel: NotificacionesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val notificaciones by viewModel.notificaciones.collectAsState()
    val naranjaApp = Color(0xFFE67E22)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("NOTIFICACIONES 🔔", fontWeight = FontWeight.Black, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.limpiarTodo() }) {
                            Icon(Icons.Default.DeleteSweep, "Limpiar todo", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = naranjaApp)
                )
            },
            bottomBar = {
                BottomNav(
                    selectedItem = -1,
                    onNavigateToHome = onNavigateToHome,
                    onNavigateToFiltros = onNavigateToFiltros,
                    onNavigateToFavoritos = onNavigateToFavoritos,
                    onNavigateToProfile = onNavigateToProfile
                )
            }
        ) { padding ->
            if (notificaciones.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                    ) {
                        Text(
                            "No tienes notificaciones nuevas", 
                            modifier = Modifier.padding(24.dp),
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notificaciones) { notif ->
                        NotificacionItem(notif) {
                            viewModel.marcarComoLeida(notif.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificacionItem(notificacion: Notificacion, onRead: () -> Unit) {
    val naranjaApp = Color(0xFFE67E22)
    
    Card(
        onClick = onRead,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida) Color.White.copy(alpha = 0.7f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (notificacion.leida) 1.dp else 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (notificacion.leida) Color.LightGray.copy(alpha = 0.2f) else naranjaApp.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (notificacion.tipo == "ZONA_NUEVA") Icons.Default.Place else Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (notificacion.leida) Color.Gray else naranjaApp,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    notificacion.titulo, 
                    fontWeight = if (notificacion.leida) FontWeight.Bold else FontWeight.Black, 
                    fontSize = 16.sp,
                    color = if (notificacion.leida) Color.Gray else Color.Black
                )
                Text(
                    notificacion.mensaje, 
                    fontSize = 14.sp, 
                    color = if (notificacion.leida) Color.Gray.copy(alpha = 0.8f) else Color.DarkGray
                )
            }
            
            if (!notificacion.leida) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(naranjaApp)
                )
            }
        }
    }
}
