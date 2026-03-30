package com.example.seguimiento.features.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.R

val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMascotaDestacada: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onNavigateToNutricion: () -> Unit = {},
    onNavigateToRequisitos: () -> Unit = {},
    onNavigateToRefugios: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToHistorias: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {},
    onNavigateToMapa: () -> Unit = {}
) {
    val userName by viewModel.userName.collectAsState()
    val mascotasFeed by viewModel.mascotasFeed.collectAsState()
    val mascotasRecomendadas by viewModel.mascotasRecomendadas.collectAsState()
    val selectedItem by viewModel.selectedNavItem.collectAsState()
    val filtroCategoria by viewModel.filtroCategoria.collectAsState()

    val categorias = listOf("Todos", "Perro", "Gato", "Otro")

    Scaffold(
        bottomBar = {
            BottomNav(selectedItem) { index -> 
                viewModel.onNavItemClicked(index)
                if (index == 3) onNavigateToProfile()
                if (index == 1) onNavigateToFiltros()
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToMapa, containerColor = NaranjaApp) {
                Icon(Icons.Default.Map, "Ver en Mapa", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFFDFBFA))
        ) {
            // --- HEADER ---
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
                        Text("Hola, $userName 🐾", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text("¡Encuentra tu mascota ideal!", color = Color.White.copy(0.9f), fontSize = 16.sp)
                    }
                    
                    IconButton(onClick = onNavigateToNotificaciones) {
                        Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    Spacer(Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                }
            }

            // --- CATEGORÍAS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CategoryItem("Refugios", Color(0xFF00ACC1), Icons.Default.Apartment) { onNavigateToRefugios() }
                CategoryItem("Nutrición", Color(0xFFFB8C00), Icons.AutoMirrored.Filled.MenuBook) { onNavigateToNutricion() }
                CategoryItem("Requisitos", Color(0xFFE53935), Icons.AutoMirrored.Filled.ListAlt) { onNavigateToRequisitos() }
                CategoryItem("Filtros", Color(0xFF7CB342), Icons.Default.Tune) { onNavigateToFiltros() }
            }

            // --- FILTRO DE FEED ---
            Text(
                "Explorar Publicaciones",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(categorias) { cat ->
                    val isSelected = (filtroCategoria == cat) || (filtroCategoria == null && cat == "Todos")
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.filtrarPorCategoria(if(cat == "Todos") null else cat) },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaranjaApp,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // --- FEED DE PUBLICACIONES ---
            if (mascotasFeed.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Text("No hay publicaciones en esta categoría", color = Color.Gray)
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    mascotasFeed.forEach { mascota ->
                        FeedCard(
                            mascota = mascota,
                            onVotar = { viewModel.votarImportante(mascota.id) },
                            onClick = { onNavigateToMascotaDestacada(mascota.id, mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl) }
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            // --- SECCIÓN RECOMENDADOS (DESTACADOS) ---
            Text(
                "Recomendados para ti",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 16.dp, bottom = 20.dp)
            ) {
                mascotasRecomendadas.forEach { mascota ->
                    PetCard(mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl) {
                        onNavigateToMascotaDestacada(mascota.id, mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl)
                    }
                }
            }

            // --- HISTORIAS FELICES ---
            Text(
                "Historias Felices",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )

            HappyStoryCard { onNavigateToHistorias() }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun FeedCard(mascota: Mascota, onVotar: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = mascota.imagenUrl,
                contentDescription = null,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(mascota.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${mascota.tipo} • ${mascota.ubicacion}", fontSize = 14.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text("${mascota.votosImportante} votos", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }
            IconButton(onClick = onVotar) {
                Icon(Icons.Default.Favorite, "Es importante", tint = Color.Red)
            }
        }
    }
}

@Composable
fun CategoryItem(label: String, color: Color, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(15.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            color = color,
            shape = RoundedCornerShape(15.dp),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Text(
            text = label,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp),
            color = Color.Gray
        )
    }
}

@Composable
fun PetCard(nombre: String, edad: String, ciudad: String, url: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .width(160.dp)
            .padding(end = 15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.petadopticono),
                error = painterResource(id = R.drawable.petadopticono)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$edad • $ciudad", fontSize = 12.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(10.dp) )
                
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Adoptar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HappyStoryCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp)
    ) {
        Box {
            AsyncImage(
                model = "https://images.pexels.com/photos/4587995/pexels-photo-4587995.jpeg",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.petadopticono),
                error = painterResource(id = R.drawable.petadopticono)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(0.7f))
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "¡Max encontró hogar!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Surface(
                    color = Color(0xFFFF6D00),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Ver más",
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
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

        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                icon = { Icon(icon, null) },
                label = { Text(label) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
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
