package com.example.seguimiento.features.MiPerfil

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.R

val NaranjaApp = Color(0xFFE67E22)
val VerdeApp = Color(0xFF4CAF50)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val user = state.user

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
                BottomNav(selectedItem = 3) { index ->
                    when(index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToFiltros()
                        3 -> onNavigateToProfile()
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Header con Foto y Datos Básicos
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(3.dp, NaranjaApp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (user?.profilePictureUrl?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = user.profilePictureUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = NaranjaApp)
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(user?.name ?: "Usuario", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(user?.getLevelName() ?: "Novato", fontSize = 16.sp, color = Color.White.copy(0.9f))
                        
                        Spacer(Modifier.height(20.dp))

                        // Dashboard de Estadísticas Personales
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard("Activas", state.activePosts.toString(), Color(0xFF2196F3), Modifier.weight(1f))
                            StatCard("Finalizadas", state.resolvedPosts.toString(), VerdeApp, Modifier.weight(1f))
                            StatCard("Pendientes", state.pendingPosts.toString(), Color(0xFFFFC107), Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(20.dp))
                        
                        Text("Puntos: ${user?.points ?: 0} 🏆", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        
                        Spacer(Modifier.height(16.dp))
                        Text("Mis Publicaciones", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                    }
                }

                items(state.userPosts) { mascota ->
                    UserPostItem(
                        mascota = mascota,
                        onResolve = { viewModel.resolvePost(mascota.id) },
                        onDelete = { viewModel.deletePost(mascota.id) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.9f))
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

@Composable
fun UserPostItem(mascota: Mascota, onResolve: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.95f))
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = mascota.imagenUrl,
                contentDescription = null,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(mascota.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(mascota.estado.name, color = when(mascota.estado) {
                    PublicacionEstado.VERIFICADA -> VerdeApp
                    PublicacionEstado.PENDIENTE -> Color(0xFFFFC107)
                    PublicacionEstado.RECHAZADA -> Color.Red
                    PublicacionEstado.RESUELTA -> Color.Gray
                }, fontSize = 12.sp)
            }
            
            if (mascota.estado == PublicacionEstado.VERIFICADA) {
                IconButton(onClick = onResolve) {
                    Icon(Icons.Default.CheckCircle, "Finalizar", tint = VerdeApp)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
            }
        }
    }
}

@Composable
fun BottomNav(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            Triple("Inicio", Icons.Default.Home, 0),
            Triple("Buscar", Icons.Default.Search, 1),
            Triple("Favs", Icons.Default.FavoriteBorder, 2),
            Triple("Perfil", Icons.Default.Person, 3)
        )
        items.forEach { (titulo, icono, indice) ->
            NavigationBarItem(
                selected = selectedItem == indice,
                onClick = { onItemSelected(indice) },
                icon = { Icon(icono, contentDescription = titulo) },
                label = { Text(titulo, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NaranjaApp,
                    selectedTextColor = NaranjaApp,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFFFFF4C2)
                )
            )
        }
    }
}
