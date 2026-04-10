package com.example.seguimiento.features.MercadoAdmin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.example.seguimiento.Dominio.modelos.Producto
import com.example.seguimiento.R
import com.example.seguimiento.core.navigation.AdminBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaAdminScreen(
    viewModel: TiendaAdminViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToEstadisticas: () -> Unit = {},
    onNavigateToListaSolicitudes: () -> Unit = {},
    onNavigateToEncontrarMascotas: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val productos by viewModel.productos.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }
    
    val naranjaApp = Color(0xFFE67E22)
    val cafeApp = Color(0xFF5D2E17)

    Scaffold(
        bottomBar = {
            AdminBottomBar(
                currentRoute = "admin_tienda",
                onNavigateToEstadisticas = onNavigateToEstadisticas,
                onNavigateToListaSolicitudes = onNavigateToListaSolicitudes,
                onNavigateToEncontrarMascotas = onNavigateToEncontrarMascotas,
                onLogout = onLogout
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = naranjaApp,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.admin_store_fab_add))
            }
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
                    Column {
                        Text(
                            stringResource(R.string.admin_store_title),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.admin_store_subtitle),
                            color = Color.White.copy(0.9f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (productos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.admin_store_empty), color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productos) { producto ->
                        TarjetaProductoAdmin(
                            producto = producto,
                            onDelete = { viewModel.eliminarProducto(producto.id) },
                            onEdit = { productoAEditar = it }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductFormDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { n, d, p, u, s, c ->
                viewModel.subirProducto(n, d, p, u, s, c) {
                    showAddDialog = false
                }
            }
        )
    }

    if (productoAEditar != null) {
        ProductFormDialog(
            producto = productoAEditar,
            onDismiss = { productoAEditar = null },
            onConfirm = { n, d, p, u, s, c ->
                val actualizado = productoAEditar!!.copy(
                    nombre = n,
                    descripcion = d,
                    precioPuntos = p,
                    imagenUrl = u,
                    stock = s,
                    categoria = c
                )
                viewModel.actualizarProducto(actualizado) {
                    productoAEditar = null
                }
            }
        )
    }
}

@Composable
fun TarjetaProductoAdmin(
    producto: Producto, 
    onDelete: () -> Unit,
    onEdit: (Producto) -> Unit
) {
    val cafeApp = Color(0xFF5D2E17)
    val naranjaApp = Color(0xFFE67E22)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.petadopticono)
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cafeApp)
                Text("${producto.categoria} • ${producto.precioPuntos} pts", fontSize = 14.sp, color = Color.Gray)
                Text(stringResource(R.string.admin_store_stock_label, producto.stock), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if(producto.stock > 0) Color(0xFF4CAF50) else Color.Red)
            }
            Row {
                IconButton(onClick = { onEdit(producto) }) {
                    Icon(Icons.Default.Edit, stringResource(R.string.pet_mgmt_edit_desc), tint = Color(0xFF2196F3))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, stringResource(R.string.pet_mgmt_btn_delete), tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun ProductFormDialog(
    producto: Producto? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String, Int, String) -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var desc by remember { mutableStateOf(producto?.descripcion ?: "") }
    var puntos by remember { mutableStateOf(producto?.precioPuntos?.toString() ?: "") }
    var url by remember { mutableStateOf(producto?.imagenUrl ?: "") }
    var stock by remember { mutableStateOf(producto?.stock?.toString() ?: "") }
    var categoria by remember { mutableStateOf(producto?.categoria ?: "Comida") }
    var expanded by remember { mutableStateOf(false) }
    
    val categorias = listOf(
        stringResource(R.string.store_cat_food) to "Comida",
        stringResource(R.string.store_cat_toys) to "Juguetes",
        stringResource(R.string.store_cat_acc) to "Accesorios",
        stringResource(R.string.store_cat_health) to "Salud"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if(producto == null) stringResource(R.string.admin_store_dialog_new_title) else stringResource(R.string.admin_store_dialog_edit_title), fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text(stringResource(R.string.admin_store_label_name)) })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text(stringResource(R.string.admin_store_label_desc)) })
                OutlinedTextField(value = puntos, onValueChange = { puntos = it }, label = { Text(stringResource(R.string.admin_store_label_points)) })
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text(stringResource(R.string.admin_store_label_url)) })
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text(stringResource(R.string.admin_store_label_stock)) })
                
                Box {
                    val labelActual = categorias.find { it.second == categoria }?.first ?: categoria
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.admin_store_btn_category, labelActual))
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categorias.forEach { (label, value) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = { categoria = value; expanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                val p = puntos.toIntOrNull() ?: 0
                val s = stock.toIntOrNull() ?: 0
                onConfirm(nombre, desc, p.coerceAtMost(2000), url, s, categoria)
            }) { Text(if(producto == null) stringResource(R.string.admin_store_btn_add) else stringResource(R.string.admin_store_btn_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) } }
    )
}
