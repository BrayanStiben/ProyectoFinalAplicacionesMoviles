package com.example.seguimiento.features.Refugios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.R

// --- COLORES ---
val CieloGradient = Brush.verticalGradient(listOf(Color(0xFF81D4FA), Color(0xFFB3E5FC)))
val FondoCard = Color(0xFFFDF7E7)
val VerdeApp = Color(0xFF7CB342)
val RojoApp = Color(0xFFE53935)
val AzulDoc = Color(0xFF4FC3F7)
val NaranjaApp = Color(0xFFF37021)

@Composable
fun RefugiosScreen(viewModel: RefugioViewModel = viewModel()) {
    val listaRefugios by viewModel.refugios.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNav(
                selectedItem = viewModel.tabSeleccionada,
                onItemSelected = { viewModel.tabSeleccionada = it }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CieloGradient)
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // LOGO GIGANTE
                    Image(
                        painter = painterResource(id = R.drawable.petadopticono),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxWidth().height(380.dp),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        "Gestión de Refugios",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        ),
                        modifier = Modifier.offset(y = (-110).dp)
                    )
                }

                // SECCIÓN VERIFICADOS
                item {
                    Box(modifier = Modifier.offset(y = (-100).dp)) {
                        SeccionTitulo("Refugios Verificados")
                    }
                }
                items(listaRefugios.filter { !it.esNuevo }) { refugio ->
                    Box(modifier = Modifier.offset(y = (-100).dp)) {
                        TarjetaRefugio(refugio, viewModel)
                    }
                }

                // SECCIÓN NUEVOS
                item {
                    Box(modifier = Modifier.offset(y = (-100).dp)) {
                        SeccionTitulo("Nuevas Aplicaciones")
                    }
                }
                items(listaRefugios.filter { it.esNuevo }) { refugio ->
                    Box(modifier = Modifier.offset(y = (-100).dp)) {
                        TarjetaRefugio(refugio, viewModel)
                    }
                }

                item { Spacer(Modifier.height(40.dp)) }
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

@Composable
fun TarjetaRefugio(refugio: Refugio, viewModel: RefugioViewModel) {
    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = FondoCard),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(50.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Pets, null, tint = if (refugio.verificado) VerdeApp else Color.Gray)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(refugio.nombre, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(refugio.estado, fontSize = 13.sp, color = Color.Gray)
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.End) {
                if (!refugio.verificado) {
                    BotonM3("Aprobar", VerdeApp) { viewModel.aprobarRefugio(refugio.id) }
                } else {
                    BotonM3("Ver Documentos", AzulDoc) {}
                }
            }
        }
    }
}

@Composable
fun SeccionTitulo(titulo: String) {
    Text(titulo, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color(0xFF1B5E20),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp))
}

@Composable
fun BotonM3(texto: String, color: Color, onClick: () -> Unit) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)) {
        Text(texto, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}