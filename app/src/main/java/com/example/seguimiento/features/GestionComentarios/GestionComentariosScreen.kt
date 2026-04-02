package com.example.seguimiento.features.GestionComentarios

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.Dominio.modelos.Comentario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionComentariosScreen(
    viewModel: GestionComentariosViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val mascotasConComentarios by viewModel.mascotasConComentarios.collectAsState()
    var expandirTodo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moderación de Chat", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { expandirTodo = !expandirTodo }) {
                        Icon(
                            if (expandirTodo) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                            contentDescription = "Expandir/Contraer Todo",
                            tint = Color(0xFFE67E22)
                        )
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
            
            if (mascotasConComentarios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CommentsDisabled, null, modifier = Modifier.size(80.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("No hay mensajes nuevos", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(mascotasConComentarios) { item ->
                        ExpandableMascotaCard(
                            item = item,
                            defaultExpanded = expandirTodo,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableMascotaCard(
    item: MascotaConComentarios,
    defaultExpanded: Boolean,
    viewModel: GestionComentariosViewModel
) {
    var isExpanded by remember(defaultExpanded) { mutableStateOf(defaultExpanded) }
    var respondientoA by remember { mutableStateOf<Comentario?>(null) }
    var respuestaTexto by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.mascota.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.mascota.nombre, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF5D2E17))
                    Text("${item.comentarios.size} mensajes", fontSize = 13.sp, color = Color(0xFFFF6D00), fontWeight = FontWeight.Bold)
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            if (isExpanded) {
                Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val principales = item.comentarios.filter { it.parentId == null }
                    principales.forEach { principal ->
                        ItemComentarioModeracion(
                            comentario = principal,
                            onCensurar = { viewModel.censurarComentario(principal.id) },
                            onEliminar = { viewModel.eliminarComentario(principal.id) },
                            onReply = { respondientoA = principal }
                        )
                        
                        val respuestas = item.comentarios.filter { it.parentId == principal.id }
                        respuestas.forEach { respuesta ->
                            Row(modifier = Modifier.padding(start = 24.dp)) {
                                ItemComentarioModeracion(
                                    comentario = respuesta,
                                    onCensurar = { viewModel.censurarComentario(respuesta.id) },
                                    onEliminar = { viewModel.eliminarComentario(respuesta.id) },
                                    isReply = true
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (respondientoA != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF4C2), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Respuesta a ${respondientoA?.autorNombre}", fontSize = 11.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { respondientoA = null }, modifier = Modifier.size(16.dp)) { 
                                Icon(Icons.Default.Close, null) 
                            }
                        }
                    }
                    
                    OutlinedTextField(
                        value = respuestaTexto,
                        onValueChange = { respuestaTexto = it },
                        placeholder = { Text(if(respondientoA != null) "Responder..." else "Mensaje al chat...") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (respuestaTexto.isNotBlank()) {
                                    viewModel.responderComentario(item.mascota.id, respuestaTexto, respondientoA?.id)
                                    respuestaTexto = ""
                                    respondientoA = null
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color(0xFFFF6D00))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemComentarioModeracion(
    comentario: Comentario,
    onCensurar: () -> Unit,
    onEliminar: () -> Unit,
    onReply: (() -> Unit)? = null,
    isReply: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(if(isReply) 24.dp else 32.dp).background(Color(0xFFFFF4C2), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(comentario.autorNombre.take(1).uppercase(), fontWeight = FontWeight.Bold, color = Color(0xFFFF6D00), fontSize = if(isReply) 10.sp else 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(comentario.autorNombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (isReply) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.Reply, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = comentario.contenido,
            fontSize = 14.sp,
            color = if (comentario.contenido.contains("censurado")) Color.Red else Color.DarkGray
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            if (onReply != null && !isReply) {
                IconButton(onClick = onReply) {
                    Icon(Icons.AutoMirrored.Filled.Reply, "Responder", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }
            IconButton(onClick = onCensurar) {
                Icon(Icons.Default.Block, "Censurar", tint = Color(0xFFFF6D00), modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red, modifier = Modifier.size(18.dp))
            }
        }
    }
}
