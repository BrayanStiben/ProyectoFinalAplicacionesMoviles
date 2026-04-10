package com.example.seguimiento.features.ListaDeSolicitudes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.R
import com.example.seguimiento.core.navigation.AdminBottomBar

val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)

@Composable
fun ListaSolicitudesScreen(
    viewModel: AdopcionViewModel = hiltViewModel(),
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToListaSolicitudes: () -> Unit,
    onNavigateToEncontrarMascotas: () -> Unit,
    onLogout: () -> Unit
) {
    val solicitudes by viewModel.solicitudes.collectAsState()
    var showRejectDialog by remember { mutableStateOf<String?>(null) }
    var rejectReason by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            AdminBottomBar(
                currentRoute = "lista_solicitudes",
                onNavigateToEstadisticas = onNavigateToEstadisticas,
                onNavigateToListaSolicitudes = onNavigateToListaSolicitudes,
                onNavigateToEncontrarMascotas = onNavigateToEncontrarMascotas,
                onLogout = onLogout
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFDFBFA))
        ) {
            // --- HEADER ESTILO PANEL/MASCOTAS ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 45.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Text(
                        stringResource(R.string.admin_list_requests_title),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.admin_list_requests_subtitle),
                        color = Color.White.copy(0.9f),
                        fontSize = 15.sp
                    )
                }
            }

            if (solicitudes.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(solicitudes) { solicitud ->
                        ModeracionItem(
                            solicitud = solicitud,
                            onApprove = { viewModel.aprobarPublicacion(solicitud.id) },
                            onReject = { showRejectDialog = solicitud.id }
                        )
                    }
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.admin_adoption_no_pending), color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (showRejectDialog != null) {
            AlertDialog(
                onDismissRequest = { showRejectDialog = null },
                title = { Text(stringResource(R.string.admin_list_requests_dialog_reject_title)) },
                text = {
                    Column {
                        Text(stringResource(R.string.admin_list_requests_dialog_reject_instruction))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = rejectReason,
                            onValueChange = { rejectReason = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.rechazarPublicacion(showRejectDialog!!, rejectReason)
                            showRejectDialog = null
                            rejectReason = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) { Text(stringResource(R.string.admin_list_requests_btn_confirm_reject)) }
                },
                dismissButton = {
                    TextButton(onClick = { showRejectDialog = null }) { Text(stringResource(R.string.btn_cancel)) }
                }
            )
        }
    }
}

@Composable
fun ModeracionItem(solicitud: Mascota, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = solicitud.imagenUrl,
                    contentDescription = solicitud.nombre,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.petadopticono)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(solicitud.nombre, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = CafeApp)
                    Text("${solicitud.tipo} • ${solicitud.raza}", color = Color.Gray, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = NaranjaApp, modifier = Modifier.size(14.dp))
                        Text(solicitud.ubicacion, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            if (solicitud.resumenIA.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(solicitud.resumenIA, fontSize = 12.sp, color = Color(0xFF1976D2), lineHeight = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.admin_list_requests_btn_verify), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.admin_adoption_btn_reject), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
