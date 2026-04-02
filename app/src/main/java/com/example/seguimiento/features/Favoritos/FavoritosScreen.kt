package com.example.seguimiento.features.Favoritos

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.seguimiento.features.home.BottomNav

@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToDetail: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    val mascotasFavoritas by viewModel.mascotasFavoritas.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    val userName = currentUser?.name ?: "Usuario"
    val userProfilePicture = currentUser?.profilePictureUrl

    Scaffold(
        bottomBar = {
            BottomNav(
                selectedItem = 2,
                onNavigateToHome = onNavigateToHome,
                onNavigateToFiltros = onNavigateToFiltros,
                onNavigateToFavoritos = { /* Ya estamos aquí */ },
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFDFBFA)) // Fondo consistente con Home
        ) {
            // --- HEADER (ESTILO HOME) ---
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tus favoritos, $userName ❤️", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("¡Encuentra el amor de tu vida!", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                    }

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (userProfilePicture?.isNotEmpty() == true) {
                            AsyncImage(
                                model = userProfilePicture,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (mascotasFavoritas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.petadopticono),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp).alpha(0.3f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("No tienes favoritos aún", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    items(mascotasFavoritas) { mascota ->
                        FavoritePetCard(
                            mascota = mascota,
                            currentUserId = currentUser?.id ?: "",
                            onLikeClick = { viewModel.toggleLike(mascota.id) },
                            onDetailClick = {
                                onNavigateToDetail(
                                    mascota.id, mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritePetCard(
    mascota: Mascota,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    val isLiked = mascota.likerIds.contains(currentUserId)
    val otherLikes = if (isLiked) mascota.totalLikes - 1 else mascota.totalLikes
    val isAdopted = mascota.estado == PublicacionEstado.ADOPTADA
    
    val textLikes = when {
        isLiked && otherLikes > 0 -> "Tú y $otherLikes personas"
        isLiked -> "Tú"
        otherLikes > 0 -> "$otherLikes personas"
        else -> ""
    }

    val bgColors = listOf(Color(0xFFB2EBF2), Color(0xFFE1BEE7), Color(0xFFC8E6C9), Color(0xFFFFF9C4))
    val bgColor = remember(mascota.id) { bgColors.random() }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(bgColor)
                    .clickable { onDetailClick() }
            ) {
                AsyncImage(
                    model = mascota.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                if (isAdopted) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "ADOPTADO",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = mascota.nombre.uppercase(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Text(
                    text = "(${mascota.edad})",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Home, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text(mascota.ubicacion, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                    }
                    
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isLiked) Color(0xFFFF5722) else Color.Gray
                        )
                    }
                }

                if (textLikes.isNotEmpty()) {
                    Text(
                        text = textLikes,
                        fontSize = 10.sp,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.fillMaxWidth().height(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAdopted) Color(0xFF4CAF50) else Color(0xFF0097A7)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        if (isAdopted) "Adoptado ✓" else "¡Adoptame!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
