package com.example.seguimiento.features.nutricionanimal

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R

@Composable
fun PantallaNutricion(
    viewmodel: NutricionViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val listaRegulaciones by viewmodel.regulaciones.collectAsState()
    val itemSeleccionado by viewmodel.itemSeleccionado.collectAsState()
    val categoriaSel by viewmodel.categoriaSeleccionada.collectAsState()
    val context = LocalContext.current
    
    var showDialog by remember { mutableStateOf(false) }
    var urlToOpen by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomNav(selectedItem = 0) { index ->
                when(index) {
                    0 -> onNavigateToHome()
                    1 -> onNavigateToFiltros()
                    3 -> onNavigateToProfile()
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Imagen de Fondo (fondo2)
            Image(
                painter = painterResource(id = R.drawable.fondo2),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Overlay sutil para legibilidad
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)))

            Column(modifier = Modifier.fillMaxSize()) {
                // BOTÓN VOLVER
                IconButton(
                    onClick = { onNavigateToHome() },
                    modifier = Modifier.padding(16.dp).background(Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }

                Text(
                    text = "Nutrición para\nperros y gatos",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp)
                )

                // FILA DE CATEGORÍAS ACTUALIZADA
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BotonCategoria("Nutrición", categoriaSel == "nutricion", Modifier.weight(1f)) {
                        viewmodel.seleccionarCategoria("nutricion")
                    }
                    BotonCategoria("Necesidades de adopción", categoriaSel == "necesidades", Modifier.weight(1.5f)) {
                        viewmodel.seleccionarCategoria("necesidades")
                    }
                    BotonCategoria("Así funciona", categoriaSel == "asifunciona", Modifier.weight(1f)) {
                        viewmodel.seleccionarCategoria("asifunciona")
                    }
                    BotonCategoria("Acerca de", categoriaSel == "acerca", Modifier.weight(1f)) {
                        viewmodel.seleccionarCategoria("acerca")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // LISTA DE TARJETAS SIN CHECKBOX
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(listaRegulaciones) { item ->
                        TarjetaRegulacion(item) {
                            urlToOpen = item.url
                            showDialog = true
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Abrir Enlace", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de abrir la url?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen))
                    context.startActivity(intent)
                }) {
                    Text("Sí", color = Color(0xFFE67E22), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun TarjetaRegulacion(regulacion: Regulacion, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del Icono adaptada al tamaño del contenedor (65dp)
            Surface(
                modifier = Modifier.size(65.dp),
                color = Color(0xFFE67E22).copy(alpha = 0.1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Image(
                    painter = painterResource(id = regulacion.imagenRecurso),
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp).fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            
            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(
                    text = regulacion.titulo, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 16.sp,
                    color = Color(0xFF5D2E17)
                )
                Text(
                    text = regulacion.subtitulo, 
                    fontSize = 12.sp, 
                    color = Color.Gray,
                    lineHeight = 16.sp
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

@Composable
fun BotonCategoria(texto: String, seleccionado: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(55.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (seleccionado) Color(0xFFE67E22) else Color.White.copy(alpha = 0.9f),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(
                text = texto,
                color = if (seleccionado) Color.White else Color(0xFF5D2E17),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp, // Tamaño reducido para que quepa "Necesidades de adopción"
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun BottomNav(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val NaranjaApp = Color(0xFFE67E22)
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
                label = { Text(label) },
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
