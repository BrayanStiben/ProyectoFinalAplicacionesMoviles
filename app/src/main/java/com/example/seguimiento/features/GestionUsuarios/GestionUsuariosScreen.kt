package com.example.seguimiento.features.GestionUsuarios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosScreen(
    viewModel: GestionUsuariosViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val ColorVivoNaranja = Color(0xFFFF6D00)
    val ColorVivoVerde = Color(0xFF00C853)
    
    var tabIndex by remember { mutableIntStateOf(0) }
    var userParaBanear by remember { mutableStateOf<User?>(null) }
    var motivoBaneo by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_user_mgmt_title), fontWeight = FontWeight.Black, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.9f))
            )

            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color.White.copy(alpha = 0.9f),
                contentColor = ColorVivoNaranja,
                indicator = { tabPositions ->
                    if (tabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                            color = ColorVivoNaranja
                        )
                    }
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val listaMostrar = if (tabIndex == 0) state.listaUsuarios else state.listaUsuariosBaneados
                
                if (listaMostrar.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.admin_user_empty_list), color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                } else {
                    listaMostrar.forEach { user ->
                        UsuarioCardDetallada(
                            user = user,
                            onBanClick = { userParaBanear = it },
                            onUnbanClick = { viewModel.desbanearUsuario(user.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }

    // Dialogo para Banear
    if (userParaBanear != null) {
        AlertDialog(
            onDismissRequest = { userParaBanear = null; motivoBaneo = "" },
            title = { Text(stringResource(R.string.admin_user_dialog_ban_title), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(stringResource(R.string.admin_user_dialog_ban_question, userParaBanear?.name ?: ""))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = motivoBaneo,
                        onValueChange = { motivoBaneo = it },
                        placeholder = { Text(stringResource(R.string.admin_user_dialog_ban_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userParaBanear?.let {
                            viewModel.banearUsuario(it.id, motivoBaneo)
                        }
                        userParaBanear = null
                        motivoBaneo = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(stringResource(R.string.admin_user_btn_ban), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { userParaBanear = null; motivoBaneo = "" }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
fun UsuarioCardDetallada(
    user: User,
    onBanClick: (User) -> Unit,
    onUnbanClick: (User) -> Unit
) {
    val ColorVivoNaranja = Color(0xFFFF6D00)
    val ColorVivoVerde = Color(0xFF00C853)

    Card(
        modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = user.profilePictureUrl.ifEmpty { "https://cdn-icons-png.flaticon.com/512/149/149071.png" },
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                    Text(user.email, fontSize = 13.sp, color = Color.Gray)
                    
                    Spacer(Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = ColorVivoNaranja, modifier = Modifier.size(16.dp))
                        Text(
                            text = user.city.ifEmpty { stringResource(R.string.admin_user_location_undefined) },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorVivoNaranja
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = if(user.role.name == "ADMIN") Color(0xFF0091EA) else ColorVivoVerde,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            user.role.name,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    if (user.role.name != "ADMIN") {
                        if (!user.isBanned) {
                            IconButton(onClick = { onBanClick(user) }) {
                                Icon(Icons.Default.Block, stringResource(R.string.admin_user_btn_ban), tint = Color.Red)
                            }
                        } else {
                            IconButton(onClick = { onUnbanClick(user) }) {
                                Icon(Icons.Default.CheckCircle, stringResource(R.string.admin_user_btn_unban), tint = ColorVivoVerde)
                            }
                        }
                    }
                }
            }
            
            if (user.isBanned && user.banReason.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_user_ban_reason_label, user.banReason),
                        modifier = Modifier.padding(8.dp),
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
