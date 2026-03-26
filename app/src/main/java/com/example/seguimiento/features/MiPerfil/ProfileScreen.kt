package com.example.seguimiento.features.MiPerfil

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

// Colores consistentes
val ColorNaranjaApp = Color(0xFFE67E22)
val ColorTurquesaApp = Color(0xFF439191)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val nombre by viewModel.nombre.collectAsState()
    val apellido by viewModel.apellido.collectAsState()
    val ubicacion by viewModel.ubicacion.collectAsState()
    val fotoUri by viewModel.fotoUri.collectAsState()
    val itemSeleccionado by viewModel.itemSeleccionado.collectAsState()

    val esPerro by viewModel.esPerro.collectAsState()
    val esGato by viewModel.esGato.collectAsState()
    val esOtro by viewModel.esOtro.collectAsState()
    val otroTexto by viewModel.otroTexto.collectAsState()

    // Selector de Galería
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> viewModel.actualizarFoto(uri) }

    // Permisos de GPS (Se solicitan ambos para mayor precisión)
    val launcherPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val fineLocationGarantizado = permisos[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGarantizado = permisos[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGarantizado || coarseLocationGarantizado) {
            viewModel.obtenerUbicacionActual(context)
        }
    }

    Scaffold(
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
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF2F2F2))) {

            // Header
            Row(modifier = Modifier.fillMaxWidth().background(ColorNaranjaApp).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onNavigateToHome() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Mi Perfil", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

                    // FOTO DE PERFIL
                    Text("Foto de Perfil", fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier.size(100.dp).padding(8.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable { launcherGaleria.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoUri != null) {
                            AsyncImage(model = fotoUri, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                        } else {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CajaTextoEditable("Nombre", nombre) { viewModel.actualizarNombre(it) }
                    CajaTextoEditable("Apellido", apellido) { viewModel.actualizarApellido(it) }

                    // UBICACIÓN (GPS)
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("Ubicación Actual", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        OutlinedTextField(
                            value = ubicacion, onValueChange = {}, readOnly = true,
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    launcherPermisos.launch(arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )) 
                                }) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = ColorNaranjaApp)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tipo de Mascota", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))

                    // CHECKBOXES (Perro, Gato, Otro)
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        FilaCheck("Perro", esPerro) { viewModel.togglePerro(it) }
                        FilaCheck("Gato", esGato) { viewModel.toggleGato(it) }
                        FilaCheck("Otro", esOtro) { viewModel.toggleOtro(it) }
                    }

                    // Campo extra si elige "Otro"
                    if (esOtro) {
                        CajaTextoEditable("¿Qué mascota tienes?", otroTexto) { viewModel.actualizarOtroTexto(it) }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BOTÓN GUARDAR
                    Button(
                        onClick = { viewModel.guardarCambios() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorNaranjaApp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CajaTextoEditable(label: String, valor: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OutlinedTextField(value = valor, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
    }
}

@Composable
fun FilaCheck(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, colors = CheckboxDefaults.colors(checkedColor = ColorTurquesaApp))
        Text(label, fontSize = 14.sp)
    }
}

@Composable
fun BottomNav(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val ColorNaranjaApp = Color(0xFFE67E22)
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
                    selectedIconColor = ColorNaranjaApp,
                    selectedTextColor = ColorNaranjaApp,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFFFFF4C2)
                )
            )
        }
    }
}
