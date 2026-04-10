package com.example.seguimiento.features.Mercado

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.example.seguimiento.Dominio.modelos.Producto
import com.example.seguimiento.R
import com.example.seguimiento.features.home.BottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaScreen(
    viewModel: TiendaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
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
            // --- HEADER ---
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
                        onClick = onNavigateBack,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.store_market_title),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.store_market_subtitle),
                            color = Color.White.copy(0.9f),
                            fontSize = 14.sp
                        )
                    }
                    // PUNTOS ACTUALES
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                    ) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("${uiState.puntosUsuario}", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // --- FILTRO CATEGORÍAS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categorias = listOf(
                    stringResource(R.string.store_cat_all) to "Todos",
                    stringResource(R.string.store_cat_food) to "Comida",
                    stringResource(R.string.store_cat_toys) to "Juguetes",
                    stringResource(R.string.store_cat_acc) to "Accesorios",
                    stringResource(R.string.store_cat_health) to "Salud"
                )
                categorias.forEach { (label, value) ->
                    val isSelected = uiState.categoriaSeleccionada == value
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.seleccionarCategoria(value) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = naranjaApp,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // --- GRID DE PRODUCTOS ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.productos) { producto ->
                    TarjetaProducto(producto) {
                        viewModel.comprarProducto(producto)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaProducto(producto: Producto, onBuy: () -> Unit) {
    val naranjaApp = Color(0xFFE67E22)
    val cafeApp = Color(0xFF5D2E17)
    val estaAgotado = producto.stock <= 0

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.petadopticono),
                    error = painterResource(id = R.drawable.petadopticono)
                )
                
                Column(Modifier.padding(12.dp)) {
                    Text(
                        producto.nombre, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 15.sp, 
                        color = cafeApp, 
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        producto.descripcion, 
                        fontSize = 11.sp, 
                        color = Color.Gray, 
                        maxLines = 2, 
                        lineHeight = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.height(28.dp) // Altura fija para 2 líneas
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, null, tint = naranjaApp, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${producto.precioPuntos}", fontWeight = FontWeight.Black, color = naranjaApp, fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(4.dp))
                    
                    Text(
                        text = if (estaAgotado) stringResource(R.string.store_out_of_stock) else stringResource(R.string.store_stock_value, producto.stock),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (estaAgotado) Color.Red else Color.Gray
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onBuy,
                        enabled = !estaAgotado,
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = naranjaApp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if(estaAgotado) stringResource(R.string.store_btn_sold_out) else stringResource(R.string.store_btn_redeem), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // LETRERO AGOTADO (Overlay Centrado en toda la tarjeta)
            if (estaAgotado) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .rotate(-15f)
                            .background(Color.Red.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(stringResource(R.string.store_btn_sold_out), color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}
