package com.example.seguimiento.features.EsperandoPorTi

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.modelos.Comentario
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.R
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstaEsperandoPorTiScreen(
    id: String = "",
    nombre: String = "",
    edad: String = "",
    ubicacion: String = "",
    url: String = "",
    miViewModel: EstaEsperandoViewModel = hiltViewModel(),
    adoptionViewModel: AdoptionViewModel, 
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToRequisitos: () -> Unit = {},
    onNavigateToCertificado: (String) -> Unit = {},
    onNavigateToRechazo: (String) -> Unit = {}
) {
    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            miViewModel.seleccionarMascota(id)
        }
    }

    val mascota by miViewModel.mascota.collectAsState()
    val todosComentarios by miViewModel.comentarios.collectAsState()
    val approvedRequest by miViewModel.approvedRequest.collectAsState()
    val rejectedRequest by miViewModel.rejectedRequest.collectAsState()
    val isPenalized by miViewModel.isPenalized.collectAsState()
    val penaltyTime by miViewModel.penaltyTimeLeft.collectAsState()
    val req by miViewModel.currentRequest.collectAsState()
    val currentUser by miViewModel.currentUser.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var nuevoComentarioTexto by remember { mutableStateOf("") }
    var respondientoA by remember { mutableStateOf<Comentario?>(null) }
    val comentariosExpandidos = remember { mutableStateMapOf<String, Boolean>() }

    val petName = mascota?.nombre ?: nombre
    val petAge = mascota?.edad ?: edad
    val petLoc = mascota?.ubicacion ?: ubicacion
    val petImg = mascota?.imagenUrl ?: url
    val petType = mascota?.tipo ?: ""
    val estadoMascota = mascota?.estado ?: PublicacionEstado.PENDIENTE
    val isLiked = mascota?.likerIds?.contains(currentUser?.id) ?: false
    
    val isMyAdoption = approvedRequest != null
    val isMyRejection = rejectedRequest != null

    val comentariosPrincipales = todosComentarios.filter { it.parentId == null }

    val favRemovedMsg = stringResource(R.string.pet_detail_fav_removed)
    val favAddedMsg = stringResource(R.string.pet_detail_fav_added)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                val items = listOf(
                    Triple(stringResource(R.string.nav_home), Icons.Default.Home, 0), 
                    Triple(stringResource(R.string.nav_search), Icons.Default.Search, 1), 
                    Triple(stringResource(R.string.nav_favorites), Icons.Default.FavoriteBorder, 2), 
                    Triple(stringResource(R.string.nav_profile), Icons.Default.Person, 3)
                )
                items.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        icon = { Icon(icon, null) }, 
                        label = { Text(label, fontSize = 10.sp) }, 
                        selected = false, 
                        onClick = { 
                            when(index) {
                                0 -> onNavigateToHome()
                                1 -> onNavigateToFiltros()
                                2 -> onNavigateToFavoritos()
                                3 -> onNavigateToProfile()
                            }
                        }, 
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFE67E22), 
                            unselectedIconColor = Color.Gray, 
                            indicatorColor = Color(0xFFFFF4C2)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFFDF7F2))) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // HERO HEADER
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                        AsyncImage(model = petImg, contentDescription = petName, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent), endY = 300f)))
                        
                        if (estadoMascota == PublicacionEstado.ADOPTADA) {
                            StatusStamp(text = stringResource(R.string.status_adopted), color = Color(0xFF4CAF50))
                        } else if (isMyRejection) {
                            StatusStamp(text = stringResource(R.string.status_rejected), color = Color.Red)
                        }

                        Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onNavigateToHome() }, modifier = Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                            }
                            Text(stringResource(R.string.pet_detail_title), modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(Modifier.width(48.dp))
                        }
                    }
                }

                // INFO CARD
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).offset(y = (-40).dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = petName, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF5D2E17))
                                        if (estadoMascota == PublicacionEstado.ADOPTADA) {
                                            Spacer(Modifier.width(8.dp))
                                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                                        } else if (isMyRejection) {
                                            Spacer(Modifier.width(8.dp))
                                            Icon(Icons.Default.Cancel, null, tint = Color.Red, modifier = Modifier.size(24.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE67E22), modifier = Modifier.size(16.dp))
                                        Text(petLoc, color = Color.Gray, fontSize = 14.sp)
                                        Spacer(Modifier.width(12.dp))
                                        Icon(Icons.Default.Timer, null, tint = Color(0xFFE67E22), modifier = Modifier.size(16.dp))
                                        Text(petAge, color = Color.Gray, fontSize = 14.sp)
                                    }
                                }
                                IconButton(
                                    onClick = { 
                                        miViewModel.toggleLike()
                                        scope.launch { snackbarHostState.showSnackbar(if(isLiked) favRemovedMsg else favAddedMsg) } 
                                    }, 
                                    modifier = Modifier.size(48.dp).background(Color(0xFFFEEAE6), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                                        null, 
                                        tint = Color(0xFFE76F51)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-20).dp)) {
                        Text(stringResource(R.string.pet_detail_description), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF5D2E17))
                        Text(text = mascota?.descripcion ?: "...", fontSize = 16.sp, color = Color.DarkGray, lineHeight = 24.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(stringResource(R.string.pet_detail_comments, comentariosPrincipales.size), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF5D2E17))
                        Spacer(modifier = Modifier.height(8.dp))

                        AnimatedVisibility(visible = respondientoA != null) {
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).background(Color(0xFFFFF4C2), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.Reply, null, tint = Color(0xFFE67E22), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(text = stringResource(R.string.pet_detail_replying_to, respondientoA?.autorNombre ?: ""), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D2E17), modifier = Modifier.weight(1f))
                                IconButton(onClick = { respondientoA = null }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) }
                            }
                        }

                        OutlinedTextField(
                            value = nuevoComentarioTexto,
                            onValueChange = { nuevoComentarioTexto = it },
                            placeholder = { Text(if(respondientoA == null) stringResource(R.string.pet_detail_comment_placeholder) else stringResource(R.string.pet_detail_reply_placeholder)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (nuevoComentarioTexto.isNotBlank()) {
                                        miViewModel.agregarComentario(nuevoComentarioTexto, respondientoA?.id)
                                        nuevoComentarioTexto = ""
                                        respondientoA = null
                                    }
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color(0xFFE67E22))
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE67E22), unfocusedBorderColor = Color.LightGray, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                items(comentariosPrincipales) { principal ->
                    val respuestas = todosComentarios.filter { it.parentId == principal.id }
                    val estaExpandido = comentariosExpandidos[principal.id] ?: false

                    ComentarioCardDetail(
                        comentario = principal,
                        onReply = { respondientoA = principal },
                        hasReplies = respuestas.isNotEmpty(),
                        isExpanded = estaExpandido,
                        onToggleExpand = { comentariosExpandidos[principal.id] = !estaExpandido }
                    )

                    AnimatedVisibility(visible = estaExpandido, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                        Column {
                            respuestas.forEach { respuesta ->
                                Row(modifier = Modifier.padding(start = 48.dp)) {
                                    ComentarioCardDetail(comentario = respuesta, isReply = true, onReply = { respondientoA = principal })
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Mostrar penalización si existe
                    if (isPenalized) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDECEA)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, null, tint = Color.Red)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    stringResource(R.string.pet_detail_penalty_active, penaltyTime),
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Botón para cancelar si hay una solicitud activa (Pendiente o Rechazada)
                    val reqCancelledMsg = stringResource(R.string.pet_detail_request_cancelled)
                    if (req != null && !isMyAdoption && estadoMascota != PublicacionEstado.ADOPTADA) {
                         OutlinedButton(
                            onClick = { 
                                req?.let { miViewModel.cancelarSolicitud(it.id) }
                                scope.launch { snackbarHostState.showSnackbar(reqCancelledMsg) }
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).height(50.dp),
                            border = BorderStroke(2.dp, Color.Red.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.pet_detail_btn_cancel_request), fontWeight = FontWeight.Bold)
                        }
                    }

                    TextButton(onClick = { onNavigateToHome() }, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                        Icon(Icons.Default.Home, null, tint = Color(0xFFE67E22))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.pet_detail_btn_back_home), color = Color(0xFFE67E22), fontWeight = FontWeight.Bold)
                    }
                    
                    val buttonText = when {
                        estadoMascota == PublicacionEstado.ADOPTADA && isMyAdoption -> stringResource(R.string.pet_detail_btn_view_cert)
                        isMyRejection -> stringResource(R.string.pet_detail_btn_view_rejection)
                        estadoMascota == PublicacionEstado.ADOPTADA -> stringResource(R.string.pet_detail_already_adopted)
                        req?.status == AdoptionRequestStatus.PENDING -> stringResource(R.string.pet_detail_request_pending)
                        else -> stringResource(R.string.pet_detail_btn_adopt)
                    }
                    val buttonColor = when {
                        estadoMascota == PublicacionEstado.ADOPTADA && isMyAdoption -> Color(0xFF4CAF50)
                        isMyRejection -> Color.Red
                        estadoMascota == PublicacionEstado.ADOPTADA -> Color.Gray
                        req?.status == AdoptionRequestStatus.PENDING -> Color(0xFFFFA000)
                        else -> Color(0xFFE67E22)
                    }
                    val enabled = when {
                        estadoMascota == PublicacionEstado.ADOPTADA && isMyAdoption -> true
                        isMyRejection -> true
                        estadoMascota == PublicacionEstado.ADOPTADA -> false
                        req?.status == AdoptionRequestStatus.PENDING -> true
                        else -> !isPenalized
                    }

                    val penaltyMsg = stringResource(R.string.pet_detail_penalty_msg, penaltyTime)
                    val reviewMsg = stringResource(R.string.pet_detail_request_review_msg)

                    Button(
                        onClick = {
                            if (isPenalized) {
                                scope.launch { snackbarHostState.showSnackbar(penaltyMsg) }
                            } else if (isMyRejection) {
                                rejectedRequest?.let { onNavigateToRechazo(it.id) }
                            } else if (estadoMascota == PublicacionEstado.ADOPTADA) {
                                if (isMyAdoption) approvedRequest?.let { onNavigateToCertificado(it.id) }
                            } else if (req?.status == AdoptionRequestStatus.PENDING) {
                                scope.launch { snackbarHostState.showSnackbar(reviewMsg) }
                            } else {
                                adoptionViewModel.setPetInfo(id, petName, petType, petAge)
                                onNavigateToRequisitos()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        shape = RoundedCornerShape(16.dp),
                        enabled = enabled
                    ) {
                        Text(buttonText, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    
                    // Botón de reintento si fue rechazado y no hay penalización
                    if (isMyRejection && !isPenalized) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                req?.let { miViewModel.cancelarSolicitud(it.id) }
                                adoptionViewModel.setPetInfo(id, petName, petType, petAge)
                                onNavigateToRequisitos()
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Refresh, null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.pet_detail_btn_retry), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun StatusStamp(text: String, color: Color) {
    Box(modifier = Modifier.fillMaxSize().padding(top = 100.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.rotate(-15f).background(color.copy(alpha = 0.9f), RoundedCornerShape(8.dp)).border(2.dp, Color.White, RoundedCornerShape(8.dp)).padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(text = text, color = Color.White, fontWeight = FontWeight.Black, fontSize = 32.sp)
        }
    }
}

@Composable
fun ComentarioCardDetail(comentario: Comentario, isReply: Boolean = false, onReply: (() -> Unit)? = null, hasReplies: Boolean = false, isExpanded: Boolean = false, onToggleExpand: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp).offset(y = (-30).dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(if(isReply) 32.dp else 42.dp).background(if(isReply) Color(0xFFE0E0E0) else Color(0xFFFFE0B2), CircleShape), contentAlignment = Alignment.Center) {
            Text(comentario.autorNombre.take(1).uppercase(), fontWeight = FontWeight.Bold, color = if(isReply) Color.Gray else Color(0xFFE67E22), fontSize = if(isReply) 12.sp else 14.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(comentario.autorNombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF5D2E17), modifier = Modifier.weight(1f))
                    if (onReply != null) IconButton(onClick = onReply, modifier = Modifier.size(24.dp)) { Icon(Icons.AutoMirrored.Filled.Reply, null, tint = Color.Gray, modifier = Modifier.size(18.dp)) }
                    if (hasReplies && onToggleExpand != null) IconButton(onClick = onToggleExpand, modifier = Modifier.size(24.dp)) { Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color(0xFFE67E22), modifier = Modifier.size(22.dp)) }
                }
                Text(comentario.contenido, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}
