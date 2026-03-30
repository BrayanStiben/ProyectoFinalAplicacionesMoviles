package com.example.seguimiento.features.IngresarMascota

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroMascota(
    viewModel: MascotaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsState()
    val selector = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.alSeleccionarFoto(it)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐶", fontSize = 22.sp) 
                        Spacer(Modifier.width(8.dp))
                        Text("Ingresar Mascota", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("🐱", fontSize = 22.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFE67E22))
            )
        },
        bottomBar = {
            BottomNav(selectedItem = 0) { index ->
                when(index) {
                    0 -> onNavigateToHome()
                    1 -> onNavigateToFiltros()
                    3 -> onNavigateToProfile()
                }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).fillMaxSize().background(Color(0xFFFDE3D3)).verticalScroll(rememberScrollState())) {
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    if (estado.aiWarning != null) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                                Spacer(Modifier.width(8.dp))
                                Text(estado.aiWarning!!, color = Color.Red, fontSize = 14.sp)
                            }
                        }
                    }

                    Box(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F4F1))
                        .border(1.dp, Color(0xFF00796B), RoundedCornerShape(12.dp))
                        .clickable { selector.launch("image/*") },
                        contentAlignment = Alignment.Center) {
                        if (estado.fotoUri != null) {
                            AsyncImage(model = estado.fotoUri, contentDescription = null, contentScale = ContentScale.Crop)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, null, tint = Color(0xFF00796B))
                                Text("Añadir fotos/video", color = Color(0xFF00796B), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Etiqueta("Nombre de la mascota")
                    CampoEntrada(estado.nombre, "Nombre") { viewModel.cambiarNombre(it) }

                    Etiqueta("Descripción")
                    OutlinedTextField(
                        value = estado.descripcion,
                        onValueChange = { viewModel.cambiarDescripcion(it) },
                        placeholder = { Text("Escribe algo sobre la mascota...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Etiqueta("Tipo")
                    CampoEntrada(estado.tipo, "Tipo (Perro, Gato, etc)") { viewModel.cambiarTipo(it) }

                    Etiqueta("Raza")
                    CampoEntrada(estado.raza, "Raza") { viewModel.cambiarRaza(it) }

                    Etiqueta("Edad")
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = estado.edad, onValueChange = { viewModel.cambiarEdad(it) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                        Text("+", fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = estado.unidadEdad, onValueChange = { viewModel.cambiarUnidadEdad(it) }, modifier = Modifier.weight(1.2f), shape = RoundedCornerShape(10.dp))
                    }

                    Etiqueta("Sexo")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = estado.sexo == "Hembra", onClick = { viewModel.cambiarSexo("Hembra") })
                        Text("Hembra")
                        Spacer(Modifier.width(10.dp))
                        RadioButton(selected = estado.sexo == "Macho", onClick = { viewModel.cambiarSexo("Macho") })
                        Text("Macho")
                    }

                    Etiqueta("Ubicación")
                    CampoEntrada(estado.ciudad, "Ciudad") { viewModel.cambiarCiudad(it) }

                    Spacer(Modifier.height(10.dp))

                    if (estado.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Button(
                            onClick = { 
                                viewModel.guardarMascota {
                                    onNavigateToHome()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Text("Guardar y Publicar", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable fun Etiqueta(t: String) = Text(t, fontWeight = FontWeight.Bold, fontSize = 14.sp)
@Composable fun CampoEntrada(v: String, p: String, onV: (String) -> Unit) {
    OutlinedTextField(value = v, onValueChange = onV, placeholder = { Text(p) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
}

@Composable
fun BottomNav(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val NaranjaApp = Color(0xFFE67E22)
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
                    selectedIconColor = NaranjaApp,
                    selectedTextColor = NaranjaApp,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFFFFF4C2)
                )
            )
        }
    }
}
