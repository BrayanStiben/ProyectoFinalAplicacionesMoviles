package com.example.seguimiento.features.HistoriaMascota

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.seguimiento.R
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import coil.compose.AsyncImage
import com.example.seguimiento.Dominio.modelos.HistoriaFeliz
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.core.navigation.AdminBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPetAdopta(
    viewModel: HistoriaMascotaViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToEstadisticas: () -> Unit = {},
    onNavigateToListaSolicitudes: () -> Unit = {},
    onNavigateToEncontrarMascotas: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val textoHistoria by viewModel.textoHistoria.collectAsState()
    val mascotaNombre by viewModel.mascotaNombre.collectAsState()
    val imagenSeleccionada by viewModel.imagenSeleccionada.collectAsState()
    val historiasAprobadas by viewModel.historiasAprobadas.collectAsState()
    val currentUser by viewModel.authRepository.currentUser.collectAsState()

    var showPostForm by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.alSeleccionarImagen(uri)
    }

    Scaffold(
        bottomBar = {
            if (currentUser?.role == UserRole.ADMIN) {
                AdminBottomBar(
                    currentRoute = "historias_exito",
                    onNavigateToEstadisticas = onNavigateToEstadisticas,
                    onNavigateToListaSolicitudes = onNavigateToListaSolicitudes,
                    onNavigateToEncontrarMascotas = onNavigateToEncontrarMascotas,
                    onLogout = onLogout
                )
            } else {
                BottomNavPet(selectedItem = 0) { index -> 
                    when(index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToFiltros()
                        3 -> onNavigateToProfile()
                    }
                }
            }
        },
        floatingActionButton = {
            if (!showPostForm) {
                ExtendedFloatingActionButton(
                    onClick = { showPostForm = true },
                    containerColor = Color(0xFFE67E22),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Compartir Historia") },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Image(
                painter = painterResource(id = R.drawable.fondo3),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))),
                                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                            )
                            .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                            }
                            Column {
                                Text(
                                    "PetAdopta Stories ✨",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    "Historias que cambian vidas",
                                    color = Color.White.copy(0.9f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                items(historiasAprobadas) { historia ->
                    SocialHistoryCard(
                        historia = historia,
                        currentUserId = currentUser?.id ?: "",
                        onLike = { viewModel.toggleLike(historia.id) },
                        onFollow = { viewModel.toggleFollow(historia.id) },
                        viewModel = viewModel
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (showPostForm) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showPostForm = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clickable(enabled = false) {},
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Nueva Historia 🐾", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF5D2E17))
                                IconButton(onClick = { showPostForm = false }) {
                                    Icon(Icons.Default.Close, null)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = mascotaNombre,
                                onValueChange = { viewModel.alCambiarNombreMascota(it) },
                                label = { Text("Nombre de la mascota") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = textoHistoria,
                                onValueChange = { viewModel.alCambiarTexto(it) },
                                label = { Text("Cuéntanos tu historia...") },
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (imagenSeleccionada != null) {
                                Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))) {
                                    AsyncImage(
                                        model = imagenSeleccionada,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { launcher.launch("image/*") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, null, tint = Color(0xFFE67E22))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Elegir Foto", color = Color(0xFFE67E22))
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { 
                                    viewModel.compartir {
                                        showPostForm = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                                shape = RoundedCornerShape(16.dp),
                                enabled = mascotaNombre.isNotBlank() && textoHistoria.isNotBlank()
                            ) {
                                Text("COMPARTIR AHORA", fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialHistoryCard(
    historia: HistoriaFeliz,
    currentUserId: String,
    onLike: () -> Unit,
    onFollow: () -> Unit,
    viewModel: HistoriaMascotaViewModel
) {
    var showComments by remember { mutableStateOf(false) }
    val isLiked = historia.likerIds.contains(currentUserId)
    val isFollowed = historia.followersIds.contains(currentUserId)
    
    val totalLikes = historia.likerIds.size
    val totalFollowers = historia.followersIds.size

    val textLikes = when {
        isLiked && totalLikes > 1 -> "Tú y ${totalLikes - 1} personas"
        isLiked -> "Tú"
        totalLikes > 0 -> "$totalLikes personas"
        else -> "0 likes"
    }

    val textFollows = when {
        isFollowed && totalFollowers > 1 -> "Tú y ${totalFollowers - 1} personas siguen esto"
        isFollowed -> "Sigues esta historia"
        totalFollowers > 0 -> "$totalFollowers seguidores"
        else -> "0 seguidores"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE67E22).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountCircle, null, tint = Color(0xFFE67E22), modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(historia.autorNombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Acaba de compartir un momento", fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onFollow) {
                    Icon(
                        imageVector = if (isFollowed) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Follow",
                        tint = if (isFollowed) Color(0xFFE67E22) else Color.Gray
                    )
                }
            }

            AsyncImage(
                model = historia.imagenUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                IconButton(onClick = onLike) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null,
                        tint = if (isLiked) Color.Red else Color.Black
                    )
                }
                IconButton(onClick = { showComments = !showComments }) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                }
            }

            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                Text(textLikes, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(textFollows, fontSize = 11.sp, color = Color.Gray)
                
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "La historia de ${historia.mascotaNombre} 🐾",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Color(0xFFE67E22)
                )
                Text(
                    text = historia.texto,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }

            if (showComments) {
                CommentsSection(historiaId = historia.id, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CommentsSection(historiaId: String, viewModel: HistoriaMascotaViewModel) {
    val comentarios by viewModel.getComentarios(historiaId).collectAsState(emptyList())
    var nuevoComentario by remember { mutableStateOf("") }
    var replyingToId by remember { mutableStateOf<String?>(null) }
    var replyingToName by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp).background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp)).padding(12.dp)) {
        Text("Comentarios", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        
        comentarios.filter { it.parentId == null }.forEach { principal ->
            CommentItem(principal, onReply = { 
                replyingToId = principal.id
                replyingToName = principal.autorNombre
            })
            // Respuestas
            comentarios.filter { it.parentId == principal.id }.forEach { respuesta ->
                CommentItem(respuesta, isReply = true)
            }
        }

        Spacer(Modifier.height(12.dp))

        if (replyingToId != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                Text("Respondiendo a $replyingToName", fontSize = 11.sp, color = Color(0xFFE67E22), modifier = Modifier.weight(1f))
                IconButton(onClick = { replyingToId = null }, modifier = Modifier.size(16.dp)) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(12.dp))
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = nuevoComentario,
                onValueChange = { nuevoComentario = it },
                placeholder = { Text("Escribe un comentario...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                maxLines = 2
            )
            IconButton(onClick = { 
                if (nuevoComentario.isNotBlank()) {
                    viewModel.agregarComentario(historiaId, nuevoComentario, replyingToId)
                    nuevoComentario = ""
                    replyingToId = null
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color(0xFFE67E22))
            }
        }
    }
}

@Composable
fun CommentItem(comentario: com.example.seguimiento.Dominio.modelos.Comentario, isReply: Boolean = false, onReply: (() -> Unit)? = null) {
    Row(modifier = Modifier.padding(top = 8.dp, start = if (isReply) 32.dp else 0.dp)) {
        Box(modifier = Modifier.size(24.dp).background(Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = Color.White)
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comentario.autorNombre, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                if (onReply != null) {
                    Text(
                        "Responder", 
                        fontSize = 10.sp, 
                        color = Color.Blue, 
                        modifier = Modifier.padding(start = 12.dp).clickable { onReply() }
                    )
                }
            }
            Text(comentario.contenido, fontSize = 12.sp)
        }
    }
}

@Composable
fun BottomNavPet(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val naranjaNav = Color(0xFFE67E22)
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
                    selectedIconColor = naranjaNav,
                    selectedTextColor = naranjaNav,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFFFFF4C2)
                )
            )
        }
    }
}
