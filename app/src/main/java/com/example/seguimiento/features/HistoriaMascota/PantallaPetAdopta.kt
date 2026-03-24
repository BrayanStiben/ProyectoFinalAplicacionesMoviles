package com.example.seguimiento.features.HistoriaMascota

import com.example.seguimiento.R
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import kotlin.math.absoluteValue
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PantallaPetAdopta(
    viewModel: HistoriaMascotaViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val textoHistoria by viewModel.textoHistoria.collectAsState()
    val estadoPager = rememberPagerState(pageCount = { viewModel.fotosCarrusel.size })
    val scrollState = rememberScrollState()

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
            // IMAGEN DE FONDO (fondo3)
            Image(
                painter = painterResource(id = R.drawable.fondo3),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay sutil
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.25f)))

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
                        onClick = { onNavigateToHome() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)
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
                        .size(380.dp)
                        .padding(top = 10.dp)
                )

                // TEXTO SUBIDO PARA QUE TOPE CON EL ICONO
                Text(
                    text = "Comparte tu Historia Feliz",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF5D2E17),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-100).dp)
                )

                // CAMPO DE TEXTO SUBIDO
                OutlinedTextField(
                    value = textoHistoria,
                    onValueChange = { viewModel.alCambiarTexto(it) },
                    placeholder = { Text("¿Cómo conociste a tu mejor amigo?", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .height(150.dp)
                        .offset(y = (-80).dp)
                        .shadow(15.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFFE67E22),
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                // BOTÓN SUBIDO
                Button(
                    onClick = { viewModel.compartir() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .height(60.dp)
                        .offset(y = (-65).dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                    shape = RoundedCornerShape(30.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                ) {
                    Text("¡Publicar ahora! 🐾", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }

                // CARRUSEL PROFESIONAL SUBIDO
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-45).dp)
                        .padding(bottom = 30.dp)
                        .shadow(20.dp, RoundedCornerShape(35.dp)),
                    color = Color.White.copy(alpha = 0.98f),
                    shape = RoundedCornerShape(35.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 25.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Favorite, null, tint = Color(0xFFE67E22))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Momentos Inolvidables", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFF5D2E17))
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        HorizontalPager(
                            state = estadoPager,
                            modifier = Modifier.fillMaxWidth().height(320.dp),
                            contentPadding = PaddingValues(horizontal = 60.dp),
                            pageSpacing = 20.dp
                        ) { pagina ->
                            val pageOffset = ((estadoPager.currentPage - pagina) + estadoPager.currentPageOffsetFraction).absoluteValue
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.85f)
                                    .graphicsLayer {
                                        val scale = lerp(
                                            start = 0.85f,
                                            stop = 1f,
                                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                        )
                                        scaleX = scale
                                        scaleY = scale
                                        alpha = lerp(
                                            start = 0.6f,
                                            stop = 1f,
                                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                        )
                                    },
                                shape = RoundedCornerShape(32.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                                border = BorderStroke(3.dp, Color.White)
                            ) {
                                AsyncImage(
                                    model = viewModel.fotosCarrusel[pagina],
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Row(
                            Modifier.padding(top = 25.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(viewModel.fotosCarrusel.size) { i ->
                                val selected = estadoPager.currentPage == i
                                val width by androidx.compose.animation.core.animateDpAsState(
                                    targetValue = if (selected) 24.dp else 8.dp, label = ""
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .height(8.dp)
                                        .width(width)
                                        .clip(CircleShape)
                                        .background(if (selected) Color(0xFFE67E22) else Color.LightGray.copy(alpha = 0.6f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNav(selectedItem: Int, onItemSelected: (Int) -> Unit) {
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
