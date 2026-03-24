package com.example.seguimiento.features.FormularioDeAdopction.StepTwoScreen

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepTwoScreen(
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
                        "Cuestionario de Adopción", 
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
                        
                        // SECCIÓN 1: DATOS MASCOTA
                        SectionHeader(Icons.Default.Pets, "Datos de la Mascota")
                        
                        CustomInput(
                            label = "Nombre de la mascota", 
                            value = vm.state.petName,
                            icon = Icons.Default.Badge
                        ) { vm.updateState(vm.state.copy(petName = it)) }

                        CustomInput(
                            label = "Edad aproximada", 
                            value = vm.state.petAge,
                            icon = Icons.Default.Event
                        ) { vm.updateState(vm.state.copy(petAge = it)) }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECCIÓN 2: HOGAR
                        SectionHeader(Icons.Default.Home, "Hogar y Familia")
                        
                        CustomInput(
                            label = "Tipo de vivienda", 
                            value = vm.state.homeType,
                            icon = Icons.Default.Apartment
                        ) { vm.updateState(vm.state.copy(homeType = it)) }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECCIÓN 3: EXPERIENCIA
                        SectionHeader(Icons.Default.AutoAwesome, "Experiencia")
                        
                        BooleanOption(
                            question = "¿Tiene patio cercado?", 
                            value = vm.state.hasFencedYard
                        ) { vm.updateState(vm.state.copy(hasFencedYard = it)) }

                        Spacer(modifier = Modifier.height(24.dp))

                        // INDICADOR DE AVANCE (Movido al final después de la experiencia)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(4) { index ->
                                val estaCompletado = index <= 1
                                Surface(
                                    modifier = Modifier.size(30.dp),
                                    shape = CircleShape,
                                    color = if (estaCompletado) NaranjaApp else Color.LightGray.copy(alpha = 0.5f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (index < 3) {
                                    Box(modifier = Modifier.width(15.dp).height(2.dp).background(if (index < 1) NaranjaApp else Color.LightGray.copy(alpha = 0.5f)))
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
fun BooleanOption(question: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(question, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = value,
                onClick = { onValueChange(true) },
                label = { Text("Sí") },
                leadingIcon = if (value) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) } } else null,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.width(12.dp))
            FilterChip(
                selected = !value,
                onClick = { onValueChange(false) },
                label = { Text("No") },
                leadingIcon = if (!value) { { Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp)) } } else null,
                shape = RoundedCornerShape(12.dp)
            )
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