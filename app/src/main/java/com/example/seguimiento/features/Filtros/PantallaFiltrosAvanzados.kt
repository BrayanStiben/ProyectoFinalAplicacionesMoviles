package com.example.seguimiento.features.Filtros

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.* 
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.roundToInt

// Colores personalizados
val NaranjaPrincipal = Color(0xFFE67E22)
val FondoCrema = Color(0xFFFEF9E7)
val GrisChip = Color(0xFFE5D3B3)
val TextoMarron = Color(0xFF5D4037)
val VerdeLupa = Color(0xFF8BC34A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFiltrosAvanzado(
    modelo: FiltroViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BottomNav(selectedItem = 1) { index ->
                when(index) {
                    0 -> onNavigateToHome()
                    1 -> onNavigateToFiltros()
                    3 -> onNavigateToProfile()
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NaranjaPrincipal)
                .padding(padding)
        ) {
            // Encabezado
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onNavigateToHome() },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Filtrar Búsqueda Avanzada", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            // Cuerpo con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(FondoCrema)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Chips de Animales
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipItem("Perros", modelo.tipoMascotaSeleccionado == "Perros") { modelo.tipoMascotaSeleccionado = "Perros" }
                    ChipItem("Gatos", modelo.tipoMascotaSeleccionado == "Gatos") { modelo.tipoMascotaSeleccionado = "Gatos" }
                    ChipItem("Otros", modelo.tipoMascotaSeleccionado == "Otros") { modelo.tipoMascotaSeleccionado = "Otros" }
                }

                // Buscador de Raza
                OutlinedTextField(
                    value = modelo.razaSeleccionada,
                    onValueChange = { modelo.razaSeleccionada = it },
                    placeholder = { Text("Seleccionar Raza") },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).background(Color.White, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Box(Modifier.size(36.dp).background(VerdeLupa, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Search, null, tint = Color.White)
                        }
                    }
                )

                SeccionTitulo("Edad:")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipItem("Cachorro", modelo.categoriaEdad == "Cachorro") { modelo.categoriaEdad = "Cachorro" }
                    ChipItem("Adulto", modelo.categoriaEdad == "Adulto") { modelo.categoriaEdad = "Adulto" }
                    ChipItem("Senior", modelo.categoriaEdad == "Senior") { modelo.categoriaEdad = "Senior" }
                }

                // --- TAMAÑO POSITIVO DINÁMICO ---
                Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tamaño (kg):", color = TextoMarron, fontWeight = FontWeight.Bold)
                    Text("${modelo.rangoPeso.start.roundToInt()} - ${modelo.rangoPeso.endInclusive.roundToInt()} kg", color = NaranjaPrincipal, fontWeight = FontWeight.ExtraBold)
                }
                RangeSlider(
                    value = modelo.rangoPeso,
                    onValueChange = { modelo.actualizarRangoPeso(it) },
                    valueRange = 1f..50f,
                    colors = SliderDefaults.colors(thumbColor = NaranjaPrincipal, activeTrackColor = NaranjaPrincipal)
                )

                // --- SIMULADOR GPS ---
                Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Ubicación (Radio):", color = TextoMarron, fontWeight = FontWeight.Bold)
                    Text("${modelo.radioGps.roundToInt()} km", color = NaranjaPrincipal, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFC5E1A5)),
                    contentAlignment = Alignment.Center
                ) {
                    // Círculo de radar que crece/encoge con el slider
                    Box(
                        modifier = Modifier
                            .size((modelo.radioGps * 4).coerceIn(40f, 130f).dp)
                            .background(AzulGps.copy(alpha = 0.2f), CircleShape)
                            .border(2.dp, AzulGps, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MyLocation, null, tint = AzulGps)
                    }
                }
                Slider(
                    value = modelo.radioGps,
                    onValueChange = { modelo.radioGps = it },
                    valueRange = 1f..50f,
                    colors = SliderDefaults.colors(thumbColor = NaranjaPrincipal, activeTrackColor = NaranjaPrincipal)
                )

                SeccionTitulo("Características:")
                FilaOpcion("Vacunado", modelo.estaVacunado) { modelo.estaVacunado = it }
                FilaOpcion("Esterilizado", modelo.estaEsterilizado) { modelo.estaEsterilizado = it }
                FilaOpcion("Juguetón", modelo.esJugueton) { modelo.esJugueton = it }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { onNavigateToHome() },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrincipal)
                ) {
                    Text("Aplicar Filtros", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }

                TextButton(onClick = { modelo.limpiarFiltros() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Limpiar Filtros", color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun SeccionTitulo(texto: String) {
    Text(texto, color = TextoMarron, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
}

@Composable
fun ChipItem(texto: String, seleccionado: Boolean, alClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { alClick() }.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (seleccionado) NaranjaPrincipal else GrisChip
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(texto, color = if (seleccionado) Color.White else TextoMarron, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FilaOpcion(texto: String, marcado: Boolean, alCambiar: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { alCambiar(!marcado) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(24.dp).border(2.dp, TextoMarron, RoundedCornerShape(4.dp)).background(Color.White), contentAlignment = Alignment.Center) {
            if (marcado) Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp), tint = TextoMarron)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(texto, color = TextoMarron)
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
