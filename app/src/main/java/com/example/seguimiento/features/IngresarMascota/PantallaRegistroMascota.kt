package com.example.seguimiento.features.IngresarMascota

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.features.FormularioDeAdopction.StepOneScreen.BottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroMascota(
    mascotaId: String? = null,
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

    LaunchedEffect(mascotaId) {
        if (mascotaId != null) {
            viewModel.cargarMascotaParaEdicion(mascotaId)
        }
    }

    val naranjaApp = Color(0xFFE67E22)

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
                        Text(if (mascotaId == null) "🐶" else "📝", fontSize = 22.sp) 
                        Spacer(Modifier.width(8.dp))
                        Text(if (mascotaId == null) "Ingresar Mascota" else "Editar Mascota", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text(if (mascotaId == null) "🐱" else "✨", fontSize = 22.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = naranjaApp)
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
                        } else if (mascotaId != null && estado.nombre.isNotEmpty()) {
                            Text("Foto actual mantenida", color = Color.Gray)
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

                    Etiqueta("Tipo de Mascota")
                    SelectorMascota(
                        label = "Tipo",
                        seleccionado = estado.tipo,
                        opciones = estado.listaTipos,
                        icon = Icons.Default.Pets
                    ) { viewModel.cambiarTipo(it) }

                    Etiqueta("Raza")
                    if (estado.tipo == "Otro") {
                        OutlinedTextField(
                            value = estado.raza,
                            onValueChange = { viewModel.cambiarRaza(it) },
                            placeholder = { Text("Escribe la raza...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = naranjaApp,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                    } else {
                        SelectorMascota(
                            label = "Raza",
                            seleccionado = estado.raza,
                            opciones = estado.listaRazas,
                            icon = Icons.AutoMirrored.Filled.List,
                            enabled = estado.tipo.isNotEmpty() && estado.listaRazas.isNotEmpty()
                        ) { viewModel.cambiarRaza(it) }
                    }

                    Etiqueta("Edad")
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = estado.edad,
                            onValueChange = { viewModel.cambiarEdad(it) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
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

                    Etiqueta("Departamento")
                    SelectorMascota(
                        label = "Departamento",
                        seleccionado = estado.departamento,
                        opciones = estado.listaDepartamentos,
                        icon = Icons.Default.Map
                    ) { viewModel.cambiarDepartamento(it) }

                    Etiqueta("Municipio / Ciudad")
                    SelectorMascota(
                        label = "Ciudad",
                        seleccionado = estado.ciudad,
                        opciones = estado.listaCiudades,
                        icon = Icons.Default.LocationOn,
                        enabled = estado.departamento.isNotEmpty()
                    ) { viewModel.cambiarCiudad(it) }

                    Spacer(Modifier.height(10.dp))

                    if (estado.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = naranjaApp)
                    } else {
                        Button(
                            onClick = {
                                viewModel.guardarMascota {
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = naranjaApp),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Text(if (mascotaId == null) "Guardar y Publicar" else "Actualizar Cambios", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Etiqueta(texto: String) {
    Text(
        text = texto,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF5D2E17),
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun CampoEntrada(value: String, placeholder: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFE67E22),
            unfocusedBorderColor = Color.LightGray
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorMascota(
    label: String,
    seleccionado: String,
    opciones: List<String>,
    icon: ImageVector,
    enabled: Boolean = true,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido && enabled,
        onExpandedChange = { if (enabled) expandido = !expandido },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icon, null, tint = Color(0xFFE67E22)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            enabled = enabled,
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = enabled).fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE67E22),
                unfocusedBorderColor = Color.LightGray,
                disabledBorderColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )

        ExposedDropdownMenu(
            expanded = expandido && enabled,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
