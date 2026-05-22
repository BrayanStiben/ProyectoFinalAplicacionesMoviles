package com.example.seguimiento.features.Filtros

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.example.seguimiento.R
import com.example.seguimiento.features.Favoritos.FavoritePetCard
import com.example.seguimiento.features.home.BottomNav

// Colores de la paleta de la app
val NaranjaPrincipal = Color(0xFFE67E22)
val FondoCrema = Color(0xFFFEF9E7)
val GrisChip = Color(0xFFE5D3B3)
val TextoMarron = Color(0xFF5D4037)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFiltrosAvanzado(
    modelo: FiltroViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMapa: () -> Unit = {},
    onNavigateToDetail: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    val resultados by modelo.resultados.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNav(
                selectedItem = 1,
                onNavigateToHome = onNavigateToHome,
                onNavigateToFiltros = onNavigateToFiltros,
                onNavigateToFavoritos = onNavigateToFavoritos,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        floatingActionButton = {
            // Botón flotante actualizado con color naranja y icono de ubicación
            FloatingActionButton(
                onClick = { onNavigateToMapa() },
                containerColor = NaranjaPrincipal,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .size(60.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.LocationOn, "Ver en Mapa", modifier = Modifier.size(32.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NaranjaPrincipal)
                .padding(padding)
        ) {
            // Header con diseño limpio
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onNavigateToHome() },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.filters_title),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Contenedor principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(FondoCrema)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Filtro por Nombre
                    FiltroSection(
                        label = stringResource(R.string.filters_label_name),
                        isEnabled = modelo.habilitarNombre,
                        onToggle = { modelo.habilitarNombre = it }
                    ) {
                        CustomInputField(
                            value = modelo.nombreFiltro,
                            onValueChange = { modelo.nombreFiltro = it },
                            placeholder = stringResource(R.string.filters_placeholder_name),
                            isEnabled = modelo.habilitarNombre,
                            icon = Icons.Default.Search
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Filtro por Tipo de Animal
                    FiltroSection(
                        label = stringResource(R.string.filters_label_type),
                        isEnabled = modelo.habilitarTipo,
                        onToggle = { modelo.habilitarTipo = it }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .alphaIfDisabled(!modelo.habilitarTipo),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AnimalTypeChip(
                                label = stringResource(R.string.home_category_dog),
                                icon = Icons.Default.Pets,
                                selected = (modelo.tipoSeleccionado == "Perro" || modelo.tipoSeleccionado == stringResource(R.string.home_category_dog)) && modelo.habilitarTipo,
                                onClick = { if (modelo.habilitarTipo) modelo.tipoSeleccionado = "Perro" }
                            )
                            AnimalTypeChip(
                                label = stringResource(R.string.home_category_cat),
                                icon = Icons.Default.Pets,
                                selected = (modelo.tipoSeleccionado == "Gato" || modelo.tipoSeleccionado == stringResource(R.string.home_category_cat)) && modelo.habilitarTipo,
                                onClick = { if (modelo.habilitarTipo) modelo.tipoSeleccionado = "Gato" }
                            )
                            AnimalTypeChip(
                                label = stringResource(R.string.home_category_other),
                                icon = Icons.Default.Extension,
                                selected = (modelo.tipoSeleccionado == "Otro" || modelo.tipoSeleccionado == stringResource(R.string.home_category_other)) && modelo.habilitarTipo,
                                onClick = { if (modelo.habilitarTipo) modelo.tipoSeleccionado = "Otro" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Filtro por Ubicación
                    FiltroSection(
                        label = stringResource(R.string.filters_label_location),
                        isEnabled = modelo.habilitarUbicacion,
                        onToggle = { modelo.habilitarUbicacion = it }
                    ) {
                        Column(
                            modifier = Modifier.alphaIfDisabled(!modelo.habilitarUbicacion),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SelectorFiltro(
                                label = stringResource(R.string.reg_pet_label_dept),
                                seleccionado = modelo.departamentoSeleccionado,
                                opciones = modelo.listaDepartamentos,
                                isEnabled = modelo.habilitarUbicacion,
                                onSeleccion = { modelo.cambiarDepartamento(it) }
                            )
                            SelectorFiltro(
                                label = stringResource(R.string.reg_pet_label_city),
                                seleccionado = modelo.ciudadSeleccionada,
                                opciones = modelo.listaCiudades,
                                isEnabled = modelo.habilitarUbicacion && modelo.departamentoSeleccionado.isNotEmpty(),
                                onSeleccion = { modelo.ciudadSeleccionada = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Filtro por Edad
                    FiltroSection(
                        label = stringResource(R.string.filters_label_age),
                        isEnabled = modelo.habilitarEdad,
                        onToggle = { modelo.habilitarEdad = it }
                    ) {
                        CustomInputField(
                            value = modelo.edadFiltro,
                            onValueChange = { modelo.edadFiltro = it },
                            placeholder = stringResource(R.string.filters_placeholder_age),
                            isEnabled = modelo.habilitarEdad,
                            icon = Icons.Default.Cake
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón Aplicar
                    Button(
                        onClick = { modelo.aplicarFiltros() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrincipal),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Icon(Icons.Default.FilterAlt, null)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.filters_btn_apply),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = { modelo.limpiarFiltros() },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.filters_btn_clear),
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Lista de Resultados
                if (resultados.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.filters_results_header),
                                fontWeight = FontWeight.ExtraBold,
                                color = TextoMarron,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(resultados) { mascota ->
                                    FavoritePetCard(
                                        mascota = mascota,
                                        currentUserId = "",
                                        onLikeClick = { },
                                        onDetailClick = {
                                            onNavigateToDetail(mascota.id, mascota.nombre, mascota.edad, mascota.ubicacion, mascota.imagenUrl)
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else if (resultados.isEmpty() && (modelo.habilitarNombre || modelo.habilitarTipo || modelo.habilitarUbicacion || modelo.habilitarEdad)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.filters_empty_results),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FiltroSection(
    label: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(!isEnabled) }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = TextoMarron,
                fontSize = 17.sp
            )
            Checkbox(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(
                    checkedColor = NaranjaPrincipal,
                    uncheckedColor = Color.Gray.copy(alpha = 0.6f)
                )
            )
        }
        content()
    }
}

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isEnabled: Boolean,
    icon: ImageVector
) {
    TextField(
        value = if (isEnabled) value else "",
        onValueChange = onValueChange,
        enabled = isEnabled,
        placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.7f)) },
        leadingIcon = { Icon(icon, null, tint = if (isEnabled) NaranjaPrincipal else Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isEnabled) Color.White else Color.Gray.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.Gray.copy(alpha = 0.05f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = NaranjaPrincipal
        )
    )
}

@Composable
fun AnimalTypeChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .height(44.dp)
            .widthIn(min = 90.dp),
        shape = RoundedCornerShape(22.dp),
        color = if (selected) NaranjaPrincipal else GrisChip,
        shadowElevation = if (selected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else TextoMarron,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (selected) Color.White else TextoMarron,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFiltro(
    label: String,
    seleccionado: String,
    opciones: List<String>,
    isEnabled: Boolean,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido && isEnabled,
        onExpandedChange = { if (isEnabled) expandido = !expandido },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            enabled = isEnabled,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.Gray.copy(alpha = 0.05f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        ExposedDropdownMenu(
            expanded = expandido && isEnabled,
            onDismissRequest = { expandido = false },
            modifier = Modifier.background(Color.White)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, color = TextoMarron) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

fun Modifier.alphaIfDisabled(disabled: Boolean): Modifier = if (disabled) this.then(Modifier.alpha(0.5f)) else this
