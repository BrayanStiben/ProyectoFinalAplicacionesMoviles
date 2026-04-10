package com.example.seguimiento.features.CertificadoRechazo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificadoRechazoScreen(
    requestId: String,
    viewModel: CertificadoRechazoViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(requestId) {
        viewModel.loadRequestData(requestId)
    }

    val req = uiState.request
    val masc = uiState.mascota
    val isRejected = req?.status == AdoptionRequestStatus.REJECTED

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.cert_rechazo_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.btn_close))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Red
                )
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
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.cert_rechazo_petadopta),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (masc != null) {
                                AsyncImage(
                                    model = masc.imagenUrl,
                                    contentDescription = masc.nombre,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    alpha = if (uiState.isPenalized) 0.3f else 1f
                                )
                            }
                            
                            if (uiState.isPenalized) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Timer, 
                                        contentDescription = null, 
                                        tint = Color.Red,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = uiState.penaltyTimeLeft,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = stringResource(id = R.string.cert_rechazo_locked),
                                        color = Color.Red,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = stringResource(id = R.string.cert_rechazo_header),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        val fechaExacta = sdf.format(Date(req?.timestamp ?: System.currentTimeMillis()))
                        
                        val petName = req?.petName ?: "[Nombre]"
                        val petType = req?.petType ?: "[Tipo]"
                        val userName = req?.userName ?: "[Nombre del Solicitante]"
                        
                        // Lógica de conteo de intentos mejorada
                        val attemptNumber = when {
                            uiState.isPenalized -> 3
                            isRejected -> req?.rejectionCount ?: uiState.userRejectionCount
                            else -> uiState.userRejectionCount + 1
                        }

                        val accountStatus = if (uiState.isPenalized) {
                            stringResource(id = R.string.cert_rechazo_penalty_status)
                        } else {
                            stringResource(id = R.string.cert_rechazo_not_approved_status, attemptNumber)
                        }

                        val additionalInfo = if (uiState.isPenalized) {
                            stringResource(id = R.string.cert_rechazo_penalty_msg)
                        } else {
                            stringResource(id = R.string.cert_rechazo_retry_msg, attemptNumber)
                        }

                        val textoRechazo = stringResource(
                            id = R.string.cert_rechazo_body,
                            petName,
                            petType,
                            userName,
                            fechaExacta,
                            accountStatus,
                            additionalInfo
                        )

                        Text(
                            text = textoRechazo,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Justify,
                            lineHeight = 18.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(stringResource(id = R.string.cert_rechazo_signature_label), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(vertical = 8.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .background(Color(0xFFFAFAFA)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isRejected && !req?.adminSignature.isNullOrBlank()) {
                                val decodedBitmap = remember(req.adminSignature) {
                                    try {
                                        val bytes = Base64.decode(req.adminSignature, Base64.DEFAULT)
                                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                    } catch (e: Exception) { null }
                                }
                                if (decodedBitmap != null) {
                                    Image(
                                        bitmap = decodedBitmap.asImageBitmap(),
                                        contentDescription = "Firma",
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            } else if (uiState.isAdmin) {
                                SignaturePadRechazo { bitmap ->
                                    viewModel.setSignature(bitmap)
                                }
                            } else {
                                Text(stringResource(id = R.string.cert_rechazo_system_validated), color = Color.Gray, fontStyle = FontStyle.Italic)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.isAdmin && (req?.adminSignature.isNullOrBlank())) {
                            Button(
                                onClick = { viewModel.finalizeRejection() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                shape = RoundedCornerShape(12.dp),
                                enabled = uiState.adminSignature != null && !uiState.isLoading
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text(stringResource(id = R.string.cert_rechazo_btn_confirm), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        if (isRejected || !uiState.isAdmin) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (isRejected) {
                                    Surface(
                                        color = if (uiState.isPenalized) Color(0xFFFFEBEE) else Color(0xFFEEEEEE),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (uiState.isPenalized) stringResource(id = R.string.cert_rechazo_user_penalized) else stringResource(id = R.string.cert_rechazo_notified),
                                            color = if (uiState.isPenalized) Color.Red else Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(12.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                Button(
                                    onClick = onFinish,
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(stringResource(id = R.string.btn_exit), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SignaturePadRechazo(onSignatureCaptured: (Bitmap) -> Unit) {
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPath = Path().apply { moveTo(offset.x, offset.y) }
                        paths.add(currentPath!!)
                    },
                    onDrag = { change, _ ->
                        currentPath?.lineTo(change.position.x, change.position.y)
                        val last = paths.removeAt(paths.size - 1)
                        paths.add(last)
                    },
                    onDragEnd = {
                        val bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(bitmap)
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            style = android.graphics.Paint.Style.STROKE
                            strokeWidth = 10f
                            isAntiAlias = true
                            strokeCap = android.graphics.Paint.Cap.ROUND
                            strokeJoin = android.graphics.Paint.Join.ROUND
                        }
                        paths.forEach { path ->
                            canvas.drawPath(path.asAndroidPath(), paint)
                        }
                        onSignatureCaptured(bitmap)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            paths.forEach { path ->
                drawPath(path, Color.Black, style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
        if (paths.isEmpty()) {
            Text(stringResource(id = R.string.cert_rechazo_signature_hint), modifier = Modifier.align(Alignment.Center), color = Color.LightGray, fontStyle = FontStyle.Italic)
        }
        IconButton(onClick = { paths.clear() }, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(Icons.Default.Delete, stringResource(id = R.string.btn_delete), tint = Color.Red.copy(alpha = 0.5f))
        }
    }
}
