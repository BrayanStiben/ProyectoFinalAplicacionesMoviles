package com.example.seguimiento.features.MisAdopciones

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.features.home.BottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisAdopcionesScreen(
    viewModel: MisAdopcionesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToDetail: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onNavigateToSalud: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val naranjaApp = Color(0xFFE67E22)
    val cafeApp = Color(0xFF5D2E17)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.my_adoptions_title), fontWeight = FontWeight.Black, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = naranjaApp)
            )
        },
        bottomBar = {
            BottomNav(
                selectedItem = 3,
                onNavigateToHome = onNavigateToHome,
                onNavigateToFiltros = onNavigateToFiltros,
                onNavigateToFavoritos = onNavigateToFavoritos,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Image(
                painter = painterResource(id = R.drawable.fondo2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.5f
            )

            if (uiState.adopciones.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                    ) {
                        Text(
                            stringResource(R.string.my_adoptions_empty), 
                            modifier = Modifier.padding(24.dp),
                            color = cafeApp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.adopciones) { item ->
                        TarjetaAdopcionExitosa(
                            item = item,
                            onClick = {
                                item.mascota?.let { m ->
                                    onNavigateToDetail(m.id, m.nombre, m.edad, m.ubicacion, m.imagenUrl)
                                }
                            },
                            onSaludClick = {
                                item.mascota?.let { m ->
                                    onNavigateToSalud(m.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaAdopcionExitosa(item: AdopcionConMascota, onClick: () -> Unit, onSaludClick: () -> Unit) {
    val cafeApp = Color(0xFF5D2E17)
    val naranjaApp = Color(0xFFE67E22)
    val verdeExito = Color(0xFF4CAF50)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.clickable { onClick() }.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                ) {
                    if (item.mascota?.imagenUrl?.isNotEmpty() == true) {
                        AsyncImage(
                            model = item.mascota.imagenUrl,
                            contentDescription = item.request.petName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Pets, 
                            null, 
                            tint = naranjaApp.copy(alpha = 0.5f), 
                            modifier = Modifier.align(Alignment.Center).size(40.dp)
                        )
                    }
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.request.petName, fontWeight = FontWeight.Black, fontSize = 18.sp, color = cafeApp)
                    Text(item.request.petType, fontSize = 13.sp, color = Color.Gray)
                    Surface(
                        color = verdeExito.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            stringResource(R.string.status_adopted), 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = verdeExito, 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 10.sp
                        )
                    }
                }
                
                Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
            }

            // BOTÓN CARNET DE SALUD
            Button(
                onClick = onSaludClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(Icons.Default.MedicalServices, null, tint = Color(0xFF2196F3), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.my_adoptions_btn_health), color = Color(0xFF2196F3), fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }
    }
}
