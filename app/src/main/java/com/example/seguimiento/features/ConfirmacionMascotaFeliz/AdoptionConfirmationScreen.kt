package com.example.seguimiento.features.ConfirmacionMascotaFeliz

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R

// Paleta de colores oficial
val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)
val FondoCrema = Color(0xFFFDF7E7)
val VerdeExito = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptionConfirmationScreen(viewModel: AdoptionViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var tabSeleccionada by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NaranjaApp,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { /* Back */ },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    
                    Text(
                        text = "ADOPCIÓN EXITOSA", 
                        color = Color.White, 
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        bottomBar = {
            BottomNav(tabSeleccionada) { tabSeleccionada = it }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // 1. FONDO DE PANTALLA (fondo3)
            Image(
                painter = painterResource(id = R.drawable.fondo3),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay sutil
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.1f)))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 2. IMAGEN "FELIZ" (PNG SIN FONDO)
                Image(
                    painter = painterResource(id = R.drawable.feliz),
                    contentDescription = "Mascota Feliz",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(top = 20.dp),
                    contentScale = ContentScale.Fit
                )

                // ESPACIO CORREGIDO: Reducido para que la caja suba más
                Spacer(modifier = Modifier.height(10.dp))

                // TARJETA DE DETALLES PREMIUM
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Miniatura redonda con IMAGEN DE INTERNET (Gatito naranja de la foto)
                            Surface(
                                modifier = Modifier.size(75.dp),
                                shape = CircleShape,
                                border = BorderStroke(3.dp, NaranjaApp)
                            ) {
                                AsyncImage(
                                    model = "https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&q=80&w=300",
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(uiState.petName, fontWeight = FontWeight.Black, fontSize = 24.sp, color = CafeApp)
                                Text("¡Ya está en casa!", fontSize = 14.sp, color = Color.Gray)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

                        FilaDetalle(Icons.Default.CalendarToday, "Fecha", uiState.date)
                        FilaDetalle(Icons.Default.Storefront, "Refugio", uiState.shelter)
                        FilaDetalle(Icons.Default.FamilyRestroom, "Adoptado por", uiState.adoptedBy)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.onHistoryClick() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NaranjaApp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Default.HistoryEdu, null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("VER MI HISTORIA", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }

                        TextButton(
                            onClick = { viewModel.onShareClick() },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Compartir alegría 🐾", color = NaranjaApp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun FilaDetalle(icono: ImageVector, label: String, valor: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, null, tint = NaranjaApp, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text("$label: ", color = Color.Gray, fontSize = 14.sp)
        Text(valor, color = CafeApp, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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