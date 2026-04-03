package com.example.seguimiento.features.nutricionanimal

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import com.example.seguimiento.features.home.BottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNutricion(
    viewmodel: NutricionViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val listaRegulaciones by viewmodel.regulaciones.collectAsState()
    val categoriaSel by viewmodel.categoriaSeleccionada.collectAsState()
    val context = LocalContext.current
    
    var showDialog by remember { mutableStateOf(false) }
    var urlToOpen by remember { mutableStateOf("") }

    val naranjaApp = Color(0xFFE67E22)
    val cafeApp = Color(0xFF5D2E17)

    Scaffold(
        bottomBar = {
            BottomNav(
                selectedItem = -1,
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
                .padding(padding)
                .background(Color(0xFFFDFBFA))
        ) {
            // --- HEADER ESTILO UNIFICADO ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 45.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateToHome,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Guía de Nutrición 🍖",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Consejos para una vida saludable",
                            color = Color.White.copy(0.9f),
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // --- FILA DE CATEGORÍAS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Nutrición" to "nutricion",
                    "Necesidades" to "necesidades",
                    "Así funciona" to "asifunciona",
                    "Acerca de" to "acerca"
                ).forEach { (label, slug) ->
                    val isSelected = categoriaSel == slug
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewmodel.seleccionarCategoria(slug) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = naranjaApp,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = cafeApp
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.LightGray,
                            selectedBorderColor = naranjaApp,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 2.dp
                        )
                    )
                }
            }

            // --- CONTENIDO ---
            if (listaRegulaciones.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = naranjaApp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listaRegulaciones) { item ->
                        TarjetaInformativa(item) {
                            urlToOpen = item.url
                            showDialog = true
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Ver más detalles", fontWeight = FontWeight.Bold, color = cafeApp) },
            text = { Text("¿Deseas abrir el enlace externo para leer más sobre este tema?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = naranjaApp)
                ) {
                    Text("CONTINUAR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun TarjetaInformativa(regulacion: Regulacion, onClick: () -> Unit) {
    val naranjaApp = Color(0xFFE67E22)
    val cafeApp = Color(0xFF5D2E17)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(naranjaApp.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = if (regulacion.imagenRecurso != 0) regulacion.imagenRecurso else R.drawable.petadopticono),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = regulacion.titulo, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 17.sp,
                    color = cafeApp
                )
                Text(
                    text = regulacion.subtitulo, 
                    fontSize = 13.sp, 
                    color = Color.Gray,
                    maxLines = 2
                )
            }
            
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos, 
                contentDescription = null, 
                tint = Color.LightGray, 
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
