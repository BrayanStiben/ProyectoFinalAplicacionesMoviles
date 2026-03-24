package com.example.seguimiento.features.FormularioDeAdopction.StepOneScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel

// Paleta de colores consistente
val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)
val AzulForm = Color(0xFF42A5F5)
val FondoApp = Color(0xFFFDF7E7)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StepOneScreen(
    vm: AdoptionViewModel = viewModel(),
    onNext: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Formulario",
                        color = Color.White, 
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AzulForm
                )
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoApp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        
                        // SECCIÓN 1: SELECCIÓN DE MASCOTA
                        SectionHeader(Icons.Default.Pets, "Preferencia")
                        CustomInput(
                            label = "Tipo de animal (Perro/Gato/Otro)", 
                            value = vm.state.petType,
                            icon = Icons.Default.Category
                        ) { vm.updateState(vm.state.copy(petType = it)) }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECCIÓN 2: MOTIVACIÓN
                        SectionHeader(Icons.Default.Favorite, "Motivación")
                        CustomInput(
                            label = "¿Por qué quieres adoptar ahora?", 
                            value = vm.state.motivation,
                            icon = Icons.Default.ChatBubble
                        ) { vm.updateState(vm.state.copy(motivation = it)) }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECCIÓN 3: EXPECTATIVAS (Chips)
                        SectionHeader(Icons.Default.Star, "Expectativas")
                        Text("¿Qué personalidad buscas?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        
                        val opcionesExpectativas = listOf("Activo", "Tranquilo", "Guardián", "Compañía")
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            opcionesExpectativas.forEach { opcion ->
                                FilterChip(
                                    selected = vm.state.expectations == opcion,
                                    onClick = { vm.updateState(vm.state.copy(expectations = opcion)) },
                                    label = { Text(opcion) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECCIÓN 4: TIEMPO DISPONIBLE
                        SectionHeader(Icons.Default.Schedule, "Tiempo Disponible")
                        Text("Horas que pasará sola al día: ${vm.state.hoursAlone}h", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Slider(
                            value = vm.state.hoursAlone.toFloat(),
                            onValueChange = { vm.updateState(vm.state.copy(hoursAlone = it.toInt())) },
                            valueRange = 0f..24f,
                            steps = 23,
                            colors = SliderDefaults.colors(
                                thumbColor = NaranjaApp,
                                activeTrackColor = NaranjaApp,
                                inactiveTrackColor = NaranjaApp.copy(alpha = 0.24f)
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // INDICADOR DE AVANCE (Solo 1 marcado)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(4) { index ->
                                val estaMarcado = index == 0
                                Surface(
                                    modifier = Modifier.size(30.dp),
                                    shape = CircleShape,
                                    color = if (estaMarcado) NaranjaApp else Color.LightGray.copy(alpha = 0.5f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (index < 3) {
                                    Box(modifier = Modifier.width(15.dp).height(2.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                                }
                            }
                        }

                        Button(
                            onClick = onNext,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NaranjaApp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text("CONTINUAR", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    ) {
        Icon(icon, null, tint = NaranjaApp, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = CafeApp
        )
    }
}

@Composable
fun CustomInput(label: String, value: String, icon: ImageVector, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = NaranjaApp.copy(alpha = 0.7f)) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NaranjaApp,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            focusedLabelColor = NaranjaApp,
            unfocusedLabelColor = Color.Gray
        )
    )
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