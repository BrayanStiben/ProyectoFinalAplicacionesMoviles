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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import com.example.seguimiento.features.home.BottomNav
import com.example.seguimiento.features.necesidades.SeccionNecesidades
import com.example.seguimiento.features.asifunciona.SeccionAsiFunciona
import com.example.seguimiento.features.acercade.SeccionAcercaDe

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
                            stringResource(R.string.nutrition_title),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.nutrition_subtitle),
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
                    stringResource(R.string.nutrition_cat_nutri) to "nutricion",
                    stringResource(R.string.nutrition_cat_needs) to "necesidades",
                    stringResource(R.string.nutrition_cat_how_it_works) to "asifunciona",
                    stringResource(R.string.nutrition_cat_about) to "acerca"
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
                        )
                    )
                }
            }

            // --- CONTENIDO DINÁMICO ---
            Box(modifier = Modifier.weight(1f)) {
                when (categoriaSel) {
                    "nutricion" -> {
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
                        }
                    }
                    "necesidades" -> SeccionNecesidades()
                    "asifunciona" -> SeccionAsiFunciona()
                    "acerca" -> SeccionAcercaDe()
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.nutrition_dialog_title), fontWeight = FontWeight.Bold, color = cafeApp) },
            text = { Text(stringResource(R.string.nutrition_dialog_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = naranjaApp)
                ) {
                    Text(stringResource(R.string.btn_continue), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.btn_cancel), color = Color.Gray)
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
                    text = stringResource(regulacion.tituloRes), 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 17.sp,
                    color = cafeApp
                )
                Text(
                    text = stringResource(regulacion.subtituloRes),
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
