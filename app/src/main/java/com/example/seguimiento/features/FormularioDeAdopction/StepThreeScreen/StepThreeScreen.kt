package com.example.seguimiento.features.FormularioDeAdopction.StepThreeScreen

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
fun StepThreeScreen(
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
                        fontWeight = FontWeight.Bold,
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
                        
                        Text(
                            text = "Compromiso y Logística",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF5D2E17)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // SECCIÓN 1: PRESUPUESTO
                        SectionHeaderLocal(Icons.Default.Payments, "Presupuesto")
                        BooleanOptionLocal(
                            question = "¿Es consciente de los gastos de alimentación y veterinario?",
                            value = vm.state.awareOfCosts
                        ) { vm.updateState(vm.state.copy(awareOfCosts = it)) }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECCIÓN 2: PLAN DE CONTINGENCIA
                        SectionHeaderLocal(Icons.Default.HealthAndSafety, "Plan de contingencia")
                        CustomInputLocal(
                            label = "¿Quién cuidará de la mascota si viaja o se enferma?", 
                            value = vm.state.contingencyPlan,
                            icon = Icons.Default.SupportAgent
                        ) { vm.updateState(vm.state.copy(contingencyPlan = it)) }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECCIÓN 3: ACUERDO
                        SectionHeaderLocal(Icons.Default.Gavel, "Acuerdos")
                        BooleanOptionLocal(
                            question = "Acuerdo de castración/vacunación: ¿Acepta las normas?",
                            value = vm.state.acceptsTerms
                        ) { vm.updateState(vm.state.copy(acceptsTerms = it)) }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECCIÓN 4: MUDANZAS
                        SectionHeaderLocal(Icons.Default.LocalShipping, "Mudanzas")
                        CustomInputLocal(
                            label = "¿Qué pasaría con la mascota si se muda?", 
                            value = vm.state.movingPlan,
                            icon = Icons.Default.Info
                        ) { vm.updateState(vm.state.copy(movingPlan = it)) }

                        Spacer(modifier = Modifier.height(32.dp))

                        // INDICADOR DE AVANCE (1, 2 y 3 marcados)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(4) { index ->
                                val estaMarcado = index <= 2
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
                                    Box(modifier = Modifier.width(15.dp).height(2.dp).background(if (index < 2) NaranjaApp else Color.LightGray.copy(alpha = 0.5f)))
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
fun SectionHeaderLocal(icon: ImageVector, title: String) {
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
fun CustomInputLocal(label: String, value: String, icon: ImageVector, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = NaranjaApp.copy(alpha = 0.7f)) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NaranjaApp,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            focusedLabelColor = NaranjaApp,
            unfocusedLabelColor = Color.Gray
        )
    )
}

@Composable
fun BooleanOptionLocal(question: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
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
                    indicatorColor = Color(0xFFFFF4C2)
                )
            )
        }
    }
}