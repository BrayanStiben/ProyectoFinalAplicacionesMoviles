package com.example.seguimiento.features.CertificadoAdopcion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.seguimiento.R
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificadoAdopcionScreen(
    requestId: String,
    viewModel: CertificadoAdopcionViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(requestId) {
        viewModel.loadRequestData(requestId)
    }

    val req = uiState.request
    val isApproved = req?.status == AdoptionRequestStatus.APPROVED

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.cert_adopcion_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.btn_close))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF5D2E17)
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
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = stringResource(id = R.string.cert_rechazo_petadopta),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE67E22),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = stringResource(id = R.string.cert_adopcion_header),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "CO"))
                        val fechaActual = sdf.format(Date(req?.timestamp ?: System.currentTimeMillis()))
                        val masc = uiState.mascota

                        val petName = req?.petName ?: masc?.nombre ?: stringResource(id = R.string.placeholder_name)
                        val petType = req?.petType ?: masc?.tipo ?: stringResource(id = R.string.placeholder_type)
                        val petRaza = masc?.raza ?: stringResource(id = R.string.placeholder_breed)
                        val petAge = req?.petAge ?: masc?.edad ?: stringResource(id = R.string.placeholder_age)
                        val petIdStr = req?.mascotaId ?: masc?.id ?: stringResource(id = R.string.placeholder_id)
                        
                        val userName = req?.userName ?: stringResource(id = R.string.placeholder_adopter)
                        val userAddressPart = if (req?.userAddress?.isNotBlank() == true) stringResource(id = R.string.cert_adopcion_residente, req.userAddress) else ""
                        val userEmailPart = if (req?.userEmail?.isNotBlank() == true) stringResource(id = R.string.cert_adopcion_email, req.userEmail) else ""

                        val textoCertificado = stringResource(
                            id = R.string.cert_adopcion_body,
                            petName, petType, petRaza, petAge, petIdStr,
                            userName, userAddressPart, userEmailPart,
                            fechaActual
                        )

                        Text(
                            text = textoCertificado,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Justify,
                            lineHeight = 18.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = stringResource(id = R.string.cert_adopcion_signature_label),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(vertical = 8.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .background(Color(0xFFFAFAFA)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isApproved && !req?.adminSignature.isNullOrBlank()) {
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
                            } else if (!isApproved && uiState.isAdmin) {
                                SignaturePadFinal { bitmap ->
                                    viewModel.setSignature(bitmap)
                                }
                            } else {
                                Text(stringResource(id = R.string.cert_adopcion_pending_signature), color = Color.Gray, fontStyle = FontStyle.Italic)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botones de acción según el rol
                        if (!isApproved && uiState.isAdmin) {
                            Button(
                                onClick = { viewModel.finalizeAdoption() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp),
                                enabled = uiState.adminSignature != null && !uiState.isLoading
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text(stringResource(id = R.string.cert_adopcion_btn_confirm), fontWeight = FontWeight.Bold)
                                }
                            }
                        } else if (isApproved) {
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Verified, null, tint = Color(0xFF2E7D32))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(id = R.string.cert_adopcion_validated),
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = onFinish,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D2E17)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(id = R.string.btn_back), fontWeight = FontWeight.Bold)
                            }
                        } else if (!uiState.isAdmin) {
                            // Usuario normal viendo un certificado aún no firmado por Admin
                            Text(
                                stringResource(id = R.string.cert_adopcion_wait_msg),
                                color = Color.Gray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SignaturePadFinal(onSignatureCaptured: (Bitmap) -> Unit) {
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
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
        if (paths.isEmpty()) {
            Text(
                stringResource(id = R.string.cert_adopcion_signature_hint),
                modifier = Modifier.align(Alignment.Center),
                color = Color.LightGray,
                fontStyle = FontStyle.Italic
            )
        }
        
        IconButton(
            onClick = { paths.clear() },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Default.Delete, stringResource(id = R.string.btn_delete), tint = Color.Red.copy(alpha = 0.5f))
        }
    }
}
