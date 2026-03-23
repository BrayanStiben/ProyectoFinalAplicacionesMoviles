package com.example.seguimiento.features.IngresarMascota

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.draw.clip

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImage

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroMascota(viewModel: MascotaViewModel = viewModel()) {
    val estado by viewModel.estado.collectAsState()
    val selector = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.alSeleccionarFoto(it)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { /* Volver */ }) {
                        Icon(Icons.Default.ArrowBackIosNew, "Volver", tint = Color.White)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐶", fontSize = 22.sp) // Icono Izquierdo
                        Spacer(Modifier.width(8.dp))
                        Text("Ingresar Mascota", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("🐱", fontSize = 22.sp) // Icono Derecho
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFE67E22))
            )
        }
    ) { p ->
        Column(Modifier.padding(p).fillMaxSize().background(Color(0xFFFDE3D3)).verticalScroll(rememberScrollState())) {
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Recuadro de Foto con línea punteada (simulada con border)
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

                    Etiqueta("Tipo")
                    CampoEntrada(estado.tipo, "Tipo") { viewModel.cambiarTipo(it) }

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

                    Button(
                        onClick = { viewModel.guardarMascota() },
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

@Composable fun Etiqueta(t: String) = Text(t, fontWeight = FontWeight.Bold, fontSize = 14.sp)
@Composable fun CampoEntrada(v: String, p: String, onV: (String) -> Unit) {
    OutlinedTextField(value = v, onValueChange = onV, placeholder = { Text(p) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
}