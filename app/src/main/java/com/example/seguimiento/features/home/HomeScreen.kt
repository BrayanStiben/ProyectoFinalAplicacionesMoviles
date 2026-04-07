package com.example.seguimiento.features.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
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
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToHistorias: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {},
    onNavigateToMapa: () -> Unit = {},
    onNavigateToRegistroMascota: () -> Unit = {},
    onNavigateToLogros: () -> Unit = {},
    onNavigateToTienda: () -> Unit = {},
    onNavigateToMisAdopciones: () -> Unit = {}
) {
    val userName by viewModel.userName.collectAsState()
    val userProfilePicture by viewModel.userProfilePicture.collectAsState()
    val mascotasFeed by viewModel.mascotasFeed.collectAsState()
    val mascotasRecomendadas by viewModel.mascotasRecomendadas.collectAsState()
    val filtroCategoria by viewModel.filtroCategoria.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val notificaciones by viewModel.notificaciones.collectAsState()

    val categorias = listOf("Todos", "Perro", "Gato", "Otro")

    Scaffold(
        bottomBar = {
            BottomNav(
                selectedItem = 0,
                onNavigateToHome = { /* Ya estamos aqui */ },
                onNavigateToFiltros = onNavigateToFiltros,
                onNavigateToFavoritos = onNavigateToFavoritos,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRegistroMascota, 
                containerColor = NaranjaApp,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Registrar Mascota")
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
            // --- HEADER LIMPIO ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 45.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hola, $userName 🐾", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("¡Encuentra tu mascota ideal!", color = Color.White.copy(0.9f), fontSize = 14.sp)
                        Text("${currentUser?.points ?: 0} pts", color = Color.White.copy(0.8f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    
                    IconButton(onClick = onNavigateToLogros) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    IconButton(onClick = onNavigateToNotificaciones) {
                        BadgedBox(badge = {
                            if (notificaciones.any { !it.leida }) {
                                Badge(containerColor = Color.Red)
                            }
                        }) {
                            Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(48.dp)
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
                            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }

            // --- CATEGORÍAS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 25.dp, bottom = 20.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CategoryItem("Refugios", Color(0xFF00ACC1), Icons.Default.Apartment) { onNavigateToRefugios() }
                CategoryItem("Tienda", Color(0xFFFF9800), Icons.Default.Storefront) { onNavigateToTienda() }
                CategoryItem("Nutrición", Color(0xFFFB8C00), Icons.AutoMirrored.Filled.MenuBook) { onNavigateToNutricion() }
                CategoryItem("Filtros", Color(0xFF7CB342), Icons.Default.Tune) { onNavigateToFiltros() }
                CategoryItem("Citas", Color(0xFF2196F3), Icons.Default.CalendarMonth) { onNavigateToMisAdopciones() }
            }

            // --- RESTO DEL CONTENIDO ---
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

            if (mascotasFeed.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Text("No hay publicaciones en esta categoría", color = Color.Gray)
                }
            } else {
                val pagerState = rememberPagerState(pageCount = { mascotasFeed.size })
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().height(130.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    pageSpacing = 12.dp
                ) { page ->
                    val mascota = mascotasFeed[page]
                    FeedCard(
                        mascota = mascota,
                        currentUserId = currentUser?.id ?: "",
                        onLike = { viewModel.toggleLike(mascota.id) },
                        onClick = { onNavigateToMascotaDestacada(mascota.id, mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl) }
                    )
                }
            }

            Text(
                "Recomendados para ti",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(start = 16.dp, bottom = 20.dp)
            ) {
                mascotasRecomendadas.forEach { mascota ->
                    PetCard(mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl) {
                        onNavigateToMascotaDestacada(mascota.id, mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl)
                    }
                }
            }

            Text("Historias Felices", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, bottom = 12.dp))
            HappyStoryCard { onNavigateToHistorias() }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun FeedCard(mascota: Mascota, currentUserId: String, onLike: () -> Unit, onClick: () -> Unit) {
    val isLiked = mascota.likerIds.contains(currentUserId)
    val otherLikes = if (isLiked) mascota.totalLikes - 1 else mascota.totalLikes
    val textLikes = when {
        isLiked && otherLikes > 0 -> "Tú y $otherLikes personas"
        isLiked -> "Tú"
        otherLikes > 0 -> "$otherLikes personas"
        else -> "Sin likes aún"
    }

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
                Text("${mascota.tipo} • ${mascota.ubicacion}", fontSize = 14.sp, color = Color.Gray, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Text(textLikes, fontSize = 12.sp, color = NaranjaApp, fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onLike) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Gray
                )
            }
        }
    }
}

@Composable
fun CategoryItem(label: String, color: Color, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp).clip(RoundedCornerShape(15.dp)).clickable { onClick() }.padding(8.dp)
    ) {
        Surface(modifier = Modifier.size(60.dp), color = color, shape = RoundedCornerShape(15.dp), shadowElevation = 4.dp) {
            Box(contentAlignment = Alignment.Center) { Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(28.dp)) }
        }
        Text(text = label, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp), color = Color.Gray)
    }
}

@Composable
fun PetCard(nombre: String, edad: String, ciudad: String, url: String, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.width(160.dp).padding(end = 15.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
        Column {
            AsyncImage(model = url, contentDescription = null, modifier = Modifier.height(130.dp).fillMaxWidth(), contentScale = ContentScale.Crop, placeholder = painterResource(id = R.drawable.petadopticono), error = painterResource(id = R.drawable.petadopticono))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$edad • $ciudad", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp) )
                Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(36.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)), contentPadding = PaddingValues(0.dp), shape = RoundedCornerShape(10.dp)) { Text("Adoptar", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun HappyStoryCard(onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(180.dp).clickable { onClick() }, shape = RoundedCornerShape(20.dp)) {
        Box {
            AsyncImage(model = "https://images.pexels.com/photos/4587995/pexels-photo-4587995.jpeg", contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, placeholder = painterResource(id = R.drawable.petadopticono), error = painterResource(id = R.drawable.petadopticono))
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))))
            Row(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("¡Max encontró hogar!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Surface(color = Color(0xFFFF6D00), shape = RoundedCornerShape(8.dp)) { Text("Ver más", fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) }
            }
        }
    }
}

@Composable
fun BottomNav(selectedItem: Int, onNavigateToHome: () -> Unit = {}, onNavigateToFiltros: () -> Unit = {}, onNavigateToFavoritos: () -> Unit = {}, onNavigateToProfile: () -> Unit = {}) {
    val naranjaNav = Color(0xFFE67E22)
    NavigationBar(containerColor = Color.White) {
        val items = listOf(Triple("Inicio", Icons.Default.Home, 0), Triple("Buscar", Icons.Default.Search, 1), Triple("Favs", Icons.Default.FavoriteBorder, 2), Triple("Perfil", Icons.Default.Person, 3))
        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                icon = { Icon(icon, null) },
                label = { Text(label) },
                selected = selectedItem == index,
                onClick = { when(index) { 0 -> onNavigateToHome(); 1 -> onNavigateToFiltros(); 2 -> onNavigateToFavoritos(); 3 -> onNavigateToProfile() } },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = naranjaNav, selectedTextColor = naranjaNav, unselectedIconColor = Color.Gray, indicatorColor = Color(0xFFFFF4C2))
            )
        }
    }
}
