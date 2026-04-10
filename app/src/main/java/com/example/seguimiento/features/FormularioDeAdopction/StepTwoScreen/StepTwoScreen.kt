package com.example.seguimiento.features.FormularioDeAdopction.StepTwoScreen

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel

// Paleta de colores consistente
val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)
val AzulForm = Color(0xFF42A5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepTwoScreen(
    vm: AdoptionViewModel = hiltViewModel(),
    onNext: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de pantalla
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            stringResource(R.string.form_step2_title), 
                            color = Color.White, 
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        ) 
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = AzulForm.copy(alpha = 0.9f)
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
                    .padding(paddingValues),
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
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(32.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            
                            // SECCIÓN 1: DATOS MASCOTA
                            SectionHeader(Icons.Default.Pets, stringResource(R.string.form_step2_pet_data))
                            
                            CustomInput(
                                label = stringResource(R.string.form_step2_pet_name_label), 
                                value = vm.state.petName,
                                icon = Icons.Default.Badge
                            ) { vm.updateState(vm.state.copy(petName = it)) }

                            CustomInput(
                                label = stringResource(R.string.form_step2_pet_age_label), 
                                value = vm.state.petAge,
                                icon = Icons.Default.Event
                            ) { vm.updateState(vm.state.copy(petAge = it)) }

                            Spacer(modifier = Modifier.height(12.dp))

                            // SECCIÓN 2: HOGAR
                            SectionHeader(Icons.Default.Home, stringResource(R.string.form_step2_home_family))
                            
                            CustomInput(
                                label = stringResource(R.string.form_step2_home_type_label), 
                                value = vm.state.homeType,
                                icon = Icons.Default.Apartment
                            ) { vm.updateState(vm.state.copy(homeType = it)) }

                            Spacer(modifier = Modifier.height(12.dp))

                            // SECCIÓN 3: EXPERIENCIA
                            SectionHeader(Icons.Default.AutoAwesome, stringResource(R.string.form_step2_experience))
                            
                            BooleanOption(
                                question = stringResource(R.string.form_step2_yard_q), 
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
                                Text(stringResource(R.string.form_btn_continue), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                            }
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
                label = { Text(stringResource(R.string.form_yes)) },
                leadingIcon = if (value) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) } } else null,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.width(12.dp))
            FilterChip(
                selected = !value,
                onClick = { onValueChange(false) },
                label = { Text(stringResource(R.string.form_no)) },
                leadingIcon = if (!value) { { Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp)) } } else null,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun BottomNav(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White.copy(alpha = 0.9f)) {
        val items = listOf(
            Triple(stringResource(R.string.nav_home), Icons.Default.Home, 0),
            Triple(stringResource(R.string.nav_search), Icons.Default.Search, 1),
            Triple(stringResource(R.string.nav_favorites), Icons.Default.FavoriteBorder, 2),
            Triple(stringResource(R.string.nav_profile), Icons.Default.Person, 3)
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
