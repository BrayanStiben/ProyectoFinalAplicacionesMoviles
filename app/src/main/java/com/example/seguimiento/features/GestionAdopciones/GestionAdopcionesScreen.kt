package com.example.seguimiento.features.GestionAdopciones

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionAdopcionesScreen(
    viewModel: GestionAdopcionesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCertificado: (String) -> Unit,
    onNavigateToRechazo: (String) -> Unit
) {
    val requests by viewModel.requests.collectAsState()
    var requestSeleccionada by remember { mutableStateOf<AdoptionRequest?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Solicitudes", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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

            // Filtramos solo las pendientes para que el admin vea lo que falta procesar
            val pendingRequests = requests.filter { it.status == AdoptionRequestStatus.PENDING }

            if (pendingRequests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No hay solicitudes pendientes", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                // AQUÍ USAMOS LAZYCOLUMN PARA MOSTRAR TODAS LAS SOLICITUDES DE DIFERENTES USUARIOS
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            "SOLICITUDES PENDIENTES (${pendingRequests.size})", 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 20.sp, 
                            color = Color(0xFF5D2E17),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(pendingRequests) { request ->
                        CardSolicitud(request) {
                            requestSeleccionada = request
                        }
                    }
                }
            }
        }
    }

    requestSeleccionada?.let { request ->
        DetalleSolicitudDialog(
            request = request,
            onDismiss = { requestSeleccionada = null },
            onAccept = {
                requestSeleccionada = null
                onNavigateToCertificado(request.id)
            },
            onReject = {
                requestSeleccionada = null
                onNavigateToRechazo(request.id)
            }
        )
    }
}

@Composable
fun CardSolicitud(request: AdoptionRequest, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).background(Color(0xFFFFF4C2), CircleShape), contentAlignment = Alignment.Center) {
                Text(request.userName.take(1).uppercase(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE67E22))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(request.userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Desea adoptar a ${request.petName}", fontSize = 14.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = if (request.rejectionCount >= 3) Color.Red.copy(alpha = 0.1f) else Color.DarkGray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "INTENTOS: ${request.rejectionCount}/3",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (request.rejectionCount >= 3) Color.Red else Color.DarkGray
                    )
                }
            }
            StatusChip(request.status)
        }
    }
}

@Composable
fun StatusChip(status: AdoptionRequestStatus) {
    val color = when(status) {
        AdoptionRequestStatus.PENDING -> Color.Gray
        AdoptionRequestStatus.APPROVED -> Color(0xFF4CAF50)
        AdoptionRequestStatus.REJECTED -> Color.Red
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun DetalleSolicitudDialog(
    request: AdoptionRequest,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    var timeLeft by remember { mutableLongStateOf(0L) }
    
    LaunchedEffect(request.penaltyEndTime) {
        while (true) {
            val now = System.currentTimeMillis()
            timeLeft = (request.penaltyEndTime - now) / 1000
            if (timeLeft < 0) {
                timeLeft = 0
                break
            }
            delay(1000)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text("Revisión de Solicitud", fontWeight = FontWeight.Black)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                InfoItemLocal("Adoptante", request.userName)
                InfoItemLocal("Mascota", "${request.petName} (${request.petType})")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                InfoItemLocal("Motivación", request.motivation)
                InfoItemLocal("Vivienda", request.homeType)
                InfoItemLocal("Horas solo", "${request.hoursAlone} horas")
                InfoItemLocal("Referencia", "${request.referenceName} (${request.referencePhone})")
                
                if (timeLeft > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = Color.Red.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Timer, null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Bloqueado por $timeLeft seg (Penalización)",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept, 
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = timeLeft <= 0
            ) {
                Text("APROBAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onReject) {
                Text("RECHAZAR", color = Color.Red)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White
    )
}

@Composable
fun InfoItemLocal(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, color = Color.Black)
    }
}
