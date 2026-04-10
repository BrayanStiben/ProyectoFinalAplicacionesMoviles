package com.example.seguimiento.features.Refugios

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
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
import com.example.seguimiento.Dominio.modelos.RefugioTipo
import com.example.seguimiento.R
import com.example.seguimiento.features.home.BottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroRefugioScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToFavoritos: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: RefugioViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var tipoSeleccionado by remember { mutableStateOf(RefugioTipo.REFUGIO) }
    var estaCargando by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fotoUri = uri
    }

    val naranjaApp = Color(0xFFE67E22)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reg_ally_title), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.btn_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = naranjaApp)
            )
        },
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
                .background(Color(0xFFFDFBFA))
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 20.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.reg_ally_join_title), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(stringResource(R.string.reg_ally_join_subtitle), color = Color.White.copy(0.9f), fontSize = 14.sp)
                }
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .offset(y = (-20).dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    
                    Text(stringResource(R.string.reg_ally_type_q), fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = tipoSeleccionado == RefugioTipo.REFUGIO,
                            onClick = { tipoSeleccionado = RefugioTipo.REFUGIO },
                            label = { Text(stringResource(R.string.reg_ally_type_shelter)) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = tipoSeleccionado == RefugioTipo.VETERINARIA,
                            onClick = { tipoSeleccionado = RefugioTipo.VETERINARIA },
                            label = { Text(stringResource(R.string.reg_ally_type_vet)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))
                            .border(1.dp, naranjaApp.copy(0.3f), RoundedCornerShape(16.dp))
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoUri != null) {
                            AsyncImage(model = fotoUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, null, tint = naranjaApp, modifier = Modifier.size(40.dp))
                                Text(stringResource(R.string.reg_ally_photo_label), color = naranjaApp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = nombre, onValueChange = { nombre = it },
                        label = { Text(stringResource(R.string.reg_ally_label_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = direccion, onValueChange = { direccion = it },
                        label = { Text(stringResource(R.string.reg_ally_label_address)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = naranjaApp) }
                    )

                    OutlinedTextField(
                        value = telefono, onValueChange = { telefono = it },
                        label = { Text(stringResource(R.string.reg_ally_label_phone)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = naranjaApp) }
                    )

                    OutlinedTextField(
                        value = descripcion, onValueChange = { descripcion = it },
                        label = { Text(stringResource(R.string.reg_ally_label_desc)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (estaCargando) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = naranjaApp)
                    } else {
                        Button(
                            onClick = {
                                estaCargando = true
                                viewModel.registrarRefugioConTipo(nombre, direccion, telefono, descripcion, fotoUri?.toString() ?: "", tipoSeleccionado) {
                                    estaCargando = false
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = naranjaApp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = nombre.isNotBlank() && direccion.isNotBlank() && telefono.isNotBlank()
                        ) {
                            Text(stringResource(R.string.reg_ally_btn_register), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
