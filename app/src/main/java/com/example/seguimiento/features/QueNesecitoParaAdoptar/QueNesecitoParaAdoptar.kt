package com.example.seguimiento.features.QueNesecitoParaAdoptar

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.R

// Paleta de colores oficial
val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)
val FondoCrema = Color(0xFFFDF7E7)

// 1. DEFINICIÓN DE LA FORMA CURVA
val BannerCurvoShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height - 40f)
    quadraticTo(
        size.width / 2f, size.height + 20f,
        0f, size.height - 40f
    )
    close()
}

@Composable
fun QueNesecitoParaAdoptar(
    viewModel: QueNesecitoParaAdoptarViewModel = viewModel()
) {
    val seccionActiva by viewModel.seccionSeleccionada.collectAsState()
    val tabSeleccionada by viewModel.tabSeleccionada.collectAsState()
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomNav(tabSeleccionada) { viewModel.cambiarTab(it) }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // IMAGEN DE FONDO
            Image(
                painter = painterResource(id = R.drawable.fondo2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ENCABEZADO CURVO
                BannerNaranjaHeader()

                // DISTRIBUCIÓN EQUITATIVA (Solo espacio, sin estirar las tarjetas)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.SpaceEvenly, // Esto reparte el espacio de forma igual entre ellas
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // OPCIÓN 1: TUS DATOS
                    TarjetaRequisito(
                        titulo = "Tus datos",
                        subtitulo = "Documentación y requisitos legales",
                        icono = Icons.Default.AssignmentInd
                    ) { viewModel.abrirPopUp(SeccionAdopcion.TUS_DATOS) }

                    // OPCIÓN 2: CALCULAR COMIDA
                    TarjetaRequisito(
                        titulo = "Calcular comida de animal",
                        subtitulo = "Planes nutricionales a medida",
                        icono = Icons.Default.Calculate
                    ) { viewModel.abrirPopUp(SeccionAdopcion.CALCULAR_COMIDA) }

                    // OPCIÓN 3: HOGAR FELIZ
                    TarjetaRequisito(
                        titulo = "¿Hogar feliz?",
                        subtitulo = "Condiciones ideales de vivienda",
                        icono = Icons.Default.HomeWork
                    ) { viewModel.abrirPopUp(SeccionAdopcion.HOGAR_FELIZ) }
                }
            }
        }

        // POP-UP DETALLADO
        seccionActiva?.let { seccion ->
            AlertDialog(
                onDismissRequest = { viewModel.cerrarPopUp() },
                confirmButton = {
                    if (seccion == SeccionAdopcion.CALCULAR_COMIDA) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.foodforjoe.es/calculator"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = NaranjaApp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("CALCULADORA", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://listado.mercadolibre.com.co/comida-de-mascota#D[A:comida%20de%20mascota]"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = CafeApp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("COMPRAR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.cerrarPopUp() },
                            colors = ButtonDefaults.buttonColors(containerColor = NaranjaApp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ENTENDIDO", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                title = {
                    Text(
                        text = if (seccion == SeccionAdopcion.CALCULAR_COMIDA) "ALIMENTACIÓN" else seccion.name.replace("_", " "),
                        fontWeight = FontWeight.Black,
                        color = CafeApp
                    )
                },
                text = {
                    Text(
                        text = viewModel.contenidosPopUp[seccion] ?: "",
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color.DarkGray
                    )
                },
                containerColor = FondoCrema,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }
}

@Composable
fun BannerNaranjaHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .shadow(6.dp, BannerCurvoShape)
            .clip(BannerCurvoShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF98D33), Color(0xFFF36E1F))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "¿Qué necesito para\nadoptar una mascota?",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }
}

@Composable
fun TarjetaRequisito(
    titulo: String, 
    subtitulo: String, 
    icono: ImageVector, 
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Altura fija para todas para que sean iguales
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(NaranjaApp.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, null, tint = NaranjaApp, modifier = Modifier.size(30.dp))
            }
            
            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CafeApp)
                Text(text = subtitulo, fontSize = 13.sp, color = Color.Gray)
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos, 
                null, 
                tint = Color.LightGray, 
                modifier = Modifier.size(16.dp)
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