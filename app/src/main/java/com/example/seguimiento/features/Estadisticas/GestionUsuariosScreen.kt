package com.example.seguimiento.features.Estadisticas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.R
import com.example.seguimiento.core.navigation.AdminBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosScreen(
    viewModel: EstadisticasViewModel = hiltViewModel(),
    onNavigateToEstadisticas: () -> Unit = {},
    onNavigateToListaSolicitudes: () -> Unit = {},
    onNavigateToEncontrarMascotas: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var tabIndex by remember { mutableIntStateOf(0) }
    var userParaBanear by remember { mutableStateOf<User?>(null) }
    var motivoBaneo by remember { mutableStateOf("") }

    val naranjaFuerte = Color(0xFFE67E22)
    val cafeFuerte = Color(0xFF5D2E17)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFDFBFA),
        bottomBar = {
            AdminBottomBar(
                currentRoute = "gestion_usuarios",
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
        ) {
            // --- HEADER ESTILO UNIFICADO ---
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            stringResource(R.string.admin_user_mgmt_title),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        stringResource(R.string.admin_user_mgmt_subtitle),
                        color = Color.White.copy(0.9f),
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 52.dp)
                    )
                }
            }

            // --- TABS ESTILO MODERNO ---
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color.Transparent,
                contentColor = naranjaFuerte,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                        color = naranjaFuerte,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    text = { Text(stringResource(R.string.admin_user_tab_active, state.usuariosTotales), fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    text = { Text(stringResource(R.string.admin_user_tab_banned, state.usuariosBaneados), fontWeight = FontWeight.Bold) }
                )
            }

            // --- LISTADO ---
            val listaMostrar = if (tabIndex == 0) state.listaUsuarios else state.listaUsuariosBaneados
            
            if (listaMostrar.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Text(stringResource(R.string.admin_user_empty_list), color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaMostrar) { user ->
                        UsuarioCardAdmin(
                            user = user,
                            onBanClick = { userParaBanear = it },
                            onUnbanClick = { viewModel.desbanearUsuario(user.id) }
                        )
                    }
                }
            }
        }
    }

    // --- DIALOGO DE BANEO ---
    if (userParaBanear != null) {
        AlertDialog(
            onDismissRequest = { userParaBanear = null; motivoBaneo = "" },
            title = { Text(stringResource(R.string.admin_user_dialog_ban_title), fontWeight = FontWeight.Black, color = cafeFuerte) },
            text = {
                Column {
                    Text(stringResource(R.string.admin_user_dialog_ban_question, userParaBanear?.name ?: ""), fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = motivoBaneo,
                        onValueChange = { motivoBaneo = it },
                        placeholder = { Text(stringResource(R.string.admin_user_dialog_ban_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userParaBanear?.let { viewModel.banearUsuario(it.id, motivoBaneo) }
                        userParaBanear = null
                        motivoBaneo = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(stringResource(R.string.admin_user_btn_ban), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { userParaBanear = null; motivoBaneo = "" }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }
}

@Composable
fun UsuarioCardAdmin(
    user: User,
    onBanClick: (User) -> Unit,
    onUnbanClick: () -> Unit
) {
    val naranjaApp = Color(0xFFE67E22)
    val cafeApp = Color(0xFF5D2E17)
    val verdeApp = Color(0xFF4CAF50)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = user.profilePictureUrl.ifEmpty { "https://cdn-icons-png.flaticon.com/512/149/149071.png" },
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cafeApp)
                    Text(user.email, fontSize = 13.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = naranjaApp, modifier = Modifier.size(14.dp))
                        Text(user.city.ifEmpty { stringResource(R.string.admin_user_location_undefined) }, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                // Badge de Rol
                Surface(
                    color = if(user.role.name == "ADMIN") Color(0xFF2196F3) else verdeApp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        user.role.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botón de Acción
            if (user.role.name != "ADMIN") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (!user.isBanned) {
                        Button(
                            onClick = { onBanClick(user) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Block, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.admin_user_btn_ban_short), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onUnbanClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = verdeApp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.admin_user_btn_unban), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (user.isBanned && user.banReason.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Red.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = stringResource(R.string.admin_user_ban_reason_label, user.banReason),
                        modifier = Modifier.padding(10.dp),
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
