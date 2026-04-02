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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.features.Favoritos.FavoritePetCard
import com.example.seguimiento.features.home.BottomNav

// Paleta de colores original
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NaranjaPrincipal)
                .padding(padding)
        ) {
            // --- ENCABEZADO NARANJA ---
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onNavigateToHome() },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Búsqueda Avanzada", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }

            // --- CUERPO CREMA ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(FondoCrema)
            ) {
                // PANEL DE FILTROS (Scrollable)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // FILTRO NOMBRE
                    FiltroCheckboxRow(
                        label = "Filtrar por Nombre",
                        isEnabled = modelo.habilitarNombre,
                        onToggle = { modelo.habilitarNombre = it }
                    )
                    CustomInputField(
                        value = modelo.nombreFiltro,
                        onValueChange = { modelo.nombreFiltro = it },
                        placeholder = "Ej: Max, Bella...",
                        isEnabled = modelo.habilitarNombre
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // FILTRO TIPO
                    FiltroCheckboxRow(
                        label = "Filtrar por Tipo de Animal",
                        isEnabled = modelo.habilitarTipo,
                        onToggle = { modelo.habilitarTipo = it }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .alphaIfDisabled(!modelo.habilitarTipo), 
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Perro", "Gato", "Otro").forEach { tipo ->
                            ChipFiltro(
                                texto = tipo,
                                seleccionado = modelo.tipoSeleccionado == tipo && modelo.habilitarTipo,
                                alClick = { if(modelo.habilitarTipo) modelo.tipoSeleccionado = tipo }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // FILTRO UBICACIÓN (Municipio o Departamento)
                    FiltroCheckboxRow(
                        label = "Ubicación (Municipio o Depto)",
                        isEnabled = modelo.habilitarUbicacion,
                        onToggle = { modelo.habilitarUbicacion = it }
                    )
                    CustomInputField(
                        value = modelo.ubicacionFiltro,
                        onValueChange = { modelo.ubicacionFiltro = it },
                        placeholder = "Ej: Armenia, Quindío...",
                        isEnabled = modelo.habilitarUbicacion
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // FILTRO EDAD
                    FiltroCheckboxRow(
                        label = "Filtrar por Edad/Años",
                        isEnabled = modelo.habilitarEdad,
                        onToggle = { modelo.habilitarEdad = it }
                    )
                    CustomInputField(
                        value = modelo.edadFiltro,
                        onValueChange = { modelo.edadFiltro = it },
                        placeholder = "Ej: 2 años, Cachorro...",
                        isEnabled = modelo.habilitarEdad
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // BOTONES DE ACCIÓN
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { modelo.aplicarFiltros() },
                            modifier = Modifier.weight(1f).height(55.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrincipal)
                        ) {
                            Icon(Icons.Default.FilterAlt, null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("APLICAR FILTROS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                    
                    TextButton(
                        onClick = { modelo.limpiarFiltros() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Limpiar Filtros", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }

                // RESULTADOS (GRID)
                if (resultados.isNotEmpty()) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                    Text(
                        "Resultados de búsqueda:",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                        color = TextoMarron
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
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
                } else if (resultados.isEmpty() && (modelo.habilitarNombre || modelo.habilitarTipo || modelo.habilitarUbicacion || modelo.habilitarEdad)) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No se encontraron mascotas con esos criterios.", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun FiltroCheckboxRow(label: String, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onToggle(!isEnabled) }.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold, color = TextoMarron, fontSize = 16.sp)
        Checkbox(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(
                checkedColor = NaranjaPrincipal,
                uncheckedColor = Color.Gray
            )
        )
    }
}

@Composable
fun CustomInputField(value: String, onValueChange: (String) -> Unit, placeholder: String, isEnabled: Boolean) {
    OutlinedTextField(
        value = if (isEnabled) value else "",
        onValueChange = onValueChange,
        enabled = isEnabled,
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp).background(
            if (isEnabled) Color.White else Color.LightGray.copy(alpha = 0.1f), 
            RoundedCornerShape(12.dp)
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NaranjaPrincipal,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            disabledBorderColor = Color.LightGray.copy(alpha = 0.2f),
            disabledPlaceholderColor = Color.Gray.copy(alpha = 0.5f)
        )
    )
}

@Composable
fun ChipFiltro(texto: String, seleccionado: Boolean, alClick: () -> Unit) {
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

fun Modifier.alphaIfDisabled(disabled: Boolean): Modifier = if (disabled) this.then(Modifier.alpha(0.5f)) else this
