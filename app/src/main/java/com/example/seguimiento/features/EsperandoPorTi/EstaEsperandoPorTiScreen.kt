package com.example.seguimiento.features.EsperandoPorTi

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R
import kotlinx.coroutines.launch

@Composable
fun EstaEsperandoPorTiScreen(
    miViewModel: EstaEsperandoViewModel = viewModel()
) {
    val mascota: Mascota by miViewModel.mascotaDestacada.collectAsState()
    val tabSeleccionada by miViewModel.tabSeleccionada.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNav(
                selectedItem = tabSeleccionada,
                onItemSelected = { miViewModel.actualizarTab(it) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF6A994E))
                .verticalScroll(rememberScrollState())
        ) {
            // --- CABECERA CON LOGO GIGANTE Y TEXTOS ---
            HeaderSection()

            // --- TARJETA DE MASCOTA ---
            Box(modifier = Modifier.offset(y = (-80).dp)) {
                MascotaCard(mascota) {
                    scope.launch {
                        snackbarHostState.showSnackbar("¡Te ha gustado ${mascota.nombre}! Se ha añadido a tus favoritos.")
                    }
                }
            }

            // --- ESPACIO ENTRE TARJETA Y SECCIÓN INFERIOR ---
            Spacer(modifier = Modifier.height(20.dp).offset(y = (-80).dp))

            // --- SECCIÓN INFERIOR (HISTORIAS) ---
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-80).dp),
                color = Color(0xFFFDF7E7),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Últimas Historias Felices",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF386641)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth().height(180.dp).padding(vertical = 12.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600",
                                contentDescription = "Gato adoptado",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    "¡Encontré a Max en PetAdopta! Ahora es el rey de la casa.",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "¿Interesado en adoptar?",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color(0xFFBC6C25),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    
                    Text(
                        "¿Estás listo para conocer a tu nuevo amigo?",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { miViewModel.ejecutarBusqueda() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Buscar Mascota", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
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
                    selectedIconColor = Color(0xFFF37021), // NaranjaApp
                    selectedTextColor = Color(0xFFF37021),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFFFFF4C2)
                )
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LOGO GIGANTE
        Image(
            painter = painterResource(id = R.drawable.petadopticono),
            contentDescription = "Logo PetAdopta",
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp),
            contentScale = ContentScale.Fit
        )
        
        // Texto principal
        Text(
            text = "Está esperando por ti",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-110).dp)
        )
    }
}

@Composable
fun MascotaCard(mascota: Mascota, onLike: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = mascota.imagenUrl,
                contentDescription = mascota.nombre,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(mascota.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(mascota.edad, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE76F51), modifier = Modifier.size(14.dp))
                    Text(mascota.ubicacion, fontSize = 14.sp, color = Color.Gray)
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE76F51))
                    .clickable { onLike() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Favorite, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}