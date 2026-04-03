package com.example.seguimiento.features.ConfirmacionMascotaFeliz

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.features.home.BottomNav
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable
fun AdoptionConfirmationScreen(
    requestId: String = "",
    viewModel: AdoptionViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {}
) {
    LaunchedEffect(requestId) {
        if (requestId.isNotEmpty()) {
            viewModel.setRequestId(requestId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFE67E22),
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { onNavigateToHome() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Text(
                        "¡ADOPCIÓN EXITOSA!",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }
        },
        bottomBar = {
            BottomNav(
                selectedItem = -1,
                onNavigateToHome = onNavigateToHome
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Image(
                painter = painterResource(id = R.drawable.fondo3),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.feliz),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(250.dp).padding(top = 20.dp),
                    contentScale = ContentScale.Fit
                )

                Card(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(70.dp),
                                shape = CircleShape,
                                border = BorderStroke(3.dp, Color(0xFFE67E22))
                            ) {
                                AsyncImage(
                                    model = uiState.petImg,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    uiState.petName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    color = Color(0xFF5D2E17)
                                )
                                Text(
                                    "¡Ya forma parte de tu vida!",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        FilaDetalle(Icons.Default.CalendarToday, "Fecha", uiState.date)
                        FilaDetalle(Icons.Default.Person, "Adoptante", uiState.adoptedBy)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Resumen de Solicitud:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D2E17)
                        )
                        Text(uiState.summary, fontSize = 13.sp, color = Color.DarkGray)

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp)) {
                            Text(
                                text = uiState.adminComment,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp,
                                color = Color(0xFF2E7D32),
                                lineHeight = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onNavigateToHome() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("VOLVER AL INICIO", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilaDetalle(icono: ImageVector, label: String, valor: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, null, tint = Color(0xFFE67E22), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("$label: ", color = Color.Gray, fontSize = 14.sp)
        Text(valor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
