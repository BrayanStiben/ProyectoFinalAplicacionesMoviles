package com.example.seguimiento.features.Logros

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import com.example.seguimiento.features.home.BottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogrosScreen(
    viewModel: LogrosViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val naranjaApp = Color(0xFFE67E22)
    val cafeApp = Color(0xFF5D2E17)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFDFBFA),
        bottomBar = {
            BottomNav(
                selectedItem = -1,
                onNavigateToHome = onNavigateToHome,
                onNavigateToFiltros = onNavigateToFiltros,
                onNavigateToFavoritos = onNavigateToFavoritos,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- HEADER CURVO ESTILO UNIFICADO (Sin líneas grises) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 45.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            stringResource(R.string.logros_title),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.logros_subtitle),
                            color = Color.White.copy(0.9f),
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.fondo2),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.5f
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            stringResource(R.string.logros_instruction),
                            fontSize = 14.sp,
                            color = cafeApp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(uiState.todosLosLogros) { logro ->
                        val esObtenido = uiState.obtenidosIds.contains(logro.id)
                        LogroItemView(logro = logro, esObtenido = esObtenido)
                    }
                }
            }
        }
    }
}

@Composable
fun LogroItemView(logro: com.example.seguimiento.Dominio.modelos.Logro, esObtenido: Boolean) {
    val cafeApp = Color(0xFF5D2E17)
    val naranjaApp = Color(0xFFE67E22)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esObtenido) Color.White.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(if (esObtenido) 4.dp else 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(70.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = if (logro.iconResId != 0) logro.iconResId else R.drawable.petadopticono),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).clip(CircleShape),
                    colorFilter = if (!esObtenido) {
                        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                    } else null,
                    alpha = if (esObtenido) 1f else 0.5f
                )
                if (!esObtenido) {
                    Icon(
                        Icons.Default.Lock, null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp).background(Color.Black.copy(0.3f), CircleShape).padding(4.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(logro.tituloResId),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = if (esObtenido) cafeApp else Color.Gray
                )
                Text(
                    stringResource(logro.descripcionResId),
                    fontSize = 13.sp,
                    color = if (esObtenido) Color.DarkGray else Color.Gray.copy(0.8f),
                    lineHeight = 18.sp
                )
                if (esObtenido) {
                    Text(
                        stringResource(R.string.logros_completed_tag),
                        color = naranjaApp,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
