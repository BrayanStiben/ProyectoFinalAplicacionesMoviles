package com.example.seguimiento.features.EsperandoPorTi

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.Dominio.modelos.Comentario
import com.example.seguimiento.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Composable
fun EstaEsperandoPorTiScreen(
    id: String = "",
    nombre: String = "",
    edad: String = "",
    ubicacion: String = "",
    url: String = "",
    miViewModel: EstaEsperandoViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRequisitos: () -> Unit = {}
) {
    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            miViewModel.seleccionarMascota(id)
        }
    }

    val mascota by miViewModel.mascota.collectAsState()
    val comentarios by miViewModel.comentarios.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var nuevoComentarioTexto by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNav(
                selectedItem = -1,
                onItemSelected = { index ->
                    when(index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToFiltros()
                        3 -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Image(
                painter = painterResource(id = R.drawable.fondo3),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    IconButton(
                        onClick = { onNavigateToHome() },
                        modifier = Modifier.padding(16.dp).background(Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }

                    HeaderSection()

                    Box(modifier = Modifier.offset(y = (-80).dp)) {
                        MascotaCard(
                            nombre = mascota?.nombre ?: nombre,
                            edad = mascota?.edad ?: edad,
                            ubicacion = mascota?.ubicacion ?: ubicacion,
                            imagenUrl = mascota?.imagenUrl ?: url,
                            onLike = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("¡Te ha gustado ${mascota?.nombre ?: nombre}! Añadido a favoritos.")
                                }
                            }
                        )
                    }
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth().offset(y = (-80).dp),
                        color = Color(0xFFFDF7E7).copy(alpha = 0.95f),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                "Descripción",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF386641)
                            )
                            Text(
                                mascota?.descripcion ?: "Sin descripción disponible.",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                "Ubicación Exacta",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF386641)
                            )
                            // Placeholder para Mapa
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Map, null, modifier = Modifier.size(40.dp), tint = Color.Gray)
                                    Text("Mapa de Google Maps aquí", color = Color.Gray)
                                    Text("Lat: ${mascota?.lat ?: 0.0}, Lng: ${mascota?.lng ?: 0.0}", fontSize = 10.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                "Comentarios",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF386641)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = nuevoComentarioTexto,
                                    onValueChange = { nuevoComentarioTexto = it },
                                    placeholder = { Text("Escribe un comentario...") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                IconButton(onClick = {
                                    if (nuevoComentarioTexto.isNotBlank()) {
                                        miViewModel.agregarComentario(nuevoComentarioTexto, "userId", "Usuario Demo")
                                        nuevoComentarioTexto = ""
                                    }
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color(0xFFE67E22))
                                }
                            }
                        }
                    }
                }

                items(comentarios) { comentario ->
                    ComentarioItem(comentario)
                }

                item {
                    Column(modifier = Modifier.padding(24.dp).offset(y = (-80).dp)) {
                        Button(
                            onClick = { onNavigateToRequisitos() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Iniciar Adopción", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ComentarioItem(comentario: Comentario) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp).offset(y = (-80).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(comentario.autorNombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE67E22))
            Text(comentario.contenido, fontSize = 14.sp)
        }
    }
}

@Composable
fun MascotaCard(nombre: String, edad: String, ubicacion: String, imagenUrl: String, onLike: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imagenUrl,
                contentDescription = nombre,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("$edad • $ubicacion", fontSize = 14.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE76F51), modifier = Modifier.size(14.dp))
                    Text(ubicacion, fontSize = 12.sp, color = Color.Gray)
                }
            }

            IconButton(
                onClick = onLike,
                modifier = Modifier.clip(CircleShape).background(Color(0xFFFEEAE6))
            ) {
                Icon(Icons.Default.Favorite, null, tint = Color(0xFFE76F51))
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.petadopticono),
            contentDescription = "Logo PetAdopta",
            modifier = Modifier.fillMaxWidth().height(380.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "Está esperando por ti",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().offset(y = (-110).dp)
        )
    }
}

@Composable
fun BottomNav(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val NaranjaNav = Color(0xFFE67E22) 
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
                label = { Text(label, fontSize = 10.sp) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NaranjaNav,
                    selectedTextColor = NaranjaNav,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFFFFF4C2)
                )
            )
        }
    }
}
