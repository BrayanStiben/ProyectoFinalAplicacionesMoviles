package com.example.seguimiento.features.HistoriaMascota

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.seguimiento.R
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlin.math.absoluteValue
import com.example.seguimiento.Dominio.modelos.HistoriaFeliz

@Composable
fun PantallaPetAdopta(
    viewModel: HistoriaMascotaViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val textoHistoria by viewModel.textoHistoria.collectAsState()
    val mascotaNombre by viewModel.mascotaNombre.collectAsState()
    val imagenSeleccionada by viewModel.imagenSeleccionada.collectAsState()
    val historiasAprobadas by viewModel.historiasAprobadas.collectAsState()

    val estadoPager = rememberPagerState(pageCount = { historiasAprobadas.size.coerceAtLeast(1) })
    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf<HistoriaFeliz?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.alSeleccionarImagen(uri)
    }

    Scaffold(
        bottomBar = {
            BottomNavPet(selectedItem = 0) { index -> 
                when(index) {
                    0 -> onNavigateToHome()
                    1 -> onNavigateToFiltros()
                    3 -> onNavigateToProfile()
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // IMAGEN DE FONDO
            Image(
                painter = painterResource(id = R.drawable.fondo3),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay sutil
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.3f)))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // BOTÓN VOLVER
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    IconButton(
                        onClick = { onNavigateBack() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF5D2E17))
                    }
                }

                // LOGO GIGANTE
                Image(
                    painter = painterResource(id = R.drawable.petadopticono),
                    contentDescription = "Logo PetAdopta",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(280.dp)
                        .padding(top = 0.dp)
                )

                // TEXTO CASI TOCANDO EL LOGO
                Text(
                    text = "Comparte tu Historia Feliz",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF5D2E17),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-55).dp)
                )

                // FORMULARIO
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .offset(y = (-40).dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        OutlinedTextField(
                            value = mascotaNombre,
                            onValueChange = { viewModel.alCambiarNombreMascota(it) },
                            placeholder = { Text("Nombre de tu mascota", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE67E22),
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = textoHistoria,
                            onValueChange = { viewModel.alCambiarTexto(it) },
                            placeholder = { Text("¿Cómo conociste a tu mejor amigo?", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE67E22),
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (imagenSeleccionada != null) {
                            Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(16.dp))) {
                                AsyncImage(
                                    model = imagenSeleccionada,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { viewModel.alSeleccionarImagen(null) },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, null, tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Button(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            border = BorderStroke(2.dp, Color(0xFFE67E22)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFFE67E22))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (imagenSeleccionada == null) "SUBIR FOTO 📸" else "CAMBIAR FOTO", color = Color(0xFFE67E22), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { 
                                viewModel.compartir {
                                    onNavigateBack() // Vuelve a la pantalla anterior (Estadísticas si es admin)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text("PUBLICAR AHORA 🐾", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                }

                // SECCIÓN MOMENTOS INOLVIDABLES (CARRUSEL)
                Text(
                    "Momentos Inolvidables",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = Color(0xFF5D2E17),
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .offset(y = (-30).dp)
                )

                if (historiasAprobadas.isEmpty()) {
                    Text("¡Las historias de éxito aparecerán aquí!", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, modifier = Modifier.offset(y = (-30).dp))
                } else {
                    HorizontalPager(
                        state = estadoPager,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .offset(y = (-30).dp),
                        contentPadding = PaddingValues(horizontal = 40.dp),
                        pageSpacing = 12.dp
                    ) { pagina ->
                        val historia = historiasAprobadas[pagina]
                        val pageOffset = ((estadoPager.currentPage - pagina) + estadoPager.currentPageOffsetFraction).absoluteValue
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                    scaleX = scale
                                    scaleY = scale
                                    alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                }
                                .clickable { showDialog = historia },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = historia.imagenUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = historia.mascotaNombre,
                                        color = Color(0xFFE67E22),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = historia.texto,
                                        color = Color.DarkGray,
                                        fontSize = 14.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                        Spacer(Modifier.width(4.dp))
                                        Text(historia.autorNombre, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        Modifier
                            .padding(top = 0.dp)
                            .offset(y = (-20).dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(historiasAprobadas.size) { i ->
                            val selected = estadoPager.currentPage == i
                            val width by animateDpAsState(targetValue = if (selected) 24.dp else 8.dp, label = "")
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(8.dp)
                                    .width(width)
                                    .clip(CircleShape)
                                    .background(if (selected) Color(0xFFE67E22) else Color.LightGray)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // DIALOGO DETALLE
    showDialog?.let { historia ->
        AlertDialog(
            onDismissRequest = { showDialog = null },
            confirmButton = {
                Button(
                    onClick = { showDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22))
                ) {
                    Text("Cerrar", color = Color.White)
                }
            },
            title = { 
                Text(
                    text = "La historia de ${historia.mascotaNombre}", 
                    fontWeight = FontWeight.Black, 
                    color = Color(0xFF5D2E17)
                ) 
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AsyncImage(
                        model = historia.imagenUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = historia.texto,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Publicado por: ${historia.autorNombre}", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(30.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun BottomNavPet(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val NaranjaAppColor = Color(0xFFE67E22)
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
                    selectedIconColor = NaranjaAppColor,
                    selectedTextColor = NaranjaAppColor,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFFFFF4C2)
                )
            )
        }
    }
}
