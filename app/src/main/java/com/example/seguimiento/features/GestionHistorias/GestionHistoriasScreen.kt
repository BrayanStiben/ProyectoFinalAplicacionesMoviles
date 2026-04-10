package com.example.seguimiento.features.GestionHistorias

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionHistoriasScreen(
    viewModel: GestionHistoriasViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val historiasPendientes by viewModel.historiasPendientes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_stories_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
            
            if (historiasPendientes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.HistoryEdu, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text(stringResource(R.string.admin_stories_empty), color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(historiasPendientes) { historia ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = historia.imagenUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(historia.mascotaNombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Text(stringResource(R.string.admin_stories_author, historia.autorNombre), fontSize = 14.sp, color = Color.Gray)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(historia.texto, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = { viewModel.rechazarHistoria(historia.id) },
                                        modifier = Modifier.background(Color.Red.copy(alpha = 0.1f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.admin_stories_btn_reject), tint = Color.Red)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    IconButton(
                                        onClick = { viewModel.aprobarHistoria(historia.id) },
                                        modifier = Modifier.background(Color(0xFF00C853).copy(alpha = 0.1f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.admin_stories_btn_approve), tint = Color(0xFF00C853))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
